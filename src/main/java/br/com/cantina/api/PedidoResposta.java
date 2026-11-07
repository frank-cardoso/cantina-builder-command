package br.com.cantina.api;

import java.math.BigDecimal;
import java.util.List;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public record PedidoResposta(
        int id,
        String cliente,
        List<Item> itens,
        boolean paraViagem,
        String observacao,
        StatusPedido status,
        int descontoPercentual,
        BigDecimal total) {

    public record Item(String produto, int quantidade, BigDecimal subtotal) {
    }

    public PedidoResposta(Pedido pedido) {
        this(0, null, List.of(), false, null, null, 0, null);
        throw new UnsupportedOperationException("TODO");
    }
}
