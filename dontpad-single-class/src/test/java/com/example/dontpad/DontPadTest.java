package com.example.dontpad;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DontPadTest {
    @Test
    void createsPadWithSlugAndContent() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 13, 10, 0));
        DontPad dontPad = new DontPad(clock);

        DontPad.Pad pad = dontPad.createPad("notes_123", "Hello");

        assertEquals("notes_123", pad.getSlug());
        assertEquals("Hello", pad.getContent());
        assertEquals(1, pad.getVersion());
        assertEquals(clock.now(), pad.getCreatedAt());
        assertEquals(clock.now(), pad.getUpdatedAt());
        assertTrue(dontPad.exists("notes_123"));
    }

    @Test
    void getOrCreateCreatesEmptyPadWhenSlugDoesNotExist() {
        DontPad dontPad = new DontPad();

        DontPad.Pad pad = dontPad.getOrCreate("shared-pad");

        assertEquals("shared-pad", pad.getSlug());
        assertEquals("", pad.getContent());
        assertEquals(1, dontPad.count());
    }

    @Test
    void getOrCreateReturnsExistingSharedPad() {
        DontPad dontPad = new DontPad();
        DontPad.Pad created = dontPad.createPad("team", "First");

        DontPad.Pad loaded = dontPad.getOrCreate("team");

        assertEquals(created, loaded);
        assertEquals("First", loaded.getContent());
        assertEquals(1, dontPad.count());
    }

    @Test
    void updateCreatesOrUpdatesPadBySlug() {
        MutableClock clock = new MutableClock(LocalDateTime.of(2026, 6, 13, 10, 0));
        DontPad dontPad = new DontPad(clock);

        DontPad.Pad createdByUpdate = dontPad.updatePad("draft", "Initial");
        clock.advanceMinutes(5);
        DontPad.Pad updated = dontPad.updatePad("draft", "Updated");

        assertEquals(createdByUpdate, updated);
        assertEquals("Updated", updated.getContent());
        assertEquals(3, updated.getVersion());
        assertEquals(LocalDateTime.of(2026, 6, 13, 10, 5), updated.getUpdatedAt());
    }

    @Test
    void updateAcceptsNullContentAsEmptyText() {
        DontPad dontPad = new DontPad();

        DontPad.Pad pad = dontPad.updatePad("empty", null);

        assertEquals("", pad.getContent());
    }

    @Test
    void deleteRemovesPad() {
        DontPad dontPad = new DontPad();
        dontPad.createPad("old", "Remove me");

        DontPad.Pad removed = dontPad.deletePad("old");

        assertEquals("old", removed.getSlug());
        assertFalse(dontPad.exists("old"));
        assertEquals(0, dontPad.count());
    }

    @Test
    void listPadsReturnsSummariesInCreationOrder() {
        DontPad dontPad = new DontPad();
        dontPad.createPad("b", "two");
        dontPad.createPad("a", "one");
        dontPad.updatePad("a", "three");

        List<DontPad.PadSummary> pads = dontPad.listPads();

        assertEquals(2, pads.size());
        assertEquals("b", pads.get(0).getSlug());
        assertEquals(3, pads.get(0).getContentLength());
        assertEquals(1, pads.get(0).getVersion());
        assertEquals("a", pads.get(1).getSlug());
        assertEquals(5, pads.get(1).getContentLength());
        assertEquals(2, pads.get(1).getVersion());
    }

    @Test
    void rejectsInvalidSlugs() {
        DontPad dontPad = new DontPad();

        assertThrows(IllegalArgumentException.class, () -> dontPad.createPad("bad slug", "x"));
        assertThrows(IllegalArgumentException.class, () -> dontPad.createPad("bad/slash", "x"));
        assertThrows(IllegalArgumentException.class, () -> dontPad.getOrCreate(" "));
    }

    @Test
    void rejectsDuplicateExplicitCreate() {
        DontPad dontPad = new DontPad();
        dontPad.createPad("same", "one");

        assertThrows(IllegalStateException.class, () -> dontPad.createPad("same", "two"));
    }

    @Test
    void deleteUnknownPadFailsClearly() {
        DontPad dontPad = new DontPad();

        assertThrows(IllegalArgumentException.class, () -> dontPad.deletePad("missing"));
    }

    @Test
    void createsWelcomePad() {
        DontPad dontPad = DontPad.withWelcomePad();

        assertEquals(1, dontPad.count());
        assertEquals("Welcome to DontPad Clone", dontPad.getOrCreate("welcome").getContent());
    }

    private static final class MutableClock implements DontPad.TimeSource {
        private LocalDateTime now;

        private MutableClock(LocalDateTime now) {
            this.now = now;
        }

        public LocalDateTime now() {
            return now;
        }

        private void advanceMinutes(long minutes) {
            now = now.plusMinutes(minutes);
        }
    }
}
