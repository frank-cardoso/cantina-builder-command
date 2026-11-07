package br.com.cantina.pedido;

import java.math.BigDecimal;

import br.com.cantina.cardapio.Produto;

public record ItemPedido(Produto produto, int quantidade) {

    public BigDecimal subtotal() {
        return produto.preco().multiply(BigDecimal.valueOf(quantidade));
    }
}
