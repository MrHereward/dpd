package dpd.entities;

import jakarta.persistence.*;
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
@ToString
public class Folder {
    @Id
    @NotNull
    @NotBlank
    @Length(min = 1, max = 1024)
    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Folder> subFolders;
}
