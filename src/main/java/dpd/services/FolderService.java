package dpd.services;

import dpd.DTOs.*;
import dpd.entities.Folder;
import dpd.repositories.FileRepository;
import dpd.repositories.FolderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FolderService {
    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FileRepository fileRepository;

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
            for (String folder_name : createFolderRequestDTO.getSubFolders()) {
                Optional<Folder> folder = folderRepository.findById(folder_name);
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

        if (updateFolderRequestDTO != null) {
            for (String folder_name : updateFolderRequestDTO.getSubFolders()) {
                Optional<Folder> optional_folder = folderRepository.findById(folder_name);
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

    public ResponseEntity<FolderReportResponseDTO> getFolderReport(FolderReportRequestDTO folderReportRequestDTO) {
        log.info("[FolderService] get folder report");

        List<Folder> folders = folderRepository.findAll();

        Map<String, Long> foldersFileSize = new HashMap<>();
        Map<String, Long> foldersFileCount = new HashMap<>();
        Map<String, Double> foldersFileAverageSize = new HashMap<>();

        computeFolderStats(folders, foldersFileSize, foldersFileCount, foldersFileAverageSize);

        FolderReportResponseDTO folderReportResponseDTO = FolderReportResponseDTO
                .builder()
                .foldersFileSize(
                        foldersFileSize
                                .entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(folderReportRequestDTO.getFoldersNumber())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new)))
                .foldersFileCount(
                        foldersFileCount
                                .entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(folderReportRequestDTO.getFoldersNumber())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new)))
                .foldersFileAverageSize(
                        foldersFileAverageSize
                                .entrySet()
                                .stream()
                                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                .limit(folderReportRequestDTO.getFoldersNumber())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        (e1, e2) -> e1,
                                        LinkedHashMap::new)))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(folderReportResponseDTO);
    }

    public void computeFolderStats(List<Folder> folders, Map<String, Long> foldersFileSize, Map<String, Long> foldersFileCount, Map<String, Double> foldersFileAverageSize) {
        for (Folder folder : folders) {
            Long foldersFileSizeValue = fileRepository.sumFileSizesByFolder(folder.getName());
            foldersFileSizeValue = foldersFileSizeValue == null ? 0 : foldersFileSizeValue;
            Long foldersFileCountValue = fileRepository.countFilesByFolder(folder.getName());
            foldersFileCountValue = foldersFileCountValue == null ? 0 : foldersFileCountValue;

            Long subFoldersFileSize = 0L;
            Long subFoldersFileCount = 0L;

            if (folder.getSubFolders() != null && !folder.getSubFolders().isEmpty()) {
                for (Folder subFolder : folder.getSubFolders()) {
                    subFoldersFileSize += foldersFileSize.getOrDefault(subFolder.getName(), 0L);
                    subFoldersFileCount += foldersFileCount.getOrDefault(subFolder.getName(), 0L);
                }
            }

            Long totalFileSize = foldersFileSizeValue + subFoldersFileSize;
            Long totalFileCount = foldersFileCountValue + subFoldersFileCount;
            Double averageFileSize = totalFileCount == 0 ? 0.0 : (double) totalFileSize / totalFileCount;

            foldersFileSize.put(folder.getName(), totalFileSize);
            foldersFileCount.put(folder.getName(), totalFileCount);
            foldersFileAverageSize.put(folder.getName(), averageFileSize);
        }
    }

    public ResponseEntity<DeepestFolderReportResponseDTO> getDeepestFolderReport() {
        log.info("[FolderService] get deepest folder report");

        List<Folder> folders = folderRepository.findAll();

        DeepestFolderReportResponseDTO deepestFolderReportResponseDTO = DeepestFolderReportResponseDTO
                .builder()
                .deepestFolder(generateDeepestFolderReport(folders))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(deepestFolderReportResponseDTO);
    }

    public List<String> findDeepestFolderPath(Folder folder, List<String> currentPath, Map<String, Long> currentFileCounts) {
        currentPath.add(folder.getName());
        Long fileCount = fileRepository.countFilesByFolder(folder.getName());
        currentFileCounts.put(folder.getName(), fileCount != null ? fileCount : 0L);

        if (folder.getSubFolders() == null || folder.getSubFolders().isEmpty()) {
            return currentPath;
        }

        List<String> deepestPath = new ArrayList<>();
        for (Folder subFolder : folder.getSubFolders()) {
            List<String> subFolderPath = findDeepestFolderPath(subFolder, new ArrayList<>(currentPath), currentFileCounts);
            if (subFolderPath.size() > deepestPath.size()) {
                deepestPath = subFolderPath;
            }
        }

        return deepestPath;
    }

    public LinkedHashMap<String, Long> generateDeepestFolderReport(List<Folder> allFolders) {
        List<String> deepestPath = new ArrayList<>();
        Map<String, Long> folderFileCounts = new HashMap<>();

        for (Folder folder : allFolders) {
            Map<String, Long> currentFileCounts = new HashMap<>();
            List<String> currentPath = findDeepestFolderPath(folder, new ArrayList<>(), currentFileCounts);
            if (currentPath.size() > deepestPath.size()) {
                deepestPath = currentPath;
                folderFileCounts = currentFileCounts;
            }
        }

        LinkedHashMap<String, Long> report = new LinkedHashMap<>();
        for (String folderName : deepestPath) {
            report.put(folderName, folderFileCounts.getOrDefault(folderName, 0L));
        }

        return report;
    }
}
