package br.com.cantina.cardapio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.cantina.NaoEncontradoException;

class CardapioTest {

    private final Cardapio cardapio = new Cardapio();

    @Test
    void listaOsSeisProdutosFixos() {
        List<Integer> ids = cardapio.listar().stream().map(Produto::id).toList();

        assertEquals(List.of(1, 2, 3, 4, 5, 6), ids);
    }

    @Test
    void buscaProdutoPeloId() {
        Produto suco = cardapio.buscar(5);

        assertEquals("Suco natural", suco.nome());
        assertEquals(new BigDecimal("8.00"), suco.preco());
        assertEquals(Categoria.BEBIDA, suco.categoria());
    }

    @Test
    void buscarIdInexistenteLancaNaoEncontrado() {
        NaoEncontradoException erro = assertThrows(NaoEncontradoException.class, () -> cardapio.buscar(99));

        assertEquals("Produto 99 não encontrado", erro.getMessage());
    }
}
