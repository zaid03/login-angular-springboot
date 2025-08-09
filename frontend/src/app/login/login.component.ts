import { Component, OnInit} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  errormessage: string = '';
  private isValidating = false; // Guard to prevent multiple calls

  constructor(private http: HttpClient, private router: Router, private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const ticket = params['ticket'];
      console.log('🎫 Received params:', params);
      console.log('🎫 Extracted ticket:', ticket);
      console.log('🔒 Is validating:', this.isValidating);
      
      if (ticket && !this.isValidating) {
        this.isValidating = true;
        this.validateCASTicket(ticket);
      }
    });
  }

  validateCASTicket(ticket: string) {
    console.log('🎫 Validating ticket:', ticket);
    const validateUrl = `http://localhost:8080/api/cas/validate`;
    const body = { 
      ticket: ticket,
      service: 'http://localhost:4200/login'
    };
    console.log('📤 Sending request to:', validateUrl, 'with body:', body);

    this.http.post(validateUrl, body).subscribe({
      next: (response: any) => {
        console.log('✅ Backend response:', response);
        this.isValidating = false; // Reset guard
        
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
          replaceUrl: true
        });
        
        if (response.success) {
          sessionStorage.setItem('USUCOD', response.username);
          console.log('💾 Stored username in session:', response.username);
          
          // Now call the filter API to get the same data as regular login
          this.http.get<any>('http://localhost:8080/api/filter', { params: { usucod: response.username } })
            .subscribe({
              next: (filterResponse) => {
                if (filterResponse.error) {
                  this.errormessage = filterResponse.error;
                } else {
                  sessionStorage.setItem('puaData', JSON.stringify(filterResponse));
                  console.log('💾 Stored filter data in session:', filterResponse);
                  this.router.navigate(['/ent']);
                }
              },
              error: (err) => {
                this.errormessage = 'Server error: ' + (err.message || err.statusText);
              }
            });
        } else {
          console.log('❌ Validation failed:', response);
          this.errormessage = 'CAS validation failed';
        }
      },
      error: (error) => {
        console.log('🚨 HTTP Error:', error);
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {},
          replaceUrl: true
        });
        
        this.errormessage = 'CAS validation error: ' + error.message;
      }
    });
  }

  goToCAS() {
    window.location.href = 'http://localhost:8081/cas/login?service=http://localhost:4200/login';
  }

  
}
