import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * RTDB writes for the desktop loader via the Firebase Admin SDK.
 * The service-account JSON path comes from GOOGLE_APPLICATION_CREDENTIALS.
 */
public final class AdminRtdb {
    static final String DATABASE_URL = "https://mtprotolist.firebaseio.com";
    private static final Object LOCK = new Object();
    private static boolean ready;

    private AdminRtdb() {
    }

    public static void setChild(String path, String key, Map<String, Object> value) throws Exception {
        ensureInitialized();
        FirebaseDatabase.getInstance()
                .getReference(path)
                .child(key)
                .setValueAsync(value)
                .get(60, TimeUnit.SECONDS);
    }

    static File credentialsFile(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS is not set");
        }
        File file = new File(path.trim());
        if (!file.isFile()) {
            throw new IllegalStateException("GOOGLE_APPLICATION_CREDENTIALS is not a file");
        }
        return file;
    }

    private static void ensureInitialized() throws IOException {
        synchronized (LOCK) {
            if (ready) {
                return;
            }
            File file = credentialsFile(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            try (InputStream in = new FileInputStream(file)) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .setDatabaseUrl(DATABASE_URL)
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }
            ready = true;
        }
    }
}
