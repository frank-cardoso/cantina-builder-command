package br.com.cantina.comando;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BalcaoTest {
    private Balcao balcao;

    @BeforeEach
    void setUp() {
        balcao = new Balcao();
    }

    @Test
    void desfazEmOrdemInversa() {
        Comando cmd1 = new ComandoFalso("Ação 1");
        Comando cmd2 = new ComandoFalso("Ação 2");

        balcao.executar(cmd1);
        balcao.executar(cmd2);

        assertEquals("Ação 2", balcao.desfazer());
        assertEquals("Ação 1", balcao.desfazer());
    }

    @Test
    void desfazerComHistoricoVazioLancaExcecao() {
        assertThrows(IllegalStateException.class, () -> balcao.desfazer());
    }

    @Test
    void comandoQueFalhouNaoEntraNoHistorico() {
        Comando cmdFalho = new Comando() {
            @Override public void executar() { throw new IllegalArgumentException("Erro"); }
            @Override public void desfazer() {}
            @Override public String descricao() { return "Falha"; }
        };

        assertThrows(IllegalArgumentException.class, () -> balcao.executar(cmdFalho));
        assertThrows(IllegalStateException.class, () -> balcao.desfazer()); 
    }

    private static class ComandoFalso implements Comando {
        private final String nome;
        public ComandoFalso(String nome) { this.nome = nome; }
        @Override public void executar() {}
        @Override public void desfazer() {}
        @Override public String descricao() { return nome; }
    }
}