package br.com.cantina.pedido;

import java.util.ArrayList;
import java.util.List;

import br.com.cantina.cardapio.Produto;


public class PedidoBuilder {

    static final int TAMANHO_MAXIMO_OBSERVACAO = 100;

    private String cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private boolean paraViagem;
    private String observacao = "";

    public PedidoBuilder paraCliente(String nome) {
        this.cliente = nome;
        return this;
    }

    public PedidoBuilder adicionarItem(Produto produto, int quantidade) {
        itens.add(new ItemPedido(produto, quantidade));
        return this;
    }

    public PedidoBuilder paraViagem() {
        this.paraViagem = true;
        return this;
    }

    public PedidoBuilder comObservacao(String texto) {
        this.observacao = texto == null ? "" : texto.strip();
        return this;
    }

    public Pedido build() {
        if (cliente == null || cliente.isBlank()) {
            throw new IllegalStateException("Cliente é obrigatório");
        }
        if (itens.isEmpty()) {
            throw new IllegalStateException("Pedido precisa de pelo menos 1 item");
        }
        for (ItemPedido item : itens) {
            if (item.produto() == null) {
                throw new IllegalStateException("Produto do item não pode ser nulo");
            }
            if (item.quantidade() < 1) {
                throw new IllegalStateException(
                        "Quantidade de " + item.produto().nome() + " deve ser pelo menos 1");
            }
        }
        if (observacao.length() > TAMANHO_MAXIMO_OBSERVACAO) {
            throw new IllegalStateException(
                    "Observação deve ter no máximo " + TAMANHO_MAXIMO_OBSERVACAO + " caracteres");
        }
        return new Pedido(cliente.strip(), itens, paraViagem, observacao);
    }
}
