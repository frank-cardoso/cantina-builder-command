package br.com.cantina.comando;

import br.com.cantina.pedido.Pedido;
import br.com.cantina.pedido.StatusPedido;

public class AlterarQuantidadeItemComando implements Comando {
    private final Pedido pedido;
    private final int produtoId;
    private final int quantidade;
    private int quantidadeAnterior;

    public AlterarQuantidadeItemComando(Pedido pedido, int produtoId, int quantidade) {
        this.pedido = pedido;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    @Override
    public void executar() {
        if (pedido.getStatus() != StatusPedido.RECEBIDO) {
            throw new IllegalStateException("Só é possível alterar itens de pedidos recebidos.");
        }
        if (quantidade < 1) {
            throw new IllegalArgumentException("A quantidade deve ser pelo menos 1.");
        }
        int atual = pedido.quantidadeDe(produtoId);
        pedido.alterarQuantidade(produtoId, quantidade);
        this.quantidadeAnterior = atual;
    }

    @Override
    public void desfazer() {
        pedido.alterarQuantidade(produtoId, quantidadeAnterior);
    }

    @Override
    public String descricao() {
        return "Alterar quantidade do produto " + produtoId + " para " + quantidade + " no pedido #" + pedido.getId();
    }
}
