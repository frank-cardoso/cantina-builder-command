# Projeto Final — Cantina (Builder + Command)

Plano da **Parte 1** (aplicação base). Entrega: **sexta, 13/11/2026**.

Escopo inicial e plano: Frank Cardoso (@frank-cardoso).

---

## 1. Resumo

- **O quê:** sistema de cantina. O atendente monta o pedido, o pedido entra na fila do balcão e depois recebe ações (desconto, pronto, entregar, cancelar) que podem ser **desfeitas**.
- **Como:** API REST em Spring Boot + uma página HTML simples servida pelo próprio Spring. Tudo em memória, sem banco.
- **Patterns (só esses dois):**
  - **Builder:** monta o `Pedido` passo a passo e valida.
  - **Command:** cada ação sobre o pedido vira um objeto com `executar()` e `desfazer()`.
- **Divisão:** 4 frentes (A, B, C, D), uma por pessoa, cada uma do Java até a tela.

---

## 2. O que o enunciado cobra (não pode faltar)

- Aplicação em Java, executável, **sem banco de dados**.
- Repositório **público** no GitHub.
- 2 a 3 entidades principais e um fluxo de uso de ponta a ponta.
- **Só** Builder e Command no nosso código. Pattern a mais tira nota.
- **Cada integrante com pelo menos um commit feito com a própria conta do GitHub.**
- README com:
  1. descrição do sistema;
  2. tecnologias e pré-requisitos;
  3. instruções de execução;
  4. exemplos de uso;
  5. patterns usados, classes envolvidas e problema resolvido;
  6. declaração de IA (modelo e onde foi usada);
  7. contribuição de cada integrante.
- IA é permitida na Parte 1, desde que declarada.

---

## 3. Como a cantina funciona (fluxo de ponta a ponta)

1. O atendente abre a tela e vê o cardápio.
2. Monta o pedido da Ana: 2 pães de queijo + 1 suco natural, para viagem, com a observação "suco sem açúcar".
   O **Builder** valida e cria o **pedido #1**, com status `RECEBIDO` e total de **R$ 17,00**. Registrar o pedido já é um comando, então entra no histórico.
3. Aplica 10% de desconto. O total vai para **R$ 15,30**.
4. Percebe o engano e clica em **Desfazer**. O desconto volta a 0 e o total volta para **R$ 17,00**.
5. Marca como pronto: status `PRONTO`.
6. Entrega: status `ENTREGUE`.
7. Tenta cancelar o pedido entregue. A API recusa com a mensagem "Pedido entregue não pode ser cancelado", e a tela mostra essa mensagem.

Esse roteiro serve de **exemplo de uso no README** e de **demo na apresentação**.

### Cardápio fixo (em memória)

| id | Produto       | Preço   | Categoria |
|----|---------------|---------|-----------|
| 1  | Pão de queijo | R$ 4,50 | SALGADO   |
| 2  | Coxinha       | R$ 7,00 | SALGADO   |
| 3  | Misto quente  | R$ 9,00 | SALGADO   |
| 4  | Café          | R$ 3,50 | BEBIDA    |
| 5  | Suco natural  | R$ 8,00 | BEBIDA    |
| 6  | Brigadeiro    | R$ 3,00 | DOCE      |

---

## 4. Stack e pré-requisitos

| Item       | Escolha |
|------------|---------|
| Linguagem  | **Java 25** |
| Framework  | Spring Boot 4.1.x, com a dependência **Spring Web** apenas |
| Build      | Maven com wrapper (`./mvnw`). Ninguém precisa instalar Maven. |
| Testes     | JUnit 6 (Jupiter), que já vem com o Spring Boot |
| Front      | `index.html` + `app.js` + `style.css`, em JavaScript puro, dentro de `src/main/resources/static` |
| Plano B    | Swagger UI (`springdoc-openapi-starter-webmvc-ui` **3.2.0**) para testar endpoints sem a tela |
| Dados      | Listas em memória. Somem quando a aplicação para, e é assim mesmo. |

