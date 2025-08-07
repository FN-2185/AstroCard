package astrocard.repository.custom;
import astrocard.entity.Descriptions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Repository
public class DescriptionsRepository {
    private final JdbcTemplate jdbcTemplate;
    public DescriptionsRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/Test");
        ds.setUsername("postgres");
        ds.setPassword("flamingo911");
        this.jdbcTemplate.setDataSource(ds);
    }
    public List<Descriptions> getDescriptions(Map<String, String> planet_positions_in_signs, Map<String, String> planet_positions_in_houses){
        StringBuilder sql = new StringBuilder("SELECT object, position, description FROM public.descriptions WHERE (object, position) IN (");
        List<Object> params = new ArrayList<>();
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, String> entry : planet_positions_in_signs.entrySet()) {
            joiner.add("(?, ?)");
            params.add(entry.getKey());     // object
            params.add(entry.getValue());   // position
        }
        for (Map.Entry<String, String> entry : planet_positions_in_houses.entrySet()) {
            joiner.add("(?, ?)");
            params.add(entry.getKey());     // object (e.g., "Sun")
            params.add(entry.getValue());   // position (e.g., "10") — дом
        }
        sql.append(joiner.toString()).append(")");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Descriptions pd = new Descriptions();
            pd.setObject(rs.getString("object"));
            pd.setPosition(rs.getString("position"));
            pd.setDescription(rs.getString("description"));
            return pd;
        }, params.toArray());
    }
}
