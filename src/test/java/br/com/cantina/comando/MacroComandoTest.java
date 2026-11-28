package br.com.cantina.comando;

import br.com.cantina.cardapio.Categoria;
import br.com.cantina.cardapio.Produto;
import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.PedidoBuilder;
import br.com.cantina.pedido.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MacroComandoTest {
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        Produto cafe = new Produto(4, "Café", new BigDecimal("3.50"), Categoria.BEBIDA);
        pedido = new PedidoBuilder().paraCliente("João").adicionarItem(cafe, 1).build();
    }

    @Test
    void finalizarLevaPedidoAteEntregueEDesfazTudo() {
        Comando finalizar = MacroComando.finalizar(pedido);

        finalizar.executar();
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());

        finalizar.desfazer();
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
    }

    @Test
    void executaEmOrdemEDesfazEmOrdemInversa() {
        List<String> registro = new ArrayList<>();
        MacroComando macro = new MacroComando("Macro", List.of(
            new ComandoRegistrado("A", registro),
            new ComandoRegistrado("B", registro)));

        macro.executar();
        macro.desfazer();

        assertEquals(List.of("executar A", "executar B", "desfazer B", "desfazer A"), registro);
    }

    @Test
    void falhaNoMeioDesfazOsComandosJaExecutados() {
        new MarcarProntoComando(pedido).executar();
        Comando finalizar = MacroComando.finalizar(pedido);

        assertThrows(IllegalStateException.class, finalizar::executar);
        assertEquals(StatusPedido.PRONTO, pedido.getStatus());
    }

    @Test
    void rollbackQuandoUmaEtapaPosteriorFalha() {
        List<String> registro = new ArrayList<>();
        Comando falha = new Comando() {
            @Override public void executar() { throw new IllegalStateException("Falhou"); }
            @Override public void desfazer() { registro.add("desfazer falha"); }
            @Override public String descricao() { return "Falha"; }
        };
        MacroComando macro = new MacroComando("Macro", List.of(new ComandoRegistrado("A", registro), falha));

        assertThrows(IllegalStateException.class, macro::executar);
        assertEquals(List.of("executar A", "desfazer A"), registro);
    }

    @Test
    void macroQueFalhaNaoEntraNoHistorico() {
        Balcao balcao = new Balcao();
        new MarcarProntoComando(pedido).executar();

        assertThrows(IllegalStateException.class, () -> balcao.executar(MacroComando.finalizar(pedido)));
        assertTrue(balcao.historico().isEmpty());
    }

    @Test
    void macroEntraNoHistoricoComoUmaAcao() {
        Balcao balcao = new Balcao();
        balcao.executar(MacroComando.finalizar(pedido));

        assertEquals(List.of("Finalizar pedido #" + pedido.getId() + " (pronto + entrega)"), balcao.historico());
        balcao.desfazer();
        assertEquals(StatusPedido.RECEBIDO, pedido.getStatus());
    }

    @Test
    void recusaMacroSemComandos() {
        assertThrows(IllegalArgumentException.class, () -> new MacroComando("Vazio", List.of()));
    }

    private static class ComandoRegistrado implements Comando {
        private final String nome;
        private final List<String> registro;

        ComandoRegistrado(String nome, List<String> registro) {
            this.nome = nome;
            this.registro = registro;
        }

        @Override public void executar() { registro.add("executar " + nome); }
        @Override public void desfazer() { registro.add("desfazer " + nome); }
        @Override public String descricao() { return nome; }
    }
}
