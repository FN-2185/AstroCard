package astrocard.repository.custom;

import astrocard.service.DescriptionKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class DescriptionsRepository {
    private final JdbcTemplate jdbcTemplate;
    public DescriptionsRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public Map<DescriptionKey, String> load(Set<? extends DescriptionKey> keys) {
        if (keys.isEmpty()) return Map.of();
        StringJoiner placeholders = new StringJoiner(", ");
        List<Object> parameters = new ArrayList<>();
        Map<DescriptionKey, String> descriptions = new HashMap<>();
        for (DescriptionKey key : keys) {
            placeholders.add("(?, ?)");
            parameters.add(key.object()); parameters.add(key.position());
            descriptions.put(key, "Описание пока отсутствует.");
        }
        String sql = "SELECT object, position, description FROM public.descriptions WHERE (object, position) IN ("
                + placeholders + ")";
        jdbcTemplate.query(sql, result -> {
            String text = result.getString("description");
            if (text != null) descriptions.put(new DescriptionKey(result.getString("object"), result.getString("position")), text);
        }, parameters.toArray());
        return Map.copyOf(descriptions);
    }
}
