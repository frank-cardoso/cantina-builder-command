# Cantina — Builder + Command

Sistema de cantina para o projeto final de Design Patterns. O atendente escolhe os produtos, monta um pedido com o **Builder** e acompanha as ações do balcão pelo padrão **Command**, com histórico e desfazer.

A aplicação usa dados em memória: não há banco de dados, e os pedidos são apagados quando o servidor reinicia. O plano e os contratos entre as frentes estão em [docs/PLANO.md](docs/PLANO.md).

## Tecnologias e pré-requisitos

| Item | Tecnologia |
| --- | --- |
| Linguagem | Java 25 |
| Backend | Spring Boot 4.1.2, Spring Web MVC |
| Build | Maven, pelo wrapper incluído no repositório |
| Frontend | HTML, CSS e JavaScript puro, servidos pelo Spring Boot |
| Documentação da API | Swagger UI, com springdoc 3.2.0 |
| Testes | JUnit Jupiter e MockMvc, pelo starter de testes do Spring Boot |

Instale um **JDK 25** e configure `JAVA_HOME` para ele. Confira com `java -version`. Não é necessário instalar Maven, Node.js ou ferramentas de build do frontend. Na primeira execução, é necessário acesso aos repositórios Maven para baixar as dependências.

## Como executar

Depois de clonar o repositório, entre na pasta do projeto e execute:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

- **Tela da cantina:** http://localhost:8080/
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI:** http://localhost:8080/v3/api-docs

O Spring Boot serve `src/main/resources/static/index.html` na raiz. A tela usa `fetch` na mesma origem para acessar a API; não precisa de servidor separado, configuração de CORS ou instalação de pacotes JavaScript. Abra a tela pelo endereço do Spring, não diretamente pelo arquivo HTML.

Para executar os testes:

```bash
./mvnw test
# Windows: .\mvnw.cmd test
```

## Como usar

### Pela tela

1. Abra http://localhost:8080/.
2. Preencha o nome do cliente e a quantidade dos produtos desejados. Deixe em branco os produtos que não entram no pedido.
3. Se necessário, marque **Para viagem** e escreva uma observação.
4. Clique em **Montar pedido**. Depois da confirmação da API, o formulário é limpo e o pedido aparece no balcão, com itens, subtotais, status, desconto e total retornados pelo servidor.
5. No pedido, informe o percentual e clique em **Desconto**, ou use **Pronto**, **Entregar** e **Cancelar**.
6. Confira o histórico e use **Desfazer última ação** para reverter o último comando do balcão, inclusive o registro de um pedido. O histórico é global, compartilhado entre todos os pedidos.
7. Use **Atualizar balcão** para consultar novamente os pedidos e o histórico, inclusive após ações em outra aba ou pelo Swagger. Os campos do pedido em montagem são preservados nessa atualização.

Todos os botões de ação aparecem em todos os status. O JavaScript apenas envia os dados e apresenta a resposta: o backend calcula valores, valida o pedido e decide se uma ação é permitida. Mensagens no formato `{"erro": "..."}` são exibidas com o texto devolvido pela API. Em caso de falha ao montar o pedido, os dados preenchidos são mantidos para correção ou nova tentativa.

### Roteiro de demonstração

1. Monte um pedido para **Ana**, com **2 pães de queijo** e **1 suco natural**, para viagem, com a observação **suco sem açúcar**. O total deve ser **R$ 17,00**, com status `RECEBIDO`.
2. Aplique **10%** de desconto. O total deve passar para **R$ 15,30**.
3. Clique em **Desfazer última ação**. O desconto volta a 0% e o total a **R$ 17,00**.
4. Clique em **Pronto** e depois em **Entregar**. O pedido passa por `PRONTO` até `ENTREGUE`.
5. Tente **Cancelar** o pedido entregue. A tela deve mostrar a mensagem da API: **Pedido entregue ou já cancelado não pode ser cancelado.**.

O número do pedido é gerado pelo servidor e pode variar; não é necessário que ele seja `#1`.

### Pela API

Exemplo de criação de pedido, em um terminal com `curl`:

```bash
curl -i -X POST http://localhost:8080/pedidos \
  -H 'Content-Type: application/json' \
  -d '{"cliente":"Ana","itens":[{"produtoId":1,"quantidade":2},{"produtoId":5,"quantidade":1}],"paraViagem":true,"observacao":"suco sem açúcar"}'
```

