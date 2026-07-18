import { Routes } from '@angular/router';
import { guestGuard } from './core/guards/guest.guard';
import { roleGuard } from './core/guards/role.guard';
import { LoginComponent } from './features/login/login.component';
import { MfaComponent } from './features/mfa/mfa.component';
import { PosComponent } from './features/pos/pos.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'mfa', component: MfaComponent, canActivate: [guestGuard] },
  {
    path: 'app',
    component: MainLayoutComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN', 'CAJERO'] },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      {
  path: 'inventario/ajustes',
  loadComponent: () =>
    import('./features/inventario-ajustes/inventario-ajustes.component').then((m) => m.InventarioAjustesComponent),
  canActivate: [roleGuard],
  data: { roles: ['ADMIN'] }
},
      {
        path: 'ventas/pos',
        component: PosComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'CAJERO'] }
      },
      {
        path: 'caja',
        loadComponent: () =>
          import('./features/caja/caja.component').then((m) => m.CajaComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'CAJERO'] }
      },
      {
        path: 'categorias',
        loadComponent: () =>
          import('./features/categorias/categorias.component').then((m) => m.CategoriasComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'productos',
        loadComponent: () =>
          import('./features/productos/productos.component').then((m) => m.ProductosComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'inventario/movimientos',
        loadComponent: () =>
          import('./features/inventario/movimientos.component').then((m) => m.MovimientosComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'proveedores',
        loadComponent: () =>
          import('./features/proveedores/proveedores.component').then((m) => m.ProveedoresComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
        path: 'reportes',
        loadComponent: () =>
          import('./features/reportes/reportes.component').then((m) => m.ReportesComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
  path: 'ventas/boleta/:id',
  loadComponent: () =>
    import('./features/boleta/boleta.component').then((m) => m.BoletaComponent),
  canActivate: [roleGuard],
  data: { roles: ['ADMIN', 'CAJERO'] }
},
      {
        path: 'configuracion',
        loadComponent: () =>
          import('./features/configuracion/configuracion.component').then((m) => m.ConfiguracionComponent),
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      {
  path: 'ventas/anulacion',
  loadComponent: () =>
    import('./features/anulacion-venta/anulacion-venta.component').then((m) => m.AnulacionVentaComponent),
  canActivate: [roleGuard],
  data: { roles: ['ADMIN'] }
},
{
  path: 'ventas/historial',
  loadComponent: () =>
    import('./features/ventas-historial/ventas-historial.component').then((m) => m.VentasHistorialComponent),
  canActivate: [roleGuard],
  data: { roles: ['ADMIN'] }
},
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: 'pos', redirectTo: 'app/ventas/pos', pathMatch: 'full' },
  { path: 'admin', redirectTo: 'app/dashboard', pathMatch: 'full' },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];