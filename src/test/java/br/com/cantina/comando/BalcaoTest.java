package br.com.cantina.comando;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void executarUltimoDesfeitoRefazAAcao() {
        ComandoContador cmd = new ComandoContador();
        balcao.executar(cmd);
        balcao.desfazer();

        Comando desfeito = balcao.ultimoDesfeito();
        assertSame(cmd, desfeito);
        balcao.executar(desfeito);

        assertEquals(2, cmd.execucoes);
        assertEquals(List.of("Contador"), balcao.historico());
        assertThrows(IllegalStateException.class, () -> balcao.ultimoDesfeito());
    }

    @Test
    void refazVariasAcoesNaOrdemEmQueForamDesfeitas() {
        Comando cmd1 = new ComandoFalso("Ação 1");
        Comando cmd2 = new ComandoFalso("Ação 2");
        balcao.executar(cmd1);
        balcao.executar(cmd2);
        balcao.desfazer();
        balcao.desfazer();

        balcao.executar(balcao.ultimoDesfeito());
        balcao.executar(balcao.ultimoDesfeito());

        assertEquals(List.of("Ação 1", "Ação 2"), balcao.historico());
    }

    @Test
    void acaoNovaDescartaOQueFoiDesfeito() {
        balcao.executar(new ComandoFalso("Ação 1"));
        balcao.desfazer();

        balcao.executar(new ComandoFalso("Ação 2"));

        assertThrows(IllegalStateException.class, () -> balcao.ultimoDesfeito());
    }

    @Test
    void semAcaoDesfeitaNaoHaOQueRefazer() {
        IllegalStateException erro = assertThrows(IllegalStateException.class, () -> balcao.ultimoDesfeito());
        assertEquals("Não há ações para refazer.", erro.getMessage());
    }

    private static class ComandoContador implements Comando {
        private int execucoes;
        @Override public void executar() { execucoes++; }
        @Override public void desfazer() {}
        @Override public String descricao() { return "Contador"; }
    }

    private static class ComandoFalso implements Comando {
        private final String nome;
        public ComandoFalso(String nome) { this.nome = nome; }
        @Override public void executar() {}
        @Override public void desfazer() {}
        @Override public String descricao() { return nome; }
    }
}