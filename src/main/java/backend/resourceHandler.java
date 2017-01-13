package backend;

public class resourceHandler {
    private static final String mainDataFile = "sample.las";
    private static String mainDataFilePath;
    private static String extensionRemoved;
    private static String normalVectorsSerialized;

    static {
        try {
            mainDataFilePath = resourceHandler.class.getClassLoader().getResource(mainDataFile).getFile().toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        if (mainDataFilePath.indexOf(".") > 0)
            extensionRemoved = mainDataFilePath.substring(0, mainDataFilePath.lastIndexOf("."));

        normalVectorsSerialized = extensionRemoved + "_normalVectors.ser";
    }

    public static String getMainDataFilePath() {
        return mainDataFilePath;
    }

    public static String getNormalVectorsSerialized() {
        return normalVectorsSerialized;
    }
}
