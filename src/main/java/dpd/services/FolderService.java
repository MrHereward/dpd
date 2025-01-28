package dpd.services;

import dpd.DTOs.CreateFolderRequestDTO;
import dpd.DTOs.FolderResponseDTO;
import dpd.DTOs.UpdateFolderRequestDTO;
import dpd.entities.Folder;
import dpd.repositories.FolderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderService {
    @Autowired
    FolderRepository folderRepository;

    public ResponseEntity<FolderResponseDTO> getFolder(String name) {
        log.info("[FolderService] get folder");

        Optional<Folder> folder = folderRepository.findById(name);

        if (folder.isPresent()) {
            FolderResponseDTO folderResponseDTO = FolderResponseDTO
                    .builder()
                    .name(folder.get().getName())
                    .subFolders(folder.get().getSubFolders().stream().map(Folder::getName).collect(Collectors.toList()))
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(folderResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> createFolder(CreateFolderRequestDTO createFolderRequestDTO) {
        log.info("[FolderService] create folder");

        if (folderRepository.existsById(createFolderRequestDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Folder> folders = new ArrayList<>();

        if (createFolderRequestDTO.getSubFolders() != null) {
            for (String folderName : createFolderRequestDTO.getSubFolders()) {
                Optional<Folder> folder = folderRepository.findById(folderName);
                if (folder.isPresent()) {
                    folders.add(folder.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            }
        }

        Folder folder = Folder
                .builder()
                .name(createFolderRequestDTO.getName())
                .subFolders(folders)
                .build();
        folderRepository.save(folder);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<Void> updateFolder(String name, UpdateFolderRequestDTO updateFolderRequestDTO) {
        log.info("[FolderService] update folder");

        Optional<Folder> folder = folderRepository.findById(name);

        List<Folder> folders = new ArrayList<>();

        if (updateFolderRequestDTO != null && updateFolderRequestDTO.getSubFolders() != null) {
            for (String folderName : updateFolderRequestDTO.getSubFolders()) {
                Optional<Folder> optional_folder = folderRepository.findById(folderName);
                if (optional_folder.isPresent()) {
                    folders.add(optional_folder.get());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
            }
        }

        if (folder.isPresent()) {
            folder.get().setSubFolders(folders);
            folderRepository.save(folder.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteFolder(String name) {
        log.info("[FolderService] delete folder");

        Optional<Folder> folder = folderRepository.findById(name);

        if (folder.isPresent()) {
            folderRepository.deleteById(name);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
