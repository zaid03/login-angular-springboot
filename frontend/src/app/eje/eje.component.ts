import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-eje',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './eje.component.html',
  styleUrl: './eje.component.css'
})
export class EjeComponent {
  tableData: any[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const ENTCOD = JSON.parse(sessionStorage.getItem('Entidad') || '{}').entcod;

    if (!USUCOD || !ENTCOD) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/cfg/by-ent/${ENTCOD}`)
      .subscribe({
        next: (response) => {
          if (response && response.length > 1) {
            this.tableData = response;
          }else if (response && response.length === 1) {
            sessionStorage.setItem('selected_ejercicio', JSON.stringify({ eje : response[0]}));
            this.router.navigate(['/centro-gestor']);
          }else {
            sessionStorage.clear();
            alert('No hay ejercicios activos (use la aplicación de escritorio para solucionarlo.');
            this.router.navigate(['/login']);
            return;
          }

        },
        error: (err) => {
          console.log('Server error: ' + (err.message || err.statusText));
        }
      });
  }

  selectRow(item: string) {
    sessionStorage.setItem('selected_ejercicio', JSON.stringify({ eje: item }));
    // console.log('Selected exercise:', item);

    this.router.navigate(['/centro-gestor']);
  }
  
  cancelar() {
    sessionStorage.clear();
    this.router.navigate(['/']);
  }
}
