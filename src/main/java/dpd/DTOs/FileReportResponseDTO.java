package dpd.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileReportResponseDTO {
    Long filesSize;
    Double averageFileSize;
    Long maxFileSize;
    Long minFileSize;
    Long filesNumber;
}
