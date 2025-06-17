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
  USUCOD: string = '';
  USUPASS: string = '';
  errormessage: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  login() {
    const body = {
      USUCOD: this.USUCOD,
      USUPASS: this.USUPASS
    };

    this.http.post('http://localhost:8080/api/login', body).subscribe(
      response => {
      console.log('Login response:', response);
      sessionStorage.setItem('USUCOD', this.USUCOD);

      this.http.get<any>('http://localhost:8080/api/filter', { params: { usucod: this.USUCOD } })
        .subscribe({
          next: (filterResponse) => {
            if (filterResponse.error) {
              this.errormessage = filterResponse.error;
            } else {
              sessionStorage.setItem('puaData', JSON.stringify(filterResponse));
              this.router.navigate(['/ent']);
            }
          },
          error: (err) => {
            this.errormessage = 'Server error: ' + (err.message || err.statusText);
          }
        });
      },
    error => {
      this.errormessage = 'Usucod o usupass inválido';
    }
  );
  }
}
