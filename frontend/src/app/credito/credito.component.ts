import { Component, HostListener} from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';
@Component({
  selector: 'app-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, CurrencyPipe],
  templateUrl: './credito.component.html',
  styleUrls: ['./credito.component.css'],
  providers: [CurrencyPipe]
})
export class CreditoComponent {
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
  private initialCentroGestor: string = '';
  creditos: any[] = [];
  wsData: any[] = [];
  private backupCreditos: any[] = [];
  public Math = Math;
  page = 0;
  pageSize = 20;
  tableMessage: string = '';
  tableIsError: boolean = false;
  guardarMesage: string = '';
  guardarisError: boolean = false;
  guardarMesageSuccess: string = '';
  guardarisSuccess : boolean = false;

  constructor(private http: HttpClient, private router: Router, private currency: CurrencyPipe) {}

  ngOnInit(): void {
    this.tableIsError = false;
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');

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

    this.http.get<any>(`${environment.backendUrl}/api/gbs/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.creditos = Array.isArray(response) ? [...response] : [];
          this.backupCreditos = [...this.creditos];
          this.creditos.forEach((item, idx) => {
            const org = item?.gbsorg ?? '';
            const fun = item?.gbsfun ?? '';
            const eco = item?.gbseco ?? '';
            this.http
              .get<any>(`${environment.backendUrl}/api/sical/partidas?clorg=${org}&clfun=${fun}&cleco=${eco}`)
              .subscribe({
                next: (partidas) => {
                  const partidasArr = Array.isArray(partidas) ? partidas : [];
                  this.creditos[idx].partidas = partidasArr;
                  const des = partidasArr[0]?.desc ?? '';
                  this.creditos[idx].partidaDesc = des;
                },
                error: () => {
                  this.creditos[idx].partidas = [];
                },
              });
              this.http.get<any>(`${environment.backendUrl}/api/sical/operaciones?clorg=${org}&clfun=${fun}&cleco=${eco}`)
              .subscribe({
                next: (operaciones) => {
                  const operacionesArr = Array.isArray(operaciones) ? operaciones : [];
                  this.creditos[idx].operaciones = operacionesArr;
                  const firstLinea = operacionesArr[0]?.lineaList?.[0] ?? {};
                  const saldo = this.creditos[idx].saldo = firstLinea?.saldo ?? 0;
                  const limporte = this.creditos[idx].limporte = firstLinea?.limporte ?? 0;
                },
                error: () => {
                  this.creditos[idx].operaciones = [];
                },
              });
          });
          this.page = 0;
        }
      }, error: (err) => {
        this.tableIsError = true;
        this.tableMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        alert(this.tableMessage);
      }
    });
  }

  DownloadPDF() {
    const doc = new jsPDF({orientation: 'landscape', unit: 'mm', format: 'a4'});
    const columns = [
      { header: 'Aplicación', dataKey: 'aplicacion'},
      { header: 'Desc. Aplicación', dataKey: 'desc'},
      { header: 'Operación contable', dataKey: 'gbsope'},
      { header: 'Ref. contable', dataKey: 'gbsref'},
      { header: 'Imp. Operación', dataKey: 'limporte'},
      { header: 'Saldo Operación', dataKey: 'saldo'},
      { header: 'Pte. Contabilizar SCAP', dataKey: 'getkAcPeCo'},
      { header: 'Disponible', dataKey: 'getkdispon'}
    ];

    const rows = (this.creditos || []).map((f: any) => ({
      aplicacion: `${f.gbsorg ?? ''} - ${f.gbsfun ?? ''} - ${f.gbseco ?? ''}`,
      desc: f.partidas[0]?.desc,
      gbsope: f.gbsope,
      gbsref: f.gbsref,
      limporte: f.limporte,
      saldo: f.saldo,
      getkAcPeCo: this.getkAcPeCo(f.gbsiut, f.gbsict),
      getkdispon: this.getkdispon(f.saldo, this.getkAcPeCo(f.gbsiut, f.gbsict)),
    }));

    autoTable(doc, {
      columns,
      body: rows,
      startY: 16,
      styles: { fontSize: 7 },
      headStyles: { fillColor: [15, 76, 117] },
      margin: { left: 8, right: 8 },
      columnStyles: {
        aplicacion: { cellWidth: 20 },
        desc: { cellWidth: 35 },
        gbsope: { cellWidth: 35 },
        gbsref: { cellWidth: 35 },
        limporte: { cellWidth: 20 },
        saldo: { cellWidth: 20 },
        getkAcPeCo: { cellWidth: 20 },
        getkdispon: { cellWidth: 30 }
      },
      didDrawPage: (dataArg) => {
        doc.setFontSize(11);
        doc.text('Lista de Bolsas', 12, 10);
        const pageCount = doc.getNumberOfPages();
        doc.setFontSize(8);
        const pageStr = `Página ${pageCount}`;
        doc.text(pageStr, doc.internal.pageSize.getWidth() - 20, 10);
      }
    });
    doc.save('Bolsas.pdf');
  }

  DownloadCSV() {
    interface Column { header: string; dataKey: string; }
    const columns: Column[] = [
      { header: 'Aplicación', dataKey: 'aplicacion'},
      { header: 'Desc. Aplicación', dataKey: 'desc'},
      { header: 'Operación contable', dataKey: 'gbsope'},
      { header: 'Ref. contable', dataKey: 'gbsref'},
      { header: 'Imp. Operación', dataKey: 'limporte'},
      { header: 'Saldo Operación', dataKey: 'saldo'},
      { header: 'Pte. Contabilizar SCAP', dataKey: 'getkAcPeCo'},
      { header: 'Disponible', dataKey: 'getkdispon'}
    ];

    const rows = (this.creditos || []).map((f: any) => ({
      aplicacion: `${f.gbsorg ?? ''} - ${f.gbsfun ?? ''} - ${f.gbseco ?? ''}`,
      desc: f.partidas[0]?.desc,
      gbsope: f.gbsope,
      gbsref: f.gbsref,
      limporte: f.limporte,
      saldo: f.saldo,
      getkAcPeCo: this.getkAcPeCo(f.gbsiut, f.gbsict),
      getkdispon: this.getkdispon(f.saldo, this.getkAcPeCo(f.gbsiut, f.gbsict)),
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
    a.download = 'Bolsas.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  public getkAcPeCo(gbsiut: any, gbsict: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(gbsiut);
    const b = toNum(gbsict);
    return (a - b).toFixed();
  }

  get paginatedFacturas(): any[] {
    if (!this.creditos || this.creditos.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.creditos.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.creditos?.length ?? 0) / this.pageSize));
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

  selectedBolsas: any = null;
  showDetails(factura: any) {
    this.guardarisError = false;
    this.guardarMesage = '';
    this.guardarisSuccess = false;
    this.guardarMesageSuccess = ''
    this.selectedBolsas = factura;
    const org = factura?.gbsorg ?? '';
    const fun = factura?.gbsfun ?? '';
    const eco = factura?.gbseco ?? '';
    this.bolsaCombo = `${org} - ${fun} - ${eco}`;
  }

  public bolsaCombo: string = '';
  saveDetails() {
    if (!this.selectedBolsas) return;
    const parts = this.bolsaCombo.split(' - ');
    this.selectedBolsas.gbsorg = parts[0] ?? '';
    this.selectedBolsas.gbsfun = parts[1] ?? '';
    this.selectedBolsas.gbseco = parts[2] ?? '';
  } 

  closeDetails() {
    this.selectedBolsas = null;
  }

  public getkCGECIC(cgecic: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(cgecic);
    if (a === 0) return 'No';
    if ( a === 1) return 'Si';
    if ( a === 2) return 'Cierre para contabilizar';
    return '';
  }

  public getKBoldis(gbsimp: any, gbsibg: any, gbsius: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(gbsimp)
    const b = toNum(gbsibg);
    const c = toNum(gbsius);
    return (a + b -c).toFixed();
  }

  public getkdispon(saldo: any, getkAcPeCo: any): string {
    const toNum = (v:any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };

    const a = toNum(saldo);
    const b = toNum(getkAcPeCo);
    return (a - b).toFixed();
  }

  formatGbsimp(event: Event): void {
    const input = event.target as HTMLInputElement;
    const normalized = input.value.replace(/\./g, '').replace(',', '.');
    const amount = Number(normalized);
    if (!isNaN(amount)) {
      this.selectedBolsas.gbsimp = amount;
      input.value =
        this.currency.transform(amount, 'EUR', 'symbol', '1.2-2', 'es-ES') ?? '';
    } else {
      input.value = '';
    }
  }

  guardarDetelles(gbsimp: any, getKBoldis:any, gbsref: any) {
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');

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

    console.log(this.entcod);
    console.log(this.eje);
    console.log(this.initialCentroGestor);
    console.log(gbsref);

    const toNum = (v:any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };

    let currentdate = new Date();
    const a = toNum (gbsimp);
    const b = toNum(getKBoldis);

    if ( gbsimp > b) {
      this.guardarisError = true;
      this.guardarMesage = 'HA SOBREPASADO EL DISPONIBLE DE LA REFERENCIA';
      return;
    }

    const payload = {
      gbsimp: a,
      gbsius: 0,
      gbseco: 0,
      gbsfop: currentdate.getFullYear() + "-" + (currentdate.getMonth() + 1) + "-" + currentdate.getDate()
    };
    console.log("payload here: ", payload);

    this.http.patch<void>(`${environment.backendUrl}/api/gbs/${this.entcod}/${this.eje}/${this.initialCentroGestor}/${gbsref}`, payload)
      .subscribe({
        next: () => {
          this.guardarisSuccess = true;
          this.guardarMesageSuccess = 'Bolsa actualizada correctamente';
        },
        error: (err) => {
          this.guardarisError = true;
          this.guardarMesage = err?.ex ?? 'Error al actualizar';
        }
      });
  }
}
