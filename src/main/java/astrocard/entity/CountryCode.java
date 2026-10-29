package astrocard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "countrycode")
public class CountryCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "iso", length = 2)
    private String iso;
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "iso3", length = 3)
    private String iso3;
    @Column(name = "iso_numeric")
    private Integer iso_numeric;
    @Column(name = "fips")
    private String fips;
    @Column(name = "country")
    private String country;
    @Column(name = "tld")
    private String tld;
    @Column(name = "geonameid")
    private Long geonameid;
    @Column(name = "equivalent_fips_code")
    private String equivalent_fips_code;
    public CountryCode(){}

    public CountryCode(Integer id, String iso, String iso3, Integer iso_numeric, String fips, String country, String tld, Long geonameid, String equivalent_fips_code) {
        this.id = id;
        this.iso = iso;
        this.iso3 = iso3;
        this.iso_numeric = iso_numeric;
        this.fips = fips;
        this.country = country;
        this.tld = tld;
        this.geonameid = geonameid;
        this.equivalent_fips_code = equivalent_fips_code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public Integer getIso_numeric() {
        return iso_numeric;
    }

    public void setIso_numeric(Integer iso_numeric) {
        this.iso_numeric = iso_numeric;
    }

    public String getFips() {
        return fips;
    }

    public void setFips(String fips) {
        this.fips = fips;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTld() {
        return tld;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public Long getGeonameid() {
        return geonameid;
    }

    public void setGeonameid(Long geonameid) {
        this.geonameid = geonameid;
    }

    public String getEquivalent_fips_code() {
        return equivalent_fips_code;
    }

    public void setEquivalent_fips_code(String equivalent_fips_code) {
        this.equivalent_fips_code = equivalent_fips_code;
    }
}
