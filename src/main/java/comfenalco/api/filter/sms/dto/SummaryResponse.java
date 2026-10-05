package comfenalco.api.filter.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {
    private int totalRecordsExcel;

    private int validApproved;

    private int totalDiscarded;

    private int discardedByFormat;

    private int discardedByInternalDuplicate;

    private int discardedByDatabaseOccupation;
}
