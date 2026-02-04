import { Component, HostListener } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';

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

  setBloqueado(conblo: number) {
    if (conblo == 0) {return 'No';}
    else if (conblo == 1) {return 'No';}
    else {return 'no'}
  }


  //misc
  limpiarMessages() {
    this.mainError = '';
    this.mainSuccess = '';
  }
}
