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
  ENTCOD: number | null = null;
  constructor(private http: HttpClient, private router: Router) {}

  private fail(route: string, msg: string) {
    alert(msg);
    this.router.navigate([route]);
  }

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    const entObj = safeParse(sessionStorage.getItem('Entidad'));
    const perObj = safeParse(sessionStorage.getItem('Perfil'));
    const ejeObj = safeParse(sessionStorage.getItem('EJERCICIO'));

    this.ENTCOD = entObj.ENTCOD;
    const PERCOD = perObj.PERCOD;
    const EJE = ejeObj.eje;
    
    if (!USUCOD) { this.fail('/login','Login required'); return; }
    if (this.ENTCOD == null) { this.fail('/ent','Entidad missing'); return; }
    if (PERCOD == null) { this.fail('/ent','Perfil missing'); return; }
    if (EJE == null)    { this.fail('/eje','Ejercicio missing'); return; }

    this.loading = true;
    this.fetchSwVarables();
    this.http.get<any[]>(`${environment.backendUrl}/api/centrogestor/percod/${PERCOD}/ent/${this.ENTCOD}/eje/${EJE}`)
      .subscribe({
        next: resp => {
          if (resp?.length > 1) {
            this.tableData = resp;
          } else if (resp?.length === 1) {
            this.storeAndGo(resp[0]);
          } else {
            sessionStorage.setItem('CENTROGESTOR', JSON.stringify({ value: null}));
            sessionStorage.setItem('CENTROGESTOR_NAME', JSON.stringify({ value: null}));
            sessionStorage.setItem('ESTADOGC', JSON.stringify({ value: 0}));
            sessionStorage.setItem('EsContable', JSON.stringify({value: false}));
            sessionStorage.setItem('EsComprador', JSON.stringify({value: false}));
            sessionStorage.setItem('EsAlmacen', JSON.stringify({value : false}));
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

  private storeAndGo(item: any) {
    sessionStorage.setItem('CENTROGESTOR', JSON.stringify({ value: item.cgecod }));
    if (item.depint === 1) { sessionStorage.setItem('EsContable', JSON.stringify({ value: true})); }
    if (item.depint === 0){ sessionStorage.setItem('EsContable', JSON.stringify({ value: false})); }
    if (item.depalm === 1) { sessionStorage.setItem('EsAlmacen', JSON.stringify({ value: true})); }
    if (item.depalm === 0){ sessionStorage.setItem('EsAlmacen', JSON.stringify({ value: false})); }
    if (item.depcom === 1) { sessionStorage.setItem('EsComprador', JSON.stringify({ value: true})); }
    if (item.depcom === 0){ sessionStorage.setItem('EsComprador', JSON.stringify({ value: false})); }
    sessionStorage.setItem('CENTROGESTOR_NAME', JSON.stringify({ value: item.cgedes}));
    sessionStorage.setItem('ESTADOGC', JSON.stringify({ value: item.cgecic}));
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
    sessionStorage.setItem('EsComprador', JSON.stringify({value: false}));
    sessionStorage.setItem('EsAlmacen', JSON.stringify({value : false}));
    this.router.navigate(['/dashboard']);
  }

  sicalVariables: any = [];
  fetchSwVarables() {
    this.http.get(`${environment.backendUrl}/api/ayt/fetch-all/${this.ENTCOD}`).subscribe({
      next: (res) => {
        this.sicalVariables = res;
        if (this.sicalVariables === 0) {
          alert("Faltan parámetros para conectar los WS en la configuración");
          this.router.navigate(['/']);
        }
        
        sessionStorage.setItem('WSORG', JSON.stringify({ WSORG: this.sicalVariables[0].ent_ORG}));
        sessionStorage.setItem('WSENT', JSON.stringify({ WSENT: this.sicalVariables[0].ent_COD }));
      },
      error: (err) => {
        alert("Server error");
        this.router.navigate(['/']);
      }
    })
  }
}