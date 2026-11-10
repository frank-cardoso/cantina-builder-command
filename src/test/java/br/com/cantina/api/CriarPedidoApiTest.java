package br.com.cantina.api;

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
class CriarPedidoApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void pedidoSemClienteDevolve400ComMensagem() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente": "", "itens": [{"produtoId": 1, "quantidade": 2}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Cliente é obrigatório"));
    }

    @Test
    void pedidoSemItensDevolve400ComMensagem() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente": "Ana"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Pedido precisa de pelo menos 1 item"));
    }

    @Test
    void produtoInexistenteDevolve404() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cliente": "Ana", "itens": [{"produtoId": 99, "quantidade": 1}]}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Produto 99 não encontrado"));
    }
}
