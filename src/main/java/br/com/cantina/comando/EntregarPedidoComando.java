package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public class EntregarPedidoComando implements Comando {
    private final Pedido pedido;

    public EntregarPedidoComando(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void executar() {
        if (pedido.getStatus() != StatusPedido.PRONTO) {
            throw new IllegalStateException("Só pedidos prontos podem ser entregues.");
        }
        pedido.alterarStatus(StatusPedido.ENTREGUE);
    }

    @Override
    public void desfazer() {
        pedido.alterarStatus(StatusPedido.PRONTO);
    }

    @Override
    public String descricao() {
        return "Entregar pedido #" + pedido.getId();
    }
}