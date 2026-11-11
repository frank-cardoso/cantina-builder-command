/* A tela apenas envia os dados e apresenta as respostas da API. */
"use strict";

const elements = {
  form: document.querySelector("#pedido-form"),
  menu: document.querySelector("#cardapio"),
  orders: document.querySelector("#pedidos"),
  count: document.querySelector("#pedidos-contagem"),
  history: document.querySelector("#historico"),
  message: document.querySelector("#mensagem"),
  refresh: document.querySelector("#atualizar"),
  undo: document.querySelector("#desfazer"),
};

const currency = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
const categories = { SALGADO: "Salgado", BEBIDA: "Bebida", DOCE: "Doce" };
let busy = false;

function textElement(tag, text, className) {
  const element = document.createElement(tag);
  element.textContent = text;
  if (className) element.className = className;
  return element;
}

function notify(text, error = false) {
  elements.message.textContent = text;
  elements.message.classList.toggle("message-error", error);
  elements.message.hidden = false;
}

function setBusy(value) {
  busy = value;
  document.querySelectorAll("button").forEach((button) => { button.disabled = value; });
  elements.form.setAttribute("aria-busy", String(value));
}

async function request(path, options = {}) {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 15000);
  let response;
  try {
    response = await fetch(path, {
      ...options,
      signal: controller.signal,
      headers: {
        Accept: "application/json",
        ...(options.body ? { "Content-Type": "application/json" } : {}),
      },
    });
    let data;
    try {
      data = await response.json();
    } catch (error) {
      if (error.name === "AbortError") throw error;
      throw new Error(`A cantina retornou uma resposta inesperada (HTTP ${response.status}). Tente novamente.`);
    }
    if (!response.ok) {
      throw new Error(data && typeof data.erro === "string"
        ? data.erro
        : `Não foi possível concluir a solicitação (HTTP ${response.status}). Tente novamente.`);
    }
    return data;
  } catch (error) {
    if (error.name === "AbortError") {
      throw new Error("A cantina demorou para responder. Atualize o balcão antes de tentar a ação novamente.");
    }
    if (response) throw error;
    throw new Error("Não foi possível conectar à cantina. Verifique a conexão e tente novamente.");
  } finally {
    clearTimeout(timeout);
  }
}

function renderMenu(products) {
  const fragment = document.createDocumentFragment();
  products.forEach((product) => {
    const row = document.querySelector("#produto-template").content.cloneNode(true);
    const input = row.querySelector("input");
    const label = row.querySelector("label");
    input.id = `produto-${product.id}`;
    input.dataset.productId = product.id;
    label.htmlFor = input.id;
    label.textContent = product.nome;
    input.setAttribute("aria-label", `Quantidade de ${product.nome}`);
    row.querySelector(".product-price").textContent = currency.format(product.preco);
    row.querySelector(".product-category").textContent = categories[product.categoria] || product.categoria;
    fragment.append(row);
  });
  elements.menu.replaceChildren(fragment);
}

function renderOrders(orders) {
  elements.count.textContent = orders.length;
  if (orders.length === 0) {
    const empty = document.createElement("div");
    empty.className = "empty-state";
    const symbol = textElement("span", "≡", "empty-symbol");
    symbol.setAttribute("aria-hidden", "true");
    empty.append(symbol, textElement("h3", "Tudo pronto para o primeiro pedido"),
      textElement("p", "Escolha os itens do cardápio e monte um pedido. Ele vai aparecer por aqui."));
    elements.orders.replaceChildren(empty);
    return;
  }

  const fragment = document.createDocumentFragment();
  orders.forEach((order) => {
    const card = document.querySelector("#pedido-template").content.cloneNode(true);
    card.querySelector("article").dataset.orderId = order.id;
    card.querySelector(".order-id").textContent = `PEDIDO #${order.id}`;
    card.querySelector(".order-client").textContent = order.cliente;
    const badge = card.querySelector(".status-badge");
    badge.textContent = order.status;
    const statusClasses = { RECEBIDO: "status-recebido", PRONTO: "status-pronto", ENTREGUE: "status-entregue", CANCELADO: "status-cancelado" };
    if (statusClasses[order.status]) badge.classList.add(statusClasses[order.status]);
    card.querySelector(".order-service").textContent = order.paraViagem ? "Para viagem · pode embalar" : "Para consumir na cantina";
    const items = card.querySelector(".order-items");
    order.itens.forEach((item) => {
      const line = document.createElement("li");
      line.append(textElement("span", `${item.quantidade} × ${item.produto}`),
        textElement("span", currency.format(item.subtotal)));
      items.append(line);
    });
    if (order.observacao) {
      const observation = card.querySelector(".order-observation");
      observation.textContent = `Observação: ${order.observacao}`;
      observation.hidden = false;
    }
    card.querySelector(".order-total strong").textContent = currency.format(order.total);
    card.querySelector(".order-discount").textContent = `Desconto aplicado: ${order.descontoPercentual}%`;
    const discountInput = card.querySelector(".discount-form input");
    discountInput.id = `desconto-${order.id}`;
    card.querySelector(".discount-form label").htmlFor = discountInput.id;
    card.querySelector(".discount-form label").textContent = `Percentual de desconto do pedido #${order.id}`;
    fragment.append(card);
  });
  elements.orders.replaceChildren(fragment);
}

