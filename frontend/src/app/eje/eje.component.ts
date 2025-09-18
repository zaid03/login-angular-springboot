import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

type Ejercicio = number | string;

@Component({
  selector: 'app-eje',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eje.component.html',
  styleUrls: ['./eje.component.css']
})
export class EjeComponent implements OnInit {
  tableData: Ejercicio[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const rawEntidad = sessionStorage.getItem('Entidad');
    console.log('Raw Entidad from sessionStorage:', rawEntidad);
    const entObj = safeParse(rawEntidad);
    console.log('Parsed Entidad object:', entObj);
    const ENTCOD = entObj?.ENTCOD;
    console.log('ENTCOD =', ENTCOD, 'type:', typeof ENTCOD);

    if (!USUCOD || ENTCOD == null) { 
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<Ejercicio[]>(`http://localhost:8080/api/cfg/by-ent/${ENTCOD}`)
      .subscribe({
        next: (response) => {
          if (response?.length > 1) {
            this.tableData = response;
          } else if (response?.length === 1) {
            sessionStorage.setItem('selected_ejercicio', JSON.stringify({ eje: response[0] }));
            this.router.navigate(['/centro-gestor']);
          } else {
            sessionStorage.clear();
            alert('No hay ejercicios activos (use la aplicación de escritorio para solucionarlo).');
            this.router.navigate(['/login']);
          }
        },
        error: (err) => {
          console.error('Server error:', err);
          alert('Error al cargar ejercicios.');
        }
      });
  }

  selectRow(item: Ejercicio): void {
    sessionStorage.setItem('selected_ejercicio', JSON.stringify({ eje: item }));
    this.router.navigate(['/centro-gestor']);
  }

  cancelar(): void {
    sessionStorage.clear();
    this.router.navigate(['/']);
  }
}

function safeParse(json: string | null): any {
  if (!json) return null;
  try { return JSON.parse(json); } catch { return null; }
}