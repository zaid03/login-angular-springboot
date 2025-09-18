import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

interface PuaRow {
  USUCOD: string;
  APLCOD: number;
  ENTCOD: number;
  PERCOD: string;
  ENTNOM: string;
}

@Component({
  selector: 'app-ent',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ent.component.html',
  styleUrls: ['./ent.component.css']
})
export class EntComponent implements OnInit {
  tableData: PuaRow[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');
    if (!USUCOD) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    const raw = sessionStorage.getItem('puaData');
    if (!raw) {
      console.warn('No puaData in sessionStorage');
      return;
    }
    try {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        this.tableData = parsed.map((r: any) => ({
          USUCOD: r.USUCOD ?? r.usucod,
          APLCOD: r.APLCOD ?? r.aplcod,
            ENTCOD: r.ENTCOD ?? r.entcod,
          PERCOD: r.PERCOD ?? r.percod,
          ENTNOM: r.ENTNOM ?? r.entnom
        }));
      } else {
        console.warn('puaData not an array');
      }
    } catch {
      console.warn('Invalid puaData JSON');
    }
  }

  selectRow(item: PuaRow): void {
    if (!item) return;
    console.log('Selected row:', item, 'keys:', Object.keys(item));

    const entcod = item.ENTCOD;
    if (entcod == null || item.PERCOD == null) {
      alert('Row missing ENTCOD/PERCOD');
      return;
    }

    sessionStorage.setItem('Entidad', JSON.stringify({ ENTCOD: entcod }));
    sessionStorage.setItem('Perfil', JSON.stringify({ PERCOD: item.PERCOD }));

    this.http.get<any>('http://localhost:8080/api/mnucods', { params: { PERCOD: item.PERCOD } })
      .subscribe({
        next: (resp) => {
          if (resp?.error) {
            alert('Error: ' + resp.error);
            return;
          }
          sessionStorage.setItem('mnucods', JSON.stringify(resp));
          console.log('mnucods stored, navigating to /eje');
          this.router.navigate(['/eje']);
        },
        error: (err) => {
          alert('Server error: ' + (err.message || err.statusText));
        }
      });
  }
}