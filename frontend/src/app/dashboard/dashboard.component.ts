import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  usucod: string | null = null;
  perfil: string | null = null;

  allowedMnucods: string[] = [];
  constructor(private router: Router) {}

  ngOnInit(): void {
    const usucod = sessionStorage.getItem('USUCOD');
    console.log('USUCOD from session:', usucod);
    const entidad = sessionStorage.getItem('Entidad');
    const perfil = sessionStorage.getItem('Perfil');
    const menus = sessionStorage.getItem('puaData');

    if (menus) {
      const parsed = JSON.parse(menus);
      this.allowedMnucods = parsed.map((item: any) => item.mnucod);
      console.log('Allowed mnucods:', this.allowedMnucods);
    } else {
    console.warn('No menus found in sessionStorage');
    }

    if (!usucod || !entidad || !perfil) {
      alert('Missing session data. You must be logged in.');
      this.router.navigate(['/login']);
      return;
    }

    this.usucod = usucod;
    this.perfil = JSON.parse(perfil).perfil;
    console.log('User code from session:', usucod);

    const entcod = JSON.parse(entidad).entcod;
    const percod = JSON.parse(perfil).perfil;

    console.log('Entidad (entcod):', entcod);
    console.log('Perfil (percod):', percod);
  }

  logout(): void {
    sessionStorage.clear();
    this.router.navigate(['/login']);
  }

  navigateTo(code: string): void {
  console.log('Menú selected:', code);
  
  }

  isDisabled(code: string): boolean {
  return !this.allowedMnucods.includes(code);
  }

  proveedorees(): void {
    this.router.navigate(['/proveedorees']);
  }
}