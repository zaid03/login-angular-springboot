import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent],
  templateUrl: './credito.component.html',
  styleUrls: ['./credito.component.css']
})
export class CreditoComponent {
  private entcod: number | null = null;
  private eje: number | null = null;
  public centroGestor: string = '';
  private initialCentroGestor: string = '';
  creditos: any[] = [];
  wsData: any[] = [];
  private backupCreditos: any[] = [];
  public Math = Math;
  page = 0;
  pageSize = 20;
  searchMessage: string = '';
  searchIsError: boolean = false;
  tableMessage: string = '';
  tableIsError: boolean = false;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.tableIsError = false;
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO');
    const cge = sessionStorage.getItem('CENTROGESTOR');

    if (cge){
      const parsed = JSON.parse(cge);
      this.centroGestor = parsed.value
      this.initialCentroGestor = this.centroGestor;
    }

    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD;
    }
    if (eje) {
      const parsed = JSON.parse(eje);
      this.eje = parsed.eje;
    }

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/gbs/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.creditos = Array.isArray(response) ? [...response] : [];
          this.backupCreditos = [...this.creditos];
          this.creditos.forEach((item, idx) => {
            const org = item?.gbsorg ?? '';
            const fun = item?.gbsfun ?? '';
            const eco = item?.gbseco ?? '';
            this.http
              .get<any>(`http://localhost:8080/api/sical/partidas?clorg=${org}&clfun=${fun}&cleco=${eco}`)
              .subscribe({
                next: (partidas) => {
                  const partidasArr = Array.isArray(partidas) ? partidas : [];
                  this.creditos[idx].partidas = partidasArr;
                  const des = partidasArr[0]?.desc ?? '';
                  this.creditos[idx].partidaDesc = des;
                },
                error: () => {
                  this.creditos[idx].partidas = [];
                },
              });
              this.http.get<any>(`http://localhost:8080/api/sical/operaciones?clorg=${org}&clfun=${fun}&cleco=${eco}`)
              .subscribe({
                next: (operaciones) => {
                  const operacionesArr = Array.isArray(operaciones) ? operaciones : [];
                  this.creditos[idx].operaciones = operacionesArr;
                  const firstLinea = operacionesArr[0]?.lineaList?.[0] ?? {};
                  const saldo = this.creditos[idx].saldo = firstLinea?.saldo ?? 0;
                  const limporte = this.creditos[idx].limporte = firstLinea?.limporte ?? 0;
                },
                error: () => {
                  this.creditos[idx].operaciones = [];
                },
              });
          });
          this.page = 0;
        }
      }, error: (err) => {
        this.tableIsError = true;
        this.tableMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        alert(this.tableMessage);
      }
    });
  }

  public getkAcPeCo(gbsiut: any, gbsict: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(gbsiut);
    const b = toNum(gbsict);
    return (a - b).toFixed();
  }

  get paginatedFacturas(): any[] {
    if (!this.creditos || this.creditos.length === 0) return [];
    const start = this.page * this.pageSize;
    return this.creditos.slice(start, start + this.pageSize);
  }
  get totalPages(): number {
    return Math.max(1, Math.ceil((this.creditos?.length ?? 0) / this.pageSize));
  }
  prevPage(): void {
    if (this.page > 0) this.page--;
  }
  nextPage(): void {
    if (this.page < this.totalPages - 1) this.page++;
  }
  goToPage(event: any): void {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.page = inputPage - 1;
    }
  }

  selectedBolsas: any = null;
  detallesMessage: String = '';
  dettalesIsError: boolean = false;

  showDetails(factura: any) {
    this.selectedBolsas = factura;
    const org = factura?.gbsorg ?? '';
    const fun = factura?.gbsfun ?? '';
    const eco = factura?.gbseco ?? '';
    this.bolsaCombo = `${org} - ${fun} - ${eco}`;
  }

  public bolsaCombo: string = '';
  saveDetails() {
    if (!this.selectedBolsas) return;
    const parts = this.bolsaCombo.split(' - ');
    this.selectedBolsas.gbsorg = parts[0] ?? '';
    this.selectedBolsas.gbsfun = parts[1] ?? '';
    this.selectedBolsas.gbseco = parts[2] ?? '';
  } 

  closeDetails() {
    this.selectedBolsas = null;
    this.dettalesIsError = false;
    this.detallesMessage = '';
  }

  public getkCGECIC(cgecic: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(cgecic);
    if (a === 0) return 'No';
    if ( a === 1) return 'Si';
    if ( a === 2) return 'Cierre para contabilizar';
    return '';
  }

  public getKBoldis(gbsimp: any, gbsibg: any, gbsius: any): string {
    const toNum = (v: any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };
    const a = toNum(gbsimp)
    const b = toNum(gbsibg);
    const c = toNum(gbsius);
    return (a + b -c).toFixed();
  }

  public getkdispon(saldo: any, getkAcPeCo: any): string {
    const toNum = (v:any) => {
      if (v === null || v === undefined || v === '') return 0;
      const n = Number(v);
      return isNaN(n) ? 0 : n;
    };

    const a = toNum(saldo);
    const b = toNum(getkAcPeCo);
    return (a - b).toFixed();
  }

  searchBolsas(){
    this.searchMessage = '';
    this.searchIsError = false;

    const cge = (this.centroGestor || '').toString().trim();
    if (!cge) {
      this.searchIsError = true;
      this.searchMessage = 'Please enter a centro gestor.';
      return;
    }
    if (this.entcod == null || !this.eje) {
      this.searchIsError = true;
      this.searchMessage = 'Missing entidad or ejercicio context.';
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/gbs/${this.entcod}/${this.eje}/${this.centroGestor}`).subscribe({
      next: (response) => {
        if (response.error) {
          alert('Error: ' + response.error);
        } else {
          this.creditos = response;
          this.backupCreditos = Array.isArray(response) ? [...response] : [];
          this.page = 0;
        }
      }, error: (err) => {
        this.tableIsError = true;
        this.tableMessage = 'Server error: ' + (err?.message || err?.statusText || err);
        alert(this.tableMessage);
      }
    });
  }
}
