import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';
@Component({
  selector: 'app-proveedorees',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './facturas.component.html',
  styleUrls: ['./facturas.component.css']
})
export class FacturasComponent {
  private entcod: number | null = null;
  private eje: number | null = null;
  facturas: any[] = [];
  private backupFacturas: any[] = [];
  page = 0;
  pageSize = 20;
  facturaMessage: String = '';
  facturaIsError: boolean = false;
  public Math = Math;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void{
    this.facturaIsError = false;
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('selected_ejercicio');

    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }
    console.log(this.entcod);
    if (eje) {
      const parsed = JSON.parse(eje);
      this.eje = parsed.eje;
    }
    console.log(this.eje);

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/fac/${this.entcod}/${this.eje}`).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.facturas = response;
          console.log(this.facturas);
          this.backupFacturas = Array.isArray(response) ? [...response] : [];
          this.page = 0;
          console.log('paginatedFacturas sample:', this.paginatedFacturas[0]);
        console.log('raw facturas sample:', this.facturas[0]);
        }
      }, error: (err) => {
        console.error('Facturas fetch error:', err);
        this.facturaIsError = true;
        this.facturaMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        alert(this.facturaMessage);
      }
    });
  }
  

  get paginatedFacturas(): any[] {
    if (!this.facturas || this.facturas.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.facturas.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.facturas?.length ?? 0) / this.pageSize));
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
