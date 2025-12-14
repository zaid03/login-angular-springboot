import { Component, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, provideCloudinaryLoader } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { environment } from '../../environments/environment';
@Component({
  selector: 'app-cge',
  standalone: true,
  imports: [CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './cge.component.html',
  styleUrls: ['./cge.component.css']
})
export class CgeComponent {
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
  public cge: string = '';
  centroGestores: any[] = [];
  private backupCentroGestores: any[] = [];
  private defaultCentroGestores: any[] = [];
  sortField: 'cgecod' | 'cgedes' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  page = 0;
  pageSize = 20;

  ngOnInit() {
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');

    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }

    if (eje) {
      const parsed = JSON.parse(eje);
      this.eje = parsed.eje;
    }

    if (cge) {
      const parsed = JSON.parse(cge);
      this.cge = parsed.value;
    }

    if (!entidad || this.entcod === null || this.eje === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/cge/fetch-all/${this.entcod}/${this.eje}`).subscribe({
      next: (res) => {
        this.centroGestores = Array.isArray(res) ? [...res] : [];
        this.backupCentroGestores = [...this.centroGestores];
        this.defaultCentroGestores = [...this.centroGestores];
        this.page = 0;
        this.updatePagination();
        if ( res.status === 404 ) {
          this.centroGestores = [];
          this.SearchDownMessageError = typeof res.body === 'string' ? res.body : 'sin resultados.';
        }
      }, error: (err) => {
        this.centroGestores = [];
        this.SearchDownMessageError = typeof err.error === 'string' ? err.error : 'server Error';
      }
    })
  }

  get paginatedFamilias(): any[] {
    if (!this.centroGestores || this.centroGestores.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.centroGestores.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.centroGestores?.length ?? 0) / this.pageSize));
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

  getkCGECIC(cgecic: number) {
    if (cgecic === 0) {
      return 'No';
    } else if (cgecic === 1) {
      return 'Sí';
    } else if (cgecic === 2) {
      return 'Cierre para contabilizar';
    }
    return;
  }

  setCgecic(value: number): void {
    if (!this.selectedCentroGestor) {
      return;
    }
    this.selectedCentroGestor.cgecic = value;
  }

  isCgecic(value: number): boolean {
    return (this.selectedCentroGestor?.cgecic ?? null) === value;
  }

  SearchDownMessageError: string = '';
  public searchTerm: string = '';
  handleSearchInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = (input.value ?? '').toUpperCase();
    this.searchTerm = value;
    input.value = value;

    if (!value) {
      this.SearchDownMessageError = '';
      this.centroGestores = [...this.backupCentroGestores];
      this.page = 0;
    }
  }

  searchCentroGestor(): void {
    this.SearchDownMessageError = '';
    const term = this.searchTerm.trim();

    if (!term) {
      this.SearchDownMessageError = 'Introduzca una centro gestor para buscar'
      this.centroGestores = [...this.backupCentroGestores];
      this.page = 0;
      return;
    }

    const oneToFour = /^[A-Za-z0-9]{1,4}$/
    const moreThanFour = /^[A-Za-z0-9]{5,}$/
    if (oneToFour.test(term)) {
      this.centroGestores = this.backupCentroGestores.filter((f) =>
        f.cgecod?.toString().toUpperCase() === term
      );
    } else if (moreThanFour.test(term)) {
      this.centroGestores = this.backupCentroGestores.filter((f) =>
        f.cgedes?.toString().toUpperCase().includes(term)
      );
    }

    if (this.centroGestores.length === 0) {
      this.SearchDownMessageError = 'Este Centro Gestor no existe';
    }
    
    this.defaultCentroGestores = [...this.centroGestores];
    this.sortField = null;
    this.sortDirection = 'asc';
    this.page = 0;
    this.updatePagination();
  }

  toggleSort(field: 'cgecod' | 'cgedes'): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDirection = 'asc';
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortField = null;
      this.sortDirection = 'asc';
      this.centroGestores = [...this.defaultCentroGestores];
      this.page = 0;
      this.updatePagination();
      return;
    }

    this.applySort();
  }

  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const base = [...this.defaultCentroGestores];
    const sorted = base.sort((a, b) => {
      const aVal = (this.sortField === 'cgecod'
        ? (a.cgecod ?? '').toString()
        : (a.cgedes ?? '').toString()
      ).toUpperCase();

      const bVal = (this.sortField === 'cgecod'
        ? (b.cgecod ?? '').toString()
        : (b.cgedes ?? '').toString()
      ).toUpperCase();

      return this.sortDirection === 'asc'
        ? aVal.localeCompare(bVal, 'es')
        : bVal.localeCompare(aVal, 'es');
    });

    this.centroGestores = sorted;
    this.page = 0;
    this.updatePagination();
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

  excelDownload() {
    const rows = this.backupCentroGestores.length ? this.backupCentroGestores : this.centroGestores;
    if (!rows || rows.length === 0) {
      this.SearchDownMessageError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Entidad: row.ent ?? '',
      EJE: row.eje ?? '',
      Código: row.cgecod ?? '',
      Descripción: row.cgedes ?? '',
      Orgánica: row.cgeorg ?? '',
      Programa: row.cgefun ?? '',
      Cierre_contable: this.getkCGECIC(row.cgecic) ?? ''
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['Listado de Centros de gestor']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Entidad', 'EJE', 'Código', 'Descripción', 'Orgánica', 'Programa', 'Cierre_contable']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 6 },
      { wch: 12 },
      { wch: 12 },
      { wch: 12 },
      { wch: 55 },
      { wch: 12 },
      { wch: 12 },
      { wch: 16 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Centros_gestor');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'Centros_gestor.xlsx'
    );
  }

  toPrint() {
    const rows = this.backupCentroGestores.length ? this.backupCentroGestores : this.centroGestores;
    if (!rows?.length) {
      this.SearchDownMessageError = 'No hay datos para imprimir.';
      return;
    }

    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${row.ent ?? ''}</td>
        <td>${row.eje ?? ''}</td>
        <td>${row.cgecod ?? ''}</td>
        <td>${row.cgedes ?? ''}</td>
        <td>${row.cgeorg ?? ''}</td>
        <td>${row.cgefun ?? ''}</td>
        <td>${this.getkCGECIC(row.cgecic) ?? ''}</td>
      </tr>
    `).join('');

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) return;

    printWindow.document.write(`
      <html>
        <head>
          <title>Listado de Centros de gestor</title>
          <style>
            body { font-family: 'Poppins', sans-serif; padding: 24px; }
            h1 { text-align: center; margin-bottom: 16px; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
            th { background: #f3f4f6; }
          </style>
        </head>
        <body>
          <h1>Listado de Centros de gestor</h1>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Entidad</th>
                <th>eje</th>
                <th>cgecod</th>
                <th>cgecod</th>
                <th>Orgánica</th>
                <th>Programa</th>
                <th>Cierre contable</th>
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

  selectedCentroGestor: any = null;
  centroGestorSuccessMessage: String = '';
  centroGestorErrorMessage: string = '';
  showDetails(centroGestor: any) {
    this.selectedCentroGestor = centroGestor;
  }

  closeDetails() {
    this.selectedCentroGestor = null;
    this.centroGestorSuccessMessage = '';
    this.centroGestorErrorMessage = '';
  }

  updateCentroGestor(cge: string, des: string, org: string, fun: string, dat: string) {
    this.centroGestorSuccessMessage = '';
    this.centroGestorErrorMessage = '';
    const payload = {
      cgedes: des,
      cgeorg: org,
      cgefun: fun,
      cgedat: dat,
      cgecic: this.selectedCentroGestor.cgecic
    }

    if (!org || !fun) {
      this.centroGestorErrorMessage = 'Se requieren organica y programa';
      return;
    }

    this.http.patch<any>(`${environment.backendUrl}/api/cge/update-familia/${this.entcod}/${this.eje}/${cge}`, payload).subscribe({
      next: (res) => {
        this.centroGestorErrorMessage = '';
        this.centroGestorSuccessMessage = 'Centro gestor actualizado con éxito';
      }, 
      error: (err) => {
        const message = err?.error ?? 'Error al actualizar la centro gestor.';
        this.centroGestorSuccessMessage = '';
        this.centroGestorErrorMessage = message;
      }
    })
  }

  allowOnlyDigits(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    target.value = target.value.replace(/[^0-9]/g, '');
    if (this.selectedCentroGestor) {
      this.selectedCentroGestor.cgefun = target.value;
    }
  }

  showAddConfirm: boolean = false;
  centroGestorAddError: string = '';
  launchAddCentroGestor() {
    this.showAddConfirm = true;
  }

  closeAddConfirm() {
    this.showAddConfirm = false;
    this.centroGestorAddError = '';
  }

  newCgecic = 0;
  setAddCgecic(value: number): void {
    this.newCgecic = value;
  }
  newCge = '';
  setCgeToUpper(event: Event): void {
    const target = event.target as HTMLTextAreaElement;
    const upper = (target.value ?? '').toUpperCase();
    target.value = upper;
    this.newCge = upper;
  }

  successAddCentroGestor = '';
  AddCentroGestor(cod: string, des: string, org: string, fun: string, dat: string) {
    this.centroGestorAddError = '';
    this.successAddCentroGestor = '';

    if (!cod || !des) {
      this.centroGestorAddError = 'Se requieren centrgo gestor y descripción'
      return;
    }
    const payload = {
      "ent" : this.entcod,
      "eje" : this.eje,
      "cgecod" : this.newCge,
      "cgedes" : des,
      "cgeorg" : org,
      "cgefun" : fun,
      "cgedat" : dat,
      "cgecic" : this.newCgecic
    }

    this.http.post<any>(`${environment.backendUrl}/api/cge/Insert-familia`,payload).subscribe({
      next: (res) => {
        this.successAddCentroGestor = 'centro gestor añadido con éxito'
        this.closeAddConfirm();
      },
      error: (err) => {
        this.centroGestorAddError = err?.error ?? 'Se ha producido un error.';
      }
    })
  }

  showDeleteConfirm: boolean = false;
  centroGestorToDelete: any = null;
  openDeleteConfirm(cgecod: string) {
    this.centroGestorToDelete = cgecod;
    this.showDeleteConfirm = true;
  }

  closeDeleteConfirm() {
    this.centroGestorToDelete = null;
    this.showDeleteConfirm = false;
  }

  confirmDelete(): void {
    if (this.centroGestorToDelete) {
      this.deleteCentroGestor(this.centroGestorToDelete);
      this.closeDeleteConfirm();
    }
  }

  deleteCentroGestor(cgecod: string) {
    this.http.delete<any>(`${environment.backendUrl}/api/cge/delete-centro-gestor/${this.entcod}/${this.eje}/${cgecod}`).subscribe({
      next: (res) => {
        this.successAddCentroGestor = 'cge eliminado exitosamente';
        this.centroGestores = this.centroGestores.filter(c => c.cgecod !== cgecod);
        this.backupCentroGestores = this.backupCentroGestores.filter(c => c.cgecod !== cgecod);
        this.closeDetails();
      }, error: (err) => {
        this.centroGestorErrorMessage = err?.error ?? 'Error al eliminar la familia.';
      }
    })
  }
}
