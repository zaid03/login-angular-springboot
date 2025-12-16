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
  selector: 'app-servicios',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent ],
  templateUrl: './servicios.component.html',
  styleUrls: ['./servicios.component.css']
})
export class ServiciosComponent {
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

  private entcod: number | null = null;
  private eje: number | null = null;
  services: any[] = [];
  private backupServices: any[] = [];
  private defaultServices: any[] = [];
  servicesMessageSuccess: string = '';
  servicessMessageError: string = '';
  page = 0;
  pageSize = 20;

  ngOnInit() {
    this.servicesMessageSuccess = '';
    this.servicessMessageError = '';
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');

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

    this.http.get<any>(`${environment.backendUrl}/api/dep/fetch-services/${this.entcod}/${this.eje}`).subscribe({
      next: (res) => {
        this.services = res;
        this.backupServices = Array.isArray(res) ? [...res] : [];
        this.defaultServices = [...this.backupServices];
        this.page = 0;
        this.updatePagination();
      },
      error: (err) => {
        this.servicessMessageError = 'Server error: ' + err?.error;
      }
    })
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
  
  toggleSort(field: 'depcod' | 'cgecod'): void {
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

  sortField: 'depcod' | 'cgecod' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const field = this.sortField;
    const collator = new Intl.Collator('es', { numeric: true, sensitivity: 'base' });

    const sorted = [...this.services].sort((a, b) => {
      const extract = (item: any, prop: string) =>
        (item?.[prop] ?? item?.[prop.toUpperCase()] ?? '').toString();

      if (field === 'depcod') {
        const aVal = extract(a, 'depcod');
        const bVal = extract(b, 'depcod');
        return this.sortDirection === 'asc'
          ? collator.compare(aVal, bVal)
          : collator.compare(bVal, aVal);
      }

      const aVal = extract(a, field);
      const bVal = extract(b, field);
      return this.sortDirection === 'asc'
        ? collator.compare(aVal, bVal)
        : collator.compare(bVal, aVal);
    });

    this.services = sorted;
    this.page = 0;
    this.updatePagination();
  }

  get paginatedServices(): any[] {
    if (!this.services || this.services.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.services.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.services?.length ?? 0) / this.pageSize));
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
      this.servicessMessageError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Entidad: row.ent ?? '',
      EJE: row.eje ?? '',
      Servicio: row.depcod ?? '',
      Descripción: row.depdes ?? '',
      centro_gestor: row.cgecod ?? '',
      Centro_de_Coste: row.ccocod ?? '',
      Almacén: this.checkIfChecked(row.depalm) ?? '',
      Comprador_Or_Farmacia: this.checkIfChecked(row.depcom) ?? '',
      Contabilidad: this.checkIfChecked(row.depint) ?? ''
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['listas de servicios']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Entidad', 'EJE', 'Servicio', 'Descripción', 'Cód. C.G.', 'Centro de Coste', 'Almacén', 'Comprador/Farmacia', 'Contabilidad']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 6 },
      { wch: 12 },
      { wch: 12 },
      { wch: 15 },
      { wch: 45 },
      { wch: 12 },
      { wch: 15 },
      { wch: 16 },
      { wch: 20 },
      { wch: 16 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Servicios');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'servicios.xlsx'
    );
  }

  toPrint() {
    const rows = this.backupServices.length ? this.backupServices : this.services;
    if (!rows?.length) {
      this.servicessMessageError = 'No hay datos para imprimir.';
      return;
    }

    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${row.ent ?? ''}</td>
        <td>${row.eje ?? ''}</td>
        <td>${row.depcod ?? ''}</td>
        <td>${row.depdes ?? ''}</td>
        <td>${row.cgecod ?? ''}</td>
        <td>${row.ccocod ?? ''}</td>
        <td>${this.checkIfChecked(row.depalm) ?? ''}</td>
        <td>${this.checkIfChecked(row.depcom) ?? ''}</td>
        <td>${this.checkIfChecked(row.depint) ?? ''}</td>
      </tr>
    `).join('');

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) return;

    printWindow.document.write(`
      <html>
        <head>
          <title>listas de servicios</title>
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
                <th>Servicio</th>
                <th>Descripción</th>
                <th>Cód.C.G</th>
                <th>Centro de Coste</th>
                <th>Almacén</th>
                <th>Comprador / Farmacia</th>
                <th>Contabilidad</th>
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

  selectedService: any = null;
  servicesDetailError: string = '';
  servicesDetailSuccess: string = '';
  showDetails(services: any) {
    this.selectedService = services;
    const cgecod = this.selectedService.cgecod;
    this.http.get(`${environment.backendUrl}/api/cge/fetch-description-services/${this.entcod}/${this.eje}/${cgecod}`, { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        const description = res;
        this.selectedService = {... this.selectedService, cgedes: description};
        console.log(this.selectedService);
      },
      error: (err) => {
        this.servicesDetailError = 'No se encuentra la descripción del cgecod.';
      }
    })
  }

  toggleDetailFlag(event: Event, field: 'depalm' | 'depcom' | 'depint'): void {
    if (!this.selectedService) return;
    const input = event.target as HTMLInputElement | null;
    const value = input?.checked ? 0 : 1;
    this.selectedService = { ...this.selectedService, [field]: value };
  }
  closeDetails() {
    this.selectedService = null;
    this.servicesDetailError = '';
    this.servicesDetailSuccess = '';
  }
}
