package de.br.aff.datatransferobject;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class CarDTO
{
    private Long id;
    @NotNull
    private String licensePlate;
    @Setter
    private int seatCount;
    private boolean convertible;
    private int rating;
    @NotNull
    @Setter
    private String engineType;
    private String manufacturer;
    private String model;
}
