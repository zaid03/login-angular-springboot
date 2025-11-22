import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
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
  usucod: string | null = null;
  perfil: string | null = null;
  entcod: number | null = null;
  cgecod: number | null = null;
  allowedMnucods: string[] = [];
  esContable: boolean = false;

  logoPath = 'assets/images/logo_iass.png';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const rawEntidad = sessionStorage.getItem('Entidad');
    const rawPerfil  = sessionStorage.getItem('Perfil');
    const rawMnus    = sessionStorage.getItem('mnucods');
    const rawContable = sessionStorage.getItem('EsContable');

    const entidadObj = safeParse(rawEntidad);
    const perfilObj  = safeParse(rawPerfil);
    const ContableOBJ = safeParse(rawContable);

    this.usucod = sessionStorage.getItem('USUCOD');
    this.entcod = entidadObj.ENTCOD ?? null;
    this.perfil = perfilObj.PERCOD ?? null;
    this.esContable = ContableOBJ.value === true || ContableOBJ.value === 'true';

    if (!this.usucod || this.entcod == null || !this.perfil) {
      alert('Missing session data. reiniciar el flujo.');
      this.router.navigate(['/login']);
      return;
    }

    if (rawMnus) {
      try {
        const parsed = JSON.parse(rawMnus);
        this.allowedMnucods = parsed
          .map((m: any) =>
            typeof m === 'string'
              ? m
              : (m.MNUCOD ?? m.mnucod ?? m.MENUCOD ?? m.code ?? m.codigo ?? m.id))
          .filter(Boolean);
      } catch {
        console.warn('Invalid mnucods JSON');
      }
    }
  }

  isDisabled(code: string): boolean {
    return !this.allowedMnucods.includes(code);
  }

  logout(): void {
    sessionStorage.clear();
    window.location.href = `${environment.casLoginUrl.replace('/login', '/logout')}?service=${environment.frontendUrl}/login`;
  }

  proveedorees(): void {
    if (this.isDisabled('acTer')) {
      console.warn('Not allowed');
      return;
    }
    this.router.navigate(['/proveedorees']);
  }

  isFacturasDisabled(): boolean {
    return this.isDisabled('acFac') || !this.esContable;
  }
  facturas(): void{
    if (this.isFacturasDisabled()) {
      console.warn('Not allowed');
      return;
    }
    this.router.navigate(['/facturas']);
  }

  navigateTo(code: string): void {
    if (this.isDisabled(code)) {
      console.warn('Not allowed:', code);
      return;
    }

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
      default:
        console.warn('No route configured for code:', code);
    }
  }
}