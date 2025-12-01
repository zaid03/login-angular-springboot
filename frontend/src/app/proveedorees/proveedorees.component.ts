import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  sidebarOpen = false;
  toggleSidebar() { this.sidebarOpen = !this.sidebarOpen; }
  closeSidebar() { this.sidebarOpen = false; }
  private entcod: number | null = null;

  constructor(private http: HttpClient, private router: Router) {}

  proveedores: any[] = [];
  private backupProveedores: any[] = [];
  error: string | null = null;
  ngOnInit(): void {
    this.error = '';
    const entidad = sessionStorage.getItem('Entidad');

    if (entidad) {
      const parsed = JSON.parse(entidad);
      this.entcod = parsed.ENTCOD || parsed.entcod;
    }

    if (!entidad || this.entcod === null) {
      sessionStorage.clear();
      alert('You must be logged in to access this page.');
      this.router.navigate(['/login']);
      return;
    }

    this.http.get<any>(`http://localhost:8080/api/ter/by-ent/${this.entcod}`)
      .subscribe({
        next: (response) => {
          if (response.error) {
            this.error = `Error:  ${response.error}`;
          } else {
            this.proveedores = response;
            this.backupProveedores = Array.isArray(response) ? [...response] : [];
            this.page = 0;
          }
        },
        error: (err) => {
          this.error = 'Server error';
        }
      });
  }

  page = 0;
  pageSize = 20;
  selectedProveedor: any = null;
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
    this.error = '';
  }

  closeDetails() {
    this.messageError = '';
    this.messageSuccess = '';
    this.personasContactoError = '';
    this.personasContactoErrorMessage = '';
    this.personasContactoSuccessMessage = '';
    this.articuloError = '';
    this.articuloSuccessMessage = '';
    this.articuloErrorMessage = '';
    this.selectedProveedor = null;
    this.contactPersons = null;
    this.articulos = null;
    this.showContactPersonsGrid = false;
    this.showArticulosGrid = false;
    this.page = 0;
  }

  onSearchFormSubmit(event: Event) {
    event.preventDefault();
    this.search();
  }

  clearSearch() {
    this.searchTerm = '';
    this.error = '';
    this.proveedores = [...this.backupProveedores];
    this.page = 0;
  }

  searchTerm: string = '';
  filterOption: string = 'noBloqueados';
  search() {
    this.error = '';
    if (this.searchTerm.trim() === '') {
      this.proveedores = [];
      return;
    }

    if ((/^\d+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados') && (this.searchTerm.length <= 5)) { 
      const tercod = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/tercod/${tercod}/terblo/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length <= 5)) {
      const tercod = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/tercod/${tercod}/terblo-not/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados' && (this.searchTerm.length > 5)) {
      const ternif = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/ternif/${ternif}/terblo/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }
    if (/^\d+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length > 5)) {
      const ternif = this.searchTerm;
      const terblo = 0;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/ternif/${ternif}/terblo-not/${terblo}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }
    if (/^[a-zA-Z0-9]+$/.test(this.searchTerm) && this.filterOption === 'Bloqueados' && (this.searchTerm.length > 5)) {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/search?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }
    if (/^[a-zA-Z0-9]+$/.test(this.searchTerm) && this.filterOption === 'noBloqueados' && (this.searchTerm.length > 5)) {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/search-by-term?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el código ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }


    if (this.filterOption === 'Bloqueados') {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/searchByNomOrAli?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores bloqueados con el término ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }

    if (this.filterOption === 'noBloqueados') {
      const term = this.searchTerm;
      this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/findMatchingNomOrAli?term=${term}`)
        .subscribe({
          next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores no bloqueados con el término ingresado.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          }
        });
      return;
    }

    if (this.filterOption === 'Todos') {

      if (/^\d+$/.test(this.searchTerm) && this.searchTerm.length <= 5) {
        const tercod = this.searchTerm;
        this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/tercod/${tercod}`)
          .subscribe({ 
              next: (response) => {
              this.proveedores = response;
              if (response.length === 0) {
                this.error = 'No se encontraron proveedores.';
              }
              this.page = 0;
            },
            error: (err) => {
              this.error = 'Server error';
            }
          });
        return;
      }

      if (/^\d+$/.test(this.searchTerm) && this.searchTerm.length > 5) {
        const ternif = this.searchTerm;
          this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/ternif/${ternif}`)
          .subscribe({ next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
          } 
        });
        return;
      }
        const term = this.searchTerm;
        this.http.get<any[]>(`http://localhost:8080/api/ter/by-ent/${this.entcod}/search-todos?term=${term}`)
          .subscribe({ next: (response) => {
            this.proveedores = response;
            if (response.length === 0) {
              this.error = 'No se encontraron proveedores.';
            }
            this.page = 0;
          },
          error: (err) => {
            this.error = 'Server error';
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
      this.error = 'No hay datos para exportar.';
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

  showContactPersonsGrid = false;
  contactPersons: any = null;
  personasContactoError: string = '';
  showContactPersons(proveedore: any){
    this.messageError = '';
    this.messageSuccess = '';
    this.showContactPersonsGrid = true;
    this.showArticulosGrid = false;
    this.selectedProveedor = proveedore;
    this.personasContactoError = ''
    this.articuloError = '';
    const tercod = proveedore.tercod;

    this.http.get<any[]>(`http://localhost:8080/api/more/by-tpe/${this.entcod}/${tercod}`)
      .subscribe({ next: (response) => {
        const respArray = Array.isArray(response) ? response : (response ? [response] : []);
        if (respArray.length === 0) { 
          this.contactPersons = null;           
          this.personasContactoError = 'No se encontraron personas de contacto.';
        } else {
          this.contactPersons = respArray[0];
          this.personasContactoError = '';
         }
          this.page = 0;
      },
      error: (err) => {
        this.personasContactoError = 'No se encontraron personas de contacto.';
        this.contactPersons = [];
        this.page = 0;
      } 
    });
  }

  showArticulosGrid = false;
  articulos: any = null;
  articuloError: string = '';
  showArticulos(proveedore: any){
    this.messageSuccess = ''
    this.showArticulosGrid = true;
    this.showContactPersonsGrid = false;
    this.selectedProveedor = proveedore;
    const tercod = proveedore.tercod;
    this.articuloError = ''
    this.personasContactoError = ''
    
    this.http.get<any[]>(`http://localhost:8080/api/more/by-apr/${this.entcod}/${tercod}`)
      .subscribe({ next: (response) => {
        this.articulos = Array.isArray(response) ? response : (response ? [response] : []);

        this.articulos.forEach((row: any, index: number) => {
          const artcod = String(row?.artcod ?? '').trim();
          const afacod = String(row?.afacod ?? '').trim();
          const asucod = String(row?.asucod?? '').trim();
          this.getDescription(row, index, artcod, afacod, asucod);
          this.anadirmessage = '';
          this.anadirErrorMessage = '';
        });

        if (response.length === 0) {
          this.articuloError = 'No se encontraron artículos.';
        }
        this.page = 0;
      },
      error: (err) => {
        this.articuloError = 'No se encontraron artículos.';
      } 
    });
  }

  getDescription(row: any, index: number, artcod: string, afacod: string, asucod: string){
    if (artcod === '*') {
      if (asucod === '*') {
        this.http
          .get<any[]>(`http://localhost:8080/api/afa/art-name/${this.entcod}/${afacod}`)
          .subscribe({
            next: (response) => {
              const respArray = Array.isArray(response) ? response : response ? [response] : [];
              const afades = respArray[0]?.afades;
              row.description = String(afades ?? '').trim();
            },
          });
      } else {
        this.http
          .get<any[]>(`http://localhost:8080/api/asu/art-name/${this.entcod}/${afacod}/${asucod}`)
          .subscribe({
            next: (response) => {
              const respArray = Array.isArray(response) ? response : response ? [response] : [];
              const asudes = respArray[0]?.asudes;
              row.description = String(asudes ?? '').trim();
            },
          });
      }
    } else {
      this.http
        .get<any[]>(`http://localhost:8080/api/art/art-name/${this.entcod}/${afacod}/${asucod}/${artcod}`)
        .subscribe({
          next: (response) => {
            const respArray = Array.isArray(response) ? response : response ? [response] : [];
            const artdes = respArray[0]?.artdes;
            row.description = String(artdes ?? '').trim();
          },
        });
    }
  }
  
  messageSuccess: string = '';
  messageError: string = '';
  saveChanges() {
    const updateFields ={
      TERWEB : this.selectedProveedor.terweb,
      TEROBS : this.selectedProveedor.terobs,
      TERBLO : this.selectedProveedor.terblo,
      TERACU : this.selectedProveedor.teracu
    }

    this.http.put(`http://localhost:8080/api/ter/updateFields/${this.selectedProveedor.tercod}`, updateFields, { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        this.messageSuccess = 'Proveedor actualizado correctamente';
      },
      error: (err) => {
        console.error('Error:', err);
        this.messageError = 'Error al guardar el proveedor';
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

  personasContactoErrorMessage: string = '';
  personasContactoSuccessMessage: string = '';
  updatepersonas(){
    this.personasContactoErrorMessage = '';
    this.personasContactoSuccessMessage = '';

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
        this.personasContactoSuccessMessage = 'Persona de contacto actualizada correctamente';
      },
      error: (err) => {
        console.error('Error:', err);
        this.personasContactoErrorMessage = 'Error al guardar la persona de contacto';
      }
    });
  }

  deletepersona(){
    this.http.delete(
      `http://localhost:8080/api/more/delete/${this.selectedProveedor.tercod}`,
      { responseType: 'text' }).subscribe({
        next: (res) => {
        this.personasContactoSuccessMessage = 'Persona de contacto eliminada correctamente';
        this.contactPersons = null; 
        },
        error: (err) => {
          console.error('Error:', err);
          this.personasContactoErrorMessage = 'Error al eliminar la persona de contacto';
        }
      });
  }

  onApracuChange(articulo: any, event: Event) {
    const input = event.target as HTMLInputElement;
    articulo.apracu = input.checked ? 1 : 0;
  }

  articuloSuccessMessage: string = '';
  articuloErrorMessage: string = '';
  updatearticulos(articulo: any){
    this.articuloSuccessMessage = '';
    this.articuloErrorMessage = '';

    const updateFields = {
      ENT: this.entcod,
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
        this.articuloSuccessMessage = 'Artículo actualizado correctamente';
      },
      error: (err) => {
        console.error('Error:', err);
        this.articuloErrorMessage = 'Error al guardar el artículo';
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
        this.articuloSuccessMessage = 'Artículo eliminado correctamente';
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
          this.articuloErrorMessage = 'Error al eliminar el artículo';
        }
      });
  }

  showHelloGrid = false;
  selectedFamilia: string = '';
  selectedSubfamilia: string = '';
  selectedArticulo: string = '';
  afas: any[] = [];
  asus: any[] = [];
  arts: any[] = [];
  showHello() {
    this.searchValue = '';
    this.searchResults = [];
    this.searchType = 'familia';
    this.showHelloGrid = true;
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
    this.searchPage;
    this.selectedFamilia = '';
    this.selectedSubfamilia = '';
    this.selectedArticulo = '';
    this.anadirmessage = '';
    this.anadirErrorMessage = '';
    this.afas = [];
    this.asus = [];
    this.arts = [];
  }

  anadirErrorMessage: string = '';
  anadirmessage: string = '';
  addArticulo(){
    const newArticulo = {
      ENT: this.entcod,
      TERCOD: this.selectedProveedor.tercod,
      AFACOD: this.selectedFamilia || '*',
      ASUCOD: this.selectedSubfamilia || '*',
      ARTCOD: this.selectedArticulo || '*',
    };

    if ((this.articulos ?? []).some((a: any) =>
      (a.afacod ?? '*') === newArticulo.AFACOD &&
      (a.asucod ?? '*') === newArticulo.ASUCOD &&
      (a.artcod ?? '*') === newArticulo.ARTCOD)) {
      this.anadirErrorMessage = 'El artículo ya está en la lista.';
      return;
    }

    this.http.post(
      `http://localhost:8080/api/more/add-apr`,
      newArticulo,
      { responseType: 'text' }
    ).subscribe({
      next: (res) => {
        this.anadirmessage = 'Artículo añadido correctamente';

        if (this.showArticulosGrid) {
          const newRow = {
            afacod: this.selectedFamilia || '*',
            asucod: this.selectedSubfamilia || '*',
            artcod: this.selectedArticulo || '*',
            ent: this.entcod,
            tercod: this.selectedProveedor.tercod,
            description: ''
          };
          if (!this.articulos) this.articulos = [];
          this.articulos.push(newRow);

          const artcod = String(newRow.artcod ?? '').trim();
          const afacod = String(newRow.afacod ?? '').trim();
          const asucod = String(newRow.asucod ?? '').trim();
          const index = this.articulos.length - 1;

          this.getDescription(newRow, index, artcod, afacod, asucod);
          this.articuloSuccessMessage = '';
          this.articuloErrorMessage = '';
        }
      },
      error: (err) => {
        console.error('Error:', err);
        this.anadirmessage = 'Error al añadir el artículo';
      }
    });
  }

  showDeleteConfirm = false;
  articuloToDelete: any = null;
  openDeleteConfirm(articulo: any) {
    this.articuloToDelete = articulo;
    this.showDeleteConfirm = true;
  }

  closeDeleteConfirm() {
    this.showDeleteConfirm = false;
    this.articuloToDelete = null;
    this.articuloSuccessMessage = '';
    this.articuloErrorMessage = '';
  }

  confirmDelete() {
    if (this.articuloToDelete) {
      this.deletearticulo(this.articuloToDelete);
      this.closeDeleteConfirm();
    }
  }

  searchType: string = 'familia';
  searchValue: string = '';
  searchResults: any[] = [];
  onSearch() {
    this.searchPage = 0;
    this.searchResults = [];
    if (!this.searchValue || !this.searchType) return;
    let url = '';
    if (this.searchType === 'familia') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/afa/by-ent/${this.entcod}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/afa/by-ent-like/${this.entcod}/${this.searchValue}`;
      }
    } else if (this.searchType === 'subfamilia') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/asu/by-ent/${this.entcod}/${this.searchValue}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/asu/by-ent-like/${this.entcod}/${this.searchValue}`;
      }
    } else if (this.searchType === 'articulo') {
      if(/^\d+$/.test(this.searchValue)) {
        url = `http://localhost:8080/api/art/by-ent/${this.entcod}/${this.searchValue}/${this.searchValue}/${this.searchValue}`;
      } else {
        url = `http://localhost:8080/api/art/by-ent-like/${this.entcod}/${this.searchValue}`;
      }
      
    }
    this.http.get<any[]>(url, { withCredentials: true }).subscribe({
    next: (data) => {
      this.searchResults = data;
    },error: (err) => {
        console.error('Error fetching search results:', err);
        this.searchResults = [];
      }
    });
  }

  searchPage: number = 0;
  searchPageSize: number = 5;
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
  }

  showProveedorModal = false;
  anadirProveedorErrorMessage: string = '';
  openProveedorModal(): void {
    this.showProveedorModal = true;
    this.anadirProveedorErrorMessage = '';
    this.resetProveedorModalState();
    this.onProveedorFetch();
  }

  closeProveedorModal(): void {
    this.showProveedorModal = false;
    this.clearMessages();
    this.resetProveedorModalState();
  }

  private resetProveedorModalState(): void {
    this.anadirProveedorErrorMessage = '';
    this.searchProveedor = '';
    this.proveedoresSearchPage = 0;

    if (this.fullProveedoresSearchResults && this.fullProveedoresSearchResults.length) {
      this.proveedoresSearchResults = [...this.fullProveedoresSearchResults];
    } else {
      this.proveedoresSearchResults = [];
    }
  }

  searchAdd: string = 'nif';
  searchProveedor: string = '';
  onProveedorSearchChange(): void {
    const q = (this.searchProveedor || '').toString().trim();
    if (q.length === 0) {
      this.proveedoresSearchResults = [...(this.fullProveedoresSearchResults || [])];
      this.proveedoresSearchPage = 0;
      this.anadirProveedorErrorMessage = '';
    }
  }

  guardarProveedorIssuccess = false;
  proveedoresSearchResults: any[] = [];
  fullProveedoresSearchResults: any[] = [];
  selectedProveediresFromResults: any[] = [];
  onProveedorFetch(){
    const ent = this.entcod;

    this.http.get<any[]>(`http://localhost:8080/api/sical/terceros`, { withCredentials: true}).subscribe({
      next:(data) => {
        this.fullProveedoresSearchResults = Array.isArray(data) ? data: [];
        this.proveedoresSearchResults = [...this.fullProveedoresSearchResults];
        const normalized = (Array.isArray(data) ? data : []).map(d => ({
          ENT: ent,
          TERNOM: d.nomTercero ?? '',
          TERALI: d.apellTercero ?? '',
          NIF: d.niftercero ?? d.ternif ?? '',
          TERDOM: d.domicilio ?? '',
          TERCPO: d.codigoPostal ?? '',
          TERTEL: d.telefono ?? '',
          TERFAX: d.fax ?? '',
          TERWEB: d.web ?? '',
          TERCOE: d.email ?? '',
          TEROBS: d.observaciones ?? '',
          TERPOB: d.poblacion ?? '',
          __raw: d
          }));
        this.fullProveedoresSearchResults = normalized;
        this.proveedoresSearchResults = [...normalized];
        if (this.proveedoresSearchResults.length === 0) {
          this.anadirProveedorErrorMessage = 'No se encontraron proveedores.';
        }
      }, error: (e) => {
        this.anadirProveedorErrorMessage = `${e}`;
      }
    });
  }

  onProveedorSearch(){
    const q = (this.searchProveedor || '').toString().trim();
    if (!this.searchAdd) {
      this.anadirProveedorErrorMessage = 'Selecciona tipo de búsqueda';
      return;
    }

    if (q.length === 0) {
      this.proveedoresSearchResults = [...(this.fullProveedoresSearchResults || [])];
      this.proveedoresSearchPage = 0;
      this.anadirProveedorErrorMessage = '';
      return;
    }

    const applyFilter = () => {
      const term = q.toLowerCase();
      let filtered: any[] = [];
      if (this.searchAdd === 'nif') {
        filtered = this.fullProveedoresSearchResults.filter(p => (p.NIF || p.ternif || '').toString().toLowerCase().includes(term));
      } else if (this.searchAdd === 'nom') {
        filtered = this.fullProveedoresSearchResults.filter(p => (p.TERNOM || p.ternom || '').toString().toLowerCase().includes(term));
      } else if (this.searchAdd === 'apell') {
        filtered = this.fullProveedoresSearchResults.filter(p => (p.TERALI || '').toString().toLowerCase().includes(term));
      } else {
        filtered = this.fullProveedoresSearchResults.filter(p =>
          (p.NIF || p.ternif || '').toString().toLowerCase().includes(term) ||
          (p.TERNOM || p.ternom || '').toString().toLowerCase().includes(term) ||
          (p.TERALI || '').toString().toLowerCase().includes(term)
        );
      }

      this.proveedoresSearchResults = filtered;
      this.proveedoresSearchPage = 0;
      this.anadirProveedorErrorMessage = filtered.length === 0 ? 'No se encontraron proveedores.' : '';
    };
    applyFilter();
  }

  private proveedorKey(p: any): string {
    const ent = (p?.ENT ?? '').toString();
    const ternom = (p?.TERNOM ?? p?.nomTercero ?? '').toString().trim();
    const ternif = (p?.NIF ?? p?.TERNIF ?? p?.niftercero ?? '').toString().trim();
    return `${ent}|${ternom}|${ternif}`;
  }

  isProveedorSelected(p: any): boolean {
    if (!this.selectedProveediresFromResults?.length) return false;
    const k = this.proveedorKey(p);
    return this.selectedProveediresFromResults.some(s => this.proveedorKey(s) === k);
   }

  selectProveedor(item: any){
    const k = this.proveedorKey(item);
    const idx = this.selectedProveediresFromResults.findIndex(s => this.proveedorKey(s) === k);
    if (idx === -1) {
      this.selectedProveediresFromResults.push(item);
    } else {
      this.selectedProveediresFromResults.splice(idx, 1);
    }
  }

  clearSelectedProveedores() {
    this.selectedProveediresFromResults = [];
    this.clearMessages();
  }

  private proveedorExistsInMainList(candidate: any): boolean {
    const candidateNif = (candidate?.NIF ?? candidate?.TERNIF ?? '').toString().trim().toLowerCase();
    if (!candidateNif) return false;
    return (this.proveedores ?? []).some(main =>
      (main?.ternif ?? '').toString().trim().toLowerCase() === candidateNif
    );
  }

  guardarMessageProveedor: string = '';
  saveProveedorees() {
    const ent = this.entcod;

    if (!this.selectedProveediresFromResults || this.selectedProveediresFromResults.length === 0) {
      this.anadirProveedorErrorMessage = 'No hay proveedores seleccionados';
      return;
    }

    const uniqueSelections = this.selectedProveediresFromResults.filter(p => !this.proveedorExistsInMainList(p));

    if (uniqueSelections.length === 0) {
      this.anadirProveedorErrorMessage = 'Todos los proveedores seleccionados ya existen en la lista.';
      return;
    }

    const payload = this.selectedProveediresFromResults.map(p => ({
      ENT: ent,
      TERNOM: p.TERNOM ?? '',
      TERALI: p.TERALI ?? '',
      TERNIF: p.NIF ?? '',
      TERDOM: p.TERDOM ?? '',
      TERCPO: p.TERCPO ?? '',
      TERTEL: p.TERTEL ?? '',
      TERFAX: p.TERFAX ?? '',
      TERWEB: p.TERWEB ?? '',
      TERCOE: p.TERCOE ?? '',
      TEROBS: p.TEROBS ?? '',
      TERPOB: p.TERPOB ?? '',
    }));

    const token = sessionStorage.getItem('JWT');
    const headers = token
      ? new HttpHeaders({ 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' })
      : new HttpHeaders({ 'Content-Type': 'application/json' });

    this.isSaving = true;
    this.clearMessages();
    this.http.post<any[]>(`http://localhost:8080/api/ter/save-proveedores/${ent}`, payload, { headers, observe: 'response', responseType: 'text' as 'json' })
      .subscribe({
        next: (res) => {
          this.isSaving = false;
          const savedCount = Array.isArray(res.body) ? res.body.length : this.selectedProveediresFromResults.length;
          this.clearSelectedProveedores();
          this.guardarProveedorIssuccess = true;
          this.guardarMessageProveedor = `Proveedores guardados correctamente (${savedCount}).`;
        },
        error: (err) => {
          console.error('HTTP error status=', err.status, 'body=', err.error);
          this.isSaving = false;
          const msg = err && err.error ? (typeof err.error === 'string' ? err.error : JSON.stringify(err.error)) : `Error al salvar los proveedores (status ${err.status})`;  
        }
      })
  }

  clearMessages(){
    this.anadirProveedorErrorMessage = '';
    this.guardarMessageProveedor = '';
    this.anadirProveedorErrorMessage = '';
  }

  isSaving = false;
  saveError = '';
  saveSuccess = '';
  private messageTimeout: any = null;


  confirmDeleteSelected() {
    if (!this.selectedProveediresFromResults || this.selectedProveediresFromResults.length === 0) {
      this.saveError = 'No hay elementos seleccionados para eliminar.';
      return;
    }
    const ok = confirm(`Eliminar ${this.selectedProveediresFromResults.length} proveedor(es) de la lista local? Esta acción solo afecta la lista local.`);
    if (ok) this.deleteSelectedFromResults();
  }

  deleteSelectedFromResults() {
    const toRemove = new Set(this.selectedProveediresFromResults.map((s: any) => `${s.ENT}|${s.TERNOM}|${s.TERNIF}`));
    this.proveedoresSearchResults = this.proveedoresSearchResults.filter((p: any) => !toRemove.has(`${p.ENT}|${p.TERNOM}|${p.NIF || p.TERNIF}`));
    this.fullProveedoresSearchResults = this.fullProveedoresSearchResults.filter((p: any) => !toRemove.has(`${p.ENT}|${p.TERNOM}|${p.NIF || p.TERNIF}`));
    this.clearSelectedProveedores();
    this.saveSuccess = 'Elementos eliminados de la lista local';
  }

  proveedoresSearchPage: number = 0;
  proveedoresSearchPageSize: number = 10;
  get paginatedProveedoresSearchResults() {
    const start = this.proveedoresSearchPage * this.proveedoresSearchPageSize;
    return this.proveedoresSearchResults.slice(start, start + this.proveedoresSearchPageSize);
  }

  get proveedoresSearchTotalPages() {
    return Math.ceil(this.proveedoresSearchResults.length / this.proveedoresSearchPageSize) || 1;
  }

  proveedoresSearchPrev() {
    if (this.proveedoresSearchPage > 0) this.proveedoresSearchPage--;
  }

  proveedoresSearchNext() {
    if (this.proveedoresSearchPage + 1 < this.proveedoresSearchTotalPages) this.proveedoresSearchPage++;
  }
}