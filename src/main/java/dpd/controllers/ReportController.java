package dpd.controllers;

import dpd.DTOs.DeepestFolderReportResponseDTO;
import dpd.DTOs.FileReportResponseDTO;
import dpd.DTOs.FolderReportRequestDTO;
import dpd.DTOs.FolderReportResponseDTO;
import dpd.services.FileService;
import dpd.services.FolderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/reports")
public class ReportController {
    @Autowired
    FileService fileService;

    @Autowired
    FolderService folderService;

    @GetMapping("files")
    public ResponseEntity<FileReportResponseDTO> getFileReport() {
        log.info("[ReportController] get file report");
        return fileService.getFileReport();
    }

    @PostMapping("folders")
    public ResponseEntity<FolderReportResponseDTO> getFolderReport(@RequestBody @Valid FolderReportRequestDTO folderReportRequestDTO) {
        log.info("[ReportController] get folder report");
        return folderService.getFolderReport(folderReportRequestDTO);
    }

    @GetMapping("deepest-folder")
    public ResponseEntity<DeepestFolderReportResponseDTO> getDeepestFolderReport() {
        log.info("[ReportController] get deepest folder report");
        return folderService.getDeepestFolderReport();
    }
}
