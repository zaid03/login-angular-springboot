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
    this.http.get(`${environment.backendUrl}/api/depe/personas-servicios/excel/${this.entcod}/${this.eje}`, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'personas_por_servicios.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.personasServicesError = err.error.error ?? err.error;
      }
    });
  }

  toPrint() {
    this.http.get(`${environment.backendUrl}/api/depe/personas-servicios/pdf/${this.entcod}/${this.eje}`,{ responseType: 'blob' }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'personas_por_servicios.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.personasServicesError = err.error.error ?? err.error;
      }
    });
  }
}
