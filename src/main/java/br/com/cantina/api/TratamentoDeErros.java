package br.com.cantina.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.cantina.NaoEncontradoException;

@RestControllerAdvice
public class TratamentoDeErros {

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(NaoEncontradoException e) {
        throw new UnsupportedOperationException("TODO");
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> requisicaoInvalida(RuntimeException e) {
        throw new UnsupportedOperationException("TODO");
    }
}
