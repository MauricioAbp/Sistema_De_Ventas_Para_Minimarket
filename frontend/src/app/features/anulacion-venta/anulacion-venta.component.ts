import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Venta } from '../../core/models/pos.models';
import { AuthService } from '../../core/services/auth.service';
import { PosService } from '../../core/services/pos.service';
import { ActivatedRoute, RouterLink } from '@angular/router';
@Component({
  selector: 'app-anulacion-venta',
  standalone: true,
  imports: [FormsModule, DecimalPipe, DatePipe, RouterLink],
  template: `
    <section class="page">
      <header class="page-header">
        <div>
          <p class="eyebrow">Ventas</p>
          <h1>Anulación de venta</h1>
          <p class="subtitle">Anula ventas registradas y revierte el stock afectado.</p>
        </div>
      </header>

      @if (error()) {
        <p class="banner error">{{ error() }}</p>
      }

      @if (success()) {
        <div class="banner success">
          <p>{{ success() }}</p>
          @if (venta(); as item) {
            <a
              class="document-link"
              [routerLink]="['/app/ventas/boleta', item.id_venta]"
              [queryParams]="{ ventaId: item.id_venta, tipo: 'anulacion' }"
            >
              Ver boleta de anulación
            </a>
          }
        </div>
      }

      <section class="panel search-panel">
        <form (ngSubmit)="searchVenta()">
          <label>
            Buscar venta por ID
            <input type="number" min="1" name="idVenta" [(ngModel)]="idVenta" placeholder="Ej. 15" required>
          </label>

          <button type="submit" class="btn-primary" [disabled]="loading() || !idVenta">
            {{ loading() ? 'Buscando...' : 'Buscar venta' }}
          </button>
        </form>
      </section>

      @if (venta(); as item) {
        <section class="panel warning-panel">
          <strong>Advertencia</strong>
          <p>La anulación modificará el inventario y afectará los reportes del sistema.</p>
        </section>

        <section class="grid">
          <article class="panel">
            <h2>Información de la venta</h2>

            <dl>
              <div>
                <dt>ID</dt>
                <dd>#{{ item.id_venta }}</dd>
              </div>
              <div>
                <dt>Número</dt>
                <dd>{{ item.numero_venta }}</dd>
              </div>
              <div>
                <dt>Fecha</dt>
                <dd>{{ item.fecha_venta | date: 'dd/MM/yyyy HH:mm' }}</dd>
              </div>
              <div>
                <dt>Cajero</dt>
                <dd>{{ userLabel(item) }}</dd>
              </div>
              <div>
                <dt>Método de pago</dt>
                <dd>{{ item.metodo_pago || '-' }}</dd>
              </div>
              <div>
                <dt>Estado</dt>
                <dd>
                  <span class="badge" [class.bad]="item.estado === 'ANULADA'">{{ item.estado }}</span>
                </dd>
              </div>
              <div>
                <dt>Total</dt>
                <dd>S/ {{ asNumber(item.total) | number: '1.2-2' }}</dd>
              </div>
            </dl>
          </article>

          <article class="panel">
            <h2>Productos</h2>

            @if (!item.detalles?.length) {
              <p class="muted">La venta no tiene detalles disponibles.</p>
            } @else {
              <ul class="detail-list">
                @for (detail of item.detalles; track detail.id_detalle) {
                  <li>
                    <div>
                      <strong>{{ detail.producto?.nombre_producto || 'Producto' }}</strong>
                      <span>{{ detail.cantidad }} unidades x S/ {{ asNumber(detail.precio_unitario) | number: '1.2-2' }}</span>
                    </div>
                    <b>S/ {{ asNumber(detail.subtotal) | number: '1.2-2' }}</b>
                  </li>
                }
              </ul>
            }
          </article>
        </section>

        <form class="panel cancel-form" (ngSubmit)="confirmAnulacion()">
          <h2>Confirmar anulación</h2>

          <label>
            Motivo de anulación
            <textarea name="motivo" [(ngModel)]="motivo" rows="4" maxlength="250" required></textarea>
          </label>

          <div class="actions">
            <button type="button" class="btn-secondary" (click)="clear()">Cancelar</button>
            <button
              type="submit"
              class="btn-danger"
              [disabled]="busy() || item.estado === 'ANULADA' || !motivo.trim()"
            >
              {{ busy() ? 'Anulando...' : 'Confirmar anulación' }}
            </button>
          </div>
        </form>
      }
    </section>
  `,
  styles: [`
    .page {
      display: grid;
      gap: 18px;
    }

    .page-header {
      display: flex;
      justify-content: space-between;
      gap: 16px;
    }

    .eyebrow {
      margin: 0 0 4px;
      color: #2563eb;
      font-size: 0.75rem;
      font-weight: 800;
      text-transform: uppercase;
    }

    h1,
    h2,
    p {
      margin: 0;
    }

    h1 {
      color: #0f172a;
      font-size: 1.8rem;
    }

    h2 {
      color: #0f172a;
      font-size: 1.05rem;
    }

    .subtitle,
    .muted {
      margin-top: 6px;
      color: #64748b;
    }

    .panel {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 16px;
      display: grid;
      gap: 14px;
      box-shadow: 0 10px 26px rgba(15, 23, 42, 0.05);
    }

    .search-panel form {
      display: flex;
      align-items: end;
      gap: 12px;
    }

    .grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
      align-items: start;
    }

    label {
      display: grid;
      gap: 6px;
      color: #334155;
      font-weight: 700;
      width: min(420px, 100%);
    }

    input,
    textarea {
      width: 100%;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      padding: 10px 12px;
      font: inherit;
    }

    input {
      height: 42px;
    }

    textarea {
      resize: vertical;
    }

    dl {
      display: grid;
      gap: 10px;
      margin: 0;
    }

    dl div,
    .detail-list li {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 10px;
    }

    dt {
      color: #64748b;
      font-weight: 700;
    }

    dd {
      margin: 0;
      color: #0f172a;
      font-weight: 800;
      text-align: right;
    }

    .detail-list {
      display: grid;
      gap: 10px;
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .detail-list strong {
      display: block;
      color: #0f172a;
    }

    .detail-list span {
      color: #64748b;
      font-size: 0.9rem;
    }

    .detail-list b {
      color: #0f172a;
    }

    .warning-panel {
      background: #fffbeb;
      border-color: #fde68a;
      color: #92400e;
    }

    .warning-panel strong {
      color: #92400e;
    }

    .badge {
      display: inline-flex;
      border-radius: 999px;
      background: #dcfce7;
      color: #166534;
      padding: 4px 8px;
      font-weight: 800;
      font-size: 0.85rem;
    }

    .badge.bad {
      background: #fee2e2;
      color: #991b1b;
    }

    .banner {
      border-radius: 8px;
      padding: 14px 16px;
    }

    .banner.error {
      background: #fef2f2;
      color: #b91c1c;
      border: 1px solid #fecaca;
    }

    .banner.success {
      background: #ecfdf5;
      color: #047857;
      border: 1px solid #a7f3d0;
    }

    .document-link {
      display: inline-block;
      margin-top: 10px;
      color: #047857;
      font-weight: 800;
      text-decoration: underline;
    }

    .actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }

    button {
      border: 0;
      border-radius: 8px;
      padding: 10px 14px;
      font-weight: 800;
      cursor: pointer;
      white-space: nowrap;
    }

    button:disabled {
      opacity: 0.55;
      cursor: not-allowed;
    }

    .btn-primary {
      background: #2563eb;
      color: #ffffff;
    }

    .btn-secondary {
      background: #e0f2fe;
      color: #075985;
    }

    .btn-danger {
      background: #ef4444;
      color: #ffffff;
    }

    @media (max-width: 860px) {
      .grid,
      .search-panel form {
        grid-template-columns: 1fr;
        display: grid;
      }

      label,
      button {
        width: 100%;
      }

      .actions {
        display: grid;
      }
    }
  `]
})
export class AnulacionVentaComponent {
  readonly venta = signal<Venta | null>(null);
  readonly loading = signal(false);
  readonly busy = signal(false);
  readonly error = signal('');
  readonly success = signal('');

