package dpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import dpd.DTOs.CreateFileRequestDTO;
import dpd.DTOs.UpdateFileRequestDTO;
import dpd.entities.File;
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
public class FileControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private File testFile;
    private Folder testFolder1;
    private Folder testFolder2;

    @BeforeEach
    void setUp() {
        testFolder1 = Folder.builder()
                .name("test_folder_1")
                .build();

        testFolder2 = Folder.builder()
                .name("test_folder_2")
                .build();

        folderRepository.save(testFolder1);
        folderRepository.save(testFolder2);

        testFile = File.builder()
                .filename("test_file.txt")
                .sizeInBytes(100)
                .folders(Arrays.asList(testFolder1, testFolder2))
                .build();

        fileRepository.save(testFile);
    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteById(testFile.getFilename());
        folderRepository.deleteById(testFolder1.getName());
        folderRepository.deleteById(testFolder2.getName());
    }

    @Test
    void getFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/{filename}", testFile.getFilename()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value(testFile.getFilename()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sizeInBytes").value(testFile.getSizeInBytes()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders[0]").value(testFolder1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.folders[1]").value(testFolder2.getName()));
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
                .folders(Arrays.asList(testFolder1.getName(), testFolder2.getName()))
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
                .folders(Arrays.asList(testFolder1.getName(), testFolder2.getName()))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/files")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createFileFolderNotFound() throws Exception {
        CreateFileRequestDTO createFileRequestDTO = CreateFileRequestDTO
                .builder()
                .filename("")
                .sizeInBytes(200)
                .folders(Arrays.asList(testFolder1.getName(), testFolder2.getName(), "test_folder_3"))
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
                .folders(Arrays.asList(testFolder1.getName()))
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
                .folders(Arrays.asList())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/files/{filename}", "notExistingFile.txt")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateFileFolderNotFound() throws Exception {
        UpdateFileRequestDTO updateFileRequestDTO = UpdateFileRequestDTO
                .builder()
                .sizeInBytes(1000)
                .folders(Arrays.asList("folderZ"))
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/files/{filename}", "notExistingFile.txt")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateFileRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/{filename}", testFile.getFilename()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteFileNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/files/{filename}", "NotExistingFile.txt"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
