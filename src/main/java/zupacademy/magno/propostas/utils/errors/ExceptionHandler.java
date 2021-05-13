package zupacademy.magno.propostas.utils.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@RestControllerAdvice
public class ExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadronizado> handle(MethodArgumentNotValidException exception){
        Collection<String> mensagens = new ArrayList<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        fieldErrors.forEach(e -> {
            String mensagemRecuperada = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            String mensagem = String.format("Campo %s %s", e.getField(), mensagemRecuperada);
            mensagens.add(mensagem);
        });

        ErroPadronizado erroPadronizado = new ErroPadronizado(mensagens);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroPadronizado);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroPadronizado> handle(ResponseStatusException exception){
        Collection<String> mensagens = new ArrayList<>();
        mensagens.add(exception.getMessage());
        return ResponseEntity.status(exception.getStatus())
                .body(new ErroPadronizado(mensagens));
    }
}
