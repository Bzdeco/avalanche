package backend;

public class ResourceHandler {
    private static final String mainDataFile = "sample.las";
    private static String mainDataFilePath;

    private static String extensionRemoved;

    private static String terrainDataFilePath,
            normalsDataFilePath,
            hillShadeDataFilePath,
            steepnessDataFilePath,
            dbDriver,
            dbUrl,
            dbUser,
            dbPass;

    static {
        try {
            mainDataFilePath = ResourceHandler.class.getClassLoader().getResource(mainDataFile).getFile();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        if (mainDataFilePath.indexOf(".") > 0)
            extensionRemoved = mainDataFilePath.substring(0, mainDataFilePath.lastIndexOf("."));

        terrainDataFilePath = extensionRemoved + "_terrain.ser";
        normalsDataFilePath = extensionRemoved + "_normals.ser";
        hillShadeDataFilePath = extensionRemoved + "_hillShade.ser";
        steepnessDataFilePath = extensionRemoved + "_steepness.ser";
        dbDriver = "org.postgresql.Driver";
        dbUrl = "jdbc:postgresql://localhost:5432/lawiny_test";
        dbUser = "lawiny";
        dbPass = "l1234";
    }

    public static String getMainDataFilePath() {
        return mainDataFilePath;
    }

    public static String getTerrainDataFilePath() {
        return terrainDataFilePath;
    }

    public static String getNormalsFilePath() {
        return normalsDataFilePath;
    }

    public static String getHillShadeDataFilePath() {
        return hillShadeDataFilePath;
    }

    public static String getSteepnessDataFilePath() {
        return steepnessDataFilePath;
    }

    public static String getDbDriver() { return dbDriver; }

    public static String getDbUrl() {return dbUrl;}

    public static String getDbUser() {return dbUser;}

    public static String getDbPass() {return dbPass;}
}
