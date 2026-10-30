import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class AdminRtdbTest {
    @Test(expected = IllegalStateException.class)
    public void missingEnvIsRejected() {
        AdminRtdb.credentialsFile(null);
    }

    @Test(expected = IllegalStateException.class)
    public void blankEnvIsRejected() {
        AdminRtdb.credentialsFile("  ");
    }

    @Test(expected = IllegalStateException.class)
    public void missingFileIsRejected() {
        AdminRtdb.credentialsFile("/tmp/does-not-exist-mtproto-sa.json");
    }

    @Test
    public void desktopPayloadKeepsNodeAFields() {
        MyClass.ProxyData proxy = new MyClass.ProxyData("203.0.113.10", "443", "ee");
        String geo = "{\"status\":\"success\",\"city\":\"Paris\",\"country\":\"France\","
                + "\"countryCode\":\"FR\",\"lat\":\"48.8\",\"lon\":\"2.3\"}";
        Map<String, Object> body = MyClass.mtprotoBody(proxy, geo, 1_700_000_000_000L);
        assertEquals("203.0.113.10", body.get("host"));
        assertEquals("443", body.get("port"));
        assertEquals("ee", body.get("secret"));
        assertEquals(Boolean.TRUE, body.get("enabled"));
        assertEquals(1_700_000_000_000L, body.get("update_at"));
        assertEquals("Paris", body.get("city"));
        assertEquals("France", body.get("country"));
        assertEquals("FR", body.get("code"));
        assertEquals(48.8d, (Double) body.get("lat"), 0.0001d);
        assertEquals(2.3d, (Double) body.get("lon"), 0.0001d);
        assertFalse(body.containsKey("region"));
        assertFalse(body.containsKey("rate"));
        assertNull(body.get("secret2"));
    }

    @Test
    public void emptyGeoNumbersBecomeZero() {
        MyClass.ProxyData proxy = new MyClass.ProxyData("h", "1", "s");
        Map<String, Object> body = MyClass.mtprotoBody(proxy, "{}", 5L);
        assertEquals(0d, (Double) body.get("lat"), 0d);
        assertEquals(0d, (Double) body.get("lon"), 0d);
        assertEquals("", body.get("city"));
    }
}
