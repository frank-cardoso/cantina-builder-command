package br.com.cantina.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.com.cantina.cardapio.Categoria;
import br.com.cantina.cardapio.Produto;

class PedidoBuilderTest {

    private static final Produto PAO_DE_QUEIJO = new Produto(1, "Pão de queijo", new BigDecimal("4.50"), Categoria.SALGADO);
    private static final Produto SUCO_NATURAL = new Produto(5, "Suco natural", new BigDecimal("8.00"), Categoria.BEBIDA);

    @Test
    void montaPedidoCompleto() {
        Pedido pedido = new PedidoBuilder()
                .paraCliente("Ana")
                .adicionarItem(PAO_DE_QUEIJO, 2)
                .adicionarItem(SUCO_NATURAL, 1)
                .paraViagem()
                .comObservacao("suco sem açúcar")
                .build();

        assertEquals("Ana", pedido.getCliente());
        assertEquals(2, pedido.getItens().size());
        assertTrue(pedido.isParaViagem());
        assertEquals("suco sem açúcar", pedido.getObservacao());
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
        assertEquals(new BigDecimal("17.00"), pedido.total());
    }

    @Test
    void montaPedidoSoComOsObrigatorios() {
        Pedido pedido = new PedidoBuilder()
                .paraCliente("Bruno")
                .adicionarItem(PAO_DE_QUEIJO, 1)
                .build();

        assertFalse(pedido.isParaViagem());
        assertEquals("", pedido.getObservacao());
    }

    @Test
    void recusaPedidoSemCliente() {
        PedidoBuilder semNome = new PedidoBuilder().adicionarItem(PAO_DE_QUEIJO, 1);
        PedidoBuilder nomeEmBranco = new PedidoBuilder().paraCliente("   ").adicionarItem(PAO_DE_QUEIJO, 1);

        assertEquals("Cliente é obrigatório", assertThrows(IllegalStateException.class, semNome::build).getMessage());
        assertEquals("Cliente é obrigatório", assertThrows(IllegalStateException.class, nomeEmBranco::build).getMessage());
    }

    @Test
    void recusaPedidoSemItem() {
        PedidoBuilder builder = new PedidoBuilder().paraCliente("Ana");

        IllegalStateException erro = assertThrows(IllegalStateException.class, builder::build);

        assertEquals("Pedido precisa de pelo menos 1 item", erro.getMessage());
    }

    @Test
    void recusaQuantidadeZero() {
        PedidoBuilder builder = new PedidoBuilder().paraCliente("Ana").adicionarItem(PAO_DE_QUEIJO, 0);

        IllegalStateException erro = assertThrows(IllegalStateException.class, builder::build);

        assertEquals("Quantidade de Pão de queijo deve ser pelo menos 1", erro.getMessage());
    }

    @Test
    void recusaProdutoNulo() {
        PedidoBuilder builder = new PedidoBuilder().paraCliente("Ana").adicionarItem(null, 1);

        IllegalStateException erro = assertThrows(IllegalStateException.class, builder::build);

        assertEquals("Produto do item não pode ser nulo", erro.getMessage());
    }

    @Test
    void recusaObservacaoLongaDemais() {
        PedidoBuilder builder = new PedidoBuilder()
                .paraCliente("Ana")
                .adicionarItem(PAO_DE_QUEIJO, 1)
                .comObservacao("a".repeat(101));

        IllegalStateException erro = assertThrows(IllegalStateException.class, builder::build);

        assertEquals("Observação deve ter no máximo 100 caracteres", erro.getMessage());
    }

    @Test
    void aceitaObservacaoComExatamenteCemCaracteres() {
        Pedido pedido = new PedidoBuilder()
                .paraCliente("Ana")
                .adicionarItem(PAO_DE_QUEIJO, 1)
                .comObservacao("a".repeat(100))
                .build();

        assertEquals(100, pedido.getObservacao().length());
    }
}
