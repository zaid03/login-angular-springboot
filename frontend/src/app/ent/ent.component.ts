import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-ent',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ent.component.html',
  styleUrls: ['./ent.component.css']
})
export class EntComponent implements OnInit {
  tableData: any[] = [];
  loading = false;
  errorMsg = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void{
    const USUCOD = sessionStorage.getItem('USUCOD');
    if (!USUCOD) {
      alert('No session. Login.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/filter`, { params: { usucod: USUCOD } }).subscribe({
      next: (filterResponse) => {
        if (filterResponse.error) {
          this.errorMsg = 'server error';
          this.router.navigate(['/login']);
          return;
        } 
        if (Array.isArray(filterResponse) && filterResponse.length > 1) {
          this.tableData = filterResponse;            
        } else if (Array.isArray(filterResponse) && filterResponse.length === 1) {
          const row = filterResponse[0];
          this.tableData = [row];
          this.selectRow(row);
        } else {
          this.errorMsg = 'No data returned for user.';
          sessionStorage.clear();
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.errorMsg = err?.error ?? 'Error loading data.';
      }
    });
  }
  
  selectRow(t: any): void {

    sessionStorage.setItem('Entidad', JSON.stringify({ ENTCOD: t.entcod }));
    sessionStorage.setItem('Perfil', JSON.stringify({ PERCOD: t.percod }));

    this.loading = true;
    this.http.get<any[]>(`${environment.backendUrl}/api/mnucods`, { params: { PERCOD: t.percod } })
      .subscribe({
        next: resp => {
          sessionStorage.setItem('mnucods', JSON.stringify(resp));
          this.router.navigate(['/eje']);
        },
        error: err => {
          console.error('mnucods error status:', err.status, 'body:', err.error);
          if (err.status === 401) {
            alert('Sesión expirada o token inválido. Inicie sesión nuevamente.');
            this.router.navigate(['/login']);
          } else {
            alert('Error loading menus.');
          }
        }
      }).add(() => this.loading = false);
  }

}