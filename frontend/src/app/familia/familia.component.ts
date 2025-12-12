import { Component, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-familia',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './familia.component.html',
  styleUrls: ['./familia.component.css']
})
export class FamiliaComponent {
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
  familias: any[] = [];
  private backupFamilias: any[] = [];
  tableMessage: string = '';
  page = 0;
  pageSize = 20;

  private defaultFamilias: any[] = [];
  sortField: 'afacod' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  ngOnInit():void {
    const entidad = sessionStorage.getItem('Entidad');
    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }

    if (!entidad || this.entcod === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/afa/by-ent/${this.entcod}`).subscribe({
      next: (response) => {
        if (response.error) {
          this.tableMessage = 'Error: ' + response.error || 'Server error';
        } else {
          this.familias = Array.isArray(response) ? [...response] : [];
          this.backupFamilias = [...this.familias];
          this.defaultFamilias = [...this.familias];
          this.page = 0;
        }
      },error: (err) => {
        this.tableMessage = 'Server error';
      }
    })
  }

  toggleSort(field: 'afacod'): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDirection = 'asc';
      this.defaultFamilias = [...this.familias];
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortField = null;
      this.sortDirection = 'asc';
      this.familias = [...this.defaultFamilias];
      this.page = 0;
      return;
    }

    this.applySort();
  }

  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const base = [...this.defaultFamilias];
    const sorted = base.sort((a, b) => {
      const aVal = (a.afacod ?? '').toString().toUpperCase();
      const bVal = (b.afacod ?? '').toString().toUpperCase();
      return this.sortDirection === 'asc'
        ? aVal.localeCompare(bVal)
        : bVal.localeCompare(aVal);
    });

    this.familias = sorted;
    this.page = 0;
  }
  
  get paginatedFamilias(): any[] {
    if (!this.familias || this.familias.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.familias.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.familias?.length ?? 0) / this.pageSize));
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

  selectedFamilias: any = null;
  showDetails(familia: any) {
    this.tableMessage = '';
    this.familiaMessageError = '';
    this.selectedFamilias = familia;
    this.FetchSubfamilias(this.selectedFamilias.afacod);
  }

  closeDetails() {
    this.selectedFamilias = null;
    this.familiaErrorMessage = '';
    this.familiaSucessMessage = '';
    this.successUpdateMEssage = '';
    this.errorUpdateMessage = '';
  }

  familiaMessageError: string = '';
  public searchTerm: string = '';
  searchFamilias(): void {
    this.familiaMessageError = '';
    const term = this.searchTerm.trim();

    if (!term) {
      this.familiaMessageError = 'Introduzca una familia para buscar'
      this.familias = [...this.backupFamilias];
      this.defaultFamilias = [...this.familias];
      this.sortField = null;
      this.sortDirection = 'asc';
      this.page = 0;
      return;
    }

    const numbersOnly = /^\d+$/
    if (numbersOnly.test(term)) {
      if (term.length <= 5) {
        this.familias = this.backupFamilias.filter(
          (f) => f.afacod?.toString() === term
        );
      } else {
        const lower = term.toLowerCase();
        this.familias = this.backupFamilias.filter((f) =>
          f.afades?.toString().toLowerCase().includes(lower)
        );
      }
    } else {
      const lower = term.toLowerCase();
      this.familias = this.backupFamilias.filter((f) =>
        f.afades?.toString().toLowerCase().includes(lower)
      );
    }

    if (this.familias.length === 0) {
      this.familiaMessageError = 'Este familia o subfamilia no existe';
    }
    
    this.defaultFamilias = [...this.familias];
    this.sortField = null;
    this.sortDirection = 'asc';
    this.page = 0;
  }

  excelDownload() {
    const rows = this.backupFamilias.length ? this.backupFamilias : this.familias;
    if (!rows || rows.length === 0) {
      this.tableMessage = 'No hay datos para exportar.';
      return;
    }

    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Entidad: row.ent ?? '',
      Familia: row.afacod ?? '',
      Descripción: row.afades ?? '',
    }));

    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['Listado de familias']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Entidad', 'Familia', 'Descripción']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 6 },
      { wch: 12 },
      { wch: 12 },
      { wch: 40 },
    ];

    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Familias');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'familias.xlsx'
    );
  }

  toPrint() {
    const rows = this.backupFamilias.length ? this.backupFamilias : this.familias;
    if (!rows?.length) {
      this.tableMessage = 'No hay datos para imprimir.';
      return;
    }

    const htmlRows = rows.map((row, index) => `
      <tr>
        <td>${index + 1}</td>
        <td>${row.ent ?? ''}</td>
        <td>${row.afacod ?? ''}</td>
        <td>${row.afades ?? ''}</td>
      </tr>
    `).join('');

    const printWindow = window.open('', '_blank', 'width=900,height=700');
    if (!printWindow) return;

    printWindow.document.write(`
      <html>
        <head>
          <title>Listado de familias</title>
          <style>
            body { font-family: 'Poppins', sans-serif; padding: 24px; }
            h1 { text-align: center; margin-bottom: 16px; }
            table { width: 100%; border-collapse: collapse; }
            th, td { border: 1px solid #ccc; padding: 8px 12px; text-align: left; }
            th { background: #f3f4f6; }
          </style>
        </head>
        <body>
          <h1>Listado de familias</h1>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Entidad</th>
                <th>Familia</th>
                <th>Descripción</th>
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

  afacodError:string = '';
  subfamilias: any[] = [];
  mta:any[] = [];
  FetchSubfamilias(afacod: String) {
    if (!afacod) {
      this.afacodError = 'Familia requerido'
    }

    this.http.get<any>(`${environment.backendUrl}/api/asu/by-ent-afacod/${this.entcod}/${afacod}`).subscribe({
      next: (Response) => {
        if(Response.length === 0) {
          this.afacodError = 'No se encontraron Subfamilias'
        } else {
          this.subfamilias = Array.isArray(Response) ? [...Response] : [];
          this.subfamilias.forEach((item, idx) => {
            this.http.get<any[]>(`${environment.backendUrl}/api/mta/all-mta/${this.entcod}`).subscribe({
              next: (mtas) => {
                const almacenajeArray = Array.isArray(mtas) ? mtas : [];
                this.subfamilias[idx].mta = almacenajeArray
                const mtaDescription = almacenajeArray[0]?.mtades ?? '';
                this.subfamilias[idx].mtades = mtaDescription;
              }, error: () => {
                this.subfamilias[idx].mtas = [];
              },
            })
          })
        }
      }, error: (e) => {
        this.afacodError = 'Server Error';
      }
    })
  }

  updateMtades(subfamilia: any, value: string): void {
    if (subfamilia.mta?.length) {
      subfamilia.mta[0].mtades = value;
    }
  }

  onMtaChange(subfamilia: any, selectedMtacod: number): void {
    subfamilia.mtacod = selectedMtacod;
    const match = (subfamilia.mta ?? []).find((m: any) => m.mtacod === selectedMtacod);
    if (match) {
      subfamilia.mtades = match.mtades;
    }
  }

  familiaErrorMessage:string = '';
  familiaSucessMessage: string = '';
  updateFamilia(afacod: string, afades: string): void {
    if (!afacod) { return; }
    const payload = (afades ?? '').trim();

    this.http.patch<any>(`${environment.backendUrl}/api/afa/update-familia/${this.entcod}/${afacod}`, payload, { headers: { 'Content-Type': 'text/plain' }, observe: 'response' }).subscribe({
      next: (response) => {
        this.familiaErrorMessage = '';
        this.familiaSucessMessage = 'Familia actualizada con éxito';
      }, error: (err) => {
        const message = err?.error ?? 'Error al actualizar la familia.';
        this.familiaSucessMessage = '';
        this.familiaErrorMessage = message;
      }
    })
  }

  showDeleteConfirm: boolean = false;
  familiaToDelete: any = null;
  openDeleteConfirm(afacod: string) {
    this.familiaToDelete = afacod;
    this.showDeleteConfirm = true;
  }

  closeDeleteConfirm() {
    this.familiaToDelete = null;
    this.showDeleteConfirm = false;
  }

  confirmDelete(): void {
    if (this.familiaToDelete) {
      this.deleteFamilia(this.familiaToDelete);
      this.closeDeleteConfirm();
    }
  }

  deleteFamilia(afacod: string): void {
    this.http.delete(`${environment.backendUrl}/api/art/delete-familia/${this.entcod}/${afacod}`).subscribe({
      next: () => {
        this.familiaSucessMessage = 'Familia eliminado correctamente';
        this.familias = this.familias.filter(f => f.afacod !== afacod);
        this.backupFamilias = this.backupFamilias.filter(f => f.afacod !== afacod);
        this.closeDetails();
      }, error: (err) => {
        this.familiaErrorMessage = err?.error ?? 'Error al eliminar la familia.';
      }
    })
  }

  showAddConfirm: boolean = false;
  launchAddFamilia() {
    this.showAddConfirm = true;
  }

  familiaMessage: string = '';
  familiaAddError: string = '';
  addFamilia(familia: string, descripcion: string): void {
    this.familiaAddError = '';

    if (!familia || !descripcion) { 
      this.familiaAddError = 'Se requiere descripción de la familia';
      return;
    }

    const ent = this.entcod;
    const payload = { ent, afacod: familia, afades: descripcion };
    this.http.post<any>(`${environment.backendUrl}/api/afa/Insert-familia`, payload).subscribe({
      next: () => {
        this.familiaMessage = 'familia agregada exitosamente'
        this.closeAddConfirm();
      }, error: (err) => {
        this.familiaAddError = err?.error ?? 'Se ha producido un error.';
      }
    });
  }

  closeAddConfirm() {
    this.showAddConfirm = false;
    this.familiaAddError = '';
  }

  errorUpdateMessage: string = '';
  successUpdateMEssage: string = '';
  updateSubfamilia(afacod:string, asucod: string, Description:string, Economica: string, almacenaje: any) {
    const almacenajeNum = Number(almacenaje);
    if (!afacod || !asucod || !Description || !Economica || !almacenajeNum) {
      this.errorUpdateMessage = 'datos faltantes';
    }

    const payload = { ASUDES: Description, ASUECO: Economica, MTACOD: almacenajeNum}

    this.http.patch<any>(`${environment.backendUrl}/api/asu/update-subfamilia/${this.entcod}/${afacod}/${asucod}`, payload).subscribe({
      next: (res) => {
        this.successUpdateMEssage = 'Subfamilia actualizado con éxito';
      }, error: (err) => {
        this.errorUpdateMessage = 'Server error';
      }
    })
  }

  deleteSubfamilia(afacod:string, asucod: string) {
    if (!afacod || !asucod) {
      this.errorUpdateMessage = 'datos faltantes';
    }

    this.http.delete<any>(`${environment.backendUrl}/api/art/delete-sub-familia/${this.entcod}/${afacod}/${asucod}`).subscribe({
      next: (res) => {
        this.successUpdateMEssage = 'Subfamilia eliminado exitosamente'
      }, error: (err) => {
        this.errorUpdateMessage = err?.error ?? 'Se ha producido un error.';
      }
    })
  }

  showAddConfirmSub: boolean = false;
  launchAddSubFamilia() {
    if (!this.almacenajes.length) {
      this.fetchAlmacenaje();
    }
    this.showAddConfirmSub = true;
  }

  closeAddConfirmSub() {
    this.showAddConfirmSub = false;
    this.subAddError = '';
  }

  subAddError: string = '';
  addSub(asucod: string, asudes: string, asueco: string, mtacod: string): void {
    this.subAddError = '';
    this.errorUpdateMessage = '';
    this.successUpdateMEssage = '';
    this.launchAddSubFamilia();

    this.fetchAlmacenaje()

    if (!this.selectedFamilias) {
      this.subAddError = 'Seleccione una familia antes de añadir subfamilias.';
      return;
    }

    const trimmedAsucod = (asucod ?? '').trim().toUpperCase();
    const trimmedAsudes = (asudes ?? '').trim();
    const trimmedAsueco = (asueco ?? '').trim();
    const almacenajeNum = Number(mtacod);

    if (!trimmedAsucod || !trimmedAsudes || !trimmedAsueco || Number.isNaN(almacenajeNum)) {
      this.subAddError = 'Complete todos los campos correctamente.';
      return;
    }

    const payload = {
      ent: this.entcod,
      afacod: this.selectedFamilias.afacod,
      asucod: trimmedAsucod,
      asudes: trimmedAsudes,
      asueco: trimmedAsueco,
      mtacod: almacenajeNum
    };

    this.http.post(`${environment.backendUrl}/api/asu/Insert-Subfamilia`, payload).subscribe({
      next: () => {
        this.successUpdateMEssage = 'Subfamilia añadida correctamente.';
        this.closeAddConfirmSub();
        this.FetchSubfamilias(this.selectedFamilias.afacod);
      },
      error: (err) => {
        this.subAddError = err?.error ?? 'Se ha producido un error al añadir la subfamilia.';
      }
    });
  }

  forceUppercase(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.toUpperCase();
  }

  allowOnlyDigits(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/\D+/g, '');
  }

  almacenajes: any[] = [];
  fetchAlmacenaje() {
    this.http.get<any[]>(`${environment.backendUrl}/api/mta/all-mta/${this.entcod}`).subscribe({
      next: (mtas) => {
        this.almacenajes = Array.isArray(mtas) ? mtas : [];
      }, error: () => {
        this.almacenajes = [];
      },
    });
  }
}
