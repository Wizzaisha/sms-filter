package comfenalco.api.filter.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidRecordResponse {
    private String telefonoOriginal;

    private String telefono;

    private LocalDate fechaProgramada;

    private String area;

    private String estado;
}
