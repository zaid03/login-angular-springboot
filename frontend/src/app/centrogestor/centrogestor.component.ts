import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

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
    const ejeObj = safeParse(sessionStorage.getItem('EJERCICIO'));

    const ENTCOD = entObj.ENTCOD;
    const PERCOD = perObj.PERCOD;
    const EJE = ejeObj.eje;

    if (!USUCOD) { this.fail('/login','Login required'); return; }
    if (ENTCOD == null) { this.fail('/ent','Entidad missing'); return; }
    if (PERCOD == null) { this.fail('/ent','Perfil missing'); return; }
    if (EJE == null)    { this.fail('/eje','Ejercicio missing'); return; }

    this.loading = true;
    this.http.get<any[]>(`${environment.backendUrl}/api/centrogestor/percod/${PERCOD}/ent/${ENTCOD}/eje/${EJE}`)
      .subscribe({
        next: resp => {
          if (resp?.length > 1) {
            this.tableData = resp;
          } else if (resp?.length === 1) {
            this.storeAndGo(resp[0]);
          } else {
            // Proceed with null center (business decision)
            sessionStorage.setItem('CENTROGESTOR', JSON.stringify({ value: null}));
            sessionStorage.setItem('CENTROGESTOR_NAME', JSON.stringify({ value: null}));
            sessionStorage.setItem('ESTADOGC', JSON.stringify({ value: 0}));
            sessionStorage.setItem('EsContable', JSON.stringify({ value: false}));
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
    sessionStorage.setItem('CENTROGESTOR', JSON.stringify({ value: item[0] }));
    if (item[1] === 1) {
      sessionStorage.setItem('EsContable', JSON.stringify({ value: true}));
    }
    if (item[1] === 0){
      sessionStorage.setItem('EsContable', JSON.stringify({ value: false}));
    }
    sessionStorage.setItem('CENTROGESTOR_NAME', JSON.stringify({ value: item[2]}));
    sessionStorage.setItem('ESTADOGC', JSON.stringify({ value: item[3]}));
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
    sessionStorage.setItem('CENTROGESTOR', JSON.stringify({ value: null }));
    sessionStorage.setItem('CENTROGESTOR_NAME', JSON.stringify({ value: null}));
    sessionStorage.setItem('ESTADOGC', JSON.stringify({ value: 0}));
    sessionStorage.setItem('EsContable', JSON.stringify({ value: false}));
    this.router.navigate(['/dashboard']);
  }
}