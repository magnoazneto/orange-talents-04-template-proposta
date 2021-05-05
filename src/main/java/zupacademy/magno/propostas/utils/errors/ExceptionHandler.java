package zupacademy.magno.propostas.utils.errors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class ExceptionHandler {
    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public List<ErrorsDetailsDto> handle(MethodArgumentNotValidException exception){
        List<ErrorsDetailsDto> dto = new ArrayList<>();
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        fieldErrors.forEach(e -> {
            String message = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            ErrorsDetailsDto error = new ErrorsDetailsDto(e.getField(), message);
            dto.add(error);
        });

        return dto;

    }

//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
//    public ErrorsDetailsDto handle(AuthenticationException exception){
//        return new ErrorsDetailsDto("auth", exception.getMessage());
//    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorsDetailsDto> handle(ResponseStatusException exception){
        return ResponseEntity.status(exception.getStatus())
                .body(new ErrorsDetailsDto(exception.getReason()));
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public List<ErrorsDetailsDto> handle(ConstraintViolationException exception){
        List<ErrorsDetailsDto> dto = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();

        violations.forEach(e -> {
            ErrorsDetailsDto error = new ErrorsDetailsDto(e.getInvalidValue().toString(), e.getMessage());
            dto.add(error);
        });

        return dto;
    }
}
