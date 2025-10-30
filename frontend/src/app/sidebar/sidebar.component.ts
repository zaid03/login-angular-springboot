import { Component, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  usucod: string | null = null;
  perfil: string | null = null;

  allowedMnucods: string[] = [];
  constructor(private router: Router) {}

  ngOnInit(): void {
    const usucod = sessionStorage.getItem('USUCOD');
    const entidad = sessionStorage.getItem('Entidad');
    const perfil = sessionStorage.getItem('Perfil');
    const menus = sessionStorage.getItem('mnucods');

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
    this.perfil = JSON.parse(perfil).perfil;

    const entcod = JSON.parse(entidad).entcod;
    const percod = JSON.parse(perfil).perfil;
  }

  logout(): void {
    sessionStorage.clear();
    window.location.href = `${environment.casLoginUrl.replace('/login', '/logout')}?service=${environment.frontendUrl}/login`;
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
        this.router.navigate(['/facturas']);
        break;
      case 'acGBS':
        this.router.navigate(['credito']);
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

  logoPath = 'assets/images/logo_iass.png';
}