package br.com.cantina.pedido;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Pedido {

    private static final AtomicInteger ULTIMO_ID = new AtomicInteger();
    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    private final int id;
    private final String cliente;
    private final List<ItemPedido> itens;
    private final boolean paraViagem;
    private final String observacao;
    private StatusPedido status = StatusPedido.RECEBIDO;
    private int descontoPercentual;

    // Package-private de propósito: só o PedidoBuilder cria Pedido.
    Pedido(String cliente, List<ItemPedido> itens, boolean paraViagem, String observacao) {
        this.id = ULTIMO_ID.incrementAndGet();
        this.cliente = cliente;
        this.itens = new ArrayList<>(itens);
        this.paraViagem = paraViagem;
        this.observacao = observacao == null ? "" : observacao;
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public List<ItemPedido> getItens() {
        return List.copyOf(itens);
    }

    public int quantidadeDe(int produtoId) {
        return itens.get(indiceDoProduto(produtoId)).quantidade();
    }

    public void alterarQuantidade(int produtoId, int quantidade) {
        if (quantidade < 1) {
            throw new IllegalArgumentException("Quantidade deve ser pelo menos 1");
        }
        int indice = indiceDoProduto(produtoId);
        itens.set(indice, new ItemPedido(itens.get(indice).produto(), quantidade));
    }

    private int indiceDoProduto(int produtoId) {
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).produto().id() == produtoId) {
                return i;
            }
        }
        throw new IllegalArgumentException("Produto " + produtoId + " não está no pedido #" + id);
    }

    public boolean isParaViagem() {
        return paraViagem;
    }

    public String getObservacao() {
        return observacao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public int getDescontoPercentual() {
        return descontoPercentual;
    }

    public void alterarStatus(StatusPedido novo) {
        if (novo == null) {
            throw new IllegalArgumentException("Status do pedido não pode ser nulo");
        }
        this.status = novo;
    }

    public void aplicarDesconto(int percentual) {
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("Desconto deve estar entre 0% e 100%");
        }
        this.descontoPercentual = percentual;
    }

    public BigDecimal total() {
        BigDecimal soma = itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.multiply(BigDecimal.valueOf(100 - descontoPercentual))
                .divide(CEM, 2, RoundingMode.HALF_EVEN);
    }
}
