import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginComponent } from './login/login.component';
import { EntComponent } from './ent/ent.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProveedoreesComponent } from './proveedorees/proveedorees.component';
import { CentrogestorComponent } from './centrogestor/centrogestor.component';
import { EjeComponent } from './eje/eje.component';
import { FacturasComponent } from './facturas/facturas.component';
import { CreditoComponent } from './credito/credito.component';
import { FamiliaComponent } from './familia/familia.component';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'ent', component: EntComponent },
    { path: 'dashboard', component: DashboardComponent },
    { path: 'proveedorees', component: ProveedoreesComponent },
    { path: 'eje', component: EjeComponent},
    { path: 'centro-gestor', component: CentrogestorComponent},
    { path: 'facturas', component: FacturasComponent},
    { path: 'credito', component: CreditoComponent},
    { path: 'familia', component: FamiliaComponent},
    { path: '', redirectTo: '/login', pathMatch: 'full' }, //route by default
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})

export class AppRoutingModule {}