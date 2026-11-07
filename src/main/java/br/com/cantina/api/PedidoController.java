package br.com.cantina.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cantina.cardapio.Cardapio;
import br.com.cantina.comando.Balcao;
import br.com.cantina.pedido.PedidosEmMemoria;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final Cardapio cardapio;
    private final PedidosEmMemoria pedidos;
    private final Balcao balcao;

    public PedidoController(Cardapio cardapio, PedidosEmMemoria pedidos, Balcao balcao) {
        this.cardapio = cardapio;
        this.pedidos = pedidos;
        this.balcao = balcao;
    }

    @GetMapping
    public List<PedidoResposta> listar() {
        return pedidos.listar().stream().map(PedidoResposta::new).toList();
    }

    @GetMapping("/{id}")
    public PedidoResposta buscar(@PathVariable int id) {
        return new PedidoResposta(pedidos.buscar(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResposta criar(@RequestBody NovoPedidoRequisicao requisicao) {
        throw new UnsupportedOperationException("TODO");
    }
}
