import { Component, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-persona',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent ],
  templateUrl: './persona.component.html',
  styleUrls: ['./persona.component.css']
})
export class PersonaComponent {
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

  constructor(private http: HttpClient, private router: Router) {}

  //global variables
  private entcod: number | null = null;
  private eje: number | null = null;
  personas: any[] = [];
  private backuppersonas: any[] = [];
  private defaultpersonas: any[] = [];
  personasMessageSuccess: string = '';
  personasMessageError: string = '';
  page = 0;
  pageSize = 20;

  ngOnInit(): void {
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    if (entidad) {const parsed = JSON.parse(entidad); this.entcod = parsed.ENTCOD;}
    if (eje) {const parsed = JSON.parse(eje); this.eje = parsed.eje;}

    if (!entidad || this.entcod === null || !eje || this.eje === null ) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }
    this.fetchPersonas();
  }

  //main table functions
  fetchPersonas() {
    this.http.get<any>(`${environment.backendUrl}/api/Per/fetch-all`).subscribe({
      next: (res) => {
        this.personas = res;
        this.backuppersonas = Array.isArray(res) ? [...res] : [];
        this.defaultpersonas = [...this.backuppersonas];
        this.page = 0;
        this.updatePagination();
      },
      error: (err) => {
        this.personasMessageError = err?.error?.error || 'Error desconocido';
      }
    });
  }

  toggleSort(field: 'percod' | 'pernom' | 'percoe' | 'pertel' | 'pertmo' | 'percar' | 'perobs'): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDirection = 'asc';
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortField = null;
      this.sortDirection = 'asc';
      this.personas = [...this.defaultpersonas];
      this.page = 0;
      this.updatePagination();
      return;
    }

    this.applySort();
  }

  sortField: 'percod' | 'pernom' | 'percoe' | 'pertel' | 'pertmo' | 'percar' | 'perobs' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const field = this.sortField;
    const collator = new Intl.Collator('es', { numeric: true, sensitivity: 'base' });

    const sorted = [...this.personas].sort((a, b) => {
      const extract = (item: any, prop: string) =>
        (item?.[prop] ?? item?.[prop.toUpperCase()] ?? '').toString();


      const aVal = extract(a, field);
      const bVal = extract(b, field);
      return this.sortDirection === 'asc'
        ? collator.compare(aVal, bVal)
        : collator.compare(bVal, aVal);
    });

    this.personas = sorted;
    this.page = 0;
    this.updatePagination();
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
    const table = document.querySelector('.personas-table') as HTMLTableElement;
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

  get paginatedPersonas(): any[] {
    if (!this.personas || this.personas.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.personas.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.personas?.length ?? 0) / this.pageSize));
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

  excelDownload() { 
    const rows = this.backuppersonas.length ? this.backuppersonas : this.personas;
    if (!rows || rows.length === 0) {
      this.personasMessageError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Código: row.percod ?? '',
      Nombre: row.pernom ?? '',
      Correo_Electrónico: row.percoe ?? '',
      Teléfono: row.pertel ?? '',
      Cargo: row.percar ?? '',
      Observaciones: row.perobs ?? '',
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['listas de Personas']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Código', 'Nombre', 'Correo_Electrónico', 'Teléfono', 'Cargo', 'Observaciones']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 15 },
      { wch: 30 },
      { wch: 40 },
      { wch: 35 },
      { wch: 20 },
      { wch: 40 },
      { wch: 45 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Personas');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'Personas.xlsx'
    );
  }

  toPrint() {
    const rows = this.backuppersonas.length ? this.backuppersonas : this.personas;
    if (!rows?.length) {
      this.personasMessageError = 'No hay datos para imprimir.';
      return;
    }

    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${row.percod ?? ''}</td>
        <td>${row.pernom ?? ''}</td>
        <td>${row.percoe ?? ''}</td>
        <td>${row.pertel ?? ''}</td>
        <td>${row.pertmo ?? ''}</td>
        <td>${row.percar ?? ''}</td>
        <td>${row.perobs ?? ''}</td>
      </tr>
    `).join('');

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) return;

    printWindow.document.write(`
      <html>
        <head>
          <title>listas de Personas</title>
          <style>
            body { font-family: 'Poppins', sans-serif; padding: 24px; }
            h1 { text-align: center; margin-bottom: 16px; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
            th { background: #f3f4f6; }
            th:last-child, td:last-child { width: 180px; }
          </style>
        </head>
        <body>
          <h1>listas de Personas</h1>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Código</th>
                <th>Nombre</th>
                <th>Correo Electrónico</th>
                <th>Teléfono</th>
                <th>Móvil</th>
                <th>Cargo</th>
                <th>Observaciones</th>
              </tr>
            </thead>
            <tbody>
              ${htmlRows}
            </tbody>
          </table>
        </body>
      </html>
    `);
    printWindow.document.close();
    printWindow.print();
  }

  searchPersonas: string = '';
  search() {
    if(this.searchPersonas === '') {
      this.personasMessageError = 'Ingrese algo para buscar';
      return 
    }

    if(this.searchPersonas.length <= 20) {
      this.http.get<any>(`${environment.backendUrl}/api/Per/search-cod-nom/${this.searchPersonas}`).subscribe({
        next: (res) => {
          this.personas = res;
          this.backuppersonas = Array.isArray(res) ? [...res] : [];
          this.defaultpersonas = [...this.backuppersonas];
          this.page = 0;
          this.updatePagination();
          if (!res.length) {
            this.personasMessageError = 'No se encontraron servicios con los filtros dados.';
          }
        },
        error: (err) => {
          this.personas = [];
          this.backuppersonas = [];
          this.defaultpersonas = [];
          this.page = 0;
          this.updatePagination();
          this.personasMessageError = err?.error || 'Error en la búsqueda.';
        }
      });
    } else {
      this.http.get<any>(`${environment.backendUrl}/api/Per/search-nom/{this.searchPersonas}`).subscribe({
        next: (res) => {
          this.personas = res;
          this.backuppersonas = Array.isArray(res) ? [...res] : [];
          this.defaultpersonas = [...this.backuppersonas];
          this.page = 0;
          this.updatePagination();
          if (!res.length) {
            this.personasMessageError = 'No se encontraron servicios con los filtros dados.';
          }
        },
        error: (err) => {
          this.personas = [];
          this.backuppersonas = [];
          this.defaultpersonas = [];
          this.page = 0;
          this.updatePagination();
          this.personasMessageError = err?.error || 'Error en la búsqueda.';
        }
      });
    }
  }

  limpiarSearch() {
    this.searchPersonas = '';
    this.fetchPersonas();
  }

  //detail grid functions
  selectedPersona: any = null;
  detailMessageSuccess: string = '';
  detailMessageError: string = '';
  showDetails(persona: any) {
    this.selectedPersona = persona;
  }

  closeDetails() {
    this.selectedPersona = null
  }

  //services grid functions
  activeDetailTab: 'services' | null = null;
  showSerivcesGrid = false;
  personServices: any = [];
  serviceErrorMessage: string = '';
  serviceSuccessMessage: string = '';
  showServices(persona: any) {
    this.showSerivcesGrid = true;
    this.activeDetailTab = 'services';
    const percod = persona.percod;
  }
}