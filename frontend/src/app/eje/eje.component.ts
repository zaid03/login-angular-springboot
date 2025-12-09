import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

type Ejercicio = number | string;

function safeParse(raw: string | null) {
  if (!raw) return {};
  try { return JSON.parse(raw); } catch { return {}; }
}

@Component({
  selector: 'app-eje',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eje.component.html',
  styleUrls: ['./eje.component.css']
})
export class EjeComponent implements OnInit {
  tableData: Ejercicio[] = [];
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const entObj = safeParse(sessionStorage.getItem('Entidad'));
    const ENTCOD = entObj.ENTCOD;

    if (!USUCOD) {
      alert('Login required.');
      this.router.navigate(['/login']);
      return;
    }
    if (ENTCOD == null) {
      alert('Select entidad first.');
      this.router.navigate(['/ent']);
      return;
    }

    this.loading = true;
    this.http.get<Ejercicio[]>(`${environment.backendUrl}/api/cfg/by-ent/${ENTCOD}`)
      .subscribe({
        next: resp => {
          if (resp?.length > 1) {
            this.tableData = resp;
          } else if (resp?.length === 1) {
            sessionStorage.setItem('EJERCICIO', JSON.stringify({ eje: resp[0] }));
            this.router.navigate(['/centro-gestor']);
          } else {
            alert('Sin ejercicios.');
            this.router.navigate(['/ent']);
          }
        },
        error: err => {
          console.error('EJE error', err);
          alert('Error cargando ejercicios.');
          this.router.navigate(['/ent']);
        }
      }).add(() => this.loading = false);
  }

  selectRow(item: Ejercicio): void {
    sessionStorage.setItem('EJERCICIO', JSON.stringify({ eje: item }));
    this.router.navigate(['/centro-gestor']);
  }

  cancelar(): void {
    this.router.navigate(['/ent']);
  }
}