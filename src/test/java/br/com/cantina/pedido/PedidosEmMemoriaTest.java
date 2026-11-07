package br.com.cantina.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.cantina.NaoEncontradoException;
import br.com.cantina.cardapio.Categoria;
import br.com.cantina.cardapio.Produto;

class PedidosEmMemoriaTest {

    private static final Produto COXINHA = new Produto(2, "Coxinha", new BigDecimal("7.00"), Categoria.SALGADO);

    private final PedidosEmMemoria pedidos = new PedidosEmMemoria();

    private static Pedido novoPedido(String cliente) {
        return new Pedido(cliente, List.of(new ItemPedido(COXINHA, 1)), false, null);
    }

    @Test
    void listaNaOrdemEmQueForamAdicionados() {
        Pedido ana = novoPedido("Ana");
        Pedido bruno = novoPedido("Bruno");
        Pedido carla = novoPedido("Carla");

        pedidos.adicionar(bruno);
        pedidos.adicionar(ana);
        pedidos.adicionar(carla);

        assertEquals(List.of(bruno, ana, carla), pedidos.listar());
    }

    @Test
    void buscaPeloId() {
        Pedido ana = novoPedido("Ana");
        Pedido bruno = novoPedido("Bruno");
        pedidos.adicionar(ana);
        pedidos.adicionar(bruno);

        assertSame(bruno, pedidos.buscar(bruno.getId()));
    }

    @Test
    void removerTiraOPedidoDaLista() {
        Pedido ana = novoPedido("Ana");
        Pedido bruno = novoPedido("Bruno");
        pedidos.adicionar(ana);
        pedidos.adicionar(bruno);

        pedidos.remover(ana);

        assertEquals(List.of(bruno), pedidos.listar());
        assertThrows(NaoEncontradoException.class, () -> pedidos.buscar(ana.getId()));
    }

    @Test
    void buscarIdInexistenteLancaNaoEncontrado() {
        NaoEncontradoException erro = assertThrows(NaoEncontradoException.class, () -> pedidos.buscar(9));

        assertEquals("Pedido 9 não encontrado", erro.getMessage());
    }

    @Test
    void listarDevolveUmaCopiaQueNaoPodeSerAlterada() {
        pedidos.adicionar(novoPedido("Ana"));

        List<Pedido> lista = pedidos.listar();
        pedidos.adicionar(novoPedido("Bruno"));

        assertEquals(1, lista.size());
        assertThrows(UnsupportedOperationException.class, () -> lista.add(novoPedido("Carla")));
    }
}
