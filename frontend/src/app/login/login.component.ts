import { Component, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  errormessage: string = '';
  isValidating = false;
  loadingMessage = 'Validating your credentials, please wait...'
  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const ticket = params['ticket'];
      
      if (ticket && !this.isValidating) {
        this.isValidating = true;
        this.validateCASTicket(ticket);
      }
    });
  }

  validateCASTicket(ticket: string) {
    this.isValidating = true;
    const validateUrl = `http://localhost:8080/api/cas/validate`;
    const body = { 
      ticket: ticket,
      service: 'http://localhost:4200/login'
    };

    this.http.post(validateUrl, body).subscribe({
      next: (response: any) => {
        this.isValidating = false;
        
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
          replaceUrl: true
        });
        
        if (response.success) {
          sessionStorage.setItem('USUCOD', response.username);
          
          this.http.get<any>('http://localhost:8080/api/filter', { params: { usucod: response.username } })
            .subscribe({
              next: (filterResponse) => {
                if (filterResponse.error) {
                  this.errormessage = filterResponse.error;
                  return;
                } 
                  if (Array.isArray(filterResponse) && filterResponse.length) {
                  sessionStorage.setItem('puaData', JSON.stringify(filterResponse));
                  this.router.navigate(['/ent']);
                } else {
                  this.errormessage = 'No data returned for user.';
                }
              },
              error: (err) => {
                this.errormessage = 'Server error: ' + (err.message || err.statusText);
              }
            });
        } else {
          this.errormessage = 'CAS validation failed';
        }
      },
      error: (error) => {
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
          replaceUrl: true
        });
        
        this.errormessage = 'CAS validation error: ' + error.message;
      }
    });
  }

  logoPath = 'assets/images/logo_iass.png';

  goToCAS() {
    window.location.href = 'http://localhost:8081/cas/login?service=http://localhost:4200/login';
  }
}