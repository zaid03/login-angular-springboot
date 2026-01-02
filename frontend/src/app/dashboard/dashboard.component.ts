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
  Estado: number | null = null;
  allowedMnucods: [] = [];
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
    if (contable) { const parsed = JSON.parse(contable); this.esContable = parsed.value; console.log(this.esContable)}
    if (menus) { const parsed = JSON.parse(menus); this.allowedMnucods = parsed; }

    

    

    // if (!this.usucod || this.entcod == null || !this.perfil) {
    //   alert('Missing session data. reiniciar el flujo.');
    //   this.router.navigate(['/login']);
    //   return;
    // }

    // if (rawMnus) {
    //   try {
    //     const parsed = JSON.parse(rawMnus);
    //     this.allowedMnucods = parsed
    //       .map((m: any) =>
    //         typeof m === 'string'
    //           ? m
    //           : (m.MNUCOD ?? m.mnucod ?? m.MENUCOD ?? m.code ?? m.codigo ?? m.id))
    //       .filter(Boolean);
    //   } catch {
    //     console.warn('Invalid mnucods JSON');
    //   }
    // }
  }

  // isDisabled(code: string): boolean {
  //   return !this.allowedMnucods.includes(code);
  // }

  logout(): void {
    sessionStorage.clear();
    window.location.href = `${environment.casLoginUrl.replace('/login', '/logout')}?service=${environment.frontendUrl}/login`;
  }

  // proveedorees(): void {
  //   if (this.isDisabled('acTer')) {
  //     console.warn('Not allowed');
  //     return;
  //   }
  //   this.router.navigate(['/proveedorees']);
  // }

  // isFacturasDisabled(): boolean {
  //   return this.isDisabled('acFac') || !this.esContable;
  // }

  // facturas(): void{
  //   if (this.isFacturasDisabled()) {
  //     console.warn('Not allowed');
  //     return;
  //   }
  //   this.router.navigate(['/facturas']);
  // }

  // isMonitorDisabled(): boolean {
  //   return !this.allowedMnucods.includes('acMCon') || !this.esContable;
  // }

  // isCentroGestorDisabled(): boolean {
  //   return !this.allowedMnucods.includes('acGBSM') || !this.centroGestor;
  // }

  navigateTo(code: string): void {
    // if (this.isDisabled(code) && code !== 'familia' && code !== 'centroGestor' && code !== 'servicios' && code !== 'entrega' && code !== 'coste') {
    //   console.warn('Not allowed:', code);
    //   return;
    // }

    switch (code) {
      case 'acTer':
        this.router.navigate(['/proveedorees']);
        break;
      case 'acFac':
        if (this.esContable) {
          this.router.navigate(['/facturas']);
          break;
        } else {
          console.warn("not allowed");
        }
        break
      case 'acGBS':
        this.router.navigate(['credito']);
        break;
      case 'familia':
        this.router.navigate(['/familia'])
        break;
      case 'centroGestor':
        this.router.navigate(['/centroGestor']);
        break;
      case 'servicios':
        this.router.navigate(['/servicios']);
        break;
      case 'entrega':
        this.router.navigate(['/entrega']);
        break;
      case 'coste':
        this.router.navigate(['/coste']);
        break;
      default:
        console.warn('No route configured for code:', code);
    }
  }

  goToCge() {
    this.router.navigate(['/centro-gestor']);
  }
  
  goToServices() {
    this.router.navigate(['/servicios']);
  }
}