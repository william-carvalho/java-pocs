package com.example.singleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationSettingsTest {
    @BeforeEach
    void resetSingletonState() {
        ApplicationSettings.getInstance().resetForTests();
    }

    @Test
    void returnsSameInstanceEveryTime() {
        ApplicationSettings first = ApplicationSettings.getInstance();
        ApplicationSettings second = ApplicationSettings.getInstance();

        assertSame(first, second);
    }

    @Test
    void sharesStateAcrossReferences() {
        ApplicationSettings first = ApplicationSettings.getInstance();
        ApplicationSettings second = ApplicationSettings.getInstance();

        first.set("feature.login", "enabled");

        assertEquals("enabled", second.get("feature.login"));
        assertTrue(second.contains("feature.login"));
    }

    @Test
    void startsWithDefaultSettings() {
        ApplicationSettings settings = ApplicationSettings.getInstance();

        assertEquals("singleton-single-class", settings.get("application.name"));
        assertEquals("local", settings.get("environment"));
    }

    @Test
    void updatesAndRemovesSettings() {
        ApplicationSettings settings = ApplicationSettings.getInstance();

        settings.set("theme", "dark");

        assertEquals("dark", settings.get("theme"));
        assertEquals("dark", settings.remove("theme"));
        assertNull(settings.get("theme"));
    }

    @Test
    void snapshotIsReadOnlyAndDefensive() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.set("timezone", "UTC");

        Map<String, String> snapshot = settings.snapshot();

        assertEquals("UTC", snapshot.get("timezone"));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.put("timezone", "BRT"));
        assertEquals("UTC", settings.get("timezone"));
    }

    @Test
    void validatesKeysAndValues() {
        ApplicationSettings settings = ApplicationSettings.getInstance();

        assertThrows(IllegalArgumentException.class, () -> settings.set(null, "x"));
        assertThrows(IllegalArgumentException.class, () -> settings.set("", "x"));
        assertThrows(IllegalArgumentException.class, () -> settings.set("   ", "x"));
        assertThrows(IllegalArgumentException.class, () -> settings.set("x", null));
        assertThrows(IllegalArgumentException.class, () -> settings.get(null));
        assertThrows(IllegalArgumentException.class, () -> settings.contains(""));
        assertThrows(IllegalArgumentException.class, () -> settings.remove(" "));
    }

    @Test
    void constructorIsPrivate() throws Exception {
        Constructor<ApplicationSettings> constructor = ApplicationSettings.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    @Test
    void sameInstanceIsReturnedAcrossThreads() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            Callable<ApplicationSettings> task = new Callable<ApplicationSettings>() {
                public ApplicationSettings call() {
                    return ApplicationSettings.getInstance();
                }
            };

            Future<ApplicationSettings> first = executor.submit(task);
            Future<ApplicationSettings> second = executor.submit(task);
            Future<ApplicationSettings> third = executor.submit(task);

            ApplicationSettings singleton = ApplicationSettings.getInstance();

            assertSame(singleton, first.get());
            assertSame(singleton, second.get());
            assertSame(singleton, third.get());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void synchronizedWritesAreVisibleAcrossThreads() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            Future<String> result = executor.submit(new Callable<String>() {
                public String call() {
                    ApplicationSettings settings = ApplicationSettings.getInstance();
                    settings.set("worker", "done");
                    return settings.get("worker");
                }
            });

            assertEquals("done", result.get());
            assertEquals("done", ApplicationSettings.getInstance().get("worker"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void snapshotContainsCurrentSettings() {
        ApplicationSettings settings = ApplicationSettings.getInstance();
        settings.set("region", "us-east");

        Map<String, String> snapshot = settings.snapshot();

        assertNotNull(snapshot);
        assertEquals(3, snapshot.size());
        assertEquals("singleton-single-class", snapshot.get("application.name"));
        assertEquals("local", snapshot.get("environment"));
        assertEquals("us-east", snapshot.get("region"));
    }
}
