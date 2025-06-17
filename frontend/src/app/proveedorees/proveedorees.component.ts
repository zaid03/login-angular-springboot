import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-proveedorees',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './proveedorees.component.html',
  styleUrls: ['./proveedorees.component.css']
})
export class ProveedoreesComponent {
  proveedores: any[] = [];
  searchTerm: string = '';
  filterOption: string = 'noBloqueados';
  page = 0;
  pageSize = 20;
  selectedProveedor: any = null;
  error: string | null = null;
  showContactPersonsGrid = false;
  showArticulosGrid = false;
  contactPersons: any = null;
  articulos: any = null;
  message: string = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    const entidad = sessionStorage.getItem('Entidad');
    let entcod: number | null = null;

    if (entidad) {
      entcod = JSON.parse(entidad).entcod;
    }
    if (!entidad || entcod === null) {
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/ter/by-ent/${entcod}`)
      .subscribe({
        next: (response) => {
          if (response.error) {
            alert('Error: ' + response.error);
          } else {
            this.proveedores = response;
            this.page = 0;
          }
        },
        error: (err) => {
          alert('Server error: ' + (err.message || err.statusText));
        }
      });
  }

  get filteredProveedores() {
    let filtered = this.proveedores;
    return filtered;
  }

  get totalPages(): number {
  return Math.ceil(this.proveedores.length / this.pageSize);
  }

  get paginatedProveedores() {
    const start = this.page * this.pageSize;
   return this.proveedores.slice(start, start + this.pageSize);
  }

  nextPage() {
    if (this.page + 1 < this.totalPages) {
      this.page++;
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
    }
  }

  goToPage(event: any) {
    const inputPage = Number(event.target.value);
    if (inputPage >= 1 && inputPage <= this.totalPages) {
      this.page = inputPage - 1;
    }
  }

  onSearchChange() {
    this.page = 0;
  }
  onFilterChange() {
    this.page = 0;
  }

  showDetails(proveedor: any) {
    this.selectedProveedor = proveedor;
  }
  closeDetails() {
    this.selectedProveedor = null;
    this.selectedProveedor = null;
    this.contactPersons = null;
    this.articulos = null;
    this.message = '';
    this.showContactPersonsGrid = false;
    this.showArticulosGrid = false;
  }

  search() {
    console.log('here');
    this.error = null;
    const entidad = sessionStorage.getItem('Entidad');
    let entcod: number | null = null;

    if (this.searchTerm.trim() === '') {
      this.proveedores = [];
      return;
    }
    if (entidad) {
      entcod = JSON.parse(entidad).entcod;
    }
    if (entcod === null) {
      alert('No encontrada entidad.');
      return;
    }

    if ((/^\d+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados') && (this.searchTerm.length <= 5)) { 
      const tercod = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/tercod/${tercod}/terblo/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length <= 5)) {
      const tercod = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/tercod/${tercod}/terblo-not/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados' && (this.searchTerm.length > 5)) {
      const ternif = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/ternif/${ternif}/terblo/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length > 5)) {
      const ternif = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/ternif/${ternif}/terblo-not/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }
    if (/^[a-zA-Z0-9]+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados' && (this.searchTerm.length > 5)) {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/search?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }
    if (/^[a-zA-Z0-9]+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length > 5)) {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/search-by-term?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }


    if (this.filterOption === 'Bloqueados') {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/searchByNomOrAli?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el término ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }

    if (this.filterOption === 'noBloqueados') {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/findMatchingNomOrAli?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el término ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          }
        });
      return;
    }

    if (this.filterOption === 'Todos') {

      if (/^\d+$/.test(this.searchTerm) && this.searchTerm.length <= 5) {
        const tercod = this.searchTerm;
        this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/tercod/${tercod}`)
          .subscribe({ 
              next: (response) => {
              this.proveedores = response;
              if (response.length === 0) {
                this.error = 'No se encontraron proveedores.';
              }
              this.page = 0;
            },
            error: (err) => {
              alert('Server error: ' + (err.message || err.statusText));
            }
          });
        return;
      }

      if (/^\d+$/.test(this.searchTerm) && this.searchTerm.length > 5) {
        const ternif = this.searchTerm;
          this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/ternif/${ternif}`)
          .subscribe({ next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          } 
        });
        return;
      }
        const term = this.searchTerm;
        this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${entcod}/search-todos?term=${term}`)
          .subscribe({ next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores.';
            }
            this.page = 0;
          },
          error: (err) => {
            alert('Server error: ' + (err.message || err.statusText));
          } 
        });
        return;
    }
  }
  
  DownloadPDF() {
    const doc = new jsPDF({orientation: 'landscape', unit: 'mm', format: 'a4'});
    const columns = [
      { header: 'Código', dataKey: 'tercod' },
      { header: 'Nombre', dataKey: 'ternom' },
      { header: 'Alias', dataKey: 'terali' },
      { header: 'NIF', dataKey: 'ternif' },
      { header: 'Domicilio', dataKey: 'terdom' },
      { header: 'Código Postal', dataKey: 'tercpo' },
      { header: 'Municipio', dataKey: 'terpob' },
      { header: 'Teléfono', dataKey: 'tertel' },
      { header: 'Correo electrónico', dataKey: 'tercoe' },
      { header: 'Código contable', dataKey: 'terayt' }
    ];
    const data = this.proveedores;

    autoTable(doc, {
      columns,
      body: data,
      styles: { fontSize: 6 },
      headStyles: { fillColor: [41, 128, 185] },
      margin: { left: 2, right: 2 },
      tableWidth: 'wrap',
      columnStyles: {
        tercod: { cellWidth: 15 },         
        ternom: { cellWidth: 45 },  
        terali: { cellWidth: 45 },         
        ternif: { cellWidth: 22 },          
        terdom: { cellWidth: 35 },          
        tercpo: { cellWidth: 15 },        
        terpob: { cellWidth: 35 },          
        tertel: { cellWidth: 22 },          
        tercoe: { cellWidth: 35 },       
        terayt: { cellWidth: 18 }
      },
      didDrawPage: (dataArg) => {
        doc.setFontSize(10);
        doc.text('Lista de Proveedores', 14, 10);
      }
    });

    doc.save('proveedores.pdf');
  }

  DownloadCSV() {
    const data = this.proveedores;

    if (!data || data.length === 0) {
      alert('No hay datos para exportar.');
      return;
    }
    const columns = [
      'tercod', 'ternom', 'terali', 'ternif', 'terdom', 'tercpo', 'terpob',
      'tertel', 'terfax', 'terweb', 'tercoe', 'terobs', 'terayt'
    ];

    const csvRows = [
      columns.map(col => `"${col}"`).join(',')
    ];

    data.forEach(row => {
      const values = columns.map(col => `"${row[col] !== undefined ? row[col] : ''}"`);
      csvRows.push(values.join(','));
    });

    const csvContent = csvRows.join('\r\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'proveedores.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  showContactPersons(proveedore: any){
    this.selectedProveedor = proveedore;
    const entidad = sessionStorage.getItem('Entidad');
    let entcod: number | null = null;
    if (entidad) {
      entcod = JSON.parse(entidad).entcod;
    }
    const tercod = proveedore.tercod;
    this.showContactPersonsGrid = true;
    this.showArticulosGrid = false;

    this.http.get<any[]>(`http://localhost:8080/api/more/by-tpe/ent/${entcod}/tercod/${tercod}`)
      .subscribe({ next: (response) => {
        this.contactPersons = response;
        if (response.length === 0) {
          this.message = 'No se encontraron personas de contacto.';
        }
        this.page = 0;
      },
      error: (err) => {
        this.message = 'No se encontraron personas de contacto.';
      } 
    });
  }

  showArticulos(proveedore: any){
    
    this.selectedProveedor = proveedore;
    sessionStorage.setItem('tercod', proveedore.tercod);
    const entidad = sessionStorage.getItem('Entidad');
    const tercod = sessionStorage.getItem('tercod');
    let entcod: number | null = null;
    if (entidad) {
      entcod = JSON.parse(entidad).entcod;
    }
    this.showArticulosGrid = true;
    this.showContactPersonsGrid = false;

    this.http.get<any[]>(`http://localhost:8080/api/more/by-apr/${entcod}/${tercod}`)
      .subscribe({ next: (response) => {
        this.articulos = response;
        if (response.length === 0) {
          this.message = 'No se encontraron artículos.';
        }
        this.page = 0;
      },
      error: (err) => {
        this.message = 'No se encontraron personas de contacto.';
      } 
    });
  }

  sidebarOpen = false;
  toggleSidebar() { this.sidebarOpen = !this.sidebarOpen; }
  closeSidebar() { this.sidebarOpen = false; }
}