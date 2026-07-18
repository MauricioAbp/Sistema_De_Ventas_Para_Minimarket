import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Venta } from '../../core/models/pos.models';
import { PosService } from '../../core/services/pos.service';

@Component({
  selector: 'app-ventas-historial',
  standalone: true,
  imports: [FormsModule, DatePipe, DecimalPipe, RouterLink],
  template: `
    <section class="page">
      <header class="page-header">
        <div>
          <p class="eyebrow">Ventas</p>
          <h1>Historial de ventas</h1>
          <p class="subtitle">Consulta ventas registradas, boletas y estados.</p>
        </div>

        <button type="button" class="btn-secondary" (click)="loadVentas()" [disabled]="loading()">
          Actualizar
        </button>
      </header>

      @if (error()) {
        <p class="banner error">{{ error() }}</p>
      }

      <section class="filters">
        <label>
          Buscar
          <input
            placeholder="ID, número o cajero"
            [ngModel]="search()"
            (ngModelChange)="search.set($event)"
          >
        </label>

        <label>
          Estado
          <select [ngModel]="estado()" (ngModelChange)="estado.set($event)">
            <option value="">Todos</option>
            <option value="COMPLETADA">Completada</option>
            <option value="ANULADA">Anulada</option>
          </select>
        </label>
      </section>

      <section class="panel">
        @if (loading()) {
          <div class="state-box">Cargando ventas...</div>
        } @else if (!filteredVentas().length) {
          <div class="state-box">No hay ventas que coincidan con los filtros.</div>
        } @else {
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Número</th>
                  <th>Fecha</th>
                  <th>Cajero</th>
                  <th>Método</th>
                  <th>Total</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (venta of filteredVentas(); track venta.id_venta) {
                  <tr>
                    <td>#{{ venta.id_venta }}</td>
                    <td>{{ venta.numero_venta }}</td>
                    <td>{{ venta.fecha_venta | date: 'dd/MM/yyyy HH:mm' }}</td>
                    <td>{{ userLabel(venta) }}</td>
                    <td>{{ venta.metodo_pago || '-' }}</td>
                    <td>S/ {{ asNumber(venta.total) | number: '1.2-2' }}</td>
                    <td>
                      <span class="badge" [class.bad]="venta.estado === 'ANULADA'">
                        {{ venta.estado }}
                      </span>
                    </td>
                    <td>
                      <div class="actions">
                       <a
                         class="btn-link"
                         [routerLink]="['/app/ventas/boleta', venta.id_venta]"
                         [queryParams]="{ ventaId: venta.id_venta, tipo: venta.estado === 'ANULADA' ? 'anulacion' : 'boleta' }"
                       >
                         {{ venta.estado === 'ANULADA' ? 'Ver anulación' : 'Ver boleta' }}
                       </a>
                       <a
                         class="btn-link danger"
                         [class.disabled]="venta.estado === 'ANULADA'"
                         [routerLink]="['/app/ventas/anulacion']"
                         [queryParams]="{ id: venta.id_venta }"
                       >
                         Anular
                       </a>
                     </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </section>
    </section>
  `,
  styles: [`
    .page {
      display: grid;
      gap: 18px;
    }

    .page-header,
    .filters {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 16px;
    }

    .eyebrow {
      margin: 0 0 4px;
      color: #2563eb;
      font-size: 0.75rem;
      font-weight: 800;
      text-transform: uppercase;
    }

    h1 {
      margin: 0;
      color: #0f172a;
      font-size: 1.8rem;
    }

    .subtitle {
      margin: 6px 0 0;
      color: #64748b;
    }

    .filters,
    .panel {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      box-shadow: 0 10px 26px rgba(15, 23, 42, 0.05);
    }

    .filters {
      padding: 14px;
    }

    label {
      display: grid;
      gap: 6px;
      color: #334155;
      font-weight: 700;
      width: min(420px, 100%);
    }

    input,
    select {
      height: 42px;
      border: 1px solid #cbd5e1;
      border-radius: 8px;
      padding: 0 12px;
      font: inherit;
    }

    .panel {
      overflow: hidden;
    }

    .table-wrap {
      width: 100%;
      overflow: auto;
    }

    table {
      width: 100%;
      min-width: 980px;
      border-collapse: collapse;
    }

    th,
    td {
      padding: 13px 14px;
      text-align: left;
      border-bottom: 1px solid #f1f5f9;
      vertical-align: middle;
    }

    th {
      background: #f8fafc;
      color: #475569;
      font-size: 0.8rem;
      text-transform: uppercase;
      letter-spacing: 0;
    }

    td {
      color: #334155;
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

    .actions {
      display: flex;
      gap: 10px;
      align-items: center;
    }

    .btn-link {
      color: #2563eb;
      font-weight: 800;
      text-decoration: none;
      cursor: pointer;
    }

    .btn-link.danger {
      color: #dc2626;
    }

    .btn-link.disabled {
      opacity: 0.45;
      pointer-events: none;
    }

    .banner,
    .state-box {
      border-radius: 8px;
      padding: 14px 16px;
    }

    .banner.error {
      background: #fef2f2;
      color: #b91c1c;
      border: 1px solid #fecaca;
    }

    .state-box {
      min-height: 180px;
      display: grid;
      place-items: center;
      color: #64748b;
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

    .btn-secondary {
      background: #e0f2fe;
      color: #075985;
    }

    @media (max-width: 760px) {
      .page-header,
      .filters {
        display: grid;
      }

      .btn-secondary {
        width: 100%;
      }
    }
  `]
})
export class VentasHistorialComponent implements OnInit {
  readonly ventas = signal<Venta[]>([]);
  readonly search = signal('');
  readonly estado = signal('');
  readonly loading = signal(true);
  readonly error = signal('');

  readonly filteredVentas = computed(() => {
    const term = this.search().trim().toLowerCase();
    const estado = this.estado();

    return this.ventas().filter((venta) => {
      const matchesEstado = !estado || venta.estado === estado;
      const user = this.userLabel(venta).toLowerCase();
      const matchesTerm =
        !term ||
        String(venta.id_venta).includes(term) ||
        venta.numero_venta?.toLowerCase().includes(term) ||
        user.includes(term);

      return matchesEstado && matchesTerm;
    });
  });

  constructor(private pos: PosService) {}

  ngOnInit(): void {
    this.loadVentas();
  }

  loadVentas(): void {
    this.loading.set(true);
    this.error.set('');

    this.pos.listVentas().subscribe({
      next: (ventas) => {
        this.ventas.set(ventas ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el historial de ventas.');
        this.loading.set(false);
      }
    });
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