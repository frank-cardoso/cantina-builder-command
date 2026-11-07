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
        this(pedido.getId(),
                pedido.getCliente(),
                pedido.getItens().stream()
                        .map(item -> new Item(item.produto().nome(), item.quantidade(), item.subtotal()))
                        .toList(),
                pedido.isParaViagem(),
                pedido.getObservacao(),
                pedido.getStatus(),
                pedido.getDescontoPercentual(),
                pedido.total());
    }
}
