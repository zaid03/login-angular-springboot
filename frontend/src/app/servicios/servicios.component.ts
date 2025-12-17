import { Component, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { Serializer } from '@angular/compiler';

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

    this.fetchServices();
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
  detailFlags = { depalm: false, depcom: false, depint: false };
  showDetails(services: any) {
    this.almacenErro = '';
    this.datosError = '';
    this.personasError = '';
    this.selectedService = services;
    this.option = 'personas';
    this.detailFlags = {
      depalm: services?.depalm === 1,
      depcom: services?.depcom === 1,
      depint: services?.depint === 1
    };
    const cgecod = this.selectedService.cgecod;
    this.http.get(`${environment.backendUrl}/api/cge/fetch-description-services/${this.entcod}/${this.eje}/${cgecod}`, { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        const description = res;
        this.selectedService = {... this.selectedService, cgedes: description};
      },
      error: (err) => {
        this.servicesDetailError = 'No se encuentra la descripción del cgecod.';
      }
    })

    this.fetchPersonas(services.depcod);
  }

  toggleDetailFlag(field: 'depalm' | 'depcom' | 'depint', value: boolean): void {
    this.detailFlags = { ...this.detailFlags, [field]: value };
  }

  closeDetails() {
    this.selectedService = null;
    this.servicesDetailError = '';
    this.servicesDetailSuccess = '';
    this.updateServiceSuccessMessage = '';
    this.updateServiceErrorMessage = '';
    this.almacenErro = '';
    this.datosError = '';
    this.personasError = '';
    this.personas = [];
  }

  option: 'personas' | 'datos' | 'almacen' = 'personas';
  setOption(next: 'personas' | 'datos' | 'almacen'): void {
    this.option = next;
  }

  updateServiceSuccessMessage: string = '';
  updateServiceErrorMessage: string = ''
  updateService(cod:string, des: string, cco:string) {
    this.updateServiceSuccessMessage = '';
    this.updateServiceErrorMessage = '';

    if (cod === '' || des === '' || cco === '') {
      this.updateServiceErrorMessage = 'Todos los campos son obligatorios.'
      return;
    }

    const payload = {
      depdes: des,
      depalm: this.detailFlags.depalm ? 1 : 0,
      depcom: this.detailFlags.depcom ? 1 : 0,
      depint: this.detailFlags.depint ? 1 : 0,
      ccocod: cco
    };

    this.http.patch(`${environment.backendUrl}/api/dep/update-service/${this.entcod}/${this.eje}/${cod}`, payload).subscribe({
      next: (res) => {
        this.updateServiceSuccessMessage = 'servicio actualizado exitosamente'
        this.fetchServices();
      },
      error: (err) => {
        this.updateServiceErrorMessage = err?.error;
      }
    })
  }

  private fetchServices(): void {
    if (this.entcod === null || this.eje === null) return;
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
    });
  }

  showAddConfirm: boolean = false;
  servicesAddError: string = '';
  launchAddCentroGestor() {
    this.showAddConfirm = true;
  }

  closeAddConfirm() {
    this.showAddConfirm = false;
    this.servicesAddError = '';
  }

  newCge = '';
  setInputToUpper(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    const upper = (target.value ?? '').toUpperCase();
    target.value = upper;
    this.newCge = upper;
  }

  addDepalm: boolean = false;
  addDepcom: boolean = false;
  addDepint: boolean = false;
  addServicesSuccessMessage: string = '';
  addServiceErrorMessage: string = '';
  addService(cod: string, des:string, cco:string, cge: string) {
    this.addServiceErrorMessage = '';
    this.addServicesSuccessMessage = '';
    if (cod === '' || des === '' || cco === '') {
      this.addServiceErrorMessage = 'Todos los campos son obligatorios.'
      return;
    }

    const payload = {
      "ent": this.entcod,
      "eje": this.eje,
      "depcod": cod,
      "depdes": des,
      "depalm": this.addDepalm ? 1 : 0,
      "depcom": this.addDepcom ? 1 : 0,
      "depint": this.addDepint ? 1 : 0,
      "ccocod": cco,
      "cgecod": cge
    }

    this.http.post(`${environment.backendUrl}/api/dep/Insert-service`, payload).subscribe({
      next: (res) => {
        this.addServicesSuccessMessage = 'Servicio añadido con éxito';
        this.closeAddConfirm();
      },
      error: (err) => {
        this.addServiceErrorMessage = 'Server error: ' + err?.error;
      }
    })
  }

  personasError: string = '';
  personas: any[] = [];
  personasPage = 0;
  personasPageSize = 10;
  fetchPersonas(depcod: string): void {
    this.almacenErro = '';
    this.datosError = '';
    this.personasError = '';
    if (!depcod) {
      this.personasError = 'codigo extraviado'
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/depe/fetch-service-personas/${this.entcod}/${this.eje}/${depcod}`).subscribe({
      next: (res) => {
        this.personas = res;
        this.personasPage = 0;
      },
      error: (err) => {
        this.personasError = 'Server error: ' + err?.error;
      }
    })
  }

  get paginatedPersonas(): any[] {
    const start = this.personasPage * this.personasPageSize;
    return this.personas.slice(start, start + this.personasPageSize);
  }

  get personasTotalPages(): number {
    return Math.max(1, Math.ceil(this.personas.length / this.personasPageSize));
  }

  personasPrevPage(): void {
    if (this.personasPage > 0) this.personasPage--;
  }

  personasNextPage(): void {
    if (this.personasPage < this.personasTotalPages - 1) this.personasPage++;
  }

  personasGoToPage(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.personasTotalPages) {
      this.personasPage = inputPage - 1;
    }
  }

  datosError: string = '';
  datosObject: any = null;
  datos(service: any) {
    this.almacenErro = '';
    this.datosError = '';
    this.personasError = '';
    this.datosObject = service;
  }

  almacenErro: string = '';
  almacenArray: any = null;
  almacenDatosArray: any[] = [];
  almacenDatos(depcod: string) {
    if (!depcod) {
      this.almacenErro = 'codigo extraviato';
    }

    this.http.get<any>(`${environment.backendUrl}/api/mag/fetch-almacen-nombre/${this.entcod}/${depcod}`).subscribe({
      next: (res) => {
        this.almacenArray = res;
      },
      error: (err) => {
        this.almacenErro = 'Server error: ' + err?.error;
      }
    })

    this.http.get<any>(`${environment.backendUrl}/api/mat/fetch-almacen/${this.entcod}/${depcod}`).subscribe({
      next: (res) => {
        this.almacenDatosArray = res;
        console.log(this.almacenDatosArray)
      },
      error: (err) => {
        this.almacenErro = 'Server error: ' + err?.error;
      }
    })
  }
}
