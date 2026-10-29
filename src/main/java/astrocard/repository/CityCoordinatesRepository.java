package astrocard.repository;

import astrocard.entity.Cities;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CityCoordinatesRepository extends JpaRepository<Cities, Long> {
    @Query(value = """
        SELECT c.* FROM cities c JOIN countrycode cc ON c.country_code = cc.iso
        WHERE cc.country = :country
          AND (lower(c.name) = lower(:city) OR lower(c.asciiname) = lower(:city)
               OR lower(:city) = ANY(string_to_array(lower(c.alternatenames), ','))
               OR cast(c.geonameid as text) = :city)
        ORDER BY c.geonameid LIMIT 2
        """, nativeQuery = true)
    List<Cities> findCandidates(@Param("country") String country, @Param("city") String city);
}