A resposta é `201 Created`, com o pedido criado. Use o `id` retornado no lugar de `1` nos exemplos seguintes:

```bash
curl -X POST http://localhost:8080/pedidos/1/desconto \
  -H 'Content-Type: application/json' -d '{"percentual":10}'
curl -X POST http://localhost:8080/balcao/desfazer
curl -X POST http://localhost:8080/pedidos/1/pronto
curl -X POST http://localhost:8080/pedidos/1/entrega
curl -X POST http://localhost:8080/pedidos/1/cancelamento
```

| Método | Caminho | Uso |
| --- | --- | --- |
| GET | `/cardapio` | Listar os produtos |
| GET | `/pedidos` | Listar os pedidos |
| GET | `/pedidos/{id}` | Consultar um pedido |
| POST | `/pedidos` | Montar e registrar um pedido |
| POST | `/pedidos/{id}/desconto` | Aplicar `{"percentual": 10}` |
| POST | `/pedidos/{id}/pronto` | Marcar como pronto |
| POST | `/pedidos/{id}/entrega` | Entregar |
| POST | `/pedidos/{id}/cancelamento` | Cancelar, mantendo o pedido para permitir desfazer |
| POST | `/balcao/desfazer` | Desfazer a última ação global |
| GET | `/balcao/historico` | Consultar descrições da mais antiga para a mais nova |

Erros de validação usam HTTP 400; produtos e pedidos inexistentes usam HTTP 404. Ambos são apresentados no formato `{"erro": "mensagem"}`.

## Entidades e organização

- **Produto:** item do cardápio, com id, nome, preço e categoria. Os seis produtos são definidos em `cardapio/Cardapio.java`.
- **Pedido:** cliente, itens, opção para viagem, observação, status e desconto. O total usa `BigDecimal`, com duas casas decimais e arredondamento `HALF_EVEN`.
- **ItemPedido:** produto e quantidade de uma linha do pedido, com cálculo do subtotal.

Os pacotes ficam em `src/main/java/br/com/cantina`: `cardapio` guarda os produtos, `pedido` contém o domínio e o Builder, `comando` contém os comandos e o balcão, e `api` expõe os endpoints e traduz as respostas e erros. Os arquivos da tela ficam em `src/main/resources/static`.

## Patterns

### Builder — montagem de pedidos

**Problema.** Um pedido tem partes obrigatórias (cliente e pelo menos um item) e partes opcionais (para viagem e observação). Um construtor com muitos parâmetros dificulta a leitura; preencher um objeto vazio aos poucos permite estados incompletos.

**Solução.** O `PedidoBuilder` monta o pedido em chamadas encadeadas e só entrega o `Pedido` no `build()`, depois de validar o conjunto. O construtor de `Pedido` é package-private, permitindo centralizar a criação pelo Builder fora do pacote.

```java
Pedido pedido = new PedidoBuilder()
        .paraCliente("Ana")
        .adicionarItem(paoDeQueijo, 2)
        .adicionarItem(sucoNatural, 1)
        .paraViagem()
        .comObservacao("suco sem açúcar")
        .build();
```

O `build()` recusa cliente ausente ou vazio, pedido sem itens, produto nulo, quantidade menor que 1 e observação com mais de 100 caracteres. Pela API, um `produtoId` inexistente é recusado com 404 antes de chegar ao Builder.

**Classes envolvidas:** `pedido/PedidoBuilder` (montagem e validação), `pedido/Pedido` (objeto construído), `pedido/ItemPedido` (linhas) e `api/PedidoController` (converte a requisição em chamadas ao Builder).

**Testes existentes:** `PedidoBuilderTest` e `CriarPedidoApiTest`, além dos testes do domínio.

### Command — ações com desfazer

**Problema.** Registrar, aplicar desconto, marcar pronto, entregar e cancelar precisam ter histórico e poder ser desfeitos. Colocar a lógica de reversão nos controllers mistura transporte HTTP com regras de negócio.

**Solução.** Cada ação é um objeto que implementa `Comando`, com `executar()`, `desfazer()` e `descricao()`. O `Balcao` executa e guarda os comandos bem-sucedidos em ordem; desfazer reverte o mais recente. Cada comando guarda o estado anterior necessário à própria reversão. O frontend chama o endpoint correspondente e atualiza os dados pela API.

**Classes envolvidas:**

