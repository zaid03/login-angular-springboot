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
  showHelloGrid = false;
  selectedFamilia: string = '';
  selectedSubfamilia: string = '';
  selectedArticulo: string = '';
  anadirDescripcion: string = '';
  anadirRefProv: string = '';
  anadirUdsEmbalaje: number | null = null;
  anadirObservaciones: string = '';
  anadirAcuerdo: boolean = false;
  anadirPrecioAcuerdo: number | null = null;
  messages: string = '';
  isError: boolean = false;
  contactMessage: string = '';
  contactIsError: boolean = false;
  nocontactmessage: string = '';
  articulosMessage: string = '';
  articleIsError: boolean = false;
  afas: any[] = [];
  asus: any[] = [];
  arts: any[] = [];
  anadirmessage: string = '';
  anadirIsError: boolean = false;
  showDeleteConfirm = false;
  articuloToDelete: any = null;
  searchType: string = 'familia';
  searchValue: string = '';
  searchResults: any[] = [];
  searchPage: number = 0;
  searchPageSize: number = 5;

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
    this.contactPersons = null;
    this.articulos = null;
    this.message = '';
    this.showContactPersonsGrid = false;
    this.showArticulosGrid = false;
    this.contactMessage = '';
    this.contactIsError = false;
    this.articulosMessage = '';
    this.articleIsError = false;
    this.page = 0;
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
    this.contactMessage = ''; 
    this.articulosMessage = '';
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
          this.nocontactmessage = 'No se encontraron personas de contacto.';
        }
        this.page = 0;
      },
      error: (err) => {
        this.nocontactmessage = 'error al obtener las personas de contacto.';
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

    this.nocontactmessage = '';
    this.isError = false;
    this.contactMessage = '';
    this.contactIsError = false;
    
    this.http.get<any[]>(`http://localhost:8080/api/more/by-apr/${entcod}/${tercod}`)
      .subscribe({ next: (response) => {
        this.articulos = response;
        if (response.length === 0) {
          this.nocontactmessage = 'No se encontraron artículos.';
        }
        this.page = 0;
      },
      error: (err) => {
        this.nocontactmessage = 'error al obtener los artículos.';
      } 
    });
  }

  sidebarOpen = false;
  toggleSidebar() { this.sidebarOpen = !this.sidebarOpen; }
  closeSidebar() { this.sidebarOpen = false; }

  saveChanges() {
    const updateFields ={
      TERWEB : this.selectedProveedor.terweb,
      TEROBS : this.selectedProveedor.terobs,
      TERBLO : this.selectedProveedor.terblo,
      TERACU : this.selectedProveedor.teracu
    }

    this.http.put(`http://localhost:8080/api/ter/updateFields/${this.selectedProveedor.tercod}`, 
    updateFields,
    { responseType: 'text' }
    ).subscribe({
        next: (res) => {
          console.log('Success:', res);
          this.messages = 'Proveedor actualizado correctamente';
          this.isError = false;
        },
        error: (err) => {
          console.error('Error:', err);
          this.messages = 'Error al guardar el proveedor';
          this.isError = true;
        }
      });
  }

  onCheckboxGeneric(event: Event, field: string) {
    const checked = (event.target as HTMLInputElement).checked;
    if (this.selectedProveedor && field in this.selectedProveedor) {
      (this.selectedProveedor as any)[field] = checked ? 1 : 0;
    }
    if (this.articulos && field in this.articulos) {
      (this.articulos as any)[field] = checked ? 1 : 0;
    }
  }

  updatepersonas(){
    const updateFields = {
      TPENOM : this.contactPersons.tpenom,
      TPETEL : this.contactPersons.tpetel,
      TPETMO : this.contactPersons.tpetmo,
      TPECOE : this.contactPersons.tpecoe,
      TPEOBS : this.contactPersons.tpeobs
    }
    this.http.put(
      `http://localhost:8080/api/more/modify/${this.selectedProveedor.tercod}`,
      updateFields,
      { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        console.log('Success:', res);
        this.contactMessage = 'Persona de contacto actualizada correctamente';
        this.contactIsError = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.contactMessage = 'Error al guardar la persona de contacto';
        this.contactIsError = true;
      }
    });
  }

  deletepersona(){
    this.http.delete(
      `http://localhost:8080/api/more/delete/${this.selectedProveedor.tercod}`,
      { responseType: 'text' }).subscribe({
        next: (res) => {
        console.log('Success:', res);
        this.contactMessage = 'Persona de contacto eliminada correctamente';
        this.contactIsError = false;
        this.contactPersons = null; 
        },
        error: (err) => {
          console.error('Error:', err);
          this.contactMessage = 'Error al eliminar la persona de contacto';
          this.contactIsError = true;
        }
      });
  }

  onApracuChange(articulo: any, event: Event) {
    const input = event.target as HTMLInputElement;
    articulo.apracu = input.checked ? 1 : 0;
  }

  updatearticulos(articulo: any){
    const updateFields = {
      ENT: JSON.parse(sessionStorage.getItem('Entidad') || '{}').entcod,
      TERCOD: this.selectedProveedor.tercod,
      AFACOD : articulo.afacod,
      ASUCOD : articulo.asucod,
      ARTCOD : articulo.artcod,
      APRREF : articulo.aprref,
      APRUEM : articulo.apruem,
      APROBS : articulo.aprobs,
      APRACU : articulo.apracu,
      APRPRE : articulo.aprpre
    }

    this.http.put(
      `http://localhost:8080/api/more/update-apr`,
      updateFields,
      { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        console.log('Success:', res);
        this.articulosMessage = 'Artículo actualizado correctamente';
        this.articleIsError = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.articulosMessage = 'Error al guardar el artículo';
        this.articleIsError = true;
      }
    });
  }

  deletearticulo(articulo: any){
    const params = [
    `ent=${articulo.ent}`,
    `tercod=${articulo.tercod}`,
    `afacod=${articulo.afacod}`,
    `asucod=${articulo.asucod}`,
    `artcod=${articulo.artcod}`
  ].join('&');

    this.http.delete(
      `http://localhost:8080/api/more/delete-apr?${ params}`,
      { responseType: 'text' }).subscribe({
        next: (res) => {
        console.log('Success:', res);
        this.articulosMessage = 'Artículo eliminado correctamente';
        this.articleIsError = false;
        this.articulos = this.articulos.filter((a: any) =>
        !(a.ent === articulo.ent &&
          a.tercod === articulo.tercod &&
          a.afacod === articulo.afacod &&
          a.asucod === articulo.asucod &&
          a.artcod === articulo.artcod)
      );
        },
        error: (err) => {
          console.error('Error:', err);
          this.articulosMessage = 'Error al eliminar el artículo';
          this.articleIsError = true;
        }
      });
  }

  showHello() {
    this.searchValue = '';
    this.searchResults = [];
    this.searchType = 'familia';
    this.showHelloGrid = true;
    const ent = JSON.parse(sessionStorage.getItem('Entidad') || '{}').entcod;
    this.http.get<any[]>(`http://localhost:8080/api/afa/by-ent/${ent}`)
      .subscribe(data => this.afas = data);

      this.selectedFamilia = '';
      this.selectedSubfamilia = '';
      this.selectedArticulo = '';
      this.asus = [];
      this.arts = [];
  }

  hideHello() {
    this.showHelloGrid = false;
    this.searchValue = '';
    this.searchResults = [];
    this.searchType = 'familia';
    this.searchPage = 0;
    this.selectedFamilia = '';
    this.selectedSubfamilia = '';
    this.selectedArticulo = '';
    this.anadirmessage = '';
    this.anadirIsError = false;
    this.afas = [];
    this.asus = [];
    this.arts = [];
  }

  addArticulo(){
    const newArticulo = {
      ENT: JSON.parse(sessionStorage.getItem('Entidad') || '{}').entcod,
      TERCOD: this.selectedProveedor.tercod,
      AFACOD: this.selectedFamilia || '*',
      ASUCOD: this.selectedSubfamilia || '*',
      ARTCOD: this.selectedArticulo || '*',
    };

    this.http.post(
      `http://localhost:8080/api/more/add-apr`,
      newArticulo,
      { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        console.log('Success:', res);
        this.anadirmessage = 'Artículo añadido correctamente';
        this.anadirIsError = false;

        if (this.showArticulosGrid) {
          if (!this.articulos) {
            this.articulos = [];
          }
          this.articulos.push({
            afacod: this.selectedFamilia,
            asucod: this.selectedSubfamilia,
            artcod: this.selectedArticulo,
          });
        }
      },
      error: (err) => {
        console.error('Error:', err);
        this.anadirmessage = 'Error al añadir el artículo';
        this.anadirIsError = true;
      }
    });
  }

  openDeleteConfirm(articulo: any) {
    this.articuloToDelete = articulo;
    this.showDeleteConfirm = true;
  }

  closeDeleteConfirm() {
    this.showDeleteConfirm = false;
    this.articuloToDelete = null;
    this.articulosMessage = '';
    this.articleIsError = false;
  }

  confirmDelete() {
    if (this.articuloToDelete) {
      this.deletearticulo(this.articuloToDelete);
      this.closeDeleteConfirm();
    }
  }

  onSearch() {
    this.searchPage = 0;
    this.searchResults = [];
    if (!this.searchValue || !this.searchType) return;
    console.log('Search initiated:', this.searchType, this.searchValue);
    const entidad = sessionStorage.getItem('Entidad');
    const entcod = entidad ? JSON.parse(entidad).entcod : null;
    if (!entcod) return;
    let url = '';
    if (this.searchType === 'familia') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/afa/by-ent/${entcod}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/afa/by-ent-like/${entcod}/${this.searchValue}`;
      }
    } else if (this.searchType === 'subfamilia') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/asu/by-ent/${entcod}/${this.searchValue}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/asu/by-ent-like/${entcod}/${this.searchValue}`;
      }
    } else if (this.searchType === 'articulo') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/art/by-ent/${entcod}/${this.searchValue}/${this.searchValue}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/art/by-ent-like/${entcod}/${this.searchValue}`;
      }
      
    }
    this.http.get<any[]>(url, { withCredentials: true }).subscribe({
    next: (data) => {
      this.searchResults = data;
      console.log('Search results:', this.searchResults);
    },error: (err) => {
        console.error('Error fetching search results:', err);
        this.searchResults = [];
      }
    });
  }

  get paginatedSearchResults() {
    const start = this.searchPage * this.searchPageSize;
    return this.searchResults.slice(start, start + this.searchPageSize);
  }

  get searchTotalPages() {
    return Math.ceil(this.searchResults.length / this.searchPageSize);
  }

  selectedSearchRow: any = null;
  selectSearchRow(item: any) {
    this.selectedSearchRow = item;
    this.selectedFamilia = item.afacod;
    this.selectedSubfamilia = item.asucod;
    this.selectedArticulo = item.artcod;
    console.log('Selected search row:', this.selectedSearchRow);
  }
}