package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public class CancelarPedidoComando implements Comando {
    private final Pedido pedido;
    private StatusPedido statusAnterior;

    public CancelarPedidoComando(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void executar() {
        StatusPedido statusAtual = pedido.getStatus();
        if (statusAtual != StatusPedido.RECEBIDO && statusAtual != StatusPedido.PRONTO) {
            throw new IllegalStateException("Pedido entregue ou já cancelado não pode ser cancelado.");
        }
        this.statusAnterior = statusAtual;
        pedido.alterarStatus(StatusPedido.CANCELADO);
    }

    @Override
    public void desfazer() {
        pedido.alterarStatus(statusAnterior);
    }

    @Override
    public String descricao() {
        return "Cancelar pedido #" + pedido.getId();
    }
}