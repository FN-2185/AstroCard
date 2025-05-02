package astrocard.repository;

import astrocard.entity.Cities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CityCoordinatesRepository extends JpaRepository<Cities, Long> {
    Optional<Cities> findByName(String cityName);
    @Query(value = """
        SELECT c.* FROM cities c 
        JOIN countrycode cc ON c.country_code = cc.iso 
        WHERE cc.country = :country 
        AND c.alternatenames ILIKE %:cityName%
        LIMIT 1
        """, nativeQuery = true)
    Optional<Cities> findCityByCountryAndName(@Param("country") String country,
                                              @Param("cityName") String cityName);
}
