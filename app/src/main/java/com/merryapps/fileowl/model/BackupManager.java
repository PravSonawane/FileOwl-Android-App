package com.merryapps.fileowl.model;

import android.support.annotation.NonNull;

import com.merryapps.fileowl.model.db.FrequentFileEntity;
import com.merryapps.fileowl.model.db.FrequentFileEntityDao;
import com.merryapps.fileowl.model.db.LargeFileEntity;
import com.merryapps.fileowl.model.db.LargeFileEntityDao;
import com.merryapps.fileowl.model.db.ScanStatEntity;
import com.merryapps.fileowl.model.db.ScanStatEntityDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.merryapps.framework.EntityState.LOCAL;

/**
 * Saves, deletes and fetches backup information from database.
 *
 * @author Pravin Sonawane (june.pravin@gmail.com)
 * @since v1.0.0
 */
public class BackupManager {

    private final LargeFileEntityDao largeFileDao;
    private final FrequentFileEntityDao frequentFileDao;
    private final ScanStatEntityDao scanStatDao;

    BackupManager(LargeFileEntityDao largeFileDao, FrequentFileEntityDao frequentFileDao, ScanStatEntityDao scanStatDao) {
        this.largeFileDao = largeFileDao;
        this.frequentFileDao = frequentFileDao;
        this.scanStatDao = scanStatDao;
    }

    void loadSeedData() {
        this.largeFileDao.insert(new LargeFileEntity(1L, "/sdcard/0/large_file_path.avi", 1024L * 1024L * 1024L * 1024L, LOCAL));
        this.frequentFileDao.insert(new FrequentFileEntity(1L, "avi", 1L, LOCAL));
        this.scanStatDao.insert(new ScanStatEntity(1L, 1, 1024L * 1024L * 1024L * 1024L, System.currentTimeMillis(), LOCAL));
    }

    void saveFileStat(List<FileStat> fileStats) {
        if (fileStats == null || fileStats.isEmpty()) {
            return;
        }

        List<LargeFileEntity> entities = new ArrayList<>();
        for (FileStat f : fileStats) {
            entities.add(new LargeFileEntity(f.getAbsolutePath(),f.getSize()));
        }
        largeFileDao.insertInTx(entities);
    }

    void saveFrequentFiles(List<FileTypeFrequency> fileTypeFrequencies) {
        if (fileTypeFrequencies == null || fileTypeFrequencies.isEmpty()) {
            return;
        }

        List<FrequentFileEntity> entities = new ArrayList<>();
        for (FileTypeFrequency f : fileTypeFrequencies) {
            entities.add(new FrequentFileEntity(f.getFileType(), f.getFrequency()));
        }
        frequentFileDao.insertInTx(entities);
    }

    void saveScanStat(long totalFilesScanned, long averageFileSize, long scanStartTime) {
        if (scanStartTime < 0 || totalFilesScanned < 0
                || averageFileSize < 0) {
            throw new IllegalArgumentException("arguments cannot be < 0");
        }

        scanStatDao.insert(new ScanStatEntity(1L, totalFilesScanned, averageFileSize, scanStartTime));
    }

    public void save(ScanResult scanResult) {
        deleteLastRecords();
        saveFileStat(scanResult.getFileStats());
        saveFrequentFiles(scanResult.getMostFrequentFileTypes());
        saveScanStat(scanResult.getTotalFilesScanned(),
                scanResult.getAverageFileSize(),
                new Date().getTime());
    }

    @NonNull
    public ScanResult getLastScanResult() {
        List<LargeFileEntity> largeFileEntities = largeFileDao.loadAll();
        List<FrequentFileEntity> frequentFileEntities = frequentFileDao.loadAll();
        ScanStatEntity scanStatEntity = scanStatDao.load(1L);
        return new ScanResult(
                scanStatEntity == null ? 0 : scanStatEntity.getTotalFilesScanned(),
                scanStatEntity == null ? 0 : scanStatEntity.getAverageFileSize(),
                convertToFileStatList(largeFileEntities),
                convertToFileTypeFrequencyList(frequentFileEntities));
    }

    @NonNull
    private List<FileTypeFrequency> convertToFileTypeFrequencyList(List<FrequentFileEntity> frequentFileEntities) {
        List<FileTypeFrequency> fileTypeFrequencies = new ArrayList<>();
        for (FrequentFileEntity entity : frequentFileEntities) {
            fileTypeFrequencies.add(new FileTypeFrequency(entity.getFileType(), entity.getFileFrequency()));
        }
        return fileTypeFrequencies;
    }

    @NonNull
    private List<FileStat> convertToFileStatList(List<LargeFileEntity> largeFileEntities) {
        List<FileStat> fileStats = new ArrayList<>();
        for (LargeFileEntity entity : largeFileEntities) {
            fileStats.add(new FileStat(entity.getFilePath(), entity.getFileSize()));
        }
        return fileStats;
    }

    void deleteLastRecords() {
        largeFileDao.deleteAll();
        frequentFileDao.deleteAll();
        scanStatDao.deleteAll();
    }
}