**Instalar o JDK 25:**
- **IntelliJ:** File → Project Structure → SDK → Download JDK → versão 25 (Temurin).
- **Windows:** instalador do Temurin 25 em adoptium.net.
- **Linux/Mac:** `sdk install java 25.0.x-tem`, pelo SDKMAN.

**Rodar:**
```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
# tela:    http://localhost:8080
# swagger: http://localhost:8080/swagger-ui.html
./mvnw test                 # roda os testes
```

> No start.spring.io, **não** adicionar JPA, H2 nem nada de banco.

---

## 5. Onde entram os patterns

### Builder: montar o pedido

- **Problema:** o pedido tem partes obrigatórias (cliente e itens) e opcionais (viagem e observação). Um construtor com muitos parâmetros, cheio de `null` e `false` soltos, é ilegível e deixa passar pedido inválido.
- **Solução:** o `PedidoBuilder` monta o pedido passo a passo (`paraCliente`, `adicionarItem`, `paraViagem`, `comObservacao`) e o `build()` valida tudo num ponto só. O `Pedido` só existe se estiver válido: o construtor dele não é público e só o builder chama.
- **Classes:** `PedidoBuilder` (o builder), `Pedido` (o produto), `PedidoController` (quem conduz a montagem com base no JSON recebido).

### Command: ações sobre o pedido, com desfazer

- **Problema:** as ações do balcão (registrar, aplicar desconto, marcar pronto, entregar, cancelar) precisam ficar registradas e poder ser desfeitas. Espalhar `if` de desfazer pelos controllers vira bagunça.
- **Solução:** cada ação vira um objeto que implementa `Comando` (`executar`, `desfazer`, `descricao`). O `Balcao` executa qualquer comando e empilha num histórico. Desfazer é tirar o último da pilha e chamar `desfazer()`. O `Balcao` não sabe o que cada comando faz.
- **Classes:**
  - `Comando`: a interface.
  - `RegistrarPedidoComando`, `AplicarDescontoComando`, `MarcarProntoComando`, `EntregarPedidoComando`, `CancelarPedidoComando`: os comandos concretos.
  - `Balcao`: o invoker, que guarda o histórico.
  - `Pedido` e `PedidosEmMemoria`: os receivers, que sofrem a ação.
  - Controllers: os clientes, que criam os comandos.

---

## 6. Estrutura do projeto

```
src/main/java/br/com/cantina/
├── CantinaApplication.java
├── NaoEncontradoException.java
├── cardapio/
│   ├── Categoria.java
│   ├── Produto.java
│   └── Cardapio.java
├── pedido/
│   ├── StatusPedido.java
│   ├── ItemPedido.java
│   ├── Pedido.java
│   ├── PedidoBuilder.java          ← BUILDER
│   └── PedidosEmMemoria.java
├── comando/                         ← COMMAND
│   ├── Comando.java
│   ├── Balcao.java
│   ├── RegistrarPedidoComando.java
│   ├── AplicarDescontoComando.java
│   ├── MarcarProntoComando.java
│   ├── EntregarPedidoComando.java
│   └── CancelarPedidoComando.java
└── api/
    ├── CardapioController.java
    ├── PedidoController.java
    ├── BalcaoController.java
    ├── NovoPedidoRequisicao.java
    ├── DescontoRequisicao.java
    ├── PedidoResposta.java
    └── TratamentoDeErros.java

src/main/resources/static/
├── index.html
├── app.js
└── style.css

src/test/java/br/com/cantina/
├── pedido/PedidoTest.java
├── pedido/PedidoBuilderTest.java
├── comando/ComandosTest.java
└── comando/BalcaoTest.java
```

---

## 7. Contrato entre as frentes

Todo mundo programa em cima destes nomes. O **esqueleto** (Passo 0) já cria todas as classes com estas assinaturas e o corpo `throw new UnsupportedOperationException("TODO")`. Assim o projeto compila desde o primeiro dia e cada frente só preenche a sua parte. A única espera é a ordem de merge **A → B → C** (seção 9), porque o C precisa de `Pedido` de verdade, e só o builder do B cria pedido.

**Mudar uma assinatura?** Avisa no grupo antes, porque quebra a frente de outra pessoa.

