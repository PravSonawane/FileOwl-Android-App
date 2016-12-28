package com.merryapps.fileowl.model;

import android.support.annotation.NonNull;

import com.merryapps.fileowl.model.db.FrequentFileEntity;
import com.merryapps.fileowl.model.db.FrequentFileEntityDao;
import com.merryapps.fileowl.model.db.LargeFileEntity;
import com.merryapps.fileowl.model.db.LargeFileEntityDao;
import com.merryapps.fileowl.model.db.ScanStatEntity;
import com.merryapps.fileowl.model.db.ScanStatEntityDao;

import java.util.ArrayList;
import java.util.List;

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

    void saveScanStat(long totalFilesScanned, long averageFileSize, long scanStartTime, ScanStatus status) {
        if (scanStartTime < 0 || totalFilesScanned < 0
                || averageFileSize < 0) {
            throw new IllegalArgumentException("arguments cannot be < 0");
        }

        scanStatDao.insert(new ScanStatEntity(1L, totalFilesScanned, averageFileSize, scanStartTime,status));
    }

    public void save(Result result) {
        deleteLastRecords();
        saveFileStat(result.getLargestFiles());
        saveFrequentFiles(result.getFrequentFiles());
        saveScanStat(result.getTotalFilesScanned(),
                result.getAverageFileSize(),
                result.getScanTime(),
                result.getStatus()
                );
    }

    @NonNull
    public Result getLastScanResult() {
        List<LargeFileEntity> largeFileEntities = largeFileDao.loadAll();
        List<FrequentFileEntity> frequentFileEntities = frequentFileDao.loadAll();
        ScanStatEntity scanStatEntity = scanStatDao.load(1L);
        Result result = new Result();
        result.setTotalFilesScanned(scanStatEntity.getTotalFilesScanned());
        result.setAverageFileSize(scanStatEntity.getAverageFileSize());
        List<FileStat> fileStats = new ArrayList<>(largeFileEntities.size());
        for (LargeFileEntity e : largeFileEntities) {
            fileStats.add(new FileStat(e.getFilePath(), e.getFileSize()));
        }

        List<FileTypeFrequency> ftfs = new ArrayList<>(frequentFileEntities.size());
        for (FrequentFileEntity e : frequentFileEntities) {
            ftfs.add(new FileTypeFrequency(e.getFileType(), e.getFileFrequency()));
        }
        result.setScanTime(scanStatEntity.getScanTime());
        result.setStatus(scanStatEntity.getScanStatus());
        result.setLargestFiles(fileStats);
        result.setFrequentFiles(ftfs);
        return result;
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
