package br.com.cantina.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cantina.cardapio.Cardapio;
import br.com.cantina.cardapio.Produto;

@RestController
public class CardapioController {

    private final Cardapio cardapio;

    public CardapioController(Cardapio cardapio) {
        this.cardapio = cardapio;
    }

    @GetMapping("/cardapio")
    public List<Produto> listar() {
        return cardapio.listar();
    }
}
