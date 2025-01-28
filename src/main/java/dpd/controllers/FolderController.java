package dpd.controllers;

import dpd.DTOs.CreateFolderRequestDTO;
import dpd.DTOs.FolderResponseDTO;
import dpd.DTOs.UpdateFolderRequestDTO;
import dpd.services.FolderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/folders")
public class FolderController {
    @Autowired
    FolderService folderService;

    @GetMapping("{name}")
    public ResponseEntity<FolderResponseDTO> getFolder(@PathVariable String name) {
        log.info("[FolderController] get folder");
        return folderService.getFolder(name);
    }

    @PostMapping("")
    public ResponseEntity<Void> createFolder(@RequestBody @Valid CreateFolderRequestDTO createFolderRequestDTO) {
        log.info("[FolderController] create folder");
        return folderService.createFolder(createFolderRequestDTO);
    }

    @PutMapping("{name}")
    public ResponseEntity<?> updateFolder(@PathVariable String name, @RequestBody @Valid UpdateFolderRequestDTO updateFolderRequestDTO) {
        log.info("[FolderController] update folder");
        return folderService.updateFolder(name, updateFolderRequestDTO);
    }

    @DeleteMapping("{name}")
    public ResponseEntity<Void> deleteFolder(@PathVariable String name) {
        log.info("[FolderController] delete folder");
        return folderService.deleteFolder(name);
    }
}
