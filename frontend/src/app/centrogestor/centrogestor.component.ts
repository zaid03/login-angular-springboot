import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

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
export class CentrogestorComponent implements OnInit {
  tableData: any[] = [];
  loading = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const entObj = safeParse(sessionStorage.getItem('Entidad'));
    const perObj = safeParse(sessionStorage.getItem('Perfil'));
    const ejeObj = safeParse(sessionStorage.getItem('selected_ejercicio'));

    const ENTCOD = entObj.ENTCOD;
    const PERCOD = perObj.PERCOD;
    const EJE = ejeObj.eje;

    if (!USUCOD) { this.fail('/login','Login required'); return; }
    if (ENTCOD == null) { this.fail('/ent','Entidad missing'); return; }
    if (PERCOD == null) { this.fail('/ent','Perfil missing'); return; }
    if (EJE == null)    { this.fail('/eje','Ejercicio missing'); return; }

    this.loading = true;
    this.http.get<any[]>(`http://localhost:8080/api/centrogestor/percod/${PERCOD}/ent/${ENTCOD}/eje/${EJE}`)
      .subscribe({
        next: resp => {
          if (resp?.length > 1) {
            this.tableData = resp;
          } else if (resp?.length === 1) {
            this.storeAndGo(resp[0]);
          } else {
            // Proceed with null center (business decision)
            sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ cgecod: null, cgedes: null }));
            this.router.navigate(['/dashboard']);
          }
        },
        error: err => {
          console.error('CentroGestor error', err);
          alert('Error cargando centro gestor.');
          this.router.navigate(['/dashboard']);
        }
      }).add(() => this.loading = false);
  }

  private fail(route: string, msg: string) {
    alert(msg);
    this.router.navigate([route]);
  }

  private storeAndGo(item: any) {
    sessionStorage.setItem('selected_centro_gestor', JSON.stringify({
      cgecod: item[0],
      cgedes: item[1]
    }));
    this.router.navigate(['/dashboard']);
  }

  selectRow(item: any) {
    if (!item) return;
    this.storeAndGo(item);
  }

  cancelar() {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  continuarSin() {
    sessionStorage.setItem('selected_centro_gestor', JSON.stringify({ cgecod: null, cgedes: null }));
    this.router.navigate(['/dashboard']);
  }
}