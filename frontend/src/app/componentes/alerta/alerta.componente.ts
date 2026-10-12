import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificacaoAlerta } from '../../modelos/transferencia.modelo';

@Component({
  selector: 'app-alerta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alerta.componente.html',
  styleUrls: ['./alerta.componente.css']
})
export class AlertaComponente {
  @Input() alerta: NotificacaoAlerta | null = null;
  @Output() fechar = new EventEmitter<void>();

  fecharAlerta(): void {
    this.fechar.emit();
  }
}
