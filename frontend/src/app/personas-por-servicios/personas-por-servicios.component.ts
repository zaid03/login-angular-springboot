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
  selector: 'app-personas-por-servicios',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent ],
  templateUrl: './personas-por-servicios.component.html',
  styleUrls: ['./personas-por-servicios.component.css']
})
export class PersonasPorServiciosComponent {
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
  personasServicesSuccess: string = '';
  personasServicesError: string = '';
  services: any[] = [];
  private backupServices: any[] = [];
  private defaultServices: any[] = [];
  page = 0;
  pageSize = 20;
  pageLoad = 1;

  ngOnInit() {
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');

    if (entidad) {const parsed = JSON.parse(entidad); this.entcod = parsed.ENTCOD;}
    if (eje) {const parsed = JSON.parse(eje); this.eje = parsed.eje;}

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('Debes iniciar sesión para acceder a esta página.');
      this.router.navigate(['/login']);
      return;
    }
    this.fetchServices();
  }

  //global functions
  isLoading: boolean = false;
  fetchServices() {
    if (this.entcod === null || this.eje === null) return;
    this.isLoading = true;
    this.http.get<any>(`${environment.backendUrl}/api/depe/personas-servicios/${this.entcod}/${this.eje}/${this.pageLoad}`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.services = res;
        this.backupServices = Array.isArray(res) ? [...res] : [];
        this.defaultServices = [...this.backupServices];
        this.page = 0;
        this.updatePagination();
      },
      error: (err) => {
        this.isLoading = false;
        this.personasServicesError = err.error.error ?? err.error;
      }
    });
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
  get paginatedServices(): any[] {if (!this.services || this.services.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.services.slice(start, start + this.pageSize);}
  get totalPages(): number {return Math.max(1, Math.ceil((this.services?.length ?? 0) / this.pageSize));}
  prevPage() { if (this.pageLoad === 1) return;
    this.pageLoad--; this.fetchServices(); }
  nextPage() { this.pageLoad++; this.fetchServices(); }

  goToPage(event: any): void { const inputPage = Number(event.target.value);
    this.pageLoad = inputPage; this.fetchServices(); }

  toggleSort(field: 'percod' | 'cgedes' | 'depint' | 'cgecod' | 'depdes' | 'depalm' | 'depcom' | 'depcod' | 'pernom'): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDirection = 'asc';
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortField = null;
      this.sortDirection = 'asc';
      this.services = [...this.defaultServices];
      this.page = 0;
      this.updatePagination();
      return;
    }

    this.applySort();
  }

  sortField: 'percod' | 'cgedes' | 'depint' | 'cgecod' | 'depdes' | 'depalm' | 'depcom' | 'depcod' | 'pernom' | null = null
  sortDirection: 'asc' | 'desc' = 'asc';
  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const field = this.sortField;
    const collator = new Intl.Collator('es', { numeric: true, sensitivity: 'base' });

    const getValue = (item: any, field: string) => {
      switch (field) {
        case 'percod':
          return item.percod ?? '';
        case 'pernom':
          return item.per?.pernom ?? '';
        case 'depcod':
          return item.depcod ?? '';
        case 'depdes':
          return item.dep?.depdes ?? '';
        case 'depalm':
          return item.dep?.depalm ?? '';
        case 'depcom':
          return item.dep?.depcom ?? '';
        case 'depint':
          return item.dep?.depint ?? '';
        case 'cgecod':
          return item.dep?.cge?.cgecod ?? '';
        case 'cgedes':
          return item.dep?.cge?.cgedes ?? '';
        default:
          return item[field] ?? '';
      }
    };

    const sorted = [...this.services].sort((a, b) => {
      const aVal = getValue(a, field);
      const bVal = getValue(b, field);

      if (['percod', 'depcod', 'depalm', 'depcom', 'depint', 'cgecod'].includes(field)) {
        const aNum = Number(aVal);
        const bNum = Number(bVal);
        if (!isNaN(aNum) && !isNaN(bNum)) {
          return this.sortDirection === 'asc' ? aNum - bNum : bNum - aNum;
        }
      }
      return this.sortDirection === 'asc'
        ? collator.compare(aVal.toString(), bVal.toString())
        : collator.compare(bVal.toString(), aVal.toString());
    });

    this.services = sorted;
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
    const table = document.querySelector('.personasServices-table') as HTMLTableElement;
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

  campo: 'depalm' | 'depcom' | 'depint' | null = null;
  checkIfChecked(campo: number) {
    if (campo === 0) {
      return 'Si';
    } else if (campo === 1) {
      return 'No';
    }
    return 'No';
  }

  excelDownload() {
    const rows = this.backupServices.length ? this.backupServices : this.services;
    if (!rows || rows.length === 0) {
      this.personasServicesError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Entidad: row.ent ?? '',
      EJE: row.eje ?? '',
      Cód_Persona: row.percod ?? '',
      Nombre: row.pernom ?? '',
      Cód_Servicio: row.depcod ?? '',
      Servicio: row.depdes ?? '',
      Almacén_OR_Farmacia: this.checkIfChecked(row.depalm) ?? '',
      Comprador: this.checkIfChecked(row.depcom) ?? '',
      Contable: this.checkIfChecked(row.depint) ?? '',
      Cód_Centro_Gestor: row.cgecod ?? '',
      Nombre_Centro_Gestor: row.cgedes ?? ''
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['listas de personas por servicios']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Entidad', 'EJE', 'Cód Persona', 'Nombre', 'Cód Servicio', 'Servicio', 'Almacén/Farmacia', 'Comprador', 'Contable', 'Cód Centro Gestor', 'Nombre Centro Gestor']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 6 },
      { wch: 10 },
      { wch: 12 },
      { wch: 20 },
      { wch: 40 },
      { wch: 20 },
      { wch: 40 },
      { wch: 15 },
      { wch: 15 },
      { wch: 15 },
      { wch: 15 },
      { wch: 40 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'personas por servicios');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'personas_por_servicios.xlsx'
    );
  }

  toPrint() {
    const rows = this.backupServices.length ? this.backupServices : this.services;
    if (!rows?.length) {
      this.personasServicesError = 'No hay datos para imprimir.';
      return;
    }

    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${row.ent ?? ''}</td>
        <td>${row.eje ?? ''}</td>
        <td>${row.percod ?? ''}</td>
        <td>${row.pernom ?? ''}</td>
        <td>${row.depcod ?? ''}</td>
        <td>${row.depdes ?? ''}</td>
        <td>${this.checkIfChecked(row.depalm) ?? ''}</td>
        <td>${this.checkIfChecked(row.depcom) ?? ''}</td>
        <td>${this.checkIfChecked(row.depint) ?? ''}</td>
        <td>${row.cgecod ?? ''}</td>
        <td>${row.cgedes ?? ''}</td>
      </tr>
    `).join('');

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) return;

    printWindow.document.write(`
      <html>
        <head>
          <title>listas de personas por servicios</title>
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
          <h1>listas de servicios</h1>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Entidad</th>
                <th>eje</th>
                <th>Cód_Persona</th>
                <th>Nombre</th>
                <th>Cód_Servicio</th>
                <th>Servicio</th>
                <th>Almacén_OR_Farmacia</th>
                <th>Comprador</th>
                <th>Contable</th>
                <th>Cód_Centro_Gestor</th>
                <th>Nombre_Centro_Gestor</th>
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
}
