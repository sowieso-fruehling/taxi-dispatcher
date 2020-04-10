package de.br.aff.dataaccessobject;

import de.br.aff.domainobject.CarDO;
import de.br.aff.domainobject.DriverDO;
import de.br.aff.domainvalue.OnlineStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * Database Access Object for driver table.
 * <p/>
 */
public interface DriverRepository extends CrudRepository<DriverDO, Long>
{

    List<DriverDO> findByOnlineStatus(OnlineStatus onlineStatus);

    Optional<DriverDO> findByCar(CarDO car);

    List<DriverDO> findByUsername(String username);

    List<DriverDO> findByUsernameAndOnlineStatus(String username, OnlineStatus onlineStatus);
}


