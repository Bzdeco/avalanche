package backend;

public class resourceHandler {
    private static final String mainDataFile = "sample.las";
    private static String mainDataFilePath;

    private static String extensionRemoved;

    private static String terrainDataFilePath;
    private static String normalsDataFilePath;
    private static String hillShadeDataFilePath;
    private static String steepnessDataFilePath;

    static {
        try {
            mainDataFilePath = resourceHandler.class.getClassLoader().getResource(mainDataFile).getFile().toString();
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
}
