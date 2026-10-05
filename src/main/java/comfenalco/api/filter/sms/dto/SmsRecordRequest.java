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
public class SmsRecordRequest {
    private String telefono;

    private LocalDate fechaEnvio;

    private String area;
}
