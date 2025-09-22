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
    const validateUrl = `${environment.casValidateUrl}?ticket=${ticket}&service=${environment.frontendUrl}/login`;

    this.http.get(validateUrl, { responseType: 'text' }).subscribe({
      next: (response: string) => {
        this.isValidating = false;
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
                  this.http.get<any>(`${environment.backendUrl}/api/filter`, { params: { usucod: backendResponse.username } })
                  .subscribe({
                    next: (filterResponse) => {
                      if (filterResponse.error) {
                        this.errormessage = filterResponse.error;
                        return;
                      } 
                      if (Array.isArray(filterResponse) && filterResponse.length) {
                        const normalized = filterResponse.map((r: any) => ({
                          USUCOD: r.USUCOD ?? r.usucod,
                          APLCOD: r.APLCOD ?? r.aplcod,
                          ENTCOD: r.ENTCOD ?? r.entcod,
                          PERCOD: r.PERCOD ?? r.percod,
                          ENTNOM: r.ENTNOM ?? r.entnom
                        }));
                        sessionStorage.setItem('puaData', JSON.stringify(normalized));
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
                this.errormessage = 'Backend validation failed';
              }
            },
            error: (err) => {
              this.errormessage = 'Backend validation error: ' + err.message;
            }
          });
        } else {
          this.errormessage = 'CAS validation failed: ' + response;
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
    window.location.href = `${environment.casLoginUrl}?service=${environment.frontendUrl}/login`;
  }
}