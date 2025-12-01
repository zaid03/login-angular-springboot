import { Component, Output, EventEmitter, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

function safeParse(raw: string | null) {
  if (!raw) return {};
  try { return JSON.parse(raw); } catch { return {}; }
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  usucod: string | null = null;
  perfil: string | null = null;
  esContable: boolean = false;
  allowedMnucods: string[] = [];

  logoPath = 'assets/images/logo_iass.png';

  constructor(private router: Router) {}

  ngOnInit(): void {
    const usucod = sessionStorage.getItem('USUCOD');
    const entidad = sessionStorage.getItem('Entidad');
    const perfil = sessionStorage.getItem('Perfil');
    const menus = sessionStorage.getItem('mnucods');
    const rawContable = sessionStorage.getItem('EsContable');
    const ContableOBJ = safeParse(rawContable);
    this.esContable = ContableOBJ.value === true || ContableOBJ.value === 'true';


    if (menus) {
      try {
        const parsed = JSON.parse(menus);
        const arr = Array.isArray(parsed) ? parsed : (parsed.menus || parsed.items || []);
        this.allowedMnucods = arr.map((item: any) => (item?.mnucod ?? item?.mnucod?.toString() ?? '').toString().trim()).filter((s: string) => s);
      } catch (e) {
        console.warn('Failed to parse puaData:', e);
        this.allowedMnucods = [];
      }
    } else {
      console.warn('No menus found in sessionStorage (puaData missing)');
      this.allowedMnucods = [];
    }

    if (!usucod || !entidad || !perfil) {
      sessionStorage.clear();
      alert('Missing session data. You must be logged in.');
      this.router.navigate(['/login']);
      return;
    }

    this.usucod = usucod;
    this.perfil = JSON.parse(perfil).PERCOD;
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
    if (this.isDisabled(code) && code !== 'familia') {
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
      case 'familia':
        this.router.navigate(['/familia'])
        break;
      default:
        console.warn('No route configured for code:', code);
    }
  }

  isDisabled(code: string): boolean {
    if (!code) return true;
    const normalized = code.toString().trim();
    return !this.allowedMnucods.some((c: string) => c === normalized);
  }

  dashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  collapsed = true;
  @Output() collapsedChange = new EventEmitter<boolean>();

  toggleSidebar() {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);
  }

}