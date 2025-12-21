import { Component, HostListener} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-proveedorees',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './facturas.component.html',
  styleUrls: ['./facturas.component.css']
})
export class FacturasComponent {
  showMenu = false;
  toggleMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.showMenu = false;
  }

  private entcod: number | null = null;
  private eje: number | null = null;
  public centroGestor: string = '';
  public estadogc: number | null = null;
  public facturaSearchTouched: boolean = false;
  public searchQueryTouched: boolean = false;
  private initialCentroGestor: string = '';
  facturas: any[] = [];
  private backupFacturas: any[] = [];
  page = 0;
  pageSize = 20;
  facturaMessageIsSuccess: boolean = false;
  estadoMessage: string = '';
  isEstadoMessage: boolean = false;
  public Math = Math;

  constructor(private http: HttpClient, private router: Router) {}

  private defaultFacturas: any[] = [];
  isLoading: boolean = false;
  ngOnInit(): void{
    this.estadoMessage = '';
    this.isLoading = true;
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');
    const estadoCentroGestor = sessionStorage.getItem('ESTADOGC');

    if(estadoCentroGestor){
      const parsed = JSON.parse(estadoCentroGestor);
      this.estadogc = parsed.value;

      if (this.estadogc === 1) {
        this.isEstadoMessage = true;
        this.estadoMessage = 'Centro Gestor CERRADO';
      }
      if ( this.estadogc === 2) {
        this.isEstadoMessage = true;
        this.estadoMessage = 'Centro Gestor CERRADO para CONTABILIZAR';
      }
    }

    if (cge){
      const parsed = JSON.parse(cge);
      this.centroGestor = parsed.value
      this.initialCentroGestor = this.centroGestor;
    }
    
    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }
    if (eje) {
      const parsed = JSON.parse(eje);
      this.eje = parsed.eje;
    }

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/fac/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
      next: (response) => {
        if (!Array.isArray(response) || response.length === 0) {
          this.facturaMessageIsSuccess = true;
          this.filterFacturaMessage = 'No hay facturas por las medidas de búsqueda';
          this.facturas = [];
          this.defaultFacturas = [];
          this.updatePagination();
          this.isLoading = false;
        } else {
          this.facturas = response;
          this.backupFacturas = Array.isArray(response) ? [...response] : [];
          this.defaultFacturas = [...this.backupFacturas];
          this.page = 0;
          this.updatePagination();
          this.isLoading = false;
        }
      }, error: (err) => {
        this.filterFacturaMessage= 'Server error: ' + (err?.message || err?.statusText || err);
        this.isLoading = false;
      }
    });
  }

  sortField: 'facnum' | 'tercod' | 'ternom' | 'ternif' | 'facfre' | 'facimp' | 'facdoc' | 'facann' | 'facfac' | 'facdat' | 'facado' | 'facfco' |'getPendingApply(p)' | 'cgecod' | 'getStaus(p.facado, p.facimp, p.faciec, p.facidi)' | null = null;
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

      if (this.sortColumn === 'getPendingApply(p)') {
        aValue = this.getPendingApply(a);
        bValue = this.getPendingApply(b);
      } else if (this.sortColumn === 'getStaus(p.facado, p.facimp, p.faciec, p.facidi)') {
        aValue = this.getStaus(a.facado, a.facimp, a.faciec, a.facidi);
        bValue = this.getStaus(b.facado, b.facimp, b.faciec, b.facidi);
      } else {
        aValue = a[this.sortColumn];
        bValue = b[this.sortColumn];
      }

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


  get paginatedFacturas(): any[] {
    if (!this.facturas || this.facturas.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.facturas.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.facturas?.length ?? 0) / this.pageSize));
  }
  prevPage(): void {
    if (this.page > 0) this.page--;
  }
  nextPage(): void {
    if (this.page < this.totalPages - 1) this.page++;
  }
  goToPage(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.page = inputPage - 1;
    }
  }

  DownloadPDF() {
    const doc = new jsPDF({orientation: 'landscape', unit: 'mm', format: 'a4'});
    const columns = [
      { header: 'Número Registro', dataKey: 'facnum'},
      { header: 'Fecha contable', dataKey: 'facfco'},
      { header: 'ADO', dataKey: 'facado'},
      { header: 'R.C.F', dataKey: 'factdc'},
      { header: 'Núm. Factura', dataKey: 'facdoc'},
      { header: 'Proveedor', dataKey: 'tercod'},
      { header: 'Fecha Factura', dataKey: 'facdat'},
      { header: 'C.gestor', dataKey: 'cgecod'},
      { header: 'F.Registro', dataKey: 'facfre'},
      { header: 'Total Factura', dataKey: 'facimp'}
    ];

    const formatDate = (v: any) => {
      if (!v && v !== 0) return '';
      const s = String(v);
      const d = new Date(s);
      if (!isNaN(d.getTime())) return d.toLocaleDateString();
      return s;
    };

    const rows = (this.facturas || []).map((f: any) => ({
      facnum: f.facnum,
      facfco: formatDate(f.facfco),
      facado: f.facado,
      factdc: f.factdc,
      facdoc: f.facdoc,
      tercod: f.tercod,
      facdat: formatDate(f.facdat),
      cgecod: f.cgecod,
      facfre: formatDate(f.facfre),
      facimp: f.facimp
    }));

    autoTable(doc, {
      columns,
      body: rows,
      startY: 16,
      styles: { fontSize: 7 },
      headStyles: { fillColor: [15, 76, 117] },
      margin: { left: 8, right: 8 },
      columnStyles: {
        facnum: { cellWidth: 15 },
        facfco: { cellWidth: 35 },
        facado: { cellWidth: 20 },
        factdc: { cellWidth: 15 },
        facdoc: { cellWidth: 45 },
        tercod: { cellWidth: 35 },
        facdat: { cellWidth: 15 },
        cgecod: { cellWidth: 35 },
        facfre: { cellWidth: 35 },
        facimp: { cellWidth: 15 }
      },
      didDrawPage: (dataArg) => {
        doc.setFontSize(11);
        doc.text('Lista de Facturas', 12, 10);
        const pageCount = doc.getNumberOfPages();
        doc.setFontSize(8);
        const pageStr = `Página ${pageCount}`;
        doc.text(pageStr, doc.internal.pageSize.getWidth() - 20, 10);
      }
    });

    doc.save('Facturas.pdf');
  }

  DownloadCSV() {
    interface Column { header: string; dataKey: string; }

    const columns: Column[] = [
      { header: 'Número Registro', dataKey: 'facnum' },
      { header: 'Fecha contable', dataKey: 'facfco' },
      { header: 'ADO', dataKey: 'facado' },
      { header: 'R.C.F', dataKey: 'factdc' },
      { header: 'Núm. Factura', dataKey: 'facdoc' },
      { header: 'Proveedor', dataKey: 'tercod' },
      { header: 'Fecha Factura', dataKey: 'facdat' },
      { header: 'C.gestor', dataKey: 'cgecod' },
      { header: 'F.Registro', dataKey: 'facfre' },
      { header: 'Total Factura', dataKey: 'facimp' }
    ];

    const formatDate = (v: any) => {
      if (v === null || v === undefined || v === '') return '';
      const s = String(v);
      const d = new Date(s);
      if (!isNaN(d.getTime())) return d.toLocaleDateString();
      return s;
    };

    const rows = (this.facturas || []).map((f: any) => ({
      facnum: f.facnum ?? f.FACNUM ?? f.facfac ?? f.FACFAC ?? '',
      facfco: formatDate(f.facfco ?? f.FACFCO ?? f.FACFCO),
      facado: f.facado ?? f.FACADO ?? '',
      factdc: f.factdc ?? f.FACTDC ?? '',
      facdoc: f.facdoc ?? f.FACDOC ?? '',
      tercod: f.tercod ?? f.TERCOD ?? '',
      facdat: formatDate(f.facdat ?? f.FACDAT ?? f.FACDAT),
      cgecod: f.cgecod ?? f.CGECOD ?? '',
      facfre: formatDate(f.facfre ?? f.FACFRE ?? f.facfre),
      facimp: (f.facimp ?? f.FACIMP ?? f.facimp ?? '').toString()
    }));

    const escapeCsv = (val: any) => {
      if (val === null || val === undefined) return '';
      let s = String(val);
      s = s.replace(/"/g, '""');
      if (/[,"\r\n]/.test(s)) s = `"${s}"`;
      return s;
    };

    const header = columns.map(c => escapeCsv(c.header)).join(',');
    const bodyLines = rows.map(r => columns.map(c => escapeCsv((r as any)[c.dataKey])).join(','));

    const csvContent = '\uFEFF' + [header, ...bodyLines].join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'Facturas.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  selectedFacturas: any = null;
  detallesMessage: String = '';
  fettalesIsError: boolean = false;

  showDetails(factura: any) {
    this.selectedFacturas = factura;
    this.detailView = 'Albaranes';
    this.setAlbaranesOptio('albaranes', factura?.facnum);
  }

  closeDetails() {
    this.selectedFacturas = null;
    this.detallesMessage = '';
  }

  public getPendingApply(f: any): number {
    if (!f) return 0;
    const toNum = (v: any) => (v === null || v === undefined || v === '' ? 0 : Number(v) || 0);
    let pending = toNum(f.facimp) - (toNum(f.faciec) + toNum(f.facidi));
    return Math.round(pending * 100) / 100;
  }

  public getStaus(facado: any, facimp: any, faciec: any, facidi:any ){
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };

    const FACADO = toNum(facado);
    const FACIEC = toNum(faciec);
    const FACIMP = toNum(facimp);
    const FACIDI = toNum(facidi);

    if (FACADO != 0){
      return 'contbilizadas';
    }
    if (facado === '' && (this.Math.round((FACIMP * 100) / 100) === this.Math.round((FACIEC + FACIDI) * 100) / 100)) {
      return 'Pte. Aplicada';
    }
    if (facado === '' && (this.Math.round((FACIMP * 100) / 100) != this.Math.round((FACIEC + FACIDI) * 100) / 100)) {
      return 'Pte. Sin aplicar';
    }
    return '';
  }

  fechaTipo: 'registro' | 'factura' | 'contable' | '' = '';
  estadoTipo: 'contabilizadas' | 'no-contabilizadas' | 'aplicadas' | 'sin-aplicadas' | '' = 'no-contabilizadas';
  fromDate: string = '';
  toDate: string = '';
  filterFacturaMessage: string = '';
  facturaSearch: string = '';
  public searchQuery: string = '';

  onEjeInput(event: Event) {
    const input = event.target as HTMLInputElement;
    const sanitized = input.value.replace(/\D+/g, '').slice(0, 4);
    if (sanitized !== this.facturaSearch) {
      this.facturaSearch = sanitized;
    }
    this.facturaSearchTouched = true;
  }
  
  filterFacturas(): void {
    this.isLoading = true;
    if (this.entcod == null || this.eje == null || !this.centroGestor) {
      this.filterFacturaMessage= 'Faltan datos de sesión.';
      return;
    }

    this.filterFacturaMessage= '';
    this.filterFacturaMessage = '';

    const estadoMap: Record<string, string> = {
      'contabilizadas': 'CONT',
      'no-contabilizadas': 'NO_CONT',
      'aplicadas': 'PTE_APL',
      'sin-aplicadas': 'PTE_SIN',
      'todas': 'TODAS',
      '': 'CONT'
    };
    const fechaMap: Record<string, string> = {
      'registro': 'REGISTRO',
      'factura': 'FACTURA',
      'contable': 'CONTABLE',
      '': 'REGISTRO'
    };

    const estado = estadoMap[this.estadoTipo ?? ''] ?? 'CONT';
    const dateType = fechaMap[this.fechaTipo ?? ''] ?? 'REGISTRO';

    const desde = this.fromDate?.trim() || '';
    const hasta = this.toDate?.trim() || '';
    if ((desde || hasta) && !this.fechaTipo || this.fechaTipo && !(desde || hasta)) {
      this.filterFacturaMessage = 'Seleccione un tipo de fecha y una cita antes de buscar.';
      return;
    }

    const facann = this.facturaSearch?.trim() || '';
    let facannMode = 'ANY';
    if (facann) {
      facannMode = 'VALUE';
    } else if (estado === 'CONT') {
      facannMode = 'NOT_NULL';
    } else if (estado === 'NO_CONT' || estado === 'PTE_APL' || estado === 'PTE_SIN') {
      facannMode = 'NULL';
    }

    const searchRaw = this.searchQuery?.trim() || '';
    const digits = searchRaw.replace(/\D/g, '');
    const hasLetters = /[A-Za-z]/.test(searchRaw);
    const hasDigits = /\d/.test(searchRaw);
    const NIF_MIN_DIGITS = 5;
    let searchType = 'OTROS';
    let search = '';

    if (searchRaw) {
      if (!hasLetters && hasDigits && digits.length > 0 && digits.length <= NIF_MIN_DIGITS) {
        searchType = 'TERCOD';
        search = digits;
      } else if (!hasLetters && hasDigits && digits.length > NIF_MIN_DIGITS) {
        searchType = 'NIF';
        search = digits;
      } else if (hasLetters && hasDigits && searchRaw.length > NIF_MIN_DIGITS) {
        searchType = 'NIF_LETTERS';
        search = searchRaw;
      } else {
        searchType = 'OTROS';
        search = searchRaw;
      }
    }

    const params: Record<string, string> = {
      ent: String(this.entcod),
      eje: String(this.eje),
      cgecod: this.centroGestor,
      estado,
      dateType,
      facannMode
    };
    if (desde) params['desde'] = desde;
    if (hasta) params['hasta'] = hasta;
    if (facann && facannMode === 'VALUE') params['facann'] = facann;
    if (search) {
      params['search'] = search;
      params['searchType'] = searchType;
    }

    this.http.get<any[]>(`${environment.backendUrl}/api/fac/search`, { observe: 'response', params })
      .subscribe({
        next: (res) => {
          if (res.status === 204 || !res.body || res.body.length === 0) {
            this.facturaMessageIsSuccess = true;
            this.filterFacturaMessage= 'No hay facturas para los filtros seleccionados.';
            this.facturas = [];
            this.facturas = [];
            this.defaultFacturas = [];
            this.sortField = null;
            this.sortDirection = 'asc';
            this.updatePagination();
            this.isLoading = false;
            return;
          }
          const body = res.body ?? [];
          this.facturas = body;
          this.defaultFacturas = [...body];
          if (this.sortField) {
            this.applySort();
          } else {
            this.page = 0;
            this.updatePagination();
          }
          this.isLoading = false;
        },
        error: (err) => {
          this.filterFacturaMessage= typeof err.error === 'string'
            ? err.error
            : 'Error al buscar facturas.';
          this.isLoading = false;
        }
      });
  }

  forgetAll(): void{
    this.facturaSearch = '';
    this.searchQuery = '';
    this.centroGestor = this.initialCentroGestor || '';
    this.facturaSearchTouched = false;
    this.searchQueryTouched = false;
    this.fechaTipo = '';
    this.estadoTipo = 'no-contabilizadas';
    this.fromDate = '';
    this.toDate = '';
    this.filterFacturaMessage = '';
    this.page = 0;
    this.sortField = null;
    this.sortDirection = 'asc';

    if (this.backupFacturas.length) {
      this.facturas = [...this.backupFacturas];
      this.defaultFacturas = [...this.backupFacturas];
      this.updatePagination();
    } else {
      this.facturas = [];
      this.defaultFacturas = [];
      this.updatePagination();
    }
  }

  detailView: 'Albaranes' | 'Contabilización' = 'Albaranes';
  albaranesOptio: 'albaranes' | 'aplicaciones' | 'descuentos' = 'albaranes';
  albaranes: any[] = [];
  backipAlbaranes: any[] = [];
  apalicaciones: any[] = [];
  backupAplicaciones: any[] = [];
  descuentos: any[] = [];
  backupDescuentos: any[] = [];
  moreInfoMessageSuccess: string = '';
  moreInfoMessageIsSuccess: boolean = false;
  moreInfoMessageError: string = '';
  moreInfoMessageIsError: boolean = false;

  setAlbaranesOptio(option: 'albaranes' | 'aplicaciones' | 'descuentos', facnum: number): void {
    this.albaranesOptio = option;
    this.moreInfoMessageSuccess = '';
    this.moreInfoMessageError = '';

    if ( option === 'albaranes') {
      this.http.get<any>(`${environment.backendUrl}/api/alb/${this.entcod}/${this.eje}/${facnum}`).subscribe({
        next: (response) => {
          if (!Array.isArray(response) || response.length === 0) {
            this.moreInfoMessageIsSuccess = true;
            this.moreInfoMessageSuccess = 'No hay albaranes por las medidas de búsqueda';
            this.albaranes = []
          } else {
            this.albaranes = response;
            console.log(this.albaranes);
            this.backipAlbaranes = Array.isArray(response) ? [...response] : [];
          }
        }, error: (err) => {
          this.moreInfoMessageIsError = true;
          this.moreInfoMessageError = err?.error ?? 'Error desconocido';
        }
      });
    }

    if ( option === 'aplicaciones') {
      this.http.get<any>(`${environment.backendUrl}/api/fde/${this.entcod}/${this.eje}/${facnum}`).subscribe({
        next: (response) => {
          if (!Array.isArray(response) || response.length === 0) {
            this.moreInfoMessageIsSuccess = true;
            this.moreInfoMessageSuccess = 'No hay aplicaciones por las medidas de búsqueda';
            this.apalicaciones = []
          } else {
            this.apalicaciones = response;
            console.log(this.apalicaciones);
            this.backupAplicaciones = Array.isArray(response) ? [...response] : [];
          }
        }, error: (err) => {
          this.moreInfoMessageIsError = true;
          this.moreInfoMessageError = err?.error ?? 'Error desconocido';
        }
      });
    }

    if ( option === 'descuentos') {
      this.http.get<any>(`${environment.backendUrl}/api/fdt/${this.entcod}/${this.eje}/${facnum}`).subscribe({
        next: (response) => {
          if (!Array.isArray(response) || response.length === 0) {
            this.moreInfoMessageIsSuccess = true;
            this.moreInfoMessageSuccess = 'No hay descuentos por las medidas de búsqueda';
            this.descuentos = []
          } else {
            this.descuentos = response;
            console.log(this.descuentos);
            this.backupDescuentos = Array.isArray(response) ? [...response] : [];
          }
        }, error: (err) => {
          this.moreInfoMessageIsError = true;
          this.moreInfoMessageError = err?.error ?? 'Error desconocido';
        }
      });
    }
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
}
