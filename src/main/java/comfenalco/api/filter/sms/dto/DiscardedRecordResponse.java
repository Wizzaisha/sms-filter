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
public class DiscardedRecordResponse {
    private String telefonoIngresado;

    private String telefonoNormalizado;

    private LocalDate fechaProgramada;

    private String area;

    private String motivoRechazo;
}
