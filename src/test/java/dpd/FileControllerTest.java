package dpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import dpd.DTOs.CreateFileRequestDTO;
import dpd.DTOs.UpdateFileRequestDTO;
import dpd.entities.File;
import dpd.repositories.FileRepository;
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
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private File testFile;

    @BeforeEach
    void setUp() {
        testFile = File
                .builder()
                .filename("file.txt")
                .sizeInBytes(100)
                .folders(Arrays.asList("folder1", "folder2"))
                .build();
        fileRepository.save(testFile);
    }

    @AfterEach
    void tearDown() {
        fileRepository.delete(testFile);
    }

    @Test
    void getFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/{filename}", testFile.getFilename()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value(testFile.getFilename()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sizeInBytes").value(testFile.getSizeInBytes()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders[0]").value("folder1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders[1]").value("folder2"));
    }

    @Test
    void getFileNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/{filename}", "notExistingFile.txt"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void createFile() throws Exception {
        CreateFileRequestDTO createFileRequestDTO = CreateFileRequestDTO
                .builder()
                .filename("another_file.txt")
                .sizeInBytes(200)
                .folders(Arrays.asList("folder1", "folder2"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/files")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        fileRepository.deleteById(createFileRequestDTO.getFilename());
    }

    @Test
    void createFileValidationError() throws Exception {
        CreateFileRequestDTO createFileRequestDTO = CreateFileRequestDTO
                .builder()
                .filename("")
                .sizeInBytes(200)
                .folders(Arrays.asList("folder1", "folder2"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/files")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateFile() throws Exception {
        UpdateFileRequestDTO updateFileRequestDTO = UpdateFileRequestDTO
                .builder()
                .sizeInBytes(1000)
                .folders(Arrays.asList("folderX", "folderY", "folderZ"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/files/{filename}", testFile.getFilename())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateFileNotFound() throws Exception {
        UpdateFileRequestDTO updateFileRequestDTO = UpdateFileRequestDTO
                .builder()
                .sizeInBytes(1000)
                .folders(Arrays.asList("folderX", "folderY", "folderZ"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/files/{filename}", "notExistingFile.txt")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
