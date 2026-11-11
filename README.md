# Cantina — Builder + Command

Projeto final de Design Patterns: sistema de cantina em Java 25 + Spring Boot. O atendente monta o pedido passo a passo (**Builder**) e opera o balcão com ações que podem ser desfeitas (**Command**).

> **README provisório.** A versão final (descrição, exemplos de uso, patterns, declaração de IA e contribuições) é montada pela Frente D. O plano completo da Parte 1 está em [docs/PLANO.md](docs/PLANO.md).

## Pré-requisitos

- JDK 25
- Maven não precisa: vem pelo wrapper (`./mvnw`)

## Como rodar

```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
./mvnw test                 # testes
```

- Swagger: http://localhost:8080/swagger-ui.html

## Estado atual

- Pronto: domínio, cardápio e consultas (Frente A) e o Builder com o `POST /pedidos` (Frente B).
- Em andamento: comandos e balcão (Frente C), tela e README final (Frente D).
- Até a Frente C entrar, `POST /pedidos` com um pedido válido responde 500, porque o `RegistrarPedidoComando` ainda é `TODO`. Pedidos inválidos já são recusados com 400 ou 404.

## Patterns

### Builder

**Problema.** Um pedido tem partes obrigatórias (cliente e pelo menos um item) e partes opcionais (para viagem, observação), e os itens chegam um de cada vez. Com um construtor comum, ou ele teria uma lista longa de parâmetros, vários deles opcionais, ou o objeto seria criado vazio e preenchido aos poucos, podendo ficar inválido no meio do caminho.

**Solução.** O `PedidoBuilder` monta o pedido passo a passo, em chamadas encadeadas, e só entrega o `Pedido` no `build()`, depois de validar o conjunto. O construtor de `Pedido` é package-private, então o único jeito de criar um pedido fora do pacote é pelo builder: não existe pedido inválido no sistema.

```java
Pedido pedido = new PedidoBuilder()
        .paraCliente("Ana")
        .adicionarItem(paoDeQueijo, 2)
        .adicionarItem(sucoNatural, 1)
        .paraViagem()
        .comObservacao("suco sem açúcar")
        .build();   // pedido #1, RECEBIDO, total R$ 17,00
```

O `build()` recusa, com `IllegalStateException` (a API devolve 400 com a mensagem em `erro`):

| Regra | Mensagem |
|-------|----------|
| cliente vazio ou ausente | `Cliente é obrigatório` |
| nenhum item | `Pedido precisa de pelo menos 1 item` |
| quantidade menor que 1 | `Quantidade de <produto> deve ser pelo menos 1` |
| produto nulo | `Produto do item não pode ser nulo` |
| observação com mais de 100 caracteres | `Observação deve ter no máximo 100 caracteres` |

Pela API, um `produtoId` que não existe no cardápio é recusado antes do builder, com 404 (`Produto <id> não encontrado`).

**Classes envolvidas**

- `pedido/PedidoBuilder`: o builder, com as validações.
- `pedido/Pedido`: o produto montado; construtor package-private.
- `pedido/ItemPedido`: cada linha do pedido (produto + quantidade).
- `api/PedidoController` (`POST /pedidos`): converte o `NovoPedidoRequisicao` em chamadas ao builder e registra o pedido no balcão.
- Testes: `PedidoBuilderTest` e `CriarPedidoApiTest`.

## Declaração de IA (parcial)

Modelo usado: **Claude Opus 5.5**, da Anthropic.

- **Frank Cardoso:** plano da Parte 1, esqueleto do projeto (Passo 0), Frente A (domínio, cardápio, consultas e testes) e documentação (README e plano).
- **Alexandre Tibes:** Frente B (`PedidoBuilder`, `POST /pedidos` e testes).
