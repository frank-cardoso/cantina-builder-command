package br.com.cantina.api;

import java.util.List;

public record NovoPedidoRequisicao(String cliente, List<Item> itens, boolean paraViagem, String observacao) {

    public record Item(int produtoId, int quantidade) {
    }
}
