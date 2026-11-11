package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.PedidosEmMemoria;

public class RegistrarPedidoComando implements Comando {
    private final Pedido pedido;
    private final PedidosEmMemoria pedidos;

    public RegistrarPedidoComando(Pedido pedido, PedidosEmMemoria pedidos) {
        this.pedido = pedido;
        this.pedidos = pedidos;
    }

    @Override
    public void executar() {
        pedidos.adicionar(pedido);
    }

    @Override
    public void desfazer() {
        pedidos.remover(pedido);
    }

    @Override
    public String descricao() {
        return "Registrar pedido #" + pedido.getId();
    }
}