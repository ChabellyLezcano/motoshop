import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

interface Health {
  status: string;
  service: string;
  timestamp: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  template: `
    <main style="max-width:640px;margin:48px auto;padding:24px;background:#fff;border-radius:12px;box-shadow:0 1px 4px rgba(0,0,0,.1)">
      <h1 style="color:#1f4e79">MotoShop — Sprint 0</h1>
      <p>Esqueleto contenerizado de extremo a extremo.</p>

      <div *ngIf="health() as h; else loading"
           style="margin-top:16px;padding:16px;border-radius:8px;background:#e6f4ea;border:1px solid #b7dfc4">
        <strong>Backend conectado correctamente</strong>
        <ul>
          <li>Estado: {{ h.status }}</li>
          <li>Servicio: {{ h.service }}</li>
          <li>Marca de tiempo: {{ h.timestamp }}</li>
        </ul>
      </div>

      <ng-template #loading>
        <div *ngIf="error() as e; else waiting"
             style="margin-top:16px;padding:16px;border-radius:8px;background:#fbeaea;border:1px solid #e2b7b7">
          <strong>No se pudo contactar con el backend</strong>
          <p>{{ e }}</p>
        </div>
        <ng-template #waiting>
          <p style="margin-top:16px;color:#888">Contactando con el backend…</p>
        </ng-template>
      </ng-template>
    </main>
  `
})
export class AppComponent implements OnInit {
  health = signal<Health | null>(null);
  error = signal<string | null>(null);

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<Health>(`${environment.apiUrl}/api/health`).subscribe({
      next: (res) => this.health.set(res),
      error: (err) => this.error.set(err.message ?? 'Error desconocido')
    });
  }
}