  idVenta: number | null = null;
  motivo = '';

constructor(private pos: PosService, private auth: AuthService, private route: ActivatedRoute) {
  const id = Number(this.route.snapshot.queryParamMap.get('id'));

  if (id) {
    this.idVenta = id;
    setTimeout(() => this.searchVenta());
  }
}
  searchVenta(): void {
    if (!this.idVenta) {
      return;
    }

    this.loading.set(true);
    this.error.set('');
    this.success.set('');
    this.venta.set(null);

    this.pos.getVentaById(this.idVenta).subscribe({
      next: (venta) => {
        this.venta.set(venta);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'No se pudo encontrar la venta.');
        this.loading.set(false);
      }
    });
  }

  confirmAnulacion(): void {
    const venta = this.venta();
    const user = this.auth.currentUser();
    const motivo = this.motivo.trim();

    if (!venta || !user || !motivo) {
      return;
    }

    this.busy.set(true);
    this.error.set('');
    this.success.set('');

    this.pos.anularVenta(venta.id_venta, {
      idUsuario: user.idUsuario,
      motivo
    }).subscribe({
      next: (updated) => {
        this.venta.set(updated);
        this.motivo = '';
        this.success.set('Venta anulada correctamente. Se generó la boleta de anulación con el detalle del dinero devuelto al cliente.');
        this.busy.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'No se pudo anular la venta.');
        this.busy.set(false);
      }
    });
  }

  clear(): void {
    this.venta.set(null);
    this.idVenta = null;
    this.motivo = '';
    this.error.set('');
    this.success.set('');
  }

  userLabel(venta: Venta): string {
    const user = venta.usuario;
    if (!user) {
      return '-';
    }

    const fullName = `${user.nombre ?? ''} ${user.apellido ?? ''}`.trim();
    return fullName || user.username || '-';
  }

  asNumber(value: number | string | null | undefined): number {
    return Number(value ?? 0);
  }
}