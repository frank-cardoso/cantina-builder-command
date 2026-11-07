package br.com.cantina.comando;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Balcao {

    // Só entra no histórico o comando cujo executar() não lançou exceção.
    public void executar(Comando comando) {
        throw new UnsupportedOperationException("TODO");
    }

    public String desfazer() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<String> historico() {
        throw new UnsupportedOperationException("TODO");
    }
}
