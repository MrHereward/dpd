package dpd.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFileRequestDTO {
    @NotNull
    @NotBlank
    @Length(min = 1, max = 1024)
    private String filename;

    @NotNull
    @Min(0)
    private Integer sizeInBytes;

    private List<String> folders;
}
