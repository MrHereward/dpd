package dpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import dpd.DTOs.CreateFolderRequestDTO;
import dpd.DTOs.UpdateFolderRequestDTO;
import dpd.entities.Folder;
import dpd.repositories.FileRepository;
import dpd.repositories.FolderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class FolderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Folder testFolder1;
    private Folder testFolder2;

    @BeforeEach
    void setUp() {
        testFolder1 = Folder.builder()
                .name("test_folder_1")
                .build();

        folderRepository.save(testFolder1);

        testFolder2 = Folder.builder()
                .name("test_folder_2")
                .subFolders(Arrays.asList(testFolder1))
                .build();

        folderRepository.save(testFolder2);
    }

    @AfterEach
    void tearDown() {
        folderRepository.deleteById(testFolder1.getName());
        folderRepository.deleteById(testFolder2.getName());
    }

    @Test
    void getFolder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/folders/{name}", testFolder2.getName()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testFolder2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subFolders.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subFolders[0]").value(testFolder1.getName()));
    }

    @Test
    void getFolderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/folders/{name}", "notExistingFolder"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createFolder() throws Exception {
        CreateFolderRequestDTO createFolderRequestDTO = CreateFolderRequestDTO
                .builder()
                .name("test_folder_3")
                .subFolders(Arrays.asList(testFolder2.getName()))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/folders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFolderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        folderRepository.deleteById(createFolderRequestDTO.getName());
    }

    @Test
    void createFolderValidationError() throws Exception {
        CreateFolderRequestDTO createFolderRequestDTO = CreateFolderRequestDTO
                .builder()
                .name("")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/folders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFolderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createFolderFolderNotFound() throws Exception {
        CreateFolderRequestDTO createFolderRequestDTO = CreateFolderRequestDTO
                .builder()
                .name("test_folder_3")
                .subFolders(Arrays.asList("not_existing_folder"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/folders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFolderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateFolder() throws Exception {
        UpdateFolderRequestDTO updateFolderRequestDTO = UpdateFolderRequestDTO
                .builder()
                .subFolders(Arrays.asList())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/folders/{name}", testFolder2.getName())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFolderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateFolderNotFound() throws Exception {
        UpdateFolderRequestDTO updateFolderRequestDTO = UpdateFolderRequestDTO
                .builder()
                .subFolders(Arrays.asList())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/folders/{name}", "notExistingFolder")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFolderRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteFolder() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/folders/{name}", testFolder1.getName()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteFolderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/folders/{name}", "notExistingFolder"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
