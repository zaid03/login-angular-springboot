import { Component, Output, EventEmitter } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
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
    const menus = sessionStorage.getItem('puaData');

    if (menus) {
      const parsed = JSON.parse(menus);
      this.allowedMnucods = parsed.map((item: any) => item.mnucod);
    } else {
      console.warn('No menus found in sessionStorage');
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
    
  }

  isDisabled(code: string): boolean {
  return !this.allowedMnucods.includes(code);
  }

  proveedorees(): void {
    this.router.navigate(['/proveedorees']);
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