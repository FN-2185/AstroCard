package astrocard.repository;

import astrocard.entity.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryCodeRepository extends JpaRepository<CountryCode, Long> {
    Optional<CountryCode> findCountryCodeByCountry (String country);
}
