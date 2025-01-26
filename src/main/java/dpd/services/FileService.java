package dpd.services;

import dpd.DTOs.CreateFileRequestDTO;
import dpd.DTOs.FileResponseDTO;
import dpd.DTOs.UpdateFileRequestDTO;
import dpd.entities.File;
import dpd.repositories.FileRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    public ResponseEntity<FileResponseDTO> getFile(String filename) {
        log.info("[FileService] get file");

        Optional<File> file = fileRepository.findById(filename);

        if (file.isPresent()) {
            FileResponseDTO fileResponseDTO = FileResponseDTO
                    .builder()
                    .filename(file.get().getFilename())
                    .sizeInBytes(file.get().getSizeInBytes())
                    .folders(file.get().getFolders())
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(fileResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> createFile(CreateFileRequestDTO createFileRequestDTO) {
        log.info("[FileService] create file");

        if (fileRepository.existsById(createFileRequestDTO.getFilename())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        File file = File
                .builder()
                .filename(createFileRequestDTO.getFilename())
                .sizeInBytes(createFileRequestDTO.getSizeInBytes())
                .build();
        fileRepository.save(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    public ResponseEntity<Void> updateFile(String filename, UpdateFileRequestDTO updateFileRequestDTO) {
        log.info("[FileService] update file");

        Optional<File> file = fileRepository.findById(filename);

        if (file.isPresent()) {
            file.get().setSizeInBytes(updateFileRequestDTO.getSizeInBytes());
            file.get().setFolders(updateFileRequestDTO.getFolders());
            fileRepository.save(file.get());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Transactional
    public ResponseEntity<Void> deleteFile(String filename) {
        log.info("[FileService] delete file");

        Optional<File> file = fileRepository.findById(filename);

        if (file.isPresent()) {
            fileRepository.deleteById(filename);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
