package com.example.fileshare;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileShareSystemTest {
    @Test
    void savesFileMetadataAndEncryptedContent() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        byte[] content = bytes("contract body");

        FileShareSystem.StoredFile file = system.saveFile("contract.txt", "text/plain", content);

        assertEquals(1L, file.getId());
        assertEquals("contract.txt", file.getOriginalFileName());
        assertEquals("text/plain", file.getContentType());
        assertEquals(content.length, file.getOriginalSize());
        assertTrue(file.isEncrypted());
        assertFalse(file.isDeleted());
        assertNotNull(file.getStorageName());
        assertNotEquals("contract body", new String(system.encryptedBytes(file.getId()), StandardCharsets.UTF_8));
    }

    @Test
    void restoresOriginalFileContentByDecryptingStoredBytes() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        byte[] content = bytes("hello encrypted file");
        FileShareSystem.StoredFile file = system.saveFile("hello.txt", "text/plain", content);

        byte[] restored = system.restoreFile(file.getId());

        assertArrayEquals(content, restored);
    }

    @Test
    void deletesFileLogicallyAndHidesItFromListAndSearch() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        FileShareSystem.StoredFile contract = system.saveFile("contract.pdf", "application/pdf", bytes("contract"));
        system.saveFile("invoice.pdf", "application/pdf", bytes("invoice"));

        system.deleteFile(contract.getId());

        assertTrue(contract.isDeleted());
        assertNotNull(contract.getDeletedAt());
        assertEquals(1, system.listFiles().size());
        assertEquals("invoice.pdf", system.listFiles().get(0).getOriginalFileName());
        assertEquals(0, system.search("contract").size());
    }

    @Test
    void refusesToRestoreDeletedFile() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        FileShareSystem.StoredFile file = system.saveFile("secret.txt", "text/plain", bytes("secret"));
        system.deleteFile(file.getId());

        assertThrows(IllegalArgumentException.class, () -> system.restoreFile(file.getId()));
    }

    @Test
    void listsActiveFilesInSaveOrder() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        system.saveFile("b.txt", "text/plain", bytes("b"));
        system.saveFile("a.txt", "text/plain", bytes("a"));

        List<FileShareSystem.StoredFile> files = system.listFiles();

        assertEquals(2, files.size());
        assertEquals("b.txt", files.get(0).getOriginalFileName());
        assertEquals("a.txt", files.get(1).getOriginalFileName());
    }

    @Test
    void searchesByFileNameCaseInsensitive() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        system.saveFile("Quarterly-Report.pdf", "application/pdf", bytes("report"));
        system.saveFile("photo.png", "image/png", bytes("png"));

        List<FileShareSystem.StoredFile> result = system.search("report");

        assertEquals(1, result.size());
        assertEquals("Quarterly-Report.pdf", result.get(0).getOriginalFileName());
    }

    @Test
    void searchesByContentType() {
        FileShareSystem system = new FileShareSystem("1234567890123456");
        system.saveFile("photo.png", "image/png", bytes("png"));
        system.saveFile("notes.txt", "text/plain", bytes("notes"));

        List<FileShareSystem.StoredFile> result = system.search("image");

        assertEquals(1, result.size());
        assertEquals("photo.png", result.get(0).getOriginalFileName());
    }

    @Test
    void rejectsEmptyContent() {
        FileShareSystem system = new FileShareSystem("1234567890123456");

        assertThrows(IllegalArgumentException.class, () ->
                system.saveFile("empty.txt", "text/plain", new byte[0]));
    }

    @Test
    void rejectsBlankEncryptionKey() {
        assertThrows(IllegalArgumentException.class, () -> new FileShareSystem(" "));
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
