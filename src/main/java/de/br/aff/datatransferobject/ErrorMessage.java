package de.br.aff.datatransferobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
public class ErrorMessage
{

    private String code;
    private String message;
    @Singular
    private List<Object> fields;


    //message property is required
    public static ErrorMessageBuilder builder(String message)
    {
        return new ErrorMessageBuilder().message(message);
    }

}