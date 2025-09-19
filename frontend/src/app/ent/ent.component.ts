import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

interface PuaRow {
  USUCOD: string;
  APLCOD: number;
  ENTCOD: number;
  PERCOD: string;
  ENTNOM: string;
}

@Component({
  selector: 'app-ent',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ent.component.html',
  styleUrls: ['./ent.component.css']
})
export class EntComponent implements OnInit {
  tableData: PuaRow[] = [];
  loading = false;
  errorMsg = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    if (!USUCOD) {
      alert('No session. Login.');
      this.router.navigate(['/login']);
      return;
    }
    const raw = sessionStorage.getItem('puaData');
    if (!raw) {
      this.errorMsg = 'No data (puaData) in session.';
      return;
    }
    try {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        this.tableData = parsed;
      } else {
        this.errorMsg = 'Invalid puaData format.';
      }
    } catch {
      this.errorMsg = 'Corrupt puaData JSON.';
    }
  }

  selectRow(row: PuaRow): void {
    if (!row || row.ENTCOD == null || !row.PERCOD) {
      alert('Row missing ENTCOD/PERCOD');
      return;
    }
    sessionStorage.setItem('Entidad', JSON.stringify({ ENTCOD: row.ENTCOD }));
    sessionStorage.setItem('Perfil', JSON.stringify({ PERCOD: row.PERCOD }));

    this.loading = true;
    this.http.get<any[]>('http://localhost:8080/api/mnucods', { params: { PERCOD: row.PERCOD } })
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