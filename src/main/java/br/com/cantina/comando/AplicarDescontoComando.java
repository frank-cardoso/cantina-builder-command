package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;

public class AplicarDescontoComando implements Comando {

    public AplicarDescontoComando(Pedido pedido, int percentual) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void executar() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void desfazer() {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public String descricao() {
        throw new UnsupportedOperationException("TODO");
    }
}
