package br.com.cantina.comando;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Component
public class Balcao {
    private final Stack<Comando> historico = new Stack<>();

    public void executar(Comando comando) {
        comando.executar();
        historico.push(comando);
    }

    public String desfazer() {
        if (historico.isEmpty()) {
            throw new IllegalStateException("Não há ações para desfazer.");
        }
        Comando ultimo = historico.pop();
        ultimo.desfazer();
        return ultimo.descricao();
    }

    public List<String> historico() {
        return historico.stream()
            .map(Comando::descricao)
            .collect(Collectors.toList());
    }
}