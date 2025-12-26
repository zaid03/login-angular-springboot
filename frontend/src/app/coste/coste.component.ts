import { Component, HostListener} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-entrega',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './coste.component.html',
  styleUrls: ['./coste.component.css']
})
export class CosteComponent {
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

  //main table functions
  isLoading: boolean = false;
  costes: any[] = [];
  private backupCostes: any[] = [];
  page = 0;
  pageSize = 20;
  costeError: string = '';
  ngOnInit(): void{
    this.emptyAllMessages();
    this.isLoading = true;

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

    this.fetchCostes();
  }

  fetchCostes() {
    this.http.get<any[]>(`${environment.backendUrl}/api/cco/fetch-all/${this.entcod}/${this.eje}`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.costes = res;
        this.backupCostes = [...this.costes];
        if(this.costes.length === 0) {
          this.costeError = 'no hay lugares de entrega';
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.costeError = err.error.error;
      }
    })
  }

  get paginatedCostes(): any[] {
    if (!this.costes || this.costes.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.costes.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.costes?.length ?? 0) / this.pageSize));
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

  sortField: 'ccocod' | 'ccodes' | null = null;
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
    this.costes.sort((a, b) => {
      let aValue: any = a[this.sortColumn];
      let bValue: any = b[this.sortColumn];

      if (this.sortColumn === 'lencod') {
        aValue = Number(aValue);
        bValue = Number(bValue);
        return this.sortDirection === 'asc' ? aValue - bValue : bValue - aValue;
      }

      if (this.sortColumn === 'lendes') {
        aValue = (aValue ?? '').toString().toUpperCase();
        bValue = (bValue ?? '').toString().toUpperCase();
        if (aValue < bValue) return this.sortDirection === 'asc' ? -1 : 1;
        if (aValue > bValue) return this.sortDirection === 'asc' ? 1 : -1;
        return 0;
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
    const table = document.querySelector('.coste-table') as HTMLTableElement;
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

  searchText: string = '';
  filterEntrega() {
    this.emptyAllMessages();

    if(!this.searchText) {
      this.costeError = 'Introduzca una centro de coste para buscar';
      return;
    }

    const isNumber = /^[0-9]+$/;
    const isChar = /^[A-Za-z]+$/;
    const isAlphanumeric = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]+$/;

    if(isNumber.test(this.searchText)) {
      this.http.get<any[]>(`${environment.backendUrl}/api/cco/filter-by/${this.entcod}/${this.eje}/${this.searchText}`).subscribe({
        next: (res) => {
          this.costes = res;
        },
        error: (err) => {
          this.costeError = err.error || 'server error';
          this.emptyAllMessages();
        }
      });
    }
    if(isChar.test(this.searchText) || isAlphanumeric.test(this.searchText)) {
      this.http.get<any[]>(`${environment.backendUrl}/api/cco/filter-by-des/${this.entcod}/${this.eje}/${this.searchText}`).subscribe({
        next: (res) => {
          this.costes = res;
        },
        error: (err) => {
          this.costeError = err.error || 'server error';
          this.emptyAllMessages();
        }
      });
    }
  }

  limpiarFiltro() {
    this.costes = [...this.backupCostes];
    this.searchText = '';
    this.costeError = '';
    this.page = 0;
  }

  //misc
  emptyAllMessages() {
    const timeoutId = setTimeout(() => {
      this.costeError = '';
    }, 2000)
  }
}
