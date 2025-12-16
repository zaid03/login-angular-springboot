import { Component, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

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
}
