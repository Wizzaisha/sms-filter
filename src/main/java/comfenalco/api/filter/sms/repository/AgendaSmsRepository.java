package comfenalco.api.filter.sms.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AgendaSmsRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Intenta crear una reserva.
     *
     * Retorna:
     * true  -> la reserva fue creada
     * false -> el número ya estaba ocupado para esa fecha
     */
    public boolean tryReserve(
            String telefono,
            LocalDate fechaEnvio,
            String area) {

        String sql =
                "INSERT INTO agenda_sms " +
                        "(telefono, fecha_envio, area, estado) " +
                        "VALUES (?, ?, ?, 'Ocupado') " +
                        "ON CONFLICT (telefono, fecha_envio) " +
                        "DO NOTHING";

        int affectedRows = jdbcTemplate.update(
                sql,
                telefono,
                Date.valueOf(fechaEnvio),
                area
        );

        return affectedRows == 1;
    }

    /**
     * Obtiene el área que realizó la reserva existente.
     */
    public String findOwner(
            String telefono,
            LocalDate fechaEnvio) {

        String sql =
                "SELECT area " +
                        "FROM agenda_sms " +
                        "WHERE telefono = ? " +
                        "AND fecha_envio = ?";

        List<String> result = jdbcTemplate.query(
                sql,
                new Object[]{
                        telefono,
                        Date.valueOf(fechaEnvio)
                },
                (rs, rowNum) -> rs.getString("area")
        );

        return result.isEmpty()
                ? null
                : result.get(0);
    }

    public boolean release(
            String telefono,
            LocalDate fechaEnvio,
            String area) {

        String sql =
                "DELETE FROM agenda_sms " +
                        "WHERE telefono = ? " +
                        "AND fecha_envio = ? " +
                        "AND area = ?";

        int affectedRows = jdbcTemplate.update(
                sql,
                telefono,
                Date.valueOf(fechaEnvio),
                area
        );

        return affectedRows == 1;
    }
}
