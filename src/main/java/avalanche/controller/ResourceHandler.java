package avalanche.controller;

/**
 * Class used for storing database connection parameters
 */
public class ResourceHandler {

    private static String dbDriver, dbUrl, dbUser, dbPass;

    static {
        dbDriver = "org.postgresql.Driver";
        dbUrl = "jdbc:postgresql://127.0.0.1:5432/lawiny_test";
        dbUser = "lawiny";
        dbPass = "l1234";
    }


    public static String getDbDriver() {
        return dbDriver;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPass() {
        return dbPass;
    }
}