### Classes Java

```java
// ---------- br.com.cantina ----------
public class NaoEncontradoException extends RuntimeException { ... }   // vira HTTP 404

// ---------- cardapio ----------
public enum Categoria { SALGADO, BEBIDA, DOCE }

public record Produto(int id, String nome, BigDecimal preco, Categoria categoria) {}

@Component
public class Cardapio {
    public List<Produto> listar();
    public Produto buscar(int id);            // NaoEncontradoException se não existir
}

// ---------- pedido ----------
public enum StatusPedido { RECEBIDO, PRONTO, ENTREGUE, CANCELADO }

public record ItemPedido(Produto produto, int quantidade) {
    public BigDecimal subtotal();             // preco x quantidade
}

public class Pedido {
    // NÃO é public: só o PedidoBuilder (mesmo pacote) cria Pedido.
    // id vem de um contador (AtomicInteger estático); status nasce RECEBIDO; desconto nasce 0.
    // O contador não zera entre testes: teste nenhum deve depender do número do id.
    Pedido(String cliente, List<ItemPedido> itens, boolean paraViagem, String observacao);

    public int getId();
    public String getCliente();
    public List<ItemPedido> getItens();       // lista imutável
    public boolean isParaViagem();
    public String getObservacao();            // "" quando não informada
    public StatusPedido getStatus();
    public int getDescontoPercentual();

    public void alterarStatus(StatusPedido novo);      // sem regra: quem decide é o comando
    public void aplicarDesconto(int percentual);       // aceita 0..100; a regra 1..50 fica no comando
    public BigDecimal total();                // soma dos subtotais menos desconto, 2 casas (HALF_EVEN)
}

public class PedidoBuilder {
    public PedidoBuilder paraCliente(String nome);
    public PedidoBuilder adicionarItem(Produto produto, int quantidade);
    public PedidoBuilder paraViagem();
    public PedidoBuilder comObservacao(String texto);
    public Pedido build();                    // valida; IllegalStateException com mensagem clara
}

@Component
public class PedidosEmMemoria {
    public void adicionar(Pedido pedido);
    public void remover(Pedido pedido);
    public Pedido buscar(int id);             // NaoEncontradoException se não existir
    public List<Pedido> listar();             // ordem de criação
}

// ---------- comando ----------
public interface Comando {
    void executar();                          // valida a pré-condição; IllegalStateException se não puder
    void desfazer();
    String descricao();                       // ex.: "Desconto de 10% no pedido #1"
}

@Component
public class Balcao {
    public void executar(Comando comando);    // só entra no histórico se executar() não lançar exceção
    public String desfazer();                 // desfaz o último e devolve a descricao(); IllegalStateException se vazio
    public List<String> historico();          // descrições, da mais antiga para a mais nova
}

// Construtores dos comandos
new RegistrarPedidoComando(Pedido pedido, PedidosEmMemoria pedidos);
new AplicarDescontoComando(Pedido pedido, int percentual);
new MarcarProntoComando(Pedido pedido);
new EntregarPedidoComando(Pedido pedido);
new CancelarPedidoComando(Pedido pedido);

// ---------- api ----------
public record NovoPedidoRequisicao(String cliente, List<Item> itens, boolean paraViagem, String observacao) {
    public record Item(int produtoId, int quantidade) {}
}
public record DescontoRequisicao(int percentual) {}
public record PedidoResposta(...) {
    public PedidoResposta(Pedido pedido) { ... }   // monta a resposta a partir do Pedido
}
```

### Regras de cada comando

| Comando        | Só pode quando                | O que faz                                    | Desfazer                     |
|----------------|-------------------------------|----------------------------------------------|------------------------------|
| RegistrarPedido | sempre                       | adiciona o pedido na lista                   | remove da lista              |
| AplicarDesconto | status `RECEBIDO` e 1 a 50%  | guarda o desconto anterior e aplica o novo   | volta o desconto anterior    |
| MarcarPronto   | status `RECEBIDO`             | status vira `PRONTO`                         | volta para `RECEBIDO`        |
| EntregarPedido | status `PRONTO`               | status vira `ENTREGUE`                       | volta para `PRONTO`          |
| CancelarPedido | status `RECEBIDO` ou `PRONTO` | guarda o status anterior e muda para `CANCELADO` | volta o status anterior  |

