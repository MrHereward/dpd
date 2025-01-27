package dpd.repositories;

import dpd.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, String> {
    @Query("SELECT SUM(sizeInBytes) FROM File")
    Long sumAllFilesSizeInBytes();

    @Query("SELECT AVG(sizeInBytes) FROM File")
    Double getAverageFilesSizeInBytes();

    @Query("SELECT sizeInBytes FROM File ORDER BY sizeInBytes DESC LIMIT 1")
    Long getMaxSizeInBytes();

    @Query("SELECT sizeInBytes FROM File ORDER BY sizeInBytes ASC LIMIT 1")
    Long getMinSizeInBytes();

    @Query("SELECT SUM(f.sizeInBytes) FROM File f JOIN f.folders fo WHERE fo.name = :folderName")
    Long sumFileSizesByFolder(@Param("folderName") String folderName);

    @Query("SELECT COUNT(f) FROM File f JOIN folders fo WHERE fo.name = :folderName")
    Long countFilesByFolder(@Param("folderName") String folderName);
}
