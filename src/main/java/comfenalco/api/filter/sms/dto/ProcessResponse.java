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
public class ProcessResponse {
    private SummaryResponse summary;

    private List<ValidRecordResponse> validRecords;

    private List<DiscardedRecordResponse> discardedRecords;
}
