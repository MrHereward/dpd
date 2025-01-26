package dpd.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {
    @Id
    @NotNull
    @NotBlank
    @Length(min = 1, max = 1024)
    @Column(name = "filename")
    private String filename;

    @NotNull
    @Min(0)
    @Column(name = "size_in_bytes")
    private Integer sizeInBytes;

    @Column(name = "folders")
    private List<String> folders;
}