### API REST

| Método | Caminho                       | Corpo                    | Resposta                      | Frente |
|--------|-------------------------------|--------------------------|-------------------------------|--------|
| GET    | `/cardapio`                   | —                        | lista de produtos             | A |
| GET    | `/pedidos`                    | —                        | lista de `PedidoResposta`     | A |
| GET    | `/pedidos/{id}`               | —                        | `PedidoResposta`              | A |
| POST   | `/pedidos`                    | `NovoPedidoRequisicao`   | 201 + `PedidoResposta`        | B |
| POST   | `/pedidos/{id}/desconto`      | `{"percentual": 10}`     | `PedidoResposta`              | C |
| POST   | `/pedidos/{id}/pronto`        | —                        | `PedidoResposta`              | C |
| POST   | `/pedidos/{id}/entrega`       | —                        | `PedidoResposta`              | C |
| POST   | `/pedidos/{id}/cancelamento`  | —                        | `PedidoResposta`              | C |
| POST   | `/balcao/desfazer`            | —                        | `{"desfeito": "<descrição>"}` | C |
| GET    | `/balcao/historico`           | —                        | `["Registrar pedido #1", ...]` | C |

Cancelar é um `POST /cancelamento`, não um `DELETE`, porque cancelar pode ser desfeito: o pedido continua existindo.

**Exemplo: `POST /pedidos`**
```json
{
  "cliente": "Ana",
  "itens": [
    { "produtoId": 1, "quantidade": 2 },
    { "produtoId": 5, "quantidade": 1 }
  ],
  "paraViagem": true,
  "observacao": "suco sem açúcar"
}
```

**Exemplo: `PedidoResposta`**
```json
{
  "id": 1,
  "cliente": "Ana",
  "itens": [
    { "produto": "Pão de queijo", "quantidade": 2, "subtotal": 9.00 },
    { "produto": "Suco natural",  "quantidade": 1, "subtotal": 8.00 }
  ],
  "paraViagem": true,
  "observacao": "suco sem açúcar",
  "status": "RECEBIDO",
  "descontoPercentual": 0,
  "total": 17.00
}
```

**Erros** (todos no mesmo formato, tratados em `TratamentoDeErros`):

| Exceção                                          | HTTP | Corpo |
|--------------------------------------------------|------|-------|
| `NaoEncontradoException`                         | 404  | `{"erro": "Pedido 9 não encontrado"}` |
| `IllegalStateException` / `IllegalArgumentException` | 400 | `{"erro": "Pedido entregue não pode ser cancelado"}` |

---

## 8. Divisão: o que cada um toca

### Passo 0: esqueleto (responsável pelo repo, até domingo 08/11)

**Responsável:** Frank Cardoso (@frank-cardoso)  ·  **situação:** concluído (07/11)

1. Criar o repo **público** (sugestão de nome: `cantina-builder-command`).
2. Gerar o projeto no start.spring.io com: Maven, Java 25, Spring Boot 4.1.x, group `br.com.cantina`, artifact `cantina`, dependência **Spring Web**. Depois, adicionar o springdoc 3.2.0 no `pom.xml`.
3. Criar todas as classes da seção 7 com o corpo `TODO`, e conferir que `./mvnw test` compila.
4. Criar o `.gitignore` de Java/Maven/IDE (`target/`, `.idea/`, `*.iml`, `.vscode/`).
5. Criar um README provisório apontando para este plano (em `docs/PLANO.md`).
6. Convidar os outros 3 como colaboradores (Settings → Collaborators).

### Frente A: domínio, cardápio e consultas

**Responsável:** Frank Cardoso (@frank-cardoso)  ·  **branch:** `feature/dominio`  ·  **situação:** mergeada no PR #1 (07/11)

