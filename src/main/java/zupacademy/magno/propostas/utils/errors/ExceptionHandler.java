package zupacademy.magno.propostas.utils.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

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

//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
//    public ErrorsDetailsDto handle(AuthenticationException exception){
//        return new ErrorsDetailsDto("auth", exception.getMessage());
//    }


//
//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
//    public List<ErroPadronizado> handle(ConstraintViolationException exception){
//        List<ErroPadronizado> dto = new ArrayList<>();
//        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
//
//        violations.forEach(e -> {
//            ErroPadronizado error = new ErroPadronizado(e.getInvalidValue().toString(), e.getMessage());
//            dto.add(error);
//        });
//
//        return dto;
//    }
}
