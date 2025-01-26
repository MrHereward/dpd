package dpd.DTOs;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDTO {
    private String filename;
    private Integer sizeInBytes;
    private List<String> folders;
}
