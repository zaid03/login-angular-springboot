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
  private perfil: string | null = null;
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
    const percod = sessionStorage.getItem('Perfil');
    if (entidad) {const parsed = JSON.parse(entidad); this.entcod = parsed.ENTCOD;}
    if (eje) {const parsed = JSON.parse(eje); this.eje = parsed.eje;}
    if (percod) {const parsed = JSON.parse(percod); this.perfil = parsed.perfil}

    if (!entidad || this.entcod === null || !eje || this.eje === null || this.perfil === null ) {
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
    this.personServices = [];
    this.backupServices = [];
    this.activeDetailTab = null;
    this.showSerivcesGrid = false;
  }

  updatePersona(pernom: string, percoe: string, pertel: string, pertmo: string, percar: string, perobs: string) {

    if(!pernom) {
      this.detailMessageError = 'Nombre requerido'
      return;
    }

    const payload = {
      "PERNOM" : pernom,
      "PERCOE" : percoe,
      "PERTEL" : pertel,
      "PERTMO" : pertmo,
      "PERCAR" : percar,
      "PEROBS" : perobs,
      "PERCOD" : this.selectedPersona.percod,
    }

    this.http.patch(`${environment.backendUrl}/api/Per/update-persona`, payload).subscribe({
      next: (res) => {
        this.detailMessageSuccess = 'La persona se ha actualizado correctamente';
      },
      error: (err) => {
        this.detailMessageError = err.error.error;
      }
    })
  }

  //services grid functions
  activeDetailTab: 'services' | null = null;
  showSerivcesGrid = false;
  personServices: any = [];
  backupServices: any = [];
  serviceErrorMessage: string = '';
  serviceSuccessMessage: string = '';
  showServices(persona: any) {
    this.showSerivcesGrid = true;
    this.activeDetailTab = 'services';
    const percod = persona.percod;
    this.fetchServices(percod);
  }

  pageServices: number = 0;
  fetchServices(percod: string) {
    this.http.get<any>(`${environment.backendUrl}/api/depe/fetch-persona-service/${this.entcod}/${this.eje}/${percod}`).subscribe({
      next: (res) => {
        this.personServices = res;
        this.backupServices = Array.isArray(res) ? [...res] : [];
        this.pageServices = 0;
      },
      error: (err) => {
        this.serviceErrorMessage = err?.error || 'Error desconocido';
      }
    });
  }

  get paginatedServices(): any[] {
    if (!this.personServices || this.personServices.length === 0) return [];
    const start = this.pageServices * this.pageSize;
    return this.personServices.slice(start, start + this.pageSize);
  }
  get totalPagesServices(): number {
    return Math.max(1, Math.ceil((this.personServices?.length ?? 0) / this.pageSize));
  }
  prevPageService(): void {
    if (this.pageServices > 0) this.pageServices--;
  }
  nextPageService(): void {
    if (this.pageServices < this.totalPages - 1) this.pageServices++;
  }
  goToPageService(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.pageServices = inputPage - 1;
    }
  }

  serviceToDelete: any = [];
  showDeleteGrid: boolean = false;
  showDelete(service: any) {
    this.serviceToDelete = service;
    this.showDeleteGrid = true;
  }

  closeDelete() {
    this.showDeleteGrid = false;
  }

  ErrorDelete: string = '';
  deleteService(depcod: string) {
    if(!depcod || !this.selectedPersona.percod) { return;}
    const persona = this.selectedPersona.percod;
    const service = depcod

    this.http.delete(`${environment.backendUrl}/api/depe/delete-service-persona/${this.entcod}/${this.eje}/${service}/${persona}`).subscribe({
      next: (res) => {
        this.serviceSuccessMessage = 'El servicio se ha eliminado correctamente'
        this.fetchServices(persona);
        this.closeDelete();
      },
      error: (err) => {
        this.ErrorDelete = err.error.error
      }
    })
  }

  addService: boolean = false;
  showAddService() {
    this.addService = true;
    this.fetchServicesAdd();
  }

  closeAddService() {
    this.addService = false;
    this.linesSelected = [];
    this.count = 0;
  }

  services: any = [];
  backupServicesAdd: any = [];
  servicesAddError: string = '';
  fetchServicesAdd() {
    if (this.entcod === null || this.eje === null) return;
    this.http.get<any>(`${environment.backendUrl}/api/dep/fetch-services/${this.entcod}/${this.eje}`).subscribe({
      next: (res) => {
        this.services = res;
        this.backupServices = Array.isArray(res) ? [...res] : [];
        this.backupServicesAdd = [...this.backupServices];
        this.pageAdd = 0;
      },
      error: (err) => {
        this.servicesAddError = err?.error?.error || 'Error desconocido';
      }
    });
  }

  pageAdd: number = 0;
  get paginatedServicesAdd(): any[] {
    if (!this.services || this.services.length === 0) return [];
    const start = this.pageAdd * this.pageSize;
    return this.services.slice(start, start + this.pageSize);
  }
  get totalPagesAdd(): number {
    return Math.max(1, Math.ceil((this.services?.length ?? 0) / this.pageSize));
  }
  prevPageAdd(): void {
    if (this.pageAdd > 0) this.pageAdd--;
  }
  nextPageAdd(): void {
    if (this.pageAdd < this.totalPages - 1) this.pageAdd++;
  }
  goToPageAdd(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.page = inputPage - 1;
    }
  }

  setInputToUpper(event: Event):void {
    const target = event.target as HTMLTextAreaElement;
    const upper = (target.value ?? '').toUpperCase();
    target.value = upper;
  }

  searchServicio: string = '';
  searchCentroGestor: string = '';
  searchPerfil: string = 'todos';
  searchServices() {
    this.limpiarMessages();

    const params: any = {
      ent: this.entcod,
      eje: this.eje,
      percod: this.perfil
    };
    if (this.searchServicio && this.searchServicio.trim() !== '') {
      params.search = this.searchServicio;
    }
    if (this.searchCentroGestor && this.searchCentroGestor.trim() !== '') {
      params.cgecod = this.searchCentroGestor;
    }
    if (this.searchPerfil && this.searchPerfil !== 'todos') {
      params.perfil = this.searchPerfil;
    }

    this.http.get<any>(`${environment.backendUrl}/api/dep/search`, { params }).subscribe({
      next: (res) => {
        this.services = res;
        this.backupServices = Array.isArray(res) ? [...res] : [];
        this.page = 0;
        this.updatePagination();
        if (!res.length) {
          this.servicesAddError = 'No se encontraron servicios con los filtros dados.';
        }
      },
      error: (err) => {
        this.services = [];
        this.backupServices = [];
        this.pageAdd = 0;
        this.servicesAddError = err?.error || 'Error en la búsqueda.';
      }
    });
  }

  clearSearch() {
    this.searchServicio = '';
    this.searchCentroGestor = '';
    this.searchPerfil = 'todos';
    this.limpiarMessages();
    this.fetchServicesAdd();
    this.linesSelected = [];
    this.count = 0;
  }

  linesSelected: string[] = [];
  selectedServiceCode: string | null = null;
  selectService(service: string) {
    if(this.linesSelected.includes(service)) {
      this.linesSelected = this.linesSelected.filter((s) => s !== service);
    }
    this.linesSelected = [...this.linesSelected, service];
    this.getLinesAdded()
  }

  count: number = 0;
  getLinesAdded(){
    this.count = this.linesSelected.length;
  }
  
  addServicePersona(services: string[]) {
    if(!services) {return;}

    const payload = {
      "ent": this.entcod,
      "eje": this.eje,
      "percod": this.selectedPersona.percod,
      "services": services
    }

    this.http.post(`${environment.backendUrl}/api/depe/add-persona-services`, payload).subscribe({
      next: (res) => {
        this.serviceSuccessMessage = 'Los servicios se han añadido correctamente';
        this.fetchServices(this.selectedPersona.percod);
        this.closeAddService();
      },
      error: (err) => {
        this.servicesAddError = err.error || 'Server error';
      }
    })
  }

  //add personas grid functions
  showAddConfirm: boolean = false;
  PersonaErrorMessage: string = '';
  showAdd() {
    this.showAddConfirm = true;
  }

  closeAddConfirm() {
    this.showAddConfirm = false;
  }

  addPersona(code: string, name: string, email: string, phone: string, movil: string, trans: string, obs: string) {
    if(!code || !name) {
      this.PersonaErrorMessage = 'Código y Nombre requerido'
      return;
    }

    const payload = {
      "PERCOD" : code,
      "PERNOM" : name,
      "PERCOE" : email,
      "PERTEL" : phone,
      "PERTMO" : movil,
      "PERCAR" : trans,
      "PEROBS" : obs
    }

    this.http.post(`${environment.backendUrl}/api/Per/Insert-persona`, payload).subscribe({
      next: (res) => {
        this.personasMessageSuccess = 'La persona se agregó correctamente';
        this.fetchPersonas();
        this.closeAddConfirm();
      },
      error: (err) => {
        this.PersonaErrorMessage = err.error.error;
      }
    })
  }

  forceUppercase(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.toUpperCase();
  }


  //compiar grid functions
  compiarPersona: boolean = false;
  gridMessag: string = '';
  showCopiar(percod: string) {
    this.http.get<any>(`${environment.backendUrl}/api/depe/fetch-persona-service/${this.entcod}/${this.eje}/${percod}`).subscribe({
      next: (res) => {
        this.personServices = res;
        if (!this.personServices || this.personServices.length === 0) {
          this.compiarPersona = false;
          this.detailMessageError = 'Esta persona no tiene servicios';
          return;
        }
        this.gridMessag = 'La persona activa tiene servicios asignados. Se borrarán para añadir los de la persona seleccionada. ¿Quiere seguir?';
        this.compiarPersona = true;
      },
      error: (err) => {
        this.detailMessageError = err?.error || 'Error al obtener servicios';
        this.compiarPersona = false;
      }
    });
  }

  closeCopiar() {
    this.compiarPersona = false;
    this.personServices = [];
  }

  compiarPersonaGrid: boolean = false;
  showCopiarPersonas() {
    this.fetchPersonasForCopy();
    this.compiarPersonaGrid = true;
    this.closeCopiar()
  }
  closeShowCopiarPersonas() {
    this.compiarPersonaGrid = false;
  }

  pageCopy: number = 0;
  pesonasCopy: any[] = [];
  backupPesonasCopy: any[] = [];
  errorCopy: string = '';
  fetchPersonasForCopy() {
    this.http.get<any>(`${environment.backendUrl}/api/Per/fetch-all`).subscribe({
      next: (res) => {
        this.pesonasCopy = res;
        this.backupPesonasCopy = [...this.pesonasCopy]
        this.pageCopy = 0;
      },
      error: (err) => {
        this.errorCopy = err?.error?.error || 'Error desconocido';
      }
    });
  }

  get paginatedPersonasCopy(): any[] {
    if (!this.pesonasCopy || this.pesonasCopy.length === 0) return [];
    const start = this.pageCopy * this.pageSize;
    return this.pesonasCopy.slice(start, start + this.pageSize);
  }
  get totalPagesCopy(): number {
    return Math.max(1, Math.ceil((this.pesonasCopy?.length ?? 0) / this.pageSize));
  }
  prevPageCopy(): void {
    if (this.pageCopy > 0) this.pageCopy--;
  }
  nextPageCopy(): void {
    if (this.pageCopy < this.totalPages - 1) this.pageCopy++;
  }
  goToPageCopy(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.pageCopy = inputPage - 1;
    }
  }

  searchPersonasCopy: string = '';
  searchCopy() {
    if(this.searchPersonasCopy === '') {
      this.errorCopy = 'Ingrese algo para buscar';
      return 
    }

    if(this.searchPersonasCopy.length <= 20) {
      this.http.get<any>(`${environment.backendUrl}/api/Per/search-cod-nom/${this.searchPersonasCopy}`).subscribe({
        next: (res) => {
          this.pesonasCopy = res;
          this.backupPesonasCopy = [...this.pesonasCopy]
          this.pageCopy = 0;
          if (!res.length) {
            this.errorCopy = 'No se encontraron servicios con los filtros dados.';
          }
        },
        error: (err) => {
          this.pesonasCopy = [];
          this.backupPesonasCopy = [];
          this.pageCopy = 0;
          this.errorCopy = err?.error || 'Error en la búsqueda.';
        }
      });
    } else {
      this.http.get<any>(`${environment.backendUrl}/api/Per/search-nom/{this.searchPersonasCopy}`).subscribe({
        next: (res) => {
          this.pesonasCopy = res;
          this.backupPesonasCopy = [...this.pesonasCopy]
          this.pageCopy = 0;
          if (!res.length) {
            this.errorCopy = 'No se encontraron servicios con los filtros dados.';
          }
        },
        error: (err) => {
          this.pesonasCopy = [];
          this.backupPesonasCopy = [];
          this.pageCopy = 0;
          this.errorCopy = err?.error || 'Error en la búsqueda.';
        }
      });
    }
  }

  limpiarSearcCopy() {
    this.searchPersonasCopy = '';
    this.pesonasCopy = [...this.backupPesonasCopy]
  }

  continueAdCheckGrid: boolean = false;
  continueMessag: string = '';
  isAvailable: boolean = false;
  showcontinueAdCheckGrid(info: any) {
    this.fetchPersonasForCopy();
    this.continueAdCheckGrid = true;
    this.closeShowCopiarPersonas()

    const percod = info.percod;
    if(percod === this.selectedPersona.percod) {
      this.isAvailable = true;
      this.continueMessag = 'Ha seleccionado la misma persona que está editando'
      return;
    }

    this.isAvailable = false;
    this.continueMessag = 'Se van a borrar los servicios asignados y añadir los de la persona seleccionada. ¿Quiere seguir?'
  }

  closecontinueAdCheckGrid() {
    this.continueAdCheckGrid = false;
  }

  next() {
    console.log("next")
  }
  //misc
  limpiarMessages() {

  }
}