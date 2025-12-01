import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-familia',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './familia.component.html',
  styleUrls: ['./familia.component.css']
})
export class FamiliaComponent {
  constructor(private http: HttpClient, private router: Router) {}
  
  private entcod: number | null = null;
  familias: any[] = [];
  private backupFamilias: any[] = [];
  tableMessage: string = '';
  page = 0;
  pageSize = 20;

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

    this.http.get<any>(`http://localhost:8080/api/afa/by-ent/${this.entcod}`).subscribe({
      next: (response) => {
        if (response.error) {
          this.tableMessage = 'Error: ' + response.error || 'Server error';
        } else {
          this.familias = Array.isArray(response) ? [...response] : [];
          this.backupFamilias = [...this.familias];
          this.page = 0;
        }
      },error: (err) => {
        this.tableMessage = 'Server error';
      }
    })
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
    console.log(this.selectedFamilias);
  }

  closeDetails() {
    this.selectedFamilias = null;
  }

  familiaMessageError: string = '';
  public searchTerm: string = '';
  searchFamilias(): void {
    this.familiaMessageError = '';
    const search = this.searchTerm.trim();

    if (!search) {
      this.familiaMessageError = 'Introduzca una familia para buscar'
      this.familias = [...this.backupFamilias];
      this.page = 0;
      return;
    }

    const mixed = /^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$/
    const numsOnly = /^\d+$/
    const charsOnly = /^[a-zA-Z]+$/

    if(mixed.test(search)) {
      this.familiaMessageError = 'Este familia o subfamilia no existe'
      return;
    }

    if (numsOnly.test(search)) {
      this.familias = this.backupFamilias.filter(f =>
        f.afacod?.toString().toLowerCase().includes(search.toLowerCase())
      );
    } else if (charsOnly.test(search)) {
      this.familias = this.backupFamilias.filter(f =>
        f.afades?.toString().toLowerCase().includes(search.toLowerCase())
      );
    } else {
      this.familias = [];
    }

    if (this.familias.length === 0) {
      this.familiaMessageError = 'Este familia o subfamilia no existe';
    }
    
    this.page = 0;
  }
}
