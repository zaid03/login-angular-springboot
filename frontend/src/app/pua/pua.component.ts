// import { Component, OnInit } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Router } from '@angular/router';
// import { FormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';

// @Component({
//   selector: 'app-pua',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './pua.component.html',
//   styleUrls: ['./pua.component.css']
// })
// export class PuaComponent implements OnInit {
//   usucod: string = '';
//   errormessage: string = '';

//   constructor(private http: HttpClient, private router: Router) {}

//   ngOnInit(): void {
//     const usucod = sessionStorage.getItem('USUCOD');
//     if (!usucod) {
//       alert('You must be logged in to access this page.');
//       this.router.navigate(['/login']);
//       return;
//     }
//     this.usucod = usucod;
//   }

//   onSubmit() {
//     if (!this.usucod) {
//       this.errormessage = 'No USUCOD found in session. Please log in first.';
//       return;
//     }

//     this.http.get<any>('http://localhost:8080/api/filter', { params: { usucod: this.usucod } })
//       .subscribe({
//         next: (response) => {
//           if (response.error) {
//             this.errormessage = response.error;
//           } else {
//             sessionStorage.setItem('USUCOD', this.usucod);
//             sessionStorage.setItem('puaData', JSON.stringify(response));
//             this.router.navigate(['/ent']);
//           }
//         },
//         error: (err) => {
//           this.errormessage = 'Server error: ' + (err.message || err.statusText);
//         }
//       });
//   }
// }