- `comando/Comando`: contrato comum.
- `RegistrarPedidoComando`, `AplicarDescontoComando`, `MarcarProntoComando`, `EntregarPedidoComando` e `CancelarPedidoComando`: ações concretas.
- `comando/Balcao`: executor e histórico dos comandos.
- `pedido/Pedido` e `pedido/PedidosEmMemoria`: objetos alterados pelas ações.
- `api/PedidoController` e `api/BalcaoController`: recebem as solicitações e acionam os comandos.

| Comando | Só executa quando | Desfazer |
| --- | --- | --- |
| `RegistrarPedidoComando` | sempre | remove o pedido da lista |
| `AplicarDescontoComando` | status `RECEBIDO` e desconto de 1% a 50% | volta o desconto anterior |
| `MarcarProntoComando` | status `RECEBIDO` | volta para `RECEBIDO` |
| `EntregarPedidoComando` | status `PRONTO` | volta para `PRONTO` |
| `CancelarPedidoComando` | status `RECEBIDO` ou `PRONTO` | volta o status anterior |

Quando a pré-condição falha, o comando lança exceção, não entra no histórico e a API devolve 400 com a mensagem. Desfazer com o histórico vazio também devolve 400.

**Testes existentes:** `ComandosTest` (execução, reversão e recusa de cada comando) e `BalcaoTest` (ordem inversa ao desfazer, histórico vazio e comando que falhou fora do histórico).

## Como estender

### Adicionar um passo de montagem

1. Adicione o campo e o acesso correspondente em `src/main/java/br/com/cantina/pedido/Pedido.java`.
2. Adicione o método encadeado, o armazenamento do valor e sua validação em `pedido/PedidoBuilder.java`, repassando o campo ao construir o pedido.
3. Inclua o campo em `api/NovoPedidoRequisicao.java` e conecte-o ao Builder em `api/PedidoController.java`.
4. Exponha o resultado em `api/PedidoResposta.java`.
5. Inclua o campo no formulário de `src/main/resources/static/index.html` e envie seu valor em `static/app.js`. Se precisar exibi-lo no pedido, ajuste também o template e a renderização. A validação permanece no backend.
6. Atualize os testes do Builder e da API e documente o novo campo.

### Adicionar um comando

1. Crie uma classe em `src/main/java/br/com/cantina/comando` que implemente `Comando`, com validação em `executar()`, reversão em `desfazer()` e uma descrição clara.
2. Adicione o endpoint em `api/BalcaoController.java`, buscando o pedido e executando o comando por `balcao.executar(...)`.
3. Inclua o botão no template de pedido em `static/index.html`. Para uma ação POST sem corpo, use `data-action="sufixo-do-endpoint"`: o tratamento de clique em `static/app.js` já envia para `/pedidos/{id}/sufixo-do-endpoint`. Para um comando com dados adicionais, inclua o campo e o envio pelo mesmo helper `mutate`.
4. Teste a execução, a reversão e as pré-condições no backend. Atualize os exemplos e o contrato.

O **`Balcao` não muda**: ele opera sobre a interface `Comando`, independentemente da ação concreta.

## Declaração de IA

Conforme as declarações já registradas no projeto:

| Responsável | Modelo / ferramenta | Uso declarado |
| --- | --- | --- |
| Frank Cardoso | Claude Opus 5.5, Anthropic | Plano da Parte 1, esqueleto do projeto, Frente A e documentação inicial |
| Alexandre Tibes | Claude Opus 5.5, Anthropic | Frente B: `PedidoBuilder`, `POST /pedidos`, testes e documentação do Builder |
| Iuri de Lima Marques | Codex, OpenAI, agente baseado em GPT-6 | Frente D: HTML, CSS, JavaScript integrado à API, README e verificações do frontend |
| Gustavo Nunes Teixeira | Gemini Pro, Google | Frente C: comandos, balcão e testes |

## Contribuições

| Integrante | GitHub | Contribuição |
| --- | --- | --- |
| Frank Cardoso | [@frank-cardoso](https://github.com/frank-cardoso) | Escopo, plano, esqueleto e Frente A: domínio, cardápio, consultas e testes |
| Alexandre Tibes | [@Xandetds](https://github.com/Xandetds) | Frente B: Builder, criação de pedidos pela API e testes |
| Iuri de Lima Marques | [@iurilmarques](https://github.com/iurilmarques) | Frente D: interface responsiva servida pelo Spring Boot, integração com a API e README |
| Gustavo Nunes Teixeira | [@ogustavonunes](https://github.com/ogustavonunes) | Frente C: comandos, balcão, endpoints do balcão e testes |
