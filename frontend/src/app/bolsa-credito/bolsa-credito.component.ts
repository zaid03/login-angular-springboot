import { Component, HostListener} from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-bolsa-credito',
  standalone: true,
  imports: [CommonModule, FormsModule, SidebarComponent, CurrencyPipe],
  templateUrl: './bolsa-credito.component.html',
  styleUrls: ['./bolsa-credito.component.css'],
  providers: [CurrencyPipe]
})
export class BolsaCreditoComponent {
  //3 dots menu
  showMenu = false;
  toggleMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.showMenu = false;
  }

  //global variables
  private entcod: number | null = null;
  private eje: number | null = null;
  public centroGestor: string = '';
  private initialCentroGestor: string = '';
  creditos: any[] = [];
  private backupCreditos: any[] = [];
  private defaultCreditos: any[] = [];
  wsData: any[] = [];
  public Math = Math;
  page = 0;
  pageSize = 20;
  tableMessage: string = '';
  tableIsError: boolean = false;
  guardarMesage: string = '';
  guardarisError: boolean = false;
  guardarMesageSuccess: string = '';
  guardarisSuccess : boolean = false;

  constructor(private http: HttpClient, private router: Router, private currency: CurrencyPipe) {}
  
  isLoading: boolean = false;
}
