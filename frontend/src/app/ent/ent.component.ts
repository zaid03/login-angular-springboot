import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ent',
  standalone: true,
  imports: [ CommonModule ,FormsModule],
  templateUrl: './ent.component.html',
  styleUrls: ['./ent.component.css']
})
export class EntComponent {
  tableData: any[] = [];

  constructor(private http: HttpClient, private router: Router) {}

  
  ngOnInit(): void {
    const USUCOD = sessionStorage.getItem('USUCOD');

    if (!USUCOD) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    const data = sessionStorage.getItem('puaData');
    if (data) {
      this.tableData = JSON.parse(data);
    } else {
      console.warn('No data found in sessionStorage');
    }
  }

  selectRow(item: any): void {
    sessionStorage.setItem('Entidad', JSON.stringify({ entcod: item.entcod }));
    sessionStorage.setItem('Perfil', JSON.stringify({ perfil: item.percod }));

    this.http.get<any>('http://localhost:8080/api/mnucods', { params: { PERCOD: item.percod } })
      .subscribe({
        next: (response) => {
          if (response.error) {
            alert('Error: ' + response.error);
          } else {
            sessionStorage.setItem('puaData', JSON.stringify(response));
            this.router.navigate(['/eje']);
          }
        },
        error: (err) => {
          alert('Server error: ' + (err.message || err.statusText));
        }
      });
  }
}
