package br.com.cantina.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ExtensaoApiTest {

    @Autowired
    private MockMvc mvc;

    private int criarPedido() throws Exception {
        String corpo = mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente":"Ana","itens":[{"produtoId":1,"quantidade":2},{"produtoId":5,"quantidade":1}],"paraViagem":true,"observacao":"suco sem açúcar"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(corpo, "$.id");
    }

    @Test
    void repetirPedidoCriaNovoPedidoComOsMesmosDados() throws Exception {
        int id = criarPedido();
        mvc.perform(post("/pedidos/" + id + "/cancelamento")).andExpect(status().isOk());

        mvc.perform(post("/pedidos/" + id + "/repeticao"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(Matchers.not(id)))
                .andExpect(jsonPath("$.cliente").value("Ana"))
                .andExpect(jsonPath("$.itens.length()").value(2))
                .andExpect(jsonPath("$.itens[0].produtoId").value(1))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.paraViagem").value(true))
                .andExpect(jsonPath("$.observacao").value("suco sem açúcar"))
                .andExpect(jsonPath("$.status").value("RECEBIDO"))
                .andExpect(jsonPath("$.total").value(17.00));
    }

    @Test
    void repetirPedidoInexistenteDevolve404() throws Exception {
        mvc.perform(post("/pedidos/9999/repeticao"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Pedido 9999 não encontrado"));
    }

    @Test
    void finalizarEDesfazerComoUmaUnicaAcao() throws Exception {
        int id = criarPedido();

        mvc.perform(post("/pedidos/" + id + "/finalizacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENTREGUE"));

        mvc.perform(post("/balcao/desfazer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.desfeito").value("Finalizar pedido #" + id + " (pronto + entrega)"));

        mvc.perform(post("/pedidos/" + id + "/finalizacao")).andExpect(status().isOk());
        mvc.perform(post("/pedidos/" + id + "/finalizacao"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Só pedidos recebidos podem ser marcados como prontos."));
    }

    @Test
    void alterarQuantidadeDeItemAtualizaTotal() throws Exception {
        int id = criarPedido();

        mvc.perform(post("/pedidos/" + id + "/itens/1/quantidade").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].quantidade").value(4))
                .andExpect(jsonPath("$.total").value(26.00));

        mvc.perform(post("/pedidos/" + id + "/itens/2/quantidade").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Produto 2 não está no pedido #" + id));

        mvc.perform(post("/pedidos/" + id + "/pronto")).andExpect(status().isOk());
        mvc.perform(post("/pedidos/" + id + "/itens/1/quantidade").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantidade\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Só é possível alterar itens de pedidos recebidos."));
    }

    @Test
    void refazerReaplicaAUltimaAcaoDesfeita() throws Exception {
        int id = criarPedido();
        mvc.perform(post("/pedidos/" + id + "/desconto").contentType(MediaType.APPLICATION_JSON)
                .content("{\"percentual\": 10}")).andExpect(status().isOk());
        mvc.perform(post("/balcao/desfazer")).andExpect(status().isOk());

        mvc.perform(post("/balcao/refazer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refeito").value("Aplicar desconto de 10% no pedido #" + id));

        mvc.perform(post("/balcao/refazer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Não há ações para refazer."));
    }
}
