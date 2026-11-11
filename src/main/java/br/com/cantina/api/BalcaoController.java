package br.com.cantina.api;

import br.com.cantina.comando.*;
import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.PedidosEmMemoria;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class BalcaoController {

    private final Balcao balcao;
    private final PedidosEmMemoria pedidos;

    public BalcaoController(Balcao balcao, PedidosEmMemoria pedidos) {
        this.balcao = balcao;
        this.pedidos = pedidos;
    }

    @PostMapping("/pedidos/{id}/desconto")
    public PedidoResposta aplicarDesconto(@PathVariable int id, @RequestBody DescontoRequisicao requisicao) {
        Pedido pedido = pedidos.buscar(id);
        balcao.executar(new AplicarDescontoComando(pedido, requisicao.percentual()));
        return new PedidoResposta(pedido);
    }

    @PostMapping("/pedidos/{id}/pronto")
    public PedidoResposta marcarPronto(@PathVariable int id) {
        Pedido pedido = pedidos.buscar(id);
        balcao.executar(new MarcarProntoComando(pedido));
        return new PedidoResposta(pedido);
    }

    @PostMapping("/pedidos/{id}/entrega")
    public PedidoResposta entregarPedido(@PathVariable int id) {
        Pedido pedido = pedidos.buscar(id);
        balcao.executar(new EntregarPedidoComando(pedido));
        return new PedidoResposta(pedido);
    }

    @PostMapping("/pedidos/{id}/cancelamento")
    public PedidoResposta cancelarPedido(@PathVariable int id) {
        Pedido pedido = pedidos.buscar(id);
        balcao.executar(new CancelarPedidoComando(pedido));
        return new PedidoResposta(pedido);
    }

    @PostMapping("/balcao/desfazer")
    public Map<String, String> desfazer() {
        String descricao = balcao.desfazer();
        return Map.of("desfeito", descricao);
    }

    @GetMapping("/balcao/historico")
    public List<String> historico() {
        return balcao.historico();
    }
}