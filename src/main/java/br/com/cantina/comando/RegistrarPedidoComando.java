package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.PedidosEmMemoria;

public class RegistrarPedidoComando implements Comando {

    public RegistrarPedidoComando(Pedido pedido, PedidosEmMemoria pedidos) {
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
