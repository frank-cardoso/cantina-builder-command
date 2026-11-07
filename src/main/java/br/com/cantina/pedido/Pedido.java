package br.com.cantina.pedido;

import java.math.BigDecimal;
import java.util.List;

public class Pedido {

    // Package-private de propósito: só o PedidoBuilder cria Pedido.
    Pedido(String cliente, List<ItemPedido> itens, boolean paraViagem, String observacao) {
        throw new UnsupportedOperationException("TODO");
    }

    public int getId() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getCliente() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<ItemPedido> getItens() {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isParaViagem() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getObservacao() {
        throw new UnsupportedOperationException("TODO");
    }

    public StatusPedido getStatus() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getDescontoPercentual() {
        throw new UnsupportedOperationException("TODO");
    }

    public void alterarStatus(StatusPedido novo) {
        throw new UnsupportedOperationException("TODO");
    }

    public void aplicarDesconto(int percentual) {
        throw new UnsupportedOperationException("TODO");
    }

    public BigDecimal total() {
        throw new UnsupportedOperationException("TODO");
    }
}
