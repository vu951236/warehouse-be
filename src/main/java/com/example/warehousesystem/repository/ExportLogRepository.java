package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExportLogRepository extends JpaRepository<ExportLog, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ExportLog e SET e.note = :note WHERE e.exportCode = :exportCode")
    void updateNoteByExportCode(@Param("exportCode") String exportCode, @Param("note") String note);

    @Query("SELECT e FROM ExportLog e WHERE e.exportCode = " +
            "(SELECT MAX(el.exportCode) FROM ExportLog el)")
    List<ExportLog> findLatestExportLogs();

}
