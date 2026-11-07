package br.com.cantina.cardapio;

import java.math.BigDecimal;

public record Produto(int id, String nome, BigDecimal preco, Categoria categoria) {
}
