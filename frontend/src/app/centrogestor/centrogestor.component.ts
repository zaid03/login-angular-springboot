import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

function safeParse(raw: string | null) {
  if (!raw) return {};
  try { return JSON.parse(raw); } catch { return {}; }
}

@Component({
  selector: 'app-centrogestor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './centrogestor.component.html',
  styleUrls: ['./centrogestor.component.css']
})

export class CentrogestorComponent implements OnInit{
  tableData: any[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const entidadObj = safeParse(sessionStorage.getItem('Entidad'));   // { ENTCOD: ... }
    const perfilObj  = safeParse(sessionStorage.getItem('Perfil'));    // { PERCOD: ... }
    const ejeObj     = safeParse(sessionStorage.getItem('selected_ejercicio')); // { eje: ... }

    const ENTCOD = entidadObj.ENTCOD;
    const PERCOD = perfilObj.PERCOD;
    const EJE    = ejeObj.eje;

    console.log('CentroGestor init ->', { USUCOD, ENTCOD, PERCOD, EJE });

    if (!USUCOD) {
      alert('Missing USUCOD. Login again.');
      this.router.navigate(['/login']);
      return;
    }
    if (ENTCOD == null) {
      alert('Entidad not selected.');
      this.router.navigate(['/ent']);
      return;
    }
    if (EJE == null) {
      alert('Ejercicio not selected.');
      this.router.navigate(['/eje']);
      return;
    }
    if (PERCOD == null) {
      alert('Perfil not selected.');
      this.router.navigate(['/ent']);
      return;
    }

    this.http.get<any[]>(`http://localhost:8080/api/centrogestor/percod/${PERCOD}/ent/${ENTCOD}/eje/${EJE}`)
      .subscribe({
        next: (response) => {
          console.log('CentroGestor response:', response);
          if (response && response.length > 1) {
            this.tableData = response;
            console.log(response);
          }else if (response && response.length === 1) {
            sessionStorage.setItem('selected_centro_gestor', JSON.stringify({
              cgecod: response[0][0], 
              cgedes: response[0][1] 
             }));
             console.log(JSON.stringify({
              cgecod: response[0][0], 
              cgedes: response[0][1] 
             }));
            this.router.navigate(['dashboard']);
          } else {
              sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ 
                cgecod: null, 
                cgedes: null 
              }));
              console.log('all null (centro gestor)');
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
    if (!item) return;
    
    sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ 
      cgecod: item[0], 
      cgedes: item[1] 
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
