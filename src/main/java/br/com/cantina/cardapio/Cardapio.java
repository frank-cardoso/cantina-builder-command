package br.com.cantina.cardapio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.cantina.NaoEncontradoException;

@Component
public class Cardapio {

    private final List<Produto> produtos = List.of(
            new Produto(1, "Pão de queijo", new BigDecimal("4.50"), Categoria.SALGADO),
            new Produto(2, "Coxinha", new BigDecimal("7.00"), Categoria.SALGADO),
            new Produto(3, "Misto quente", new BigDecimal("9.00"), Categoria.SALGADO),
            new Produto(4, "Café", new BigDecimal("3.50"), Categoria.BEBIDA),
            new Produto(5, "Suco natural", new BigDecimal("8.00"), Categoria.BEBIDA),
            new Produto(6, "Brigadeiro", new BigDecimal("3.00"), Categoria.DOCE));

    public List<Produto> listar() {
        return produtos;
    }

    public Produto buscar(int id) {
        return produtos.stream()
                .filter(produto -> produto.id() == id)
                .findFirst()
                .orElseThrow(() -> new NaoEncontradoException("Produto " + id + " não encontrado"));
    }
}
