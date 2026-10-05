package comfenalco.api.filter.sms.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequest {
    private String area;

    private List<SmsRecordRequest> registros;
}
