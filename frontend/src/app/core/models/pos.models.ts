export interface Categoria {
  id_categoria: number;
  nombre_categoria: string;
  descripcion?: string;
  activo?: boolean;
}
export interface Producto {
  id_producto: number;
  categoria?: {
    id_categoria?: number;
    nombre_categoria?: string;
  };
  codigo_producto: string;
  nombre_producto: string;
  descripcion?: string;
  precio_compra?: number;
  precio_venta: number;
  stock_actual: number;
  stock_minimo?: number;
  activo?: boolean;
}

export interface Caja {
  id_caja: number;
  estado: string;
  monto_apertura?: number;
  usuario?: {
    id_usuario?: number;
  };
}

export interface AbrirCajaRequest {
  idUsuario: number;
  montoApertura: number;
}

export interface CerrarCajaRequest {
  montoContado: number;
  observaciones?: string;
}

export interface VentaDetalleRequest {
  idProducto: number;
  cantidad: number;
}

export interface VentaRequest {
  idCaja: number;
  idUsuario: number;
  montoRecibido: number;
  metodoPago: string;
  detalles: VentaDetalleRequest[];
}

export interface VentaResponse {
  idVenta: number;
  idBoleta: number;
  numeroVenta: string;
  subtotal: number;
  igv: number;
  total: number;
  vuelto: number;
}

export interface CartLine {
  product: Producto;
  cantidad: number;
}

export interface Proveedor {
  id_proveedor: number;
  ruc_proveedor: string;
  razon_social: string;
  nombre_comercial?: string;
  telefono?: string;
  correo?: string;
  direccion?: string;
  activo?: boolean;
  created_at?: string;
}
export interface MovimientoInventario {
  id_movimiento: number;
  producto?: Producto;
  usuario?: {
    id_usuario?: number;
    nombre?: string;
    apellido?: string;
    username?: string;
  };
  tipo_movimiento: 'ENTRADA' | 'SALIDA' | 'AJUSTE' | string;
  cantidad: number;
  stock_anterior: number;
  stock_posterior: number;
  motivo?: string;
  id_referencia?: number;
  tabla_referencia?: string;
  fecha_movimiento?: string;

  
}
export interface BoletaResponse {
  ruc?: string;
  razonSocial?: string;
  nombreComercial?: string;
  direccion?: string;
  serie: string;
  numero: string;
  fechaEmision?: string;
  cajero?: string;
  detalles: string[];
  subtotal: number;
  igv: number;
  total: number;
  metodoPago?: string;
  tipoDocumento?: string;
  montoDevuelto?: number;
  motivo?: string;
  descripcion?: string;
}

export interface DashboardChartPoint {
  label: string;
  value: number;
}

export interface DashboardResponse {
  ventasTotales: number;
  ingresosDia: number;
  productosStockBajo: number;
  totalProductos: number;
  ventasPorDia: DashboardChartPoint[];
  metodosPago: DashboardChartPoint[];
  productosMasVendidos: DashboardChartPoint[];
}

export interface CajaResumenResponse {
  idCaja: number;
  estado: string;
  montoApertura: number;
  totalVendido: number;
  totalEfectivo: number;
  montoEsperado: number;
  ventasPorMetodo: Record<string, number>;
}

export interface VentaDetalle {
  id_detalle: number;
  producto?: Producto;
  cantidad: number;
  precio_unitario: number;
  precio_compra?: number;
  descuento?: number;
  subtotal: number;
}

export interface ComprobanteAnulacion {
  numero_comprobante: string;
  fecha_emision: string;
  monto_devuelto: number;
  metodo_pago?: string;
  motivo: string;
  descripcion: string;
  lineas?: string[];
}

export interface Venta {
  id_venta: number;
  numero_venta: string;
  subtotal: number;
  igv_monto: number;
  total: number;
  metodo_pago?: string;
  monto_recibido?: number;
  vuelto?: number;
  estado: string;
  fecha_venta?: string;
  usuario?: {
    id_usuario?: number;
    nombre?: string;
    apellido?: string;
    username?: string;
  };
  detalles?: VentaDetalle[];
  comprobante_anulacion?: ComprobanteAnulacion;
}

export interface AnularVentaRequest {
  idUsuario: number;
  motivo: string;
}

export interface ReporteChartPoint{
  label: string;
  value: number;
}

export interface ReporteResumenResponse{
  ventasTotales: number;
  ingresosTotales: number;
  costosTotales: number;
  gananciaNeta: number;
  numeroTransacciones: number;
  ventasPorDia: ReporteChartPoint[];
  gananciasPorDia: ReporteChartPoint[];
  metodosPago: ReporteChartPoint[];
}

export interface Empresa {
  id_empresa?: number;
  ruc?: string;
  razon_social?: string;
  nombre_comercial?: string;
  direccion?: string;
  telefono?: string;
  correo?: string;
  igv_porcentaje?: number;
}

export interface AjusteInventarioRequest {
  idProducto: number;
  idUsuario: number;
  idProveedor?: number;
  tipoMovimiento: 'ENTRADA' | 'AJUSTE';
  cantidad: number;
  motivo: string;
}