package de.br.aff.domainobject;

import de.br.aff.domainvalue.EngineType;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id", "dateCreated"})
@Entity
@Table(
    name = "car",
    uniqueConstraints = @UniqueConstraint(name = "license_plate", columnNames = {"licensePlate"})
)
public class CarDO
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();

    @Column(nullable = false)
    @NotNull(message = "License plate can not be null!")
    private String licensePlate;

    private int seatCount;
    private boolean convertible;
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineType engineType;

    private String manufacturer;
    private String model;

    @OneToOne(mappedBy = "car")
    private DriverDO driver;


    public CarDO(String licensePlate, int seatCount, boolean convertible, int rating, EngineType engineType, String manufacturer, String model)
    {
        this.licensePlate = licensePlate;
        this.seatCount = seatCount;
        this.convertible = convertible;
        this.rating = rating;
        this.engineType = engineType;
        this.manufacturer = manufacturer;
        this.model = model;
    }
}