- **Toca:**
  - `NaoEncontradoException`
  - `cardapio/*` (`Categoria`, `Produto` e `Cardapio` com os 6 produtos fixos)
  - `Pedido`, `ItemPedido`, `StatusPedido`, `PedidosEmMemoria`
  - `CardapioController`
  - `PedidoController`, só os GETs
  - `PedidoResposta`
  - `TratamentoDeErros`
- **Testes:** `PedidoTest`, cobrindo total sem desconto, com desconto e o arredondamento (17,00 com 10% = 15,30).
- **Pronto quando:** `GET /cardapio`, `GET /pedidos` e `GET /pedidos/{id}` respondem como no contrato, e os erros saem no formato `{"erro": ...}`.
- **Primeira a entrar (segunda 09/11):** as outras frentes usam essas classes de verdade.
- **No README:** escreve a seção de entidades (Produto, Pedido, ItemPedido).

### Frente B: Builder

**Responsável:** Alexandre Tibes (@Xandetds)  ·  **branch:** `feature/builder`  ·  **situação:** mergeada no PR #2 (10/11)

- **Toca:**
  - `PedidoBuilder`
  - `NovoPedidoRequisicao`
  - `POST /pedidos` no `PedidoController`, que monta com o builder e registra com `balcao.executar(new RegistrarPedidoComando(...))`
- **Validações no `build()`:**
  - cliente obrigatório e não vazio;
  - pelo menos 1 item;
  - quantidade ≥ 1;
  - produto não nulo (protege quem usa o builder direto, como nos testes; pela API, um `produtoId` inexistente já para antes, no `Cardapio.buscar`, com 404);
  - observação de até 100 caracteres.
- **Testes:** `PedidoBuilderTest`, cobrindo:
  - pedido completo;
  - só com os obrigatórios (viagem `false`, observação `""`);
  - sem cliente;
  - sem item;
  - quantidade 0;
  - observação longa demais.
- **Pronto quando:**
  - `PedidoBuilderTest` passa.
  - Depois do merge do C, o `POST /pedidos` com o JSON de exemplo devolve o pedido com total 17,00.
  - Um pedido inválido devolve 400 com mensagem clara.
  - Um `produtoId` inexistente devolve 404.
- **Merge:** o builder entra na terça cedo, porque os testes do C dependem dele. O endpoint fica completo quando o `RegistrarPedidoComando` do C chegar.
- **No README:** escreve a seção do pattern **Builder** (classes, problema, solução).

### Frente C: Command

**Responsável:** Gustavo Nunes Teixeira (@ogustavonunes)  ·  **branch:** `feature/command`  ·  **situação:** mergeada no PR #4 (11/11)

- **Toca:**
  - `comando/*`: `Comando`, `Balcao` e os 5 comandos, seguindo a tabela de regras da seção 7
  - `BalcaoController`: desconto, pronto, entrega, cancelamento, desfazer, histórico
  - `DescontoRequisicao`
- **Testes:**
  - `ComandosTest`: cada comando executa, desfaz e recusa quando a pré-condição falha. Cria o pedido com o `PedidoBuilder`, então só roda depois do merge de A e B.
  - `BalcaoTest`: usa um comando falso (classe anônima implementando `Comando`), então não precisa de `Pedido` e pode começar no primeiro dia. Cobre:
    - desfaz em ordem inversa;
    - desfazer com histórico vazio lança exceção;
    - comando que falhou não entra no histórico.
- **Pronto quando:** o roteiro da seção 3 (passos 3 a 7) funciona pelo Swagger.
- **No README:** escreve a seção do pattern **Command** (classes, problema, solução).

### Frente D: front + README

**Responsável:** Iuri de Lima Marques (@iurilmarques)  ·  **branch:** `feature/front-readme`  ·  **situação:** mergeada no PR #5 (11/11)

- **Toca:** `static/index.html`, `app.js`, `style.css` e o `README.md`.
- **Tela (uma página só):**
  1. Cardápio com campo de quantidade por produto.
  2. Formulário de montagem: cliente, checkbox "para viagem", observação e botão **Montar pedido**.
  3. Lista de pedidos com id, cliente, itens, status e total, e os botões **Desconto**, **Pronto**, **Entregar** e **Cancelar**.
  4. Botão **Desfazer última ação** + lista do histórico.
  5. Área de mensagem que mostra o `erro` devolvido pela API.
