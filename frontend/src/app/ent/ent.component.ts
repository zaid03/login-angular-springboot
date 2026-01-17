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

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void{
    const USUCOD = sessionStorage.getItem('USUCOD');
    if (!USUCOD) {
      alert('no hay sesión');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`${environment.backendUrl}/api/pua/filter/${USUCOD}`).subscribe({
      next: (res) => {
        if (Array.isArray(res) && res.length > 1) {
          this.tableData = res;            
        } else if (Array.isArray(res) && res.length === 1) {
          const row = res[0];
          this.tableData = [row];
          this.selectRow(row);
        } else {
          alert('No se encontraron entidades');
          sessionStorage.clear();
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        alert(err?.error.error);
        sessionStorage.clear();
        this.router.navigate(['/']);
      }
    });
  }
  
  selectRow(t: any): void {
    sessionStorage.setItem('Entidad', JSON.stringify({ ENTCOD: t.entcod }));
    sessionStorage.setItem('Perfil', JSON.stringify({ PERCOD: t.percod }));
    this.router.navigate(['/eje']);
  }
}