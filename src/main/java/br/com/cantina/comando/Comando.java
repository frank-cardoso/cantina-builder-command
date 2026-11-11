package br.com.cantina.comando;

public interface Comando {
    void executar();
    void desfazer();
    String descricao();
}