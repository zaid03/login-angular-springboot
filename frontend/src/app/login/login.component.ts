import { Component, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { environment } from '../../environments/environment';
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
  loadingMessage = 'Validando tus credenciales, por favor espera...'
  private casRedirectInitiated = false;

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const ticket = params['ticket'];
      const hasJwt = !!sessionStorage.getItem('JWT');

      if (hasJwt && !ticket) {
        this.router.navigate(['/ent']);
        return;
      }

      if (ticket && !this.isValidating) {
        this.isValidating = true;
        this.validateCASTicket(ticket);
      } else if (!ticket && !this.casRedirectInitiated) {
        this.isValidating = true;
        this.goToCAS();
      }
    });

  }

  validateCASTicket(ticket: string) {
    this.isValidating = true;
    const validateUrl = `${environment.casValidateUrl}?ticket=${ticket}&service=${environment.frontendUrl}/login`;

    this.http.get(validateUrl, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
          replaceUrl: true
        });
        
        if (response.startsWith('yes')) {
          const lines = response.split('\n');
          const username = lines.length > 1 ? lines[1] : 'unknown';
          
          this.http.post(`${environment.backendUrl}/api/cas/validate`, {
            username: username, 
            validated: true
          }).subscribe({
            next: (backendResponse: any) => {
              if (backendResponse.success) {
                sessionStorage.setItem('JWT', backendResponse.token);
                sessionStorage.setItem('USUCOD', backendResponse.username);
                this.router.navigate(['/ent']);
              } else {
                this.isValidating = false;
                this.errormessage = 'Backend validation failed';
              }
            },
            error: (err) => {
              this.isValidating = false;
              this.errormessage = 'Backend validation error: ' + err.message;
            }
          });
        } else {
          this.isValidating = false;
          this.errormessage = 'CAS validation failed: ' + response;
        }
      },
      error: (error) => {
        this.isValidating = false;
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
    this.casRedirectInitiated = true;
    this.isValidating = true;
    window.location.href = `${environment.casLoginUrl}?service=${environment.frontendUrl}/login`;
  }
}