function renderHistory(history) {
  if (history.length === 0) {
    elements.history.replaceChildren(textElement("p", "Nenhuma ação por enquanto. As ações do balcão aparecem aqui, na ordem em que acontecem.", "history-empty"));
    return;
  }
  const list = document.createElement("ol");
  list.className = "history-list";
  history.forEach((description) => list.append(textElement("li", description)));
  elements.history.replaceChildren(list);
}

async function loadSection(path, container, render) {
  container.setAttribute("aria-busy", "true");
  try {
    const data = await request(path);
    render(data);
  } catch (error) {
    // Conserva os dados já exibidos se uma atualização falhar.
    container.querySelectorAll(".loading, .section-error").forEach((node) => node.remove());
    container.prepend(textElement("p", error.message, "section-error"));
    throw error;
  } finally {
    container.setAttribute("aria-busy", "false");
  }
}

async function refreshCounter() {
  const results = await Promise.allSettled([
    loadSection("pedidos", elements.orders, renderOrders),
    loadSection("balcao/historico", elements.history, renderHistory),
  ]);
  return results.every((result) => result.status === "fulfilled");
}

async function mutate(path, body, success) {
  if (busy) return;
  setBusy(true);
  try {
    const data = await request(path, { method: "POST", ...(body === undefined ? {} : { body: JSON.stringify(body) }) });
    const message = success(data);
    notify(message);
    if (!await refreshCounter()) {
      notify(`${message} Não foi possível atualizar todo o balcão. Use “Atualizar balcão” para tentar novamente.`, true);
    }
  } catch (error) {
    notify(error.message, true);
  } finally {
    setBusy(false);
  }
}

elements.form.addEventListener("submit", (event) => {
  event.preventDefault();
  const items = Array.from(elements.menu.querySelectorAll("[data-product-id]"))
    .filter((input) => input.value !== "")
    .map((input) => ({ produtoId: Number(input.dataset.productId), quantidade: Number(input.value) }));
  mutate("pedidos", {
    cliente: elements.form.elements.cliente.value,
    itens: items,
    paraViagem: elements.form.elements.paraViagem.checked,
    observacao: elements.form.elements.observacao.value,
  }, (order) => {
    elements.form.reset();
    return `Pedido #${order.id} montado para ${order.cliente}. Bom atendimento!`;
  });
});

elements.orders.addEventListener("submit", (event) => {
  const form = event.target.closest(".discount-form");
  if (!form) return;
  event.preventDefault();
  const id = form.closest("[data-order-id]").dataset.orderId;
  mutate(`pedidos/${id}/desconto`, { percentual: Number(form.elements.percentual.value) },
    () => `Desconto do pedido #${id} atualizado.`);
});

elements.orders.addEventListener("click", (event) => {
  const button = event.target.closest("[data-action]");
  if (!button) return;
  const id = button.closest("[data-order-id]").dataset.orderId;
  mutate(`pedidos/${id}/${button.dataset.action}`, undefined, () => `Pedido #${id} atualizado.`);
});

elements.undo.addEventListener("click", () => {
  mutate("balcao/desfazer", undefined, (data) => `Ação desfeita: ${data.desfeito}`);
});

async function loadPage() {
  if (busy) return;
  setBusy(true);
  try {
    // O cardápio só é recarregado se ainda não houver campos, preservando o rascunho.
    const results = await Promise.allSettled([
      ...(elements.menu.querySelector("input") ? [] : [loadSection("cardapio", elements.menu, renderMenu)]),
      refreshCounter(),
    ]);
    const failed = results.some((result) => result.status === "rejected" || result.value === false);
    if (failed) notify("Algumas informações não puderam ser carregadas. Confira as mensagens abaixo e tente atualizar o balcão.", true);
    else elements.message.hidden = true;
  } finally {
    setBusy(false);
  }
}

elements.refresh.addEventListener("click", loadPage);
loadPage();
