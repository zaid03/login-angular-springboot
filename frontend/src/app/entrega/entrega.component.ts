import { Component, HostListener} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-entrega',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './entrega.component.html',
  styleUrls: ['./entrega.component.css']
})
export class EntregaComponent {

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

  //main table functions
  isLoading: boolean = false;
  entregas: any[] = [];
  private backupentregas: any[] = [];
  page = 0;
  pageSize = 20;
  entregasError: string = '';
  ngOnInit(): void{
    this.emptyAllMessages();
    this.isLoading = true;
    this.http.get<any[]>(`${environment.backendUrl}/api/Len/fetch-all`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.entregas = res;
        this.backupentregas = [...this.entregas];
        if(this.entregas.length === 0) {
          this.entregasError = 'no hay lugares de entrega';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.entregasError = err.error.error;
      }
    })
  }

  get paginatedEntregas(): any[] {
    if (!this.entregas || this.entregas.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.entregas.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.entregas?.length ?? 0) / this.pageSize));
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

  sortField: 'lendes' | 'lencod' | null = null;
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  private defaultProveedores: any[] = [];
  toggleSort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
    this.page = 0;
    this.updatePagination();
  }

  private applySort(): void {
    if (!this.sortColumn) return;
    this.entregas.sort((a, b) => {
      let aValue: any;
      let bValue: any;

      const aNum = Number(aValue);
      const bNum = Number(bValue);
      if (!isNaN(aNum) && !isNaN(bNum)) {
        return this.sortDirection === 'asc' ? aNum - bNum : bNum - aNum;
      }

      aValue = (aValue ?? '').toString().toUpperCase();
      bValue = (bValue ?? '').toString().toUpperCase();
      if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
      if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
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
    const table = document.querySelector('.entrega-table') as HTMLTableElement;
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

  //detail grid functions
  selectedEntregas: any = null;
  detallesMessageError: String = '';
  detallesMessageSuccess: string = '';
  fettalesIsError: boolean = false;

  showDetails(entrega: any) {
    this.selectedEntregas = entrega;
  }

  closeDetails() {
    this.selectedEntregas = null;
    this.emptyAllMessages();
  }

  updateEntrega(lencod: number, lendes: string, lentxt: string) {
    this.emptyAllMessages();

    if (!lencod || !lendes) {
      this.detallesMessageError = 'Descripción requirido'
      return;
    }
    const payload = {
      "LENDES" : lendes,
      "LENTXT" : lentxt
    }

    this.http.patch(`${environment.backendUrl}/api/Len/update-lugar/${lencod}`, payload).subscribe({
      next: (res) => {
        this.detallesMessageSuccess = 'Lugares de entrega actualizada exitosamente'
        this.emptyAllMessages();
      },
      error: (err) => {
        this.detallesMessageError = err.error || 'server error';
        this.emptyAllMessages();
      }
    })
  }

  detallesMessageErrorDelete: string = '';
  entregaSuccess: string = '';
  deleteEntrega(lencod: number) {
    this.emptyAllMessages();
    if (!lencod) {
      return;
    }

    this.http.delete(`${environment.backendUrl}/api/Len/delete-lugar/${lencod}`).subscribe({
      next: (res) => {
        this.entregaSuccess = 'Lugar de entrega eliminado exitosamente'
        this.entregas = this.entregas.filter((e: any) => e.lencod !== lencod);
        this.closeDeleteConfirm();
        this.closeDetails();
      },
      error: (err) => {
        this.detallesMessageErrorDelete = err.error || 'server error';
        this.emptyAllMessages();
      }
    })
  }

  showDeleteConfirm = false;
  entregaToDelete: any = null;
  openDeleteConfirm(entrega: any) {
    this.entregaToDelete = entrega;
    this.showDeleteConfirm = true;
  }

  closeDeleteConfirm() {
    this.showDeleteConfirm = false;
    this.entregaToDelete = null;
  }

  confirmDelete() {
    if (this.entregaToDelete) {
      this.deleteEntrega(this.entregaToDelete.lencod);
    }
  }

  //adding entrega grid functions
  showAddGrid = false;
  personaInfo: any = [];
  showAdd() {
    this.showAddGrid = true;
    this.personaInfo = this.selectedEntregas;
  }

  hideAdd() {
    this.showAddGrid = false;
  }

  errorAddEntrega: string = '';
  
  //misc
  emptyAllMessages() {
    const timeoutId = setTimeout(() => {
      this.detallesMessageErrorDelete = '';
      this.entregasError = '';
      this.detallesMessageError = '';
      this.detallesMessageSuccess = '';
      this.entregaSuccess = '';
    }, 2000);
  }
}
