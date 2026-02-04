import { Component, HostListener } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { environment } from '../../environments/environment';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-contratos',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './contratos.component.html',
  styleUrls: ['./contratos.component.css']
})
export class ContratosComponent {

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
  entcod: string | null = null;
  eje: number | null = null;
  isLoading: boolean = false;
  contratos: any[] = [];
  backupContratos: any[] = [];
  defaultContratos: any[] = [];
  mainError: string = '';
  mainSuccess: string = '';
  page = 0;
  pageSize = 20;

  ngOnInit(): void {
    this.limpiarMessages();
    const ent = sessionStorage.getItem('Entidad');
    const session = sessionStorage.getItem('EJERCICIO');

    if (ent) { const parsed = JSON.parse(ent); this.entcod = parsed.ENTCOD;}
    if (session) { const parsed = JSON.parse(session); this.eje = parsed.eje;}

    if (this.entcod == null || !this.eje) {
      alert('Missing session data. reiniciar el flujo.');
      this.router.navigate(['/login']);
      return;
    }

    this.fetchContratos();
  }

  //main functions
  fetchContratos() {
    this.isLoading = true;
    this.http.get<any>(`${environment.backendUrl}/api/con/fetch-contratos/${this.entcod}/${this.eje}`).subscribe({
      next: (res) => {
        this.contratos = res;
        this.backupContratos = Array.isArray(res) ? [...res] : [];
        this.defaultContratos = [...this.backupContratos];
        this.page = 0;
        this.updatePagination();
        this.isLoading = false;
      },
      error: (err) => {
        this.mainError = err.error.error ?? err.error;
        this.isLoading = false;
      }
    })
  }

