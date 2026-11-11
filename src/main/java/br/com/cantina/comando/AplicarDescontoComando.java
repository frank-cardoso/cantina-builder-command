package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public class AplicarDescontoComando implements Comando {
    private final Pedido pedido;
    private final int percentual;
    private int descontoAnterior;

    public AplicarDescontoComando(Pedido pedido, int percentual) {
        this.pedido = pedido;
        this.percentual = percentual;
    }

    @Override
    public void executar() {
        if (pedido.getStatus() != StatusPedido.RECEBIDO) {
            throw new IllegalStateException("Desconto só pode ser aplicado a pedidos recebidos.");
        }
        if (percentual < 1 || percentual > 50) {
            throw new IllegalArgumentException("O desconto deve ser entre 1% e 50%.");
        }
        this.descontoAnterior = pedido.getDescontoPercentual();
        pedido.aplicarDesconto(percentual);
    }

    @Override
    public void desfazer() {
        pedido.aplicarDesconto(descontoAnterior);
    }

    @Override
    public String descricao() {
        return "Aplicar desconto de " + percentual + "% no pedido #" + pedido.getId();
    }
}