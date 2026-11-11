package br.com.cantina.comando;

import br.com.cantina.cardapio.Categoria;
import br.com.cantina.cardapio.Produto;
import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.PedidoBuilder;
import br.com.cantina.pedido.PedidosEmMemoria;
import br.com.cantina.pedido.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ComandosTest {
    private Pedido pedido;
    private PedidosEmMemoria pedidos;

    @BeforeEach
    void setUp() {
        pedidos = new PedidosEmMemoria();
        Produto p = new Produto(1, "Café", new BigDecimal("3.50"), Categoria.BEBIDA);
        pedido = new PedidoBuilder()
                .paraCliente("João")
                .adicionarItem(p, 1)
                .build();
    }

    @Test
    void registrarPedido() {
        Comando cmd = new RegistrarPedidoComando(pedido, pedidos);
        cmd.executar();
        assertEquals(1, pedidos.listar().size());
        cmd.desfazer();
        assertEquals(0, pedidos.listar().size());
    }

    @Test
    void aplicarDesconto() {
        Comando cmd = new AplicarDescontoComando(pedido, 10);
        cmd.executar();
        assertEquals(10, pedido.getDescontoPercentual());
        cmd.desfazer();
        assertEquals(0, pedido.getDescontoPercentual());
    }

    @Test
    void aplicarDescontoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new AplicarDescontoComando(pedido, 60).executar());
        new MarcarProntoComando(pedido).executar();
        assertThrows(IllegalStateException.class, () -> new AplicarDescontoComando(pedido, 10).executar());
    }

    @Test
    void marcarPronto() {
        Comando cmd = new MarcarProntoComando(pedido);
        cmd.executar();
        assertEquals(StatusPedido.PRONTO, pedido.getStatus());
        cmd.desfazer();
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
    }

    @Test
    void marcarProntoInvalido() {
        new MarcarProntoComando(pedido).executar();
        assertThrows(IllegalStateException.class, () -> new MarcarProntoComando(pedido).executar());
    }

    @Test
    void entregarPedido() {
        new MarcarProntoComando(pedido).executar();
        Comando cmd = new EntregarPedidoComando(pedido);
        cmd.executar();
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
        cmd.desfazer();
        assertEquals(StatusPedido.PRONTO, pedido.getStatus());
    }

    @Test
    void entregarPedidoInvalido() {
        assertThrows(IllegalStateException.class, () -> new EntregarPedidoComando(pedido).executar());
    }

    @Test
    void cancelarPedido() {
        Comando cmd = new CancelarPedidoComando(pedido);
        cmd.executar();
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        cmd.desfazer();
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
    }

    @Test
    void cancelarPedidoInvalido() {
        new MarcarProntoComando(pedido).executar();
        new EntregarPedidoComando(pedido).executar();
        assertThrows(IllegalStateException.class, () -> new CancelarPedidoComando(pedido).executar());
    }
}