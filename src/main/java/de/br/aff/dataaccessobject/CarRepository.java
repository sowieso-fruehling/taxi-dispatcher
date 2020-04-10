package de.br.aff.dataaccessobject;

import de.br.aff.domainobject.CarDO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarDO, Long>
{
    Optional<CarDO> findByLicensePlate(String licensePlate);

    List<CarDO> findByRating(int rating);
}
