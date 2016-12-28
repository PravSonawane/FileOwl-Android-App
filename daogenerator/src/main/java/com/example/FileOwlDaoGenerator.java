package com.example;



import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates Daos and Dto objects. Also generates supporting classes for database interaction
 */
public class FileOwlDaoGenerator {

    private static final int DATABASE_VERSION = 1;

    //the package where the entity itself will be generated
    private static final String ENTITY_PACKAGE_NAME = "com.merryapps.fileowl.model.db";
    //the package where the dao will be generated
    private static final String DAO_PACKAGE_NAME = "com.merryapps.fileowl.model.db";
    //the package where the test classes will be generated
    private static final String DAO_TEST_PACKAGE_NAME = "com.merryapps.fileowl.model.db";

    private static final String ENTITY_STATE_FQDN = "com.merryapps.framework.EntityState";
    private static final String ENTITY_STATE_CONVERTER_FQDN = "com.merryapps.framework.EntityStateConverter";
    private static final String ENTITY_INTERFACE_FQDN = "com.merryapps.framework.Entity";

    //table names
    private static final String LARGE_FILE = "LARGE_FILE";
    private static final String FREQUENT_FILE = "FREQUENT_FILE";
    private static final String SCAN_STAT = "SCAN_STAT";

    public static void main(String... args) throws Exception {

        Schema dbSchema = new Schema(DATABASE_VERSION, ENTITY_PACKAGE_NAME);

        //setting the package where the DAO code will be generated
        dbSchema.setDefaultJavaPackageDao(DAO_PACKAGE_NAME);

        dbSchema.setDefaultJavaPackageTest(DAO_TEST_PACKAGE_NAME);

        //enabling keep sections
        dbSchema.enableKeepSectionsByDefault();

        //LargeFileEntity
        Entity largeFileEntity = dbSchema.addEntity("LargeFileEntity");
        describeLargeFileEntityTable(largeFileEntity);

        //FrequentFileEntity
        Entity frequentFileEntity = dbSchema.addEntity("FrequentFileEntity");
        describeFrequentFileEntityTable(frequentFileEntity);

        //ScanStatEntity
        Entity scanStatEntity = dbSchema.addEntity("ScanStatEntity");
        describeScanStatEntityTable(scanStatEntity);

        new DaoGenerator().generateAll(dbSchema, "app/src/main/java");

    }

    private static void describeLargeFileEntityTable(Entity entity) {
        entity.addIdProperty();
        entity.setTableName(LARGE_FILE);
        entity.implementsInterface(ENTITY_INTERFACE_FQDN);
        entity.addStringProperty("filePath").unique().notNull();
        entity.addLongProperty("fileSize").notNull();
        entity.addStringProperty("entityState")
                .customType(ENTITY_STATE_FQDN, ENTITY_STATE_CONVERTER_FQDN).notNull();
    }

    private static void describeFrequentFileEntityTable(Entity entity) {
        entity.addIdProperty();
        entity.setTableName(FREQUENT_FILE);
        entity.implementsInterface(ENTITY_INTERFACE_FQDN);
        entity.addStringProperty("fileType").unique().notNull();
        entity.addLongProperty("fileFrequency").notNull();
        entity.addStringProperty("entityState")
                .customType(ENTITY_STATE_FQDN, ENTITY_STATE_CONVERTER_FQDN).notNull();
    }

    private static void describeScanStatEntityTable(Entity entity) {

        final String SCAN_STATUS_CONVERTER_FQDN = DAO_PACKAGE_NAME + ".ScanStatusConverter";
        final String SCAN_FQDN = "com.merryapps.fileowl.model.ScanStatus";

        entity.addIdProperty();
        entity.setTableName(SCAN_STAT);
        entity.implementsInterface(ENTITY_INTERFACE_FQDN);
        entity.addLongProperty("totalFilesScanned").unique().notNull();
        entity.addLongProperty("averageFileSize").unique().notNull();
        entity.addLongProperty("scanTime").unique().notNull();
        entity.addStringProperty("scanStatus").unique()
            .customType(SCAN_FQDN, SCAN_STATUS_CONVERTER_FQDN).notNull();
        entity.addStringProperty("entityState")
                .customType(ENTITY_STATE_FQDN, ENTITY_STATE_CONVERTER_FQDN).notNull();
    }
}