  get paginatedContratos(): any[] {
    if (!this.contratos || this.contratos.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.contratos.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.contratos?.length ?? 0) / this.pageSize));
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

  toggleSort(field: 'concod' | 'conlot' | 'condes' | 'confin' | 'conffi' | 'conblo' | 'tercod' | 'ternom'): void {
    if (this.sortField !== field) {
      this.sortField = field;
      this.sortDirection = 'asc';
    } else if (this.sortDirection === 'asc') {
      this.sortDirection = 'desc';
    } else {
      this.sortField = null;
      this.sortDirection = 'asc';
      this.contratos = [...this.defaultContratos];
      this.page = 0;
      this.updatePagination();
      return;
    }

    this.applySort();
  }

  sortField: 'concod' | 'conlot' | 'condes' | 'confin' | 'conffi' | 'conblo' | 'tercod' | 'ternom' | null = null;
  sortDirection: 'asc' | 'desc' = 'asc';
  private applySort(): void {
    if (!this.sortField) {
      return;
    }

    const field = this.sortField;
    const collator = new Intl.Collator('es', { numeric: true, sensitivity: 'base' });

    const sorted = [...this.contratos].sort((a, b) => {
      const extract = (item: any, prop: string) =>
        (item?.[prop] ?? item?.[prop.toUpperCase()] ?? '').toString();
      const aVal = extract(a, field);
      const bVal = extract(b, field);
      return this.sortDirection === 'asc'
        ? collator.compare(aVal, bVal)
        : collator.compare(bVal, aVal);
    });

    this.contratos = sorted;
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
    const table = document.querySelector('.contratos-table') as HTMLTableElement;
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

  setBloqueado(conblo: number) {
    if (conblo == 0) {return 'No';}
    else if (conblo == 1) {return 'No';}
    else {return 'no'}
  }

  excelDownload() {
    this.limpiarMessages();
    const rows = this.backupContratos.length ? this.backupContratos : this.contratos;
    if (!rows || rows.length === 0) {
      this.mainError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map((row, index) => ({
      '#': index + 1,
      Número: row.concod ?? '',
      Económica : row.conlot ?? '',
      Descripción: row.condes ?? '',
      Fecha_inicio: row.confin ?? '',
      Fecha_fin: row.conffi ?? '',
      Bloqueado: this.setBloqueado(row.conblo) ?? '',
      Cód_Proveedor: row.tercod ?? '',
      Proveedor: row.ternom ?? ''
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['listas de contratos']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['#', 'Número', 'Económica', 'Descripción', 'Fecha inicio', 'Fecha fin', 'Bloqueado', 'Cód Proveedor', 'Proveedor']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 6 },
      { wch: 10 },
      { wch: 25 },
      { wch: 55 },
      { wch: 25 },
      { wch: 25 },
      { wch: 10 },
      { wch: 10 },
      { wch: 40 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'contratos');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'contratos.xlsx'
    );
  }

  exportPdf() {
    this.limpiarMessages();
    const source = this.backupContratos.length ? this.backupContratos : this.contratos;
    if (!source?.length) {
      this.mainError = 'No hay datos para exportar.';
      return;
    }

    const rows = source.map((row: any, index: number) => ({
      index: index + 1,
      concod: row.concod ?? '',
      conlot: row.conlot ?? '',
      condes: row.condes ?? '',
      confin: (row.confin ?? '').toString().slice(0, 10),
      conffi: (row.conffi ?? '').toString().slice(0, 10),
      conblo: this.setBloqueado(row.conblo) ?? '',
      tercod: row.tercod ?? '',
      ternom: row.ternom ?? ''
    }));

    const doc = new jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(14);
    doc.text('Listado de contratos', 40, 40);

    const columns = [
      { header: '#', dataKey: 'index' },
      { header: 'Número', dataKey: 'concod' },
      { header: 'Económica', dataKey: 'conlot' },
      { header: 'Descripción', dataKey: 'condes' },
      { header: 'Fecha_inicio', dataKey: 'confin' },
      { header: 'Fecha_fin', dataKey: 'conffi' },
      { header: 'Bloqueado', dataKey: 'conblo' },
      { header: 'Cód_Proveedor', dataKey: 'tercod' },
      { header: 'Proveedor', dataKey: 'ternom' }
    ];

    autoTable(doc, {
      startY: 60,
      head: [columns.map(col => col.header)],
      body: rows.map(row => columns.map(col => (row as any)[col.dataKey] ?? '')),
      styles: { font: 'helvetica', fontSize: 10, cellPadding: 6 },
      headStyles: { fillColor: [240, 240, 240], textColor: 33, fontStyle: 'bold' }
    });

    doc.save('contratos.pdf');
  }

  searchInput: string = '';
  searchOption: string = 'noBloque';
  search() {
    this.limpiarMessages();
    this.isLoading = true;

    if (this.isDigitsOnly(this.searchInput)) {
      if (this.searchOption === 'noBloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByCodigoNoBloque/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.updatePagination();
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Bloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByCodigoBloque/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Todos') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByCodigoTodos/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
    }
    else if (!this.isDigitsOnly(this.searchInput)) {
      if (this.searchOption === 'noBloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByDescNoBloque/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Bloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByDescBloque/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Todos') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByDescTodos/${this.entcod}/${this.eje}/${this.searchInput}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
    }
    else if (this.searchInput === '') {
      if (this.searchOption === 'noBloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByNobloq/${this.entcod}/${this.eje}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Bloque') {
        this.http.get<any>(`${environment.backendUrl}/api/con/searchByBloqu/${this.entcod}/${this.eje}`).subscribe({
          next: (res) => {
            this.contratos = res;
            this.updatePagination();
            this.backupContratos = Array.isArray(res) ? [...res] : [];
            this.defaultContratos = [...this.backupContratos];
            this.page = 0;
            this.isLoading = false;
          },
          error: (err) => {
            this.mainError = err.error.error ?? err.error;
            this.isLoading = false;
          }
        })
      }
      else if (this.searchOption === 'Todos') {
        this.fetchContratos();
      }
    }
  }

  limpiarSearch() {
    this.limpiarMessages();
    this.fetchContratos();
  }

  private isDigitsOnly(value: string): boolean {
    if (!value) return false;
    return /^\d+$/.test(value.trim());
  }

  //detail grid

  //misc
  limpiarMessages() {
    this.mainError = '';
    this.mainSuccess = '';
  }
}
