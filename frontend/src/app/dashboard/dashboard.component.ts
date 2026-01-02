import { Component, OnInit } from '@angular/core';
import { CommonModule, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

function safeParse(raw: string | null) {
  if (!raw) return {};
  try { return JSON.parse(raw); } catch { return {}; }
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  //global variables
  usucod: string | null = null;
  perfil: string | null = null;
  entcod: string | null = null;
  eje: number | null = null;
  cge: string = '';
  esContable: boolean = false;
  Estado: number = 0;
  allowedMnucods: string[] = [];
  logoPath = 'assets/images/logo_iass.png';

  constructor(private router: Router) {}

  //main functions
  ngOnInit(): void {
    const profile = sessionStorage.getItem('Perfil');
    const user = sessionStorage.getItem('USUCOD');
    const ent = sessionStorage.getItem('Entidad');
    const session = sessionStorage.getItem('EJERCICIO');
    const centroGestor = sessionStorage.getItem('CENTROGESTOR');
    const status = sessionStorage.getItem('ESTADOGC');
    const contable = sessionStorage.getItem('EsContable');
    const menus = sessionStorage.getItem('mnucods');

    if (profile) { const parsed = JSON.parse(profile); this.perfil = parsed.PERCOD; }
    if (user) { this.usucod = user; }
    if (ent) { const parsed = JSON.parse(ent); this.entcod = parsed.ENTCOD; }
    if (session) { const parsed = JSON.parse(session); this.eje = parsed.eje; }
    if (centroGestor) { const parsed = JSON.parse(centroGestor); this.cge = parsed.value; }
    if (status) { const parsed = JSON.parse(status); this.Estado = parsed.value; }
    if (contable) { const parsed = JSON.parse(contable); this.esContable = parsed.value; }
    if (menus) {
      const parsed = JSON.parse(menus);
      this.allowedMnucods = parsed
        .map((m: any) =>
          typeof m === 'string'
            ? m
            : (m.MNUCOD ?? m.mnucod ?? m.MENUCOD ?? m.code ?? m.codigo ?? m.id))
        .filter(Boolean);
    }

    if (!this.usucod || this.entcod == null || !this.perfil || !this.allowedMnucods) {
      alert('Missing session data. reiniciar el flujo.');
      this.router.navigate(['/login']);
      return;
    }
    
    if (this.Estado > 0) {
      this.getStatus(this.Estado);
    }
  }

  isDisabled(code: string): boolean {
    if ((code === 'acFac' || code === 'acMCon' || code === 'acRCon') && !this.esContable) {
      return true;
    }
    if (code === 'acGBSM' && !this.cge) {
      return true;
    }

    return !this.allowedMnucods.includes(code);
  }

  navigateTo(code: string): void {

    switch (code) {
      case 'ejercicios':
        break;
      case 'centroGestor':
        this.router.navigate(['/centroGestor']);
        break;
      case 'coste':
        this.router.navigate(['/coste']);
        break;
      case 'servicios':
        this.router.navigate(['/servicios']);
        break;
      case 'personas':
        break;
      case 'entrega':
        this.router.navigate(['/entrega']);
        break;
      case 'Cproveedores':
        break;
      case 'proveedorees':
        console.log("clicked")
        this.router.navigate(['/proveedorees']);
        break;
      case 'contratos':
        break;
      case 'Cfactura':
        break;
      case 'facturas':
        this.router.navigate(['/facturas']);
        break;
      case 'contabilizacion':
        break;
      case 'Fcontabilizadas':
        break;
      case 'Ccredito':
        break;
      case 'credito':
        this.router.navigate(['credito']);
        break;
      case 'credito-Cge':
        break;
      case 'familia':
        this.router.navigate(['/familia'])
        break;
      default:
        break;
    }
  }

  centroGestorStatus: string = '';
  getStatus(estado: number) {
    if (estado === 1) {
      return this.centroGestorStatus = 'Centro Gestor CERRADO'
    }
    if (estado === 2) {
      return this.centroGestorStatus = 'Centro Gestor CERRADO para CONTABILIZAR'
    }
    return;
  }

  logout(): void {
    sessionStorage.clear();
    window.location.href = `${environment.casLoginUrl.replace('/login', '/logout')}?service=${environment.frontendUrl}/login`;
  }

  goToCge() {
    this.router.navigate(['/centro-gestor']);
  }
  
  goToServices() {
    
  }
}