import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './credito.component.html',
  styleUrls: ['./credito.component.css']
})
export class CreditoComponent {
  page = 0;
  pageSize = 20;

  constructor(private http: HttpClient, private router: Router) {}

  // get paginatedFacturas(): any[] {
  //   if (!this.facturas || this.facturas.length === 0) return [];
  //   const start = this.page * this.pageSize;
  //   return this.facturas.slice(start, start + this.pageSize);
  // }
  // get totalPages(): number {
  //   // return Math.max(1, Math.ceil((this.facturas?.length ?? 0) / this.pageSize));
  // }
  // prevPage(): void {
  //   if (this.page > 0) this.page--;
  // }
  // nextPage(): void {
  //   if (this.page < this.totalPages - 1) this.page++;
  // }
  // goToPage(event: any): void {
  //   const inputPage = Number(event.target.value);
  //   if (inputPage >= 1 && inputPage <= this.totalPages) {
  //     this.page = inputPage - 1;
  //   }
  // }
}
