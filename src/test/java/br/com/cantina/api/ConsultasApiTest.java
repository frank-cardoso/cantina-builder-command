package br.com.cantina.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultasApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void cardapioDevolveOsSeisProdutosNoFormatoDoContrato() throws Exception {
        mvc.perform(get("/cardapio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Pão de queijo"))
                .andExpect(jsonPath("$[0].preco").value(4.50))
                .andExpect(jsonPath("$[0].categoria").value("SALGADO"));
    }

    @Test
    void pedidoInexistenteDevolve404ComErro() throws Exception {
        mvc.perform(get("/pedidos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Pedido 999 não encontrado"));
    }

    @Test
    void idQueNaoENumeroDevolve400ComErro() throws Exception {
        mvc.perform(get("/pedidos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Valor inválido para 'id': abc"));
    }

    @Test
    void jsonInvalidoNoCorpoDevolve400ComErro() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON).content("{\"itens\": \"x\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Corpo da requisição ausente ou com JSON inválido"));
    }
}
