import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  errormessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    const body = {
      email: this.email,
      password: this.password
    };

    this.http.post('http://localhost:8080/api/login', body).subscribe(
      response => {
      console.log('Login response:', response);
      sessionStorage.setItem('email', this.email);
      this.router.navigate(['/pua']); 
      },
      error => {
        console.error('Login failed', error);
        this.errormessage = 'Invalid email or password';
      }
    );
  }
}
