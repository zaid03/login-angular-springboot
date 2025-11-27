import { Component} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-proveedorees',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './facturas.component.html',
  styleUrls: ['./facturas.component.css']
})
export class FacturasComponent {
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
  facturaMessage: String = '';
  facturaIsError: boolean = false;
  facturaMessageSuccess: String = '';
  facturaMessageIsSuccess: boolean = false;
  estadoMessage: string = '';
  isEstadoMessage: boolean = false;
  public Math = Math;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void{
    this.estadoMessage = '';
    this.facturaIsError = false;
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

    this.http.get<any>(`http://localhost:8080/api/fac/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
      next: (response) => {
        if (!Array.isArray(response) || response.length === 0) {
          this.facturaMessageIsSuccess = true;
          this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
          this.facturas = []
        } else {
          this.facturas = response;
          this.backupFacturas = Array.isArray(response) ? [...response] : [];
          this.page = 0;
        }
      }, error: (err) => {
        this.facturaIsError = true;
        this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
      }
    });
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
  
  filterFacturas(){
    this.facturaMessage = '';
    this.filterFacturaMessage = '';
    const tipo = this.fechaTipo;
    const estado = this.estadoTipo;
    const desde = this.fromDate && this.fromDate.trim() ? this.fromDate : '';
    const hasta = this.toDate && this.toDate.trim() ? this.toDate : '';
    const facann = (this.facturaSearch || '').toString().trim();
    const searchRaw = (this.searchQuery || '').toString().trim();

    const searchDigits = searchRaw.replace(/\D/g, '');
    const hasLetters = /[A-Za-z]/.test(searchRaw);
    const hasDigits = /\d/.test(searchRaw);
    const NIF_MIN_DIGITS = 5;
    const doNifSearch = !hasLetters && hasDigits && searchDigits.length > NIF_MIN_DIGITS;
    const doAlphaNumSearch = hasLetters && hasDigits && searchRaw.length > NIF_MIN_DIGITS;
    const doShortDigitsExact = !hasLetters && hasDigits && searchDigits.length > 0 && searchDigits.length <= NIF_MIN_DIGITS;
    const doFallbackTextSearch = searchRaw.length > 0 && !doNifSearch && !doAlphaNumSearch && !doShortDigitsExact;
    let searchApplied = false;
    if (facann || doNifSearch || doAlphaNumSearch || doShortDigitsExact || doFallbackTextSearch) {
      searchApplied = true;
      const source = (this.backupFacturas && this.backupFacturas.length) ? this.backupFacturas : this.facturas;
      const searchUp = searchRaw.toUpperCase();
      const filtered = source.filter(f => {
        const valFac = (f.facann ?? f.FACANN ?? '').toString();
        const valCge = (f.cgecod ?? f.CGECOD ?? '').toString();
        const valTernif = (f.ternif ?? f.TERNIF ?? '').toString().toUpperCase();
        if (facann && valFac !== facann) return false;

        if (doShortDigitsExact) {
          const valTercod = (f.tercod ?? f.TERCOD ?? '').toString();
          const valTerado = (f.terado ?? f.TERADO ?? '').toString().toUpperCase();
          if (!(valTercod === searchDigits || valTerado === searchUp)) return false;
        }
        else if (doNifSearch) {
          if (!valTernif.includes(searchDigits)) return false;
        }
        else if (doAlphaNumSearch) {
          const valTernom = (f.ternom ?? f.TERNOM ?? '').toString().toUpperCase();
          const valFacdoc = (f.facdoc ?? f.FACDOC ?? '').toString().toUpperCase();
          if (!(valTernif.includes(searchUp) || valTernom.includes(searchUp) || valFacdoc.includes(searchUp))) return false;
        }
        else if (doFallbackTextSearch) {
          const valTernom = (f.ternom ?? f.TERNOM ?? '').toString().toUpperCase();
          const valFacdoc = (f.facdoc ?? f.FACDOC ?? '').toString().toUpperCase();
          if (!(valTernom.includes(searchUp) || valFacdoc.includes(searchUp))) return false;
        }
        return true;
      });

      this.facturas = filtered;
      this.page = 0;
      const parts = [];
      if (facann) parts.push(`FACANN = ${facann}`);
      if (doShortDigitsExact) parts.push(`TERCOD = ${searchDigits} OR TERADO = "${searchRaw}"`);
      else if (doNifSearch) parts.push(`TERNIF contains "${searchDigits}"`);
      else if (doAlphaNumSearch) parts.push(`TERNIF/TERNOM/FACDOC contains "${searchRaw}"`);
      else if (doFallbackTextSearch) parts.push(`TERNOM/FACDOC contains "${searchRaw}"`);
      else if (searchRaw) parts.push(`Search ignored (need more than ${NIF_MIN_DIGITS} digits or alphanumeric length > ${NIF_MIN_DIGITS})`);
    }

    const hasDateInput = !!(desde || hasta);
    if (!tipo) {
      if (estado && estado != 'no-contabilizadas') {
        this.filterFacturaMessage = 'Seleccione un tipo de fecha antes de elegir un estado.';
        return;
      }
      if (hasDateInput) {
        this.filterFacturaMessage = 'Seleccione un tipo de fecha antes de filtrar por fechas.';
        return;
      }
      if (searchApplied) {
        this.filterFacturaMessage = '';
        return; 
      }
    }
    
    let datePart = '';
    if (desde && hasta) {
      datePart = `Desde: ${desde}. Hasta: ${hasta}.`;
    } else if (desde) {
      datePart = `Desde: ${desde}.`;
    } else {
      datePart = `Hasta: ${hasta}.`;
    }

    if (tipo === 'registro') {
      if (!desde && !hasta) {
        this.filterFacturaMessage = "Debe elegir una línea de tiempo";
      }
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-notnull/${this.entcod}//${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
    } else if (tipo === 'factura') {
      if (!desde && !hasta) {
        this.filterFacturaMessage = "Debe elegir una línea de tiempo";
      }
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
    } else if (tipo === 'contable') {
      if (!desde && !hasta) {
        this.filterFacturaMessage = "Debe elegir una línea de tiempo";
      }
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
            this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta/${this.entcod}/${this.eje}/${this.centroGestor}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-notnull/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-null/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-aplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        } else {
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range/${this.entcod}/${this.eje}/${this.centroGestor}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (!Array.isArray(response) || response.length === 0) {
                this.facturaMessageIsSuccess = true;
                this.facturaMessageSuccess = 'No hay facturas por las medidas de búsqueda';
                this.facturas = []
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                this.page = 0;
              }
            }, error: (err) => {
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
            }
          });
        }
      }
    } else {
      this.filterFacturaMessage = 'Tipo de fecha requirida.';
    }
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
    this.facturaMessage = '';
    this.facturaMessageSuccess = '';
    this.page = 0;
    setTimeout(() => {
      const ejeInput = document.querySelector('input[name="factura"]') as HTMLInputElement | null;
      if (ejeInput) ejeInput.value = '';
      const searchInput = document.querySelector('input[name="search"]') as HTMLInputElement | null;
      if (searchInput) searchInput.value = '';
      const cgeInput = document.querySelector('input[name="Gestor"]') as HTMLInputElement | null;
      if (cgeInput) cgeInput.value = this.centroGestor;
    }, 0);

    if (this.backupFacturas && this.backupFacturas.length) {
      this.facturas = [...this.backupFacturas];
      return;
    }

    if (this.entcod !== null && this.eje !== null) {
      this.http.get<any>(`http://localhost:8080/api/fac/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
        next: (response) => {
          this.facturas = response;
          this.backupFacturas = Array.isArray(response) ? [...response] : [];
          this.page = 0;
        },
        error: (err) => {
          this.facturaIsError = true;
          this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        }
      });
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
      this.http.get<any>(`http://localhost:8080/api/alb/${this.entcod}/${this.eje}/${facnum}`).subscribe({
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
      this.http.get<any>(`http://localhost:8080/api/fde/${this.entcod}/${this.eje}/${facnum}`).subscribe({
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
      this.http.get<any>(`http://localhost:8080/api/fdt/${this.entcod}/${this.eje}/${facnum}`).subscribe({
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

}
