package br.com.cantina.pedido;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import br.com.cantina.NaoEncontradoException;

@Component
public class PedidosEmMemoria {

    private final List<Pedido> pedidos = new CopyOnWriteArrayList<>();

    public void adicionar(Pedido pedido) {
        pedidos.add(pedido);
    }

    public void remover(Pedido pedido) {
        pedidos.remove(pedido);
    }

    public Pedido buscar(int id) {
        return pedidos.stream()
                .filter(pedido -> pedido.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NaoEncontradoException("Pedido " + id + " não encontrado"));
    }

    public List<Pedido> listar() {
        return List.copyOf(pedidos);
    }
}
