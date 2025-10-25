import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
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
  public centroGestorTouched: boolean = false;
  facturas: any[] = [];
  private backupFacturas: any[] = [];
  page = 0;
  pageSize = 20;
  facturaMessage: String = '';
  facturaIsError: boolean = false;
  public Math = Math;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void{
    this.facturaIsError = false;
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('selected_ejercicio');
    const cge = sessionStorage.getItem("selected_centro_gestor");
    try {
      if (cge) {
        const parsed = JSON.parse(cge);
        if (parsed && typeof parsed === 'object') {
          this.centroGestor = parsed.cgecod;
        } else {
          this.centroGestor = String(parsed);
        }
      }
    } catch (e) {
      console.warn('Failed to parse selected_centro_gestor:', e);
    }
    console.log(this.centroGestor);

    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }
    console.log(this.entcod);
    if (eje) {
      const parsed = JSON.parse(eje);
      this.eje = parsed.eje;
    }
    console.log(this.eje);

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/fac/${this.entcod}/${this.eje}`).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.facturas = response;
          console.log(this.facturas);
          this.backupFacturas = Array.isArray(response) ? [...response] : [];
          this.page = 0;
          console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
          console.log('raw facturas sample:', this.facturas[0]);
        }
      }, error: (err) => {
        console.error('Facturas fetch error:', err);
        this.facturaIsError = true;
        this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        alert(this.facturaMessage);
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
  }

  closeDetails() {
    this.selectedFacturas = null;
    this.detallesMessage = '';
  }

  public getPendingApply(f: any): string {
    if (!f) return '';
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const facimp = toNum(f.facimp);
    console.log(facimp);
    const faciec = toNum(f.faciec);
    console.log(faciec);
    const facidi = toNum(f.facidi);
    console.log(facidi);
    const pending = facimp - (faciec + facidi);
    console.log(pending);
    return pending.toFixed(2);
  }

  fechaTipo: 'registro' | 'factura' | 'contable' | '' = '';
  estadoTipo: 'contabilizadas' | 'no-contabilizadas' | 'aplicadas' | 'sin-aplicadas' | '' = '';
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
  }

  onCentroGestorInput(event: Event) {
    const input = event.target as HTMLInputElement;
    const sanitized = (input.value || '').toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 4);
    if (sanitized !== this.centroGestor) {
      this.centroGestor = sanitized;
    }
    this.centroGestorTouched = true;
  }
  
  filterFacturas(){
    this.facturaMessage = '';
    this.filterFacturaMessage = '';
    const tipo = this.fechaTipo;
    const estado = this.estadoTipo;
    console.log(estado);
    const desde = this.fromDate && this.fromDate.trim() ? this.fromDate : '';
    const hasta = this.toDate && this.toDate.trim() ? this.toDate : '';
    const facann = (this.facturaSearch || '').toString().trim();
    const cgeq = this.centroGestorTouched ? (this.centroGestor || '').toString().trim() : '';
    console.log('facturaSearch:', this.facturaSearch);
    console.log('centroGestor:', this.centroGestor);
    const searchRaw = (this.searchQuery || '').toString().trim();
    console.log('searchQuery raw:', searchRaw);

    // determine search type
    const searchDigits = searchRaw.replace(/\D/g, '');
    const hasLetters = /[A-Za-z]/.test(searchRaw);
    const hasDigits = /\d/.test(searchRaw);
    const NIF_MIN_DIGITS = 5; // threshold value (logic uses strictly greater than this)
    // require strictly more than 5 digits for substring TERNIF search
    const doNifSearch = !hasLetters && hasDigits && searchDigits.length > NIF_MIN_DIGITS;
    // require strictly more than 5 total chars (letters+digits) for alphanumeric substring search
    const doAlphaNumSearch = hasLetters && hasDigits && searchRaw.length > NIF_MIN_DIGITS;
    // digits-only with length 1..5 -> exact match on TERCOD or TERADO
    const doShortDigitsExact = !hasLetters && hasDigits && searchDigits.length > 0 && searchDigits.length <= NIF_MIN_DIGITS;
    // fallback: any searchRaw that didn't match other rules (e.g. letters-only) -> search TERNOM/FACDOC by substring
    const doFallbackTextSearch = searchRaw.length > 0 && !doNifSearch && !doAlphaNumSearch && !doShortDigitsExact;
    console.log('searchDigits:', searchDigits, 'doNifSearch:', doNifSearch, 'doAlphaNumSearch:', doAlphaNumSearch, 'doShortDigitsExact:', doShortDigitsExact, 'doFallbackTextSearch:', doFallbackTextSearch);

    // Local filter when any of facann, centroGestor or search rules apply.
    if (facann || cgeq || doNifSearch || doAlphaNumSearch || doShortDigitsExact || doFallbackTextSearch) {
      const source = (this.backupFacturas && this.backupFacturas.length) ? this.backupFacturas : this.facturas;
      const searchUp = searchRaw.toUpperCase();
      const filtered = source.filter(f => {
        const valFac = (f.facann ?? f.FACANN ?? '').toString();
        const valCge = (f.cgecod ?? f.CGECOD ?? '').toString();
        const valTernif = (f.ternif ?? f.TERNIF ?? '').toString().toUpperCase();
        // exact checks for EJE / C.Gestor
        if (facann && valFac !== facann) return false;
        if (cgeq && valCge !== cgeq) return false;

        // short digits (1..5): exact match on TERCOD (numeric) OR TERADO (string)
        if (doShortDigitsExact) {
          const valTercod = (f.tercod ?? f.TERCOD ?? '').toString();
          const valTerado = (f.terado ?? f.TERADO ?? '').toString().toUpperCase();
          if (!(valTercod === searchDigits || valTerado === searchUp)) return false;
        }
        // numbers-only > 5 digits: substring in TERNIF using digits-only
        else if (doNifSearch) {
          if (!valTernif.includes(searchDigits)) return false;
        }
        // alphanumeric > 5 chars: substring search in TERNIF, TERNOM, FACDOC (case-insensitive)
        else if (doAlphaNumSearch) {
          const valTernom = (f.ternom ?? f.TERNOM ?? '').toString().toUpperCase();
          const valFacdoc = (f.facdoc ?? f.FACDOC ?? '').toString().toUpperCase();
          if (!(valTernif.includes(searchUp) || valTernom.includes(searchUp) || valFacdoc.includes(searchUp))) return false;
        }
        // fallback text search (e.g. letters-only): substring search in TERNOM and FACDOC
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
      if (cgeq) parts.push(`C.Gestor = ${cgeq}`);
      if (doShortDigitsExact) parts.push(`TERCOD = ${searchDigits} OR TERADO = "${searchRaw}"`);
      else if (doNifSearch) parts.push(`TERNIF contains "${searchDigits}"`);
      else if (doAlphaNumSearch) parts.push(`TERNIF/TERNOM/FACDOC contains "${searchRaw}"`);
      else if (doFallbackTextSearch) parts.push(`TERNOM/FACDOC contains "${searchRaw}"`);
      else if (searchRaw) parts.push(`Search ignored (need more than ${NIF_MIN_DIGITS} digits or alphanumeric length > ${NIF_MIN_DIGITS})`);
      this.filterFacturaMessage = `Filtered locally by ${parts.join(' and ')}. ${filtered.length} record(s).`;
      return;
    }

    console.log(this.fromDate);
    console.log(desde);

    if (!desde && !hasta) {
      this.filterFacturaMessage = 'Por favor rellene al menos una fecha (Desde o Hasta) cuando selecciona un tipo de fecha.';
      return;
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
      this.filterFacturaMessage = `Ha seleccionado: Registro. ${datePart}`;
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-notnull/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-null/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-aplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("no estado");
            this.http.get<any>(`http://localhost:8080/api/fac/facfre-desde/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta")
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-notnull/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-null/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("hasta y no estados");
            this.http.get<any>(`http://localhost:8080/api/fac/facfre-hasta/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta y dsde")
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-notnull/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-null/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-aplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("desde y hasta no estado");
          this.http.get<any>(`http://localhost:8080/api/fac/facfre-range/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas desde hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
    } else if (tipo === 'factura') {
      this.filterFacturaMessage = `Ha seleccionado: Factura. ${datePart}`;
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          console.log(" con contabilizadaas");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-notnull/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-null/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-aplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("no estado");
            this.http.get<any>(`http://localhost:8080/api/fac/facdat-desde/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta")
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-notnull/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-null/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("hasta y no estados");
            this.http.get<any>(`http://localhost:8080/api/fac/facdat-hasta/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta y dsde")
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-notnull/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-null/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-aplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("desde y hasta no estado");
          this.http.get<any>(`http://localhost:8080/api/fac/facdat-range/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas desde hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
    } else if (tipo === 'contable') {
      if (desde && !hasta) {
        if (estado === 'contabilizadas') {
          console.log(" con contabilizadaas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-notnull/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-null/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-aplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("no estado");
            this.http.get<any>(`http://localhost:8080/api/fac/facfco-desde/${this.entcod}/${this.eje}/${desde}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if(!desde && hasta) {
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta")
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-notnull/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-null/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-aplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta-facado-sinaplicadas/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("hasta y no estados");
            this.http.get<any>(`http://localhost:8080/api/fac/facfco-hasta/${this.entcod}/${this.eje}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
      if (desde && hasta){
        if (estado === 'contabilizadas') {
          console.log("contabilizada y hasta y dsde")
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-notnull/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with facado not null",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'no-contabilizadas') {
          console.log("no contabilizadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-null/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if ( estado === 'aplicadas') {
          console.log("aplicadas y hasta y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-aplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else if (estado === 'sin-aplicadas') {
          console.log("sin aplicadas y hasta y desde");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range-facado-sinaplicadas/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas with no contabilizadas",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        } else {
          console.log("desde y hasta no estado");
          this.http.get<any>(`http://localhost:8080/api/fac/facfco-range/${this.entcod}/${this.eje}/${desde}/${hasta}`).subscribe({
            next: (response) => {
              if (response.error) {
                alert('Error: ' + response.error);
              } else {
                this.facturas = response;
                this.backupFacturas = Array.isArray(response) ? [...response] : [];
                console.log("facturas desde hasta",this.facturas);
                this.page = 0;
                console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
                console.log('raw facturas sample:', this.facturas[0]);
              }
            }, error: (err) => {
              console.error('Facturas fetch error:', err);
              this.facturaIsError = true;
              this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
              alert(this.facturaMessage);
            }
          });
        }
      }
    } else {
      this.filterFacturaMessage = 'Tipo de fecha desconocido.';
    }
  }
}
