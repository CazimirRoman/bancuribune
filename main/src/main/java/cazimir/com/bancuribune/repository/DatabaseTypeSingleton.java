package cazimir.com.bancuribune.repository;

/**
 * TODO: Add a class header comment!
 */
public class DatabaseTypeSingleton {
    private static final DatabaseTypeSingleton ourInstance = new DatabaseTypeSingleton();
    private boolean isDebug;

    public static DatabaseTypeSingleton getInstance() {
        return ourInstance;
    }

    private DatabaseTypeSingleton() {
    }

    public void setType(boolean debug) {
        isDebug = debug;
    }

    public boolean isDebug() {
        return isDebug;
    }
}
