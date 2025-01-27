package dpd.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderReportResponseDTO {
    Map<String, Long> foldersFileSize;
    Map<String, Long> foldersFileCount;
    Map<String, Double> foldersFileAverageSize;
}
