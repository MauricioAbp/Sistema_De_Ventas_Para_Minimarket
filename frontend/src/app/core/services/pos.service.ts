import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AbrirCajaRequest,
  Caja,
  Categoria,
  CerrarCajaRequest,
  Producto,
  VentaRequest,
  VentaResponse,
  MovimientoInventario,
  CajaResumenResponse,
  Proveedor,
  DashboardResponse,
  BoletaResponse,
  AnularVentaRequest,
  Venta,
  ReporteResumenResponse,
  Empresa,
  AjusteInventarioRequest,
} from '../models/pos.models';

@Injectable({ providedIn: 'root' })
export class PosService {
  constructor(private http: HttpClient) {}

  listProducts(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${environment.apiUrl}/producto`).pipe(
      map((productos) =>
        (productos ?? []).filter((p) => p.activo !== false && (p.stock_actual ?? 0) >= 0)
      )
    );
  }
  getDashboardResumen(): Observable<DashboardResponse> {
  return this.http.get<DashboardResponse>(`${environment.apiUrl}/dashboard/resumen`);
}
  listProveedores(): Observable<Proveedor[]> {
  return this.http.get<Proveedor[]>(`${environment.apiUrl}/proveedor`);
}
getCajaResumen(idCaja: number): Observable<CajaResumenResponse> {
  return this.http.get<CajaResumenResponse>(`${environment.apiUrl}/caja/${idCaja}/resumen`);
}
getReporteResumen(desde: string, hasta: string): Observable<ReporteResumenResponse> {
  return this.http.get<ReporteResumenResponse>(
    `${environment.apiUrl}/reportes/resumen?desde=${desde}&hasta=${hasta}`
  );
}
createProveedor(request: Partial<Proveedor>): Observable<Proveedor> {
  return this.http.post<Proveedor>(`${environment.apiUrl}/proveedor/registrar`, request);
}
listVentas(): Observable<Venta[]> {
  return this.http.get<Venta[]>(`${environment.apiUrl}/venta`);
}

getEmpresaPrincipal(): Observable<Empresa> {
  return this.http.get<Empresa>(`${environment.apiUrl}/empresa/principal`);
}

saveEmpresaPrincipal(request: Partial<Empresa>): Observable<Empresa> {
  return this.http.put<Empresa>(`${environment.apiUrl}/empresa/principal`, request);
}

listMovimientos(): Observable<MovimientoInventario[]> {
  return this.http.get<MovimientoInventario[]>(`${environment.apiUrl}/movimientoinventario`);
}
getVentaById(idVenta: number): Observable<Venta> {
  return this.http.get<Venta>(`${environment.apiUrl}/venta/${idVenta}`);
}

anularVenta(idVenta: number, request: AnularVentaRequest): Observable<Venta> {
  return this.http.post<Venta>(`${environment.apiUrl}/venta/${idVenta}/anular`, request);
}
updateProveedor(id: number, request: Partial<Proveedor>): Observable<Proveedor> {
  return this.http.put<Proveedor>(`${environment.apiUrl}/proveedor/${id}`, request);
}

deleteProveedor(id: number): Observable<Proveedor> {
  return this.http.delete<Proveedor>(`${environment.apiUrl}/proveedor/${id}`);
}
listAllProducts(): Observable<Producto[]> {
  return this.http.get<Producto[]>(`${environment.apiUrl}/producto`);
}
ajustarInventario(request: AjusteInventarioRequest): Observable<MovimientoInventario> {
  return this.http.post<MovimientoInventario>(`${environment.apiUrl}/movimientoinventario/ajustar`, request);
}
createProduct(request: Partial<Producto>): Observable<Producto> {
  return this.http.post<Producto>(`${environment.apiUrl}/producto/registrar`, request);
}
updateProduct(id: number, request: Partial<Producto>): Observable<Producto> {
  return this.http.put<Producto>(`${environment.apiUrl}/producto/${id}`, request);
}

deleteProduct(id: number): Observable<Producto> {
  return this.http.delete<Producto>(`${environment.apiUrl}/producto/${id}`);
}

  listCajas(): Observable<Caja[]> {
    return this.http.get<Caja[]>(`${environment.apiUrl}/caja`);
  }
  getBoletaFormal(idBoleta: number): Observable<BoletaResponse> {
  return this.http.get<BoletaResponse>(`${environment.apiUrl}/boleta/${idBoleta}/formal`);
}

  getDocumentoVenta(idVenta: number, tipo: 'boleta' | 'anulacion' = 'boleta'): Observable<BoletaResponse> {
    return this.http.get<BoletaResponse>(`${environment.apiUrl}/venta/${idVenta}/documento?tipo=${tipo}`);
  }

  openCaja(request: AbrirCajaRequest): Observable<Caja> {
    return this.http.post<Caja>(`${environment.apiUrl}/caja/abrir`, request);
  }
  listCategorias(): Observable<Categoria[]> {
  return this.http.get<Categoria[]>(`${environment.apiUrl}/categoria/listar`);
}

createCategoria(request: Partial<Categoria>): Observable<Categoria> {
  return this.http.post<Categoria>(`${environment.apiUrl}/categoria`, request);
}
updateCategoria(id: number, request: Partial<Categoria>): Observable<Categoria> {
  return this.http.put<Categoria>(`${environment.apiUrl}/categoria/${id}`, request);
}

deleteCategoria(id: number): Observable<Categoria> {
  return this.http.delete<Categoria>(`${environment.apiUrl}/categoria/${id}`);
}
  registerSale(request: VentaRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(`${environment.apiUrl}/venta/pos`, request);
  }
  closeCaja(idCaja: number, request: CerrarCajaRequest): Observable<Caja> {
  return this.http.post<Caja>(`${environment.apiUrl}/caja/${idCaja}/cerrar`, request);
}
}
