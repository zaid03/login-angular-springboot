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

  logoPath = 'assets/images/logo_iass.png';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const rawEntidad = sessionStorage.getItem('Entidad');
    const rawPerfil  = sessionStorage.getItem('Perfil');
    const rawMnus    = sessionStorage.getItem('mnucods');

    const entidadObj = safeParse(rawEntidad);
    const perfilObj  = safeParse(rawPerfil);
    const cgObj      = safeParse(sessionStorage.getItem('selected_centro_gestor'));

    this.usucod = sessionStorage.getItem('USUCOD');
    this.entcod = entidadObj.ENTCOD ?? null;
    this.perfil = perfilObj.PERCOD ?? null;
    this.cgecod = cgObj?.cgecod ?? null;

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

  navigateTo(code: string): void {
    if (this.isDisabled(code)) {
      console.warn('Not allowed:', code);
      return;
    }
  }

  logout(): void {
    sessionStorage.clear();
    window.location.href = `${environment.casLoginUrl.replace('/login', '/logout')}?service=${environment.frontendUrl}/login`;
  }

  proveedorees(): void {
    // optional: enforce permission before navigating
    if (this.isDisabled('acTer')) {
      console.warn('Not allowed: acTer');
      return;
    }
    this.router.navigate(['/proveedorees']);
  }
}