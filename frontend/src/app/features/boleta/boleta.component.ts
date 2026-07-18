import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BoletaResponse } from '../../core/models/pos.models';
import { PosService } from '../../core/services/pos.service';

@Component({
  selector: 'app-boleta',
  standalone: true,
  imports: [DatePipe, DecimalPipe, RouterLink],
  template: `
    <section class="page">
      <div class="actions no-print">
        <a routerLink="/app/ventas/pos" class="btn-secondary">Volver al POS</a>
        <button type="button" class="btn-primary" (click)="print()">Imprimir</button>
      </div>

      @if (loading()) {
        <div class="state-box no-print">Cargando boleta...</div>
      } @else if (error()) {
        <p class="banner error no-print">{{ error() }}</p>
      } @else if (boleta(); as item) {
        <article class="receipt">
          <header class="receipt-header">
            <div class="logo">MM</div>
            <h1>{{ item.nombreComercial || 'Minimarket' }}</h1>
            <p>{{ item.razonSocial || 'Razón social no registrada' }}</p>
            <p>RUC: {{ item.ruc || '-' }}</p>
            <p>{{ item.direccion || 'Dirección no registrada' }}</p>
          </header>

          <section class="receipt-meta">
            <div>
              <span>{{ isAnulacionDocument() ? 'Boleta de anulación de venta' : 'Boleta' }}</span>
              <strong>{{ item.serie }}-{{ item.numero }}</strong>
            </div>
            <div>
              <span>Fecha</span>
              <strong>{{ item.fechaEmision | date: 'dd/MM/yyyy HH:mm' }}</strong>
            </div>
            <div>
              <span>Cajero</span>
              <strong>{{ item.cajero || '-' }}</strong>
            </div>
            <div>
              <span>Método de pago</span>
              <strong>{{ item.metodoPago || '-' }}</strong>
            </div>
          </section>

          <section class="receipt-lines">
            <h2>Detalle</h2>

            <ul>
              @for (detalle of item.detalles; track detalle) {
                <li>{{ detalle }}</li>
              }
            </ul>
          </section>

          @if (isAnulacionDocument()) {
            <section class="receipt-summary">
              <h2>Resumen de devolución</h2>
              <p>{{ item.descripcion || 'Se devolvió el importe al cliente por la anulación de esta venta.' }}</p>
              <div class="summary-row">
                <span>Monto devuelto</span>
                <strong>S/ {{ ((item.montoDevuelto ?? item.total) || 0) | number: '1.2-2' }}</strong>
              </div>
              @if (item.motivo) {
                <p><strong>Motivo:</strong> {{ item.motivo }}</p>
              }
              <p><strong>Estado:</strong> Anulación registrada</p>
            </section>
          }

          <section class="totals">
            <div>
              <span>Subtotal</span>
              <strong>S/ {{ item.subtotal | number: '1.2-2' }}</strong>
            </div>
            <div>
              <span>IGV (18%)</span>
              <strong>S/ {{ item.igv | number: '1.2-2' }}</strong>
            </div>
            <div class="total-row">
              <span>Total</span>
              <strong>S/ {{ item.total | number: '1.2-2' }}</strong>
            </div>
          </section>

          <footer>
            <p>{{ isAnulacionDocument() ? 'Documento emitido por anulación de venta' : 'Gracias por su compra' }}</p>
          </footer>
        </article>
      }
    </section>
  `,
  styles: [`
    .page {
      min-height: 100%;
      display: grid;
      justify-items: center;
      gap: 18px;
    }

    .actions {
      width: min(760px, 100%);
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }

    .receipt {
      width: min(760px, 100%);
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 24px;
      display: grid;
      gap: 20px;
      box-shadow: 0 10px 26px rgba(15, 23, 42, 0.06);
      color: #0f172a;
    }

    .receipt-header {
      text-align: center;
      display: grid;
      justify-items: center;
      gap: 4px;
      border-bottom: 1px dashed #cbd5e1;
      padding-bottom: 16px;
    }

    .logo {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      background: #2563eb;
      color: #ffffff;
      display: grid;
      place-items: center;
      font-weight: 900;
      margin-bottom: 6px;
    }

    h1,
    h2,
    p {
      margin: 0;
    }

    h1 {
      font-size: 1.4rem;
    }

    h2 {
      font-size: 1rem;
    }

    .receipt-header p,
    .receipt-meta span,
    .totals span,
    footer {
      color: #64748b;
    }

    .receipt-meta {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 12px;
    }

    .receipt-meta div,
    .totals div {
      display: flex;
      justify-content: space-between;
      gap: 14px;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 10px;
    }

    .receipt-meta span {
      display: block;
      font-size: 0.85rem;
    }

    .receipt-meta strong {
      text-align: right;
    }

    .receipt-lines {
      display: grid;
      gap: 10px;
    }

    .receipt-summary {
      display: grid;
      gap: 10px;
      border: 1px solid #dbeafe;
      border-radius: 8px;
      padding: 14px;
      background: #eff6ff;
    }

    .summary-row {
      display: flex;
      justify-content: space-between;
      gap: 14px;
      font-weight: 800;
      color: #1d4ed8;
    }

    ul {
      margin: 0;
      padding: 0;
      list-style: none;
      display: grid;
      gap: 8px;
    }

    li {
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 8px;
      color: #334155;
    }

    .totals {
      display: grid;
      gap: 10px;
    }

    .total-row {
      font-size: 1.15rem;
      border-bottom: 0 !important;
    }

    .total-row strong {
      color: #2563eb;
    }

    footer {
      text-align: center;
      border-top: 1px dashed #cbd5e1;
      padding-top: 14px;
      font-weight: 700;
    }

    .banner,
    .state-box {
      width: min(760px, 100%);
      border-radius: 8px;
      padding: 14px 16px;
    }

    .banner.error {
      background: #fef2f2;
      color: #b91c1c;
      border: 1px solid #fecaca;
    }

    .state-box {
      background: #ffffff;
      border: 1px dashed #cbd5e1;
      min-height: 160px;
      display: grid;
      place-items: center;
      color: #64748b;
    }

    button,
    a {
      border: 0;
      border-radius: 8px;
      padding: 10px 14px;
      font-weight: 800;
      cursor: pointer;
      text-decoration: none;
      font: inherit;
    }

    .btn-primary {
      background: #2563eb;
      color: #ffffff;
    }

    .btn-secondary {
      background: #e0f2fe;
      color: #075985;
    }

    @media (max-width: 680px) {
      .receipt-meta {
        grid-template-columns: 1fr;
      }

      .actions {
        display: grid;
      }

      .actions a,
      .actions button {
        width: 100%;
        text-align: center;
      }
    }

    @media print {
      :host {
        display: block;
      }

      .no-print {
        display: none !important;
      }

      .page {
        display: block;
        min-height: auto;
      }

      .receipt {
        width: 100%;
        border: 0;
        box-shadow: none;
        padding: 0;
      }
    }
  `]
})
export class BoletaComponent implements OnInit {
  readonly boleta = signal<BoletaResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');

  constructor(private route: ActivatedRoute, private pos: PosService) {}

  ngOnInit(): void {
    const ventaId = Number(this.route.snapshot.queryParamMap.get('ventaId'));
    const tipo = (this.route.snapshot.queryParamMap.get('tipo') ?? 'boleta').toLowerCase();

    if (ventaId) {
      this.pos.getDocumentoVenta(ventaId, tipo === 'anulacion' || tipo === 'anulacion-venta' ? 'anulacion' : 'boleta').subscribe({
        next: (documento) => {
          this.boleta.set(documento);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('No se pudo cargar el documento.');
          this.loading.set(false);
        }
      });
      return;
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id) {
      this.error.set('Documento no válido.');
      this.loading.set(false);
      return;
    }

    this.pos.getBoletaFormal(id).subscribe({
      next: (boleta) => {
        this.boleta.set(boleta);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la boleta.');
        this.loading.set(false);
      }
    });
  }

  print(): void {
    window.print();
  }

  isAnulacionDocument(): boolean {
    return this.boleta()?.tipoDocumento === 'ANULACION';
  }
}