package br.com.cantina.api;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cantina.comando.Balcao;
import br.com.cantina.pedido.PedidosEmMemoria;

@RestController
public class BalcaoController {

    private final PedidosEmMemoria pedidos;
    private final Balcao balcao;

    public BalcaoController(PedidosEmMemoria pedidos, Balcao balcao) {
        this.pedidos = pedidos;
        this.balcao = balcao;
    }

    @PostMapping("/pedidos/{id}/desconto")
    public PedidoResposta aplicarDesconto(@PathVariable int id, @RequestBody DescontoRequisicao requisicao) {
        throw new UnsupportedOperationException("TODO");
    }

    @PostMapping("/pedidos/{id}/pronto")
    public PedidoResposta marcarPronto(@PathVariable int id) {
        throw new UnsupportedOperationException("TODO");
    }

    @PostMapping("/pedidos/{id}/entrega")
    public PedidoResposta entregar(@PathVariable int id) {
        throw new UnsupportedOperationException("TODO");
    }

    @PostMapping("/pedidos/{id}/cancelamento")
    public PedidoResposta cancelar(@PathVariable int id) {
        throw new UnsupportedOperationException("TODO");
    }

    @PostMapping("/balcao/desfazer")
    public Map<String, String> desfazer() {
        throw new UnsupportedOperationException("TODO");
    }

    @GetMapping("/balcao/historico")
    public List<String> historico() {
        throw new UnsupportedOperationException("TODO");
    }
}
