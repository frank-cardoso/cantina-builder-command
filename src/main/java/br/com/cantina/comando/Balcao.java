package br.com.cantina.comando;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@Component
public class Balcao {
    private final Stack<Comando> historico = new Stack<>();
    private final Stack<Comando> desfeitos = new Stack<>();

    public void executar(Comando comando) {
        comando.executar();
        historico.push(comando);
        if (!desfeitos.isEmpty() && desfeitos.peek() == comando) {
            desfeitos.pop();
        } else {
            desfeitos.clear();
        }
    }

    public String desfazer() {
        if (historico.isEmpty()) {
            throw new IllegalStateException("Não há ações para desfazer.");
        }
        Comando ultimo = historico.pop();
        ultimo.desfazer();
        desfeitos.push(ultimo);
        return ultimo.descricao();
    }

    public Comando ultimoDesfeito() {
        if (desfeitos.isEmpty()) {
            throw new IllegalStateException("Não há ações para refazer.");
        }
        return desfeitos.peek();
    }

    public List<String> historico() {
        return historico.stream()
            .map(Comando::descricao)
            .collect(Collectors.toList());
    }
}
