package astrocard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
@Entity
@Table(name = "cities")
public class Cities {
     @Id
     @Column(name = "geonameid")
     private Long geoNameId;
     @Column(name = "name")
     private String name;
     @Column(name = "asciiname")
     private String asciiname;
     @Column(name = "alternatenames")
     private String alternatenames;
     @Column(name = "latitude")
     private Double latitude;
     @Column(name = "longitude")
     private Double longitude;
     @Column(name = "feature_class")
     private String feature_class;
     @Column(name = "feature_code")
     private String feature_code;
     @Column(name = "country_code")
     private String country_code;
     @Column(name = "cc2")
     private String cc2;
     @Column(name = "admin1_code")
     private String admin1_code;
     @Column(name = "admin2_code")
     private String admin2_code;
     @Column(name = "admin3_code")
     private String admin3_code;
     @Column(name = "admin4_code")
     private String admin4_code;
     @Column(name = "timezone")
     private String timezone;
     @Column(name = "modification_date")
     private LocalDate modification_date;
     public Cities(){}

     public Cities(Long geoNameId, String name, String asciiname, String alternatenames, Double latitude, Double longitude, String feature_class, String feature_code, String country_code, String cc2, String admin1_code, String admin2_code, String admin3_code, String admin4_code, String timezone, LocalDate modification_date) {
          this.geoNameId = geoNameId;
          this.name = name;
          this.asciiname = asciiname;
          this.alternatenames = alternatenames;
          this.latitude = latitude;
          this.longitude = longitude;
          this.feature_class = feature_class;
          this.feature_code = feature_code;
          this.country_code = country_code;
          this.cc2 = cc2;
          this.admin1_code = admin1_code;
          this.admin2_code = admin2_code;
          this.admin3_code = admin3_code;
          this.admin4_code = admin4_code;
          this.timezone = timezone;
          this.modification_date = modification_date;
     }

     public Long getGeoNameId() {
          return geoNameId;
     }

     public void setGeoNameId(Long geoNameId) {
          this.geoNameId = geoNameId;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getAsciiname() {
          return asciiname;
     }

     public void setAsciiname(String asciiname) {
          this.asciiname = asciiname;
     }

     public String getAlternatenames() {
          return alternatenames;
     }

     public void setAlternatenames(String alternatenames) {
          this.alternatenames = alternatenames;
     }

     public Double getLatitude() {
          return latitude;
     }

     public void setLatitude(Double latitude) {
          this.latitude = latitude;
     }

     public Double getLongitude() {
          return longitude;
     }

     public void setLongitude(Double longitude) {
          this.longitude = longitude;
     }

     public String getFeature_class() {
          return feature_class;
     }

     public void setFeature_class(String feature_class) {
          this.feature_class = feature_class;
     }

     public String getFeature_code() {
          return feature_code;
     }

     public void setFeature_code(String feature_code) {
          this.feature_code = feature_code;
     }

     public String getCountry_code() {
          return country_code;
     }

     public void setCountry_code(String country_code) {
          this.country_code = country_code;
     }

     public String getCc2() {
          return cc2;
     }

     public void setCc2(String cc2) {
          this.cc2 = cc2;
     }

     public String getAdmin1_code() {
          return admin1_code;
     }

     public void setAdmin1_code(String admin1_code) {
          this.admin1_code = admin1_code;
     }

     public String getAdmin2_code() {
          return admin2_code;
     }

     public void setAdmin2_code(String admin2_code) {
          this.admin2_code = admin2_code;
     }

     public String getAdmin3_code() {
          return admin3_code;
     }

     public void setAdmin3_code(String admin3_code) {
          this.admin3_code = admin3_code;
     }

     public String getAdmin4_code() {
          return admin4_code;
     }

     public void setAdmin4_code(String admin4_code) {
          this.admin4_code = admin4_code;
     }

     public String getTimezone() {
          return timezone;
     }

     public void setTimezone(String timezone) {
          this.timezone = timezone;
     }

     public LocalDate getModification_date() {
          return modification_date;
     }

     public void setModification_date(LocalDate modification_date) {
          this.modification_date = modification_date;
     }
}
