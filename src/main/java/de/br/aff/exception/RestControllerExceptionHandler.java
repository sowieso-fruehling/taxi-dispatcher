package de.br.aff.exception;

import de.br.aff.datatransferobject.ErrorMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class RestControllerExceptionHandler
{
    @ExceptionHandler({ConstraintsViolationException.class, IllegalArgumentException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class,
        CarAlreadyInUseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorMessage bedRequestHandler(Exception ex, HttpServletRequest request)
    {
        logWarning(ex, request, "constraints violated exception");
        return ErrorMessage.builder(ex.getMessage()).build();
    }


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorMessage notFoundHandler(EntityNotFoundException ex, HttpServletRequest request)
    {
        logError(ex, request, "entity not found");
        return ErrorMessage.builder(ex.getMessage()).build();
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorMessage generalError(Exception ex, HttpServletRequest request)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);
        ps.close();

        logError(ex, request, "generalError: " + baos.toString());
        return ErrorMessage.builder(ex.getLocalizedMessage() != null ?
            ex.getLocalizedMessage() : ex.getMessage() != null ?
            ex.getMessage() : ex.toString())
            .build();
    }


    private void logError(Exception ex, HttpServletRequest request, String identifier)
    {
        log.error(request.getRequestURL().toString() + ", " + identifier + ": " + ex.getMessage());
    }


    private void logWarning(Exception ex, HttpServletRequest request, String identifier)
    {
        log.warn(request.getRequestURL().toString() + ", " + identifier + ": " + ex.getMessage());
    }
}
