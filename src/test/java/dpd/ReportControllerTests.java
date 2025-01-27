package dpd;

import com.fasterxml.jackson.databind.ObjectMapper;
import dpd.DTOs.FolderReportRequestDTO;
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
public class ReportControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private File testFile1;
    private File testFile2;
    private File testFile3;
    private Folder testFolder1;
    private Folder testFolder2;
    private Folder testFolder3;
    private Folder testFolder4;

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

        testFolder3 = Folder.builder()
                .name("test_folder_3")
                .build();
        folderRepository.save(testFolder3);

        testFolder4 = Folder.builder()
                .name("test_folder_4")
                .subFolders(Arrays.asList(testFolder2, testFolder3))
                .build();
        folderRepository.save(testFolder4);

        testFile1 = File.builder()
                .filename("test_file1.txt")
                .sizeInBytes(100)
                .folders(Arrays.asList(testFolder1, testFolder2))
                .build();
        fileRepository.save(testFile1);

        testFile2 = File.builder()
                .filename("test_file2.txt")
                .sizeInBytes(1000)
                .folders(Arrays.asList(testFolder3))
                .build();
        fileRepository.save(testFile2);

        testFile3 = File.builder()
                .filename("test_file3.txt")
                .sizeInBytes(676)
                .folders(Arrays.asList(testFolder2, testFolder4))
                .build();
        fileRepository.save(testFile3);
    }

    @AfterEach
    void tearDown() {
        fileRepository.deleteById(testFile1.getFilename());
        fileRepository.deleteById(testFile2.getFilename());
        fileRepository.deleteById(testFile3.getFilename());
        folderRepository.deleteById(testFolder1.getName());
        folderRepository.deleteById(testFolder2.getName());
        folderRepository.deleteById(testFolder3.getName());
        folderRepository.deleteById(testFolder4.getName());
    }

    @Test
    void getFileReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports/files"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.filesSize").value(1776))
                .andExpect(MockMvcResultMatchers.jsonPath("$.averageFileSize").value(592))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maxFileSize").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.minFileSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.filesNumber").value(3));
    }

    @Test
    void getFolderReport() throws Exception {
        FolderReportRequestDTO folderReportRequestDTO = FolderReportRequestDTO
                .builder()
                .foldersNumber(4)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports/folders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(folderReportRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.foldersFileSize.test_folder_4").value(2552))
                .andExpect(MockMvcResultMatchers.jsonPath("$.foldersFileCount.test_folder_4").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.foldersFileAverageSize.test_folder_4").value(510.4));
    }

    @Test
    void getFolderReportValidationError() throws Exception {
        FolderReportRequestDTO folderReportRequestDTO = FolderReportRequestDTO
                .builder()
                .foldersNumber(0)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reports/folders")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(folderReportRequestDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getDeepestFolderReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reports/deepest-folder"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.deepestFolder.test_folder_4").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deepestFolder.test_folder_2").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deepestFolder.test_folder_1").value(1));
    }
}
