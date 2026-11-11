package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public class MarcarProntoComando implements Comando {
    private final Pedido pedido;

    public MarcarProntoComando(Pedido pedido) {
        this.pedido = pedido;
    }

    @Override
    public void executar() {
        if (pedido.getStatus() != StatusPedido.RECEBIDO) {
            throw new IllegalStateException("Só pedidos recebidos podem ser marcados como prontos.");
        }
        pedido.alterarStatus(StatusPedido.PRONTO);
    }

    @Override
    public void desfazer() {
        pedido.alterarStatus(StatusPedido.RECEBIDO);
    }

    @Override
    public String descricao() {
        return "Marcar pedido #" + pedido.getId() + " como pronto";
    }
}