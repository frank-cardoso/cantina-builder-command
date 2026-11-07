package br.com.cantina.pedido;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.cantina.cardapio.Categoria;
import br.com.cantina.cardapio.Produto;

class PedidoTest {

    private static final Produto PAO_DE_QUEIJO = new Produto(1, "Pão de queijo", new BigDecimal("4.50"), Categoria.SALGADO);
    private static final Produto CAFE = new Produto(4, "Café", new BigDecimal("3.50"), Categoria.BEBIDA);
    private static final Produto SUCO_NATURAL = new Produto(5, "Suco natural", new BigDecimal("8.00"), Categoria.BEBIDA);

    private static Pedido pedidoDaAna() {
        List<ItemPedido> itens = List.of(new ItemPedido(PAO_DE_QUEIJO, 2), new ItemPedido(SUCO_NATURAL, 1));
        return new Pedido("Ana", itens, true, "suco sem açúcar");
    }

    private static Pedido pedidoDe(Produto produto) {
        return new Pedido("Bruno", List.of(new ItemPedido(produto, 1)), false, null);
    }

    @Test
    void totalSemDescontoSomaOsSubtotais() {
        assertEquals(new BigDecimal("17.00"), pedidoDaAna().total());
    }

    @Test
    void totalComDezPorCentoDeDesconto() {
        Pedido pedido = pedidoDaAna();

        pedido.aplicarDesconto(10);

        assertEquals(new BigDecimal("15.30"), pedido.total());
    }

    @Test
    void totalArredondaMeioCentavoParaOParMaisProximo() {
        Pedido cafe = pedidoDe(CAFE);
        Pedido paoDeQueijo = pedidoDe(PAO_DE_QUEIJO);

        cafe.aplicarDesconto(1);
        paoDeQueijo.aplicarDesconto(1);

        assertEquals(new BigDecimal("3.46"), cafe.total());
        assertEquals(new BigDecimal("4.46"), paoDeQueijo.total());
    }

    @Test
    void descontoAceitaOsLimitesZeroECem() {
        Pedido pedido = pedidoDaAna();

        pedido.aplicarDesconto(100);
        assertEquals(new BigDecimal("0.00"), pedido.total());

        pedido.aplicarDesconto(0);
        assertEquals(new BigDecimal("17.00"), pedido.total());
    }

    @Test
    void descontoForaDeZeroACemLancaExcecao() {
        Pedido pedido = pedidoDaAna();

        assertThrows(IllegalArgumentException.class, () -> pedido.aplicarDesconto(-1));
        assertThrows(IllegalArgumentException.class, () -> pedido.aplicarDesconto(101));
        assertEquals(0, pedido.getDescontoPercentual());
    }

    @Test
    void pedidoNasceRecebidoESemDesconto() {
        Pedido pedido = pedidoDaAna();

        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
        assertEquals(0, pedido.getDescontoPercentual());
    }

    @Test
    void observacaoNulaViraTextoVazio() {
        assertEquals("", pedidoDe(CAFE).getObservacao());
    }

    @Test
    void cadaPedidoRecebeUmIdDiferente() {
        assertNotEquals(pedidoDaAna().getId(), pedidoDaAna().getId());
    }

    @Test
    void itensNaoMudamDepoisDeCriado() {
        List<ItemPedido> itens = new ArrayList<>(List.of(new ItemPedido(CAFE, 1)));
        Pedido pedido = new Pedido("Carla", itens, false, "");

        itens.add(new ItemPedido(PAO_DE_QUEIJO, 1));

        assertEquals(1, pedido.getItens().size());
        assertThrows(UnsupportedOperationException.class, () -> pedido.getItens().add(new ItemPedido(CAFE, 1)));
    }

    @Test
    void alterarStatusAceitaQualquerStatusMenosNulo() {
        Pedido pedido = pedidoDaAna();

        pedido.alterarStatus(StatusPedido.ENTREGUE);

        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
        assertThrows(IllegalArgumentException.class, () -> pedido.alterarStatus(null));
    }
}
