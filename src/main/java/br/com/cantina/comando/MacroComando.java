package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;

import java.util.ArrayList;
import java.util.List;

public class MacroComando implements Comando {
    private final String descricao;
    private final List<Comando> comandos;
    private final List<Comando> executados = new ArrayList<>();

    public MacroComando(String descricao, List<Comando> comandos) {
        if (comandos == null || comandos.isEmpty()) {
            throw new IllegalArgumentException("Macro precisa de pelo menos um comando.");
        }
        this.descricao = descricao;
        this.comandos = List.copyOf(comandos);
    }

    public static MacroComando finalizar(Pedido pedido) {
        return new MacroComando(
            "Finalizar pedido #" + pedido.getId() + " (pronto + entrega)",
            List.of(new MarcarProntoComando(pedido), new EntregarPedidoComando(pedido)));
    }

    @Override
    public void executar() {
        executados.clear();
        try {
            for (Comando comando : comandos) {
                comando.executar();
                executados.add(comando);
            }
        } catch (RuntimeException e) {
            desfazerExecutados();
            throw e;
        }
    }

    @Override
    public void desfazer() {
        desfazerExecutados();
    }

    @Override
    public String descricao() {
        return descricao;
    }

    private void desfazerExecutados() {
        for (int i = executados.size() - 1; i >= 0; i--) {
            executados.get(i).desfazer();
        }
        executados.clear();
    }
}
