package com.example.socialphoto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SocialMediaPhotoApp {
    private final Map<String, User> usersByUsername = new LinkedHashMap<String, User>();
    private final Map<Long, Photo> photosById = new LinkedHashMap<Long, Photo>();
    private long nextUserId = 1;
    private long nextPhotoId = 1;
    private long nextCommentId = 1;

    public User createUser(String name, String username) {
        User user = new User(nextUserId++, name, username);
        if (usersByUsername.containsKey(user.getUsername())) {
            throw new IllegalStateException("Username already exists: " + user.getUsername());
        }
        usersByUsername.put(user.getUsername(), user);
        return user;
    }

    public Photo publishPhoto(String username,
                              String originalFileName,
                              String contentType,
                              byte[] content,
                              String caption) {
        User user = findUser(username);
        if (isBlank(originalFileName)) {
            throw new IllegalArgumentException("originalFileName is required");
        }
        if (isBlank(contentType) || !contentType.toLowerCase(Locale.ENGLISH).startsWith("image/")) {
            throw new IllegalArgumentException("Only image content is supported");
        }
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("photo content is required");
        }

        Photo photo = new Photo(
                nextPhotoId++,
                user,
                originalFileName.trim(),
                contentType.trim(),
                Arrays.copyOf(content, content.length),
                caption);
        photosById.put(photo.getId(), photo);
        return photo;
    }

    public Photo addTags(long photoId, String... tags) {
        Photo photo = findActivePhoto(photoId);
        if (tags == null) {
            return photo;
        }
        for (String tag : tags) {
            if (!isBlank(tag)) {
                photo.addTag(normalizeTag(tag));
            }
        }
        return photo;
    }

    public Comment addComment(long photoId, String username, String text) {
        Photo photo = findActivePhoto(photoId);
        User user = findUser(username);
        Comment comment = new Comment(nextCommentId++, photoId, user, text);
        photo.addComment(comment);
        return comment;
    }

    public void deletePhoto(long photoId) {
        findActivePhoto(photoId).delete();
    }

    public void deleteComment(long photoId, long commentId) {
        Photo photo = findActivePhoto(photoId);
        Comment comment = photo.findComment(commentId);
        comment.delete();
    }

    public byte[] photoContent(long photoId) {
        return findActivePhoto(photoId).getContent();
    }

    public List<TimelineItem> timeline() {
        return timeline(null, null);
    }

    public List<TimelineItem> timelineByUser(String username) {
        return timeline(username, null);
    }

    public List<TimelineItem> timelineByTag(String tag) {
        return timeline(null, tag);
    }

    public List<TimelineItem> timeline(String username, String tag) {
        String normalizedUsername = isBlank(username) ? null : username.trim().toLowerCase(Locale.ENGLISH);
        String normalizedTag = isBlank(tag) ? null : normalizeTag(tag);

        List<TimelineItem> items = new ArrayList<TimelineItem>();
        for (Photo photo : photosById.values()) {
            if (photo.isDeleted()) {
                continue;
            }
            if (normalizedUsername != null && !photo.getUser().getUsername().equals(normalizedUsername)) {
                continue;
            }
            if (normalizedTag != null && !photo.getTags().contains(normalizedTag)) {
                continue;
            }
            items.add(new TimelineItem(photo));
        }
        Collections.sort(items, new Comparator<TimelineItem>() {
            public int compare(TimelineItem first, TimelineItem second) {
                return second.getCreatedAt().compareTo(first.getCreatedAt());
            }
        });
        return Collections.unmodifiableList(items);
    }

    public Photo findPhoto(long photoId) {
        return findActivePhoto(photoId);
    }

    public List<User> users() {
        return Collections.unmodifiableList(new ArrayList<User>(usersByUsername.values()));
    }

    public static SocialMediaPhotoApp withDefaultUsers() {
        SocialMediaPhotoApp app = new SocialMediaPhotoApp();
        app.createUser("William Carvalho", "william");
        app.createUser("Maria Silva", "maria");
        app.createUser("Joao Souza", "joao");
        return app;
    }

    private Photo findActivePhoto(long photoId) {
        Photo photo = photosById.get(photoId);
        if (photo == null || photo.isDeleted()) {
            throw new IllegalArgumentException("Photo not found: " + photoId);
        }
        return photo;
    }

    private User findUser(String username) {
        if (isBlank(username)) {
            throw new IllegalArgumentException("username is required");
        }
        User user = usersByUsername.get(username.trim().toLowerCase(Locale.ENGLISH));
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }

    private static String normalizeTag(String tag) {
        return tag.trim().toLowerCase(Locale.ENGLISH);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static final class User {
        private final long id;
        private final String name;
        private final String username;

        private User(long id, String name, String username) {
            if (isBlank(name)) {
                throw new IllegalArgumentException("name is required");
            }
            if (isBlank(username)) {
                throw new IllegalArgumentException("username is required");
            }
            this.id = id;
            this.name = name.trim();
            this.username = username.trim().toLowerCase(Locale.ENGLISH);
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }
    }

    public static final class Photo {
        private final long id;
        private final User user;
        private final String caption;
        private final String originalFileName;
        private final String contentType;
        private final String storageName;
        private final byte[] content;
        private final LocalDateTime createdAt;
        private final Set<String> tags = new LinkedHashSet<String>();
        private final List<Comment> comments = new ArrayList<Comment>();
        private boolean deleted;

        private Photo(long id, User user, String originalFileName, String contentType, byte[] content, String caption) {
            this.id = id;
            this.user = user;
            this.originalFileName = originalFileName;
            this.contentType = contentType;
            this.content = Arrays.copyOf(content, content.length);
            this.caption = caption == null ? "" : caption.trim();
            this.storageName = UUID.randomUUID().toString();
            this.createdAt = LocalDateTime.now();
        }

        private void addTag(String tag) {
            tags.add(tag);
        }

        private void addComment(Comment comment) {
            comments.add(comment);
        }

        private Comment findComment(long commentId) {
            for (Comment comment : comments) {
                if (comment.getId() == commentId) {
                    return comment;
                }
            }
            throw new IllegalArgumentException("Comment not found: " + commentId);
        }

        private void delete() {
            deleted = true;
        }

        public long getId() {
            return id;
        }

        public User getUser() {
            return user;
        }

        public String getCaption() {
            return caption;
        }

        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getContentType() {
            return contentType;
        }

        public String getStorageName() {
            return storageName;
        }

        public byte[] getContent() {
            return Arrays.copyOf(content, content.length);
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public Set<String> getTags() {
            return Collections.unmodifiableSet(tags);
        }

        public List<Comment> getComments() {
            List<Comment> active = new ArrayList<Comment>();
            for (Comment comment : comments) {
                if (!comment.isDeleted()) {
                    active.add(comment);
                }
            }
            return Collections.unmodifiableList(active);
        }

        public int getCommentsCount() {
            return getComments().size();
        }

        public boolean isDeleted() {
            return deleted;
        }
    }

    public static final class Comment {
        private final long id;
        private final long photoId;
        private final User user;
        private final String text;
        private final LocalDateTime createdAt;
        private boolean deleted;

        private Comment(long id, long photoId, User user, String text) {
            if (isBlank(text)) {
                throw new IllegalArgumentException("comment text is required");
            }
            this.id = id;
            this.photoId = photoId;
            this.user = user;
            this.text = text.trim();
            this.createdAt = LocalDateTime.now();
        }

        private void delete() {
            deleted = true;
        }

        public long getId() {
            return id;
        }

        public long getPhotoId() {
            return photoId;
        }

        public User getUser() {
            return user;
        }

        public String getText() {
            return text;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public boolean isDeleted() {
            return deleted;
        }
    }

    public static final class TimelineItem {
        private final long photoId;
        private final User user;
        private final String caption;
        private final LocalDateTime createdAt;
        private final Set<String> tags;
        private final List<Comment> comments;

        private TimelineItem(Photo photo) {
            this.photoId = photo.getId();
            this.user = photo.getUser();
            this.caption = photo.getCaption();
            this.createdAt = photo.getCreatedAt();
            this.tags = Collections.unmodifiableSet(new LinkedHashSet<String>(photo.getTags()));
            this.comments = photo.getComments();
        }

        public long getPhotoId() {
            return photoId;
        }

        public User getUser() {
            return user;
        }

        public String getCaption() {
            return caption;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public Set<String> getTags() {
            return tags;
        }

        public List<Comment> getComments() {
            return comments;
        }
    }
}
