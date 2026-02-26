import { Component, HostListener} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { DatePipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-monitor-contabilizacion',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  providers: [DatePipe],
  templateUrl: './monitor-contabilizacion.component.html',
  styleUrls: ['./monitor-contabilizacion.component.css']
})
export class MonitorContabilizacionComponent {
  //3 dots menu 
  showMenu = false;
  toggleMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.showMenu = false;
  }

  //global variables
  private entcod: number | null = null;
  private eje: number | null = null;
  public centroGestor: string = '';
  private WSent: string = '';
  private WSorg: string = '';
  public estadogc: number | null = null;

  facturas: any[] = [];
  page = 0;
  pageSize = 20;
  estadoMessage: string = '';
  public Math = Math;

  constructor(private http: HttpClient, private router: Router, private datePipe: DatePipe ) {}

  isLoading: boolean = false;
  ngOnInit(): void{
    this.limpiarMEssages();
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');
    const estadoCentroGestor = sessionStorage.getItem('ESTADOGC');
    const entSession = sessionStorage.getItem('WSENT'); 
    const orgSession = sessionStorage.getItem('WSORG');   

    if (cge){ const parsed = JSON.parse(cge); this.centroGestor = parsed.value;}
    if (entidad) {const parsed = JSON.parse(entidad); this.entcod = parsed.ENTCOD;}
    if (eje) {const parsed = JSON.parse(eje); this.eje = parsed.eje;}
    if (entSession) {const parsed = JSON.parse(entSession); this.WSent = parsed.WSENT;}
    if (orgSession) {const parsed = JSON.parse(orgSession); this.WSorg = parsed.WSORG;}

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('Debes iniciar sesión para acceder a esta página.');
      this.router.navigate(['/login']);
      return;
    }

    if(estadoCentroGestor){
      const parsed = JSON.parse(estadoCentroGestor);
      this.estadogc = parsed.value;

      if (this.estadogc === 1) {
        this.estadoMessage = 'Centro Gestor CERRADO';
      }
      if ( this.estadogc === 2) {
        this.estadoMessage = 'Centro Gestor CERRADO para CONTABILIZAR';
      }
    }

    this.fetchFacturas();
  }

  //main table functions
  filterFacturaMessage:string = '';
  fetchFacturas() {
    this.limpiarMEssages();
    this.isLoading = true;

    this.http.get<any>(`http://localhost:8080/scap/api/fac/contabilizacion/search?ent=${this.entcod}&eje=${this.eje}&cgecod=${this.centroGestor}`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.page = 0;
        this.facturas = res;
        this.updatePagination();
      },
      error: (err) => {
        this.isLoading = false;
        this.filterFacturaMessage = err.error.error ?? err.error;
      }
    })
  }
  
  sortField: 'facnum' | 'tercod' | 'ternom' | 'ternif' | 'facfre' | 'facimp' | 'facdto' | 'facdoc' | 'facann' | 'facfac' | 'facdat' | 'factxt' | 'facado' |'FFACFCO' | null = null;
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  private defaultProveedores: any[] = [];
  toggleSort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
    this.page = 0;
    this.updatePagination();
  }

  private applySort(): void {
    if (!this.sortColumn) return;
    this.facturas.sort((a, b) => {
      let aValue: any;
      let bValue: any;
      aValue = a[this.sortColumn];
      bValue = b[this.sortColumn];

      const aNum = Number(aValue);
      const bNum = Number(bValue);
      if (!isNaN(aNum) && !isNaN(bNum)) {
        return this.sortDirection === 'asc' ? aNum - bNum : bNum - aNum;
      }

      aValue = (aValue ?? '').toString().toUpperCase();
      bValue = (bValue ?? '').toString().toUpperCase();
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  private updatePagination(): void {
    const total = this.totalPages;
    if (total === 0) {
      this.page = 0;
      return;
    }
    if (this.page >= total) {
      this.page = total - 1;
    }
  }

  get paginatedFacturas(): any[] { if (!this.facturas || this.facturas.length === 0) return [];
    const start = this.page * this.pageSize; return this.facturas.slice(start, start + this.pageSize);
  }
  get totalPages(): number { return Math.max(1, Math.ceil((this.facturas?.length ?? 0) / this.pageSize)); }
  prevPage(): void { if (this.page > 0) this.page--; }
  nextPage(): void { if (this.page < this.totalPages - 1) this.page++; }
  goToPage(event: any): void {const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) { this.page = inputPage - 1; }
  }

  private startX: number = 0;
  private startWidth: number = 0;
  private resizingColIndex: number | null = null;
  startResize(event: MouseEvent, colIndex: number) {
    this.resizingColIndex = colIndex;
    this.startX = event.pageX;
    const th = (event.target as HTMLElement).parentElement as HTMLElement;
    this.startWidth = th.offsetWidth;

    document.addEventListener('mousemove', this.onResizeMove);
    document.addEventListener('mouseup', this.stopResize);
  }

  onResizeMove = (event: MouseEvent) => {
    if (this.resizingColIndex === null) return;
    const table = document.querySelector('.facturas-table') as HTMLTableElement;
    if (!table) return;
    const th = table.querySelectorAll('th')[this.resizingColIndex] as HTMLElement;
    if (!th) return;
    const diff = event.pageX - this.startX;
    th.style.width = (this.startWidth + diff) + 'px';
  };

  stopResize = () => {
    document.removeEventListener('mousemove', this.onResizeMove);
    document.removeEventListener('mouseup', this.stopResize);
    this.resizingColIndex = null;
  };

  formatDate = (v: any) => {
    if (!v && v !== 0) return '';
    const s = String(v);
    const d = new Date(s);
    return isNaN(d.getTime()) ? s : d.toLocaleDateString('es-ES');
  };

  formatCurrency = (value: any) => {
    if (value === null || value === undefined || value === '') return '';
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'EUR',
      minimumFractionDigits: 2
    }).format(Number(value));
  };

  DownloadPDF() {
    this.limpiarMEssages();

    const source = this.facturas;
    if (!source?.length) {
      this.filterFacturaMessage = 'No hay datos para exportar.';
      return;
    }

    const rows = source.map((row: any, index: number) => ({
      index: index + 1,
      facnum: row.facnum ?? '',
      tercod: row.tercod ?? '',
      ternom: row.ter.ternom ?? '',
      ternif: row.ter.ternif ?? '',
      facfre: this.formatDate(row.facfre),
      facimp: this.formatCurrency(row.facimp),
      facdto: row.facdto ?? '',
      facdoc: row.facdoc ?? '',
      facann: row.facann ?? '',
      facfac: row.facfac ?? '',
      facdat: this.formatDate(row.facdat),
      fctxt: row.factxt ?? '',
      facado: row.facado ?? '',
      facfco: this.formatDate(row.facfco)
    }));

    const columns = [
      { header: '#', dataKey: 'index' },
      { header: 'Número Registro', dataKey: 'facnum' },
      { header: 'Código Prov', dataKey: 'tercod' },
      { header: 'Nombre Proveedor', dataKey: 'ternom' },
      { header: 'NIF Prov', dataKey: 'ternif' },
      { header: 'F.Registro', dataKey: 'facfre' },
      { header: 'Importe', dataKey: 'facimp' },
      { header: 'Descuentos', dataKey: 'facdto'},
      { header: 'Núm. Factura', dataKey: 'facdoc' },
      { header: 'Año', dataKey: 'facann' },
      { header: 'R.C.F', dataKey: 'facfac' },
      { header: 'F.Factura', dataKey: 'facdat' },
      { header: 'Descripción', dataKey: 'factxt'},
      { header: 'OP.Contable', dataKey: 'facado' },
      { header: 'F.Contable', dataKey: 'facfco' }
    ];

    const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: 'a4' });
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(14);
    doc.text('Listado de Monitor de contabilización', 10, 20);

    autoTable(doc, {
      startY: 30,
      theme: 'plain',
      head: [columns.map(c => c.header)],
      body: rows.map(row => columns.map(c => row[c.dataKey as keyof typeof row] ?? '')),
      styles: { font: 'helvetica', fontSize: 8, cellPadding: 4 },
      headStyles: {
        fillColor: [240, 240, 240],
        textColor: [33, 53, 71],
        fontStyle: 'bold',
        halign: 'left'
      },
      tableLineColor: [200, 200, 200],
      tableLineWidth: 0.5,
      columnStyles: {
        index: { cellWidth: 8 },
        facnum: { cellWidth: 18 },
        tercod: { cellWidth: 18 },
        ternom: { cellWidth: 36 },
        ternif: { cellWidth: 20 },
        facfre: { cellWidth: 20 },
        facimp: { cellWidth: 22 },
        facdto: { cellWidth: 10},
        facdoc: { cellWidth: 20 },
        facann: { cellWidth: 15 },
        facfac: { cellWidth: 18 },
        facdat: { cellWidth: 20 },
        factxt: { cellWidth: 36},
        facado: { cellWidth: 15 },
        facfco: { cellWidth: 20 }
      }
    });

    doc.save('Monitor_contabilización.pdf');
  }

  DownloadCSV() {
    this.limpiarMEssages();
    if(this.facturas.length === 0) {
      this.filterFacturaMessage = 'He has no data to export.';
      return
    }
    
    interface Column { header: string; dataKey: string; }

    const columns: Column[] = [
      { header: 'Número Registro', dataKey: 'facnum' },
      { header: 'Código Prov', dataKey: 'tercod' },
      { header: 'Nombre Proveedor', dataKey: 'ternom' },
      { header: 'NIF Prov', dataKey: 'ternif' },
      { header: 'F.Registro', dataKey: 'facfre' },
      { header: 'Importe', dataKey: 'facimp' },
      { header: 'Descuentos', dataKey: 'facdto'},
      { header: 'Núm. Factura', dataKey: 'facdoc' },
      { header: 'Año', dataKey: 'facann' },
      { header: 'R.C.F', dataKey: 'facfac' },
      { header: 'F.Factura', dataKey: 'facdat' },
      { header: 'Descripción', dataKey: 'factxt'},
      { header: 'OP.Contable', dataKey: 'facado' },
      { header: 'F.Contable', dataKey: 'facfco' }
    ];

    const rows = (this.facturas || []).map((p: any) => ({
      facnum: p.facnum,
      tercod: p.tercod,
      ternom: p.ternom,
      ternif: p.ternif,
      facfre: this.formatDate(p.facfre),
      facimp: this.formatCurrency(p.facimp),
      facdto: p.facdto,
      facdoc: p.facdoc,
      facann: p.facann,
      facfac: p.facfac,
      facdat: this.formatDate(p.facdat),
      factxt: p.factxt,
      facado: p.facado,
      facfco: this.formatDate(p.facfco)
    }));

    const escapeCsv = (val: any) => {
      if (val === null || val === undefined) return '';
      let s = String(val);
      s = s.replace(/"/g, '""');
      if (/[,"\r\n]/.test(s)) s = `"${s}"`;
      return s;
    };

    const header = columns.map(c => escapeCsv(c.header)).join(';');
    const bodyLines = rows.map(r => columns.map(c => escapeCsv((r as any)[c.dataKey])).join(';'));

    const csvContent = '\uFEFF' + [header, ...bodyLines].join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'Monitor_contabilización.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  facturaSearch: string = '';
  ejerSearch: string = '';
  fechasType: 'registro' | 'factura' = 'factura';
  desde: string = '';
  hasta: string = '';
  search() {
    this.limpiarMEssages();
    this.isLoading = true;

    let params = `ent=${this.entcod}&eje=${this.eje}&cgecod=${this.centroGestor}`;

    // fechaType
    params += `&fechaType=${this.fechasType}`;

    // desde
    if (this.desde && this.desde.trim() !== '') {
      params += `&desde=${this.desde}T00:00:00`;
    }

    // hasta
    if (this.hasta && this.hasta.trim() !== '') {
      params += `&hasta=${this.hasta}T23:59:59`;
    }

    // facann (ejerSearch)
    if (this.ejerSearch && this.ejerSearch.trim() !== '') {
      params += `&facann=${this.ejerSearch}`;
    }

    // search (facturaSearch)
    if (this.facturaSearch && this.facturaSearch.trim() !== '') {
      params += `&search=${encodeURIComponent(this.facturaSearch)}`;
    }

    this.http.get<any>(`${environment.backendUrl}/api/fac/contabilizacion/search?${params}`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.page = 0;
        this.facturas = res;
        this.updatePagination();
      },
      error: (err) => {
        this.isLoading = false;
        this.filterFacturaMessage = err.error.error ?? err.error;
      }
    });
  }

  limpiarSearch() {
    this.facturaSearch = '';
    this.ejerSearch = '';
    this.fechasType = 'factura';
    this.desde = '';
    this.hasta = '';
    this.fetchFacturas();
  }

  //misc 
  limpiarMEssages() {
    this.filterFacturaMessage = '';
  }
}
