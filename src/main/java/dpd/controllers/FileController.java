package dpd.controllers;

import dpd.DTOs.CreateFileRequestDTO;
import dpd.DTOs.FileResponseDTO;
import dpd.DTOs.UpdateFileRequestDTO;
import dpd.services.FileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/files")
public class FileController {
    @Autowired
    public FileService fileService;

    @GetMapping("{filename}")
    public ResponseEntity<FileResponseDTO> getFile(@PathVariable String filename) {
        log.info("[FileController] get file");
        return fileService.getFile(filename);
    }

    @PostMapping("")
    public ResponseEntity<Void> createFile(@RequestBody @Valid CreateFileRequestDTO createFileRequestDTO) {
        log.info("[FileController] create file");
        return fileService.createFile(createFileRequestDTO);
    }

    @PutMapping("{filename}")
    public ResponseEntity<Void> updateFile(@PathVariable String filename, @RequestBody @Valid UpdateFileRequestDTO updateFileRequestDTO) {
        log.info("[FileController] update file");
        return fileService.updateFile(filename, updateFileRequestDTO);
    }

    @DeleteMapping("{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        log.info("[FileController] delete file");
        return fileService.deleteFile(filename);
    }
}
