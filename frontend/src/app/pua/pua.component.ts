import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pua',
  standalone: true,
  imports: [ CommonModule ,FormsModule],
  templateUrl: './pua.component.html',
  styleUrls: ['./pua.component.css']
})
export class PuaComponent {
  usucod: string = '';
  errormessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  
  ngOnInit(): void {
    const email = sessionStorage.getItem('email');
    console.log('Email from session:', email);
    if (!email) {
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }
  }

  onSubmit() {
    const email = sessionStorage.getItem('email');
    if (!email) {
      this.errormessage = 'No email found in session. Please log in first.';
      return;
    }
  
      this.http.get<any>('http://localhost:8080/api/filter', { params: { usucod: this.usucod } })
      .subscribe({
        next: (response) => {
          if (response.error) {
            this.errormessage = response.error;
          } else {
            sessionStorage.setItem('email', email);
            sessionStorage.setItem('usucod', this.usucod);
            console.log('User code from session:', this.usucod);
            console.log('Response received:', response);
            sessionStorage.setItem('puaData', JSON.stringify(response));
            this.router.navigate(['/ent']);
          }
        },
        error: (err) => {
          this.errormessage = 'Server error: ' + (err.message || err.statusText);
        }
      });
  }
}
