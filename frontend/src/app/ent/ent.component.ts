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
    const email = sessionStorage.getItem('email');
    console.log('Email from session:', email);

    if (!email) {
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    const data = sessionStorage.getItem('puaData');
    if (data) {
      this.tableData = JSON.parse(data);
      console.log('Data from sessionStorage:', this.tableData);
    } else {
      console.warn('No data found in sessionStorage');
    }
  }

  selectRow(item: any): void {
    console.log('Selected row:', item);
    sessionStorage.setItem('Entidad', JSON.stringify({ entcod: item.entcod }));
    sessionStorage.setItem('Perfil', JSON.stringify({ perfil: item.percod }));
    console.log('Entidad and Perfil are:', item.entcod, item.percod);

    this.http.get<any>('http://localhost:8080/api/mnucods', { params: { PERCOD: item.percod } })
      .subscribe({
        next: (response) => {
          if (response.error) {
            alert('Error: ' + response.error);
          } else {
            console.log('Response received:', response);
            sessionStorage.setItem('puaData', JSON.stringify(response));
            console.log('Data saved to sessionStorage:', response);
            this.router.navigate(['/dashboard']);
          }
        },
        error: (err) => {
          alert('Server error: ' + (err.message || err.statusText));
        }
      });
  }
}