- **Regra de ouro: zero regra de negócio no JS.**
  - Total, validação e status vêm da API.
  - Todos os botões aparecem sempre: quem recusa é a API, e a tela só mostra a mensagem. É isso que deixa o Command visível.
- **Começa já:** usa os JSON de exemplo da seção 7 como dado falso até a API ficar pronta, e troca para o `fetch` de verdade na terça e quarta.
- **README:** monta todas as seções exigidas (seção 2) e recolhe de cada um:
  - o parágrafo do pattern que a pessoa fez;
  - a declaração de IA (modelo e onde usou);
  - a contribuição.
- **No README, inclui também** uma seção **"Como estender"**, com os arquivos de cada extensão:
  - novo passo de montagem: método no `PedidoBuilder`, campo no `Pedido`, em `NovoPedidoRequisicao` e `PedidoResposta`, e no formulário;
  - novo comando: classe que implementa `Comando`, endpoint no `BalcaoController` e botão na tela (o `Balcao` não muda).
- **Quinta:** faz o teste final "clonar do zero e rodar" numa máquina que ainda não tinha o projeto.

---

## 9. Cronograma

| Dia            | O quê |
|----------------|-------|
| sáb–dom 07–08  | Fechar este plano no grupo e preencher nomes e @github. Passo 0 (repo + esqueleto). Todo mundo clona e roda. |
| seg 09         | Frente A abre e mergeia o PR do domínio. B, C e D trabalham em cima do esqueleto (C começa pelo `Balcao`). |
| ter 10         | Manhã: B mergeia o builder. Tarde: C atualiza a branch com a `main`, fecha `ComandosTest` e mergeia. D liga a tela na API real. |
| qua 11         | Integração: roteiro da seção 3 inteiro pela tela. Correções. |
| qui 12         | README final, declaração de IA e contribuições. Teste "clonar do zero e rodar". |
| sex 13         | Folga para imprevisto. Enviar o link do repo ao professor. |

---

## 10. Git: como cada um trabalha

**Configurar a autoria no repo** (com o e-mail **da sua conta do GitHub**):
```bash
git config user.name  "Seu Nome"
git config user.email "email-da-sua-conta-github@exemplo.com"
```
Depois do primeiro push, confira no GitHub se o commit aparece com **o seu avatar**. Se aparecer sem foto ou sem link, o e-mail está errado e o commit **não conta como seu**.

**Fluxo:**
1. `git checkout -b feature/<sua-frente>`
2. Commits pequenos ao longo da semana. O enunciado pede commits incrementais, não um commitão na quinta.
3. `./mvnw test` passando antes de abrir o PR.
4. Abrir o PR para a `main`. **Outra pessoa do grupo revisa e aprova.** Ninguém commita direto na `main`.

---

## 11. Armadilhas que tiram nota

- **Pattern a mais.** Nada de:
  - `getInstance()` (Singleton);
  - classe `*Factory`;
  - interface de "estratégia de desconto" (Strategy);
  - listener ou notificação (Observer);
  - enum ou classe com comportamento por status (State).

  O status é um enum simples, e quem decide a transição é o comando. Anotações do Spring (`@Component`, `@RestController`) são do framework e não contam.
- **Banco:** nem H2, nem JPA.
- **Regra no front:** o JS só chama a API e mostra o resultado.
- **Commit sem a conta própria:** confira o avatar no GitHub.
- **Esquecer a declaração de IA:** cada um diz o que usou e onde.

---

## 12. Checklist de entrega (quinta à noite)

- [ ] `./mvnw spring-boot:run` sobe sem erro numa máquina limpa com JDK 25
- [ ] `./mvnw test` passa
- [ ] Roteiro da seção 3 roda inteiro pela tela
- [ ] Só Builder e Command no código
- [ ] README com as 7 seções + "Como estender"
- [ ] Commit de cada um dos 4, com a própria conta, aparecendo no GitHub
- [ ] Repo público e link enviado ao professor
