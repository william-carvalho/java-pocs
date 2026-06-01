package com.example.socialphoto;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocialMediaPhotoAppTest {
    @Test
    void publishesPhotoForUser() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        byte[] content = bytes("fake image bytes");

        SocialMediaPhotoApp.Photo photo = app.publishPhoto(
                "william",
                "beach.jpg",
                "image/jpeg",
                content,
                "Beach day");

        assertEquals(1L, photo.getId());
        assertEquals("william", photo.getUser().getUsername());
        assertEquals("beach.jpg", photo.getOriginalFileName());
        assertEquals("image/jpeg", photo.getContentType());
        assertEquals("Beach day", photo.getCaption());
        assertArrayEquals(content, app.photoContent(photo.getId()));
        assertNotNull(photo.getStorageName());
    }

    @Test
    void rejectsNonImagePhotoPublish() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();

        assertThrows(IllegalArgumentException.class, () ->
                app.publishPhoto("william", "notes.txt", "text/plain", bytes("text"), "No image"));
    }

    @Test
    void addsNormalizedUniqueTagsToPhoto() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo photo = publish(app, "william", "one.jpg", "one");

        app.addTags(photo.getId(), "Travel", "beach", " travel ", "SUNSET");

        assertEquals(3, photo.getTags().size());
        assertTrue(photo.getTags().contains("travel"));
        assertTrue(photo.getTags().contains("beach"));
        assertTrue(photo.getTags().contains("sunset"));
    }

    @Test
    void addsCommentsToPhoto() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo photo = publish(app, "william", "one.jpg", "one");

        SocialMediaPhotoApp.Comment comment = app.addComment(photo.getId(), "maria", "Great photo!");

        assertEquals(photo.getId(), comment.getPhotoId());
        assertEquals("maria", comment.getUser().getUsername());
        assertEquals("Great photo!", comment.getText());
        assertEquals(1, photo.getCommentsCount());
        assertEquals(comment.getId(), photo.getComments().get(0).getId());
    }

    @Test
    void timelineReturnsNewestPhotosFirst() throws Exception {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo first = publish(app, "william", "old.jpg", "old");
        Thread.sleep(2L);
        SocialMediaPhotoApp.Photo second = publish(app, "maria", "new.jpg", "new");

        List<SocialMediaPhotoApp.TimelineItem> timeline = app.timeline();

        assertEquals(second.getId(), timeline.get(0).getPhotoId());
        assertEquals(first.getId(), timeline.get(1).getPhotoId());
    }

    @Test
    void timelineCanFilterByUser() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        publish(app, "william", "one.jpg", "one");
        publish(app, "maria", "two.jpg", "two");

        List<SocialMediaPhotoApp.TimelineItem> timeline = app.timelineByUser("william");

        assertEquals(1, timeline.size());
        assertEquals("william", timeline.get(0).getUser().getUsername());
    }

    @Test
    void timelineCanFilterByTag() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo beach = publish(app, "william", "beach.jpg", "beach");
        SocialMediaPhotoApp.Photo food = publish(app, "maria", "food.jpg", "food");
        app.addTags(beach.getId(), "Beach", "Travel");
        app.addTags(food.getId(), "Food");

        List<SocialMediaPhotoApp.TimelineItem> timeline = app.timelineByTag("travel");

        assertEquals(1, timeline.size());
        assertEquals(beach.getId(), timeline.get(0).getPhotoId());
        assertTrue(timeline.get(0).getTags().contains("travel"));
    }

    @Test
    void timelineIncludesActiveCommentsAndHidesDeletedComments() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo photo = publish(app, "william", "one.jpg", "one");
        SocialMediaPhotoApp.Comment visible = app.addComment(photo.getId(), "maria", "Nice");
        SocialMediaPhotoApp.Comment deleted = app.addComment(photo.getId(), "joao", "Remove me");

        app.deleteComment(photo.getId(), deleted.getId());

        List<SocialMediaPhotoApp.Comment> comments = app.timeline().get(0).getComments();
        assertEquals(1, comments.size());
        assertEquals(visible.getId(), comments.get(0).getId());
        assertFalse(comments.get(0).isDeleted());
    }

    @Test
    void deletedPhotosDoNotAppearInTimeline() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo photo = publish(app, "william", "one.jpg", "one");

        app.deletePhoto(photo.getId());

        assertEquals(0, app.timeline().size());
        assertThrows(IllegalArgumentException.class, () -> app.findPhoto(photo.getId()));
    }

    @Test
    void rejectsBlankComment() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo photo = publish(app, "william", "one.jpg", "one");

        assertThrows(IllegalArgumentException.class, () -> app.addComment(photo.getId(), "maria", " "));
    }

    @Test
    void rejectsDuplicateUsername() {
        SocialMediaPhotoApp app = new SocialMediaPhotoApp();
        app.createUser("William", "william");

        assertThrows(IllegalStateException.class, () -> app.createUser("Other", "WILLIAM"));
    }

    @Test
    void timelineSupportsCombinedUserAndTagFilter() {
        SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();
        SocialMediaPhotoApp.Photo williamBeach = publish(app, "william", "beach.jpg", "beach");
        SocialMediaPhotoApp.Photo mariaBeach = publish(app, "maria", "maria-beach.jpg", "beach");
        app.addTags(williamBeach.getId(), "travel");
        app.addTags(mariaBeach.getId(), "travel");

        List<SocialMediaPhotoApp.TimelineItem> timeline = app.timeline("william", "travel");

        assertEquals(1, timeline.size());
        assertEquals(williamBeach.getId(), timeline.get(0).getPhotoId());
    }

    private static SocialMediaPhotoApp.Photo publish(SocialMediaPhotoApp app, String username, String fileName, String caption) {
        return app.publishPhoto(username, fileName, "image/jpeg", bytes(fileName), caption);
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
