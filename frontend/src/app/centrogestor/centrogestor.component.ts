import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-centrogestor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './centrogestor.component.html',
  styleUrl: './centrogestor.component.css'
})
export class CentrogestorComponent {
  tableData: any[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const ENTCOD = JSON.parse(sessionStorage.getItem('Entidad') || '{}').entcod;
    const EJE = JSON.parse(sessionStorage.getItem('selected_ejercicio') || '{}').eje;
    const PERCOD = JSON.parse(sessionStorage.getItem('Perfil') || '{}').perfil;

    if (!USUCOD || !ENTCOD || !EJE || !PERCOD) {
      sessionStorage.clear();
      alert('Session expired. Please log in again.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any[]>(`http://localhost:8080/api/centrogestor/percod/${PERCOD}/ent/${ENTCOD}/eje/${EJE}`)
      .subscribe({
        next: (response) => {
          if (response && response.length > 1) {
            this.tableData = response;
          }else if (response && response.length === 1) {
            sessionStorage.setItem('selected_centro_gestor', JSON.stringify({
              cgecod: response[0][0], 
              cgedes: response[0][1] 
             }));
            this.router.navigate(['dashboard']);
          } else if ( response && response.length === 0) {
              sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ 
                cgecod: null, 
                cgedes: null 
              }));
              this.router.navigate(['/dashboard']);  
          } 
        },
        error: (err) => {
          console.log('Server error: ' + (err.message || err.statusText));
          sessionStorage.clear();
          this.router.navigate(['/login']);
        }
      });
  }

  selectRow(item: any[]) {
    const cgecod = item[0];
    const cgedes = item[1];
    
    sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ 
      cgecod: cgecod, 
      cgedes: cgedes 
    }));
    this.router.navigate(['/dashboard']);
  }

  cancelar() {
    sessionStorage.clear();
    this.router.navigate(['/']);
  }

  next() {
    sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ 
      cgecod: null, 
      cgedes: null 
    }));
    this.router.navigate(['/dashboard']);
  }
}
