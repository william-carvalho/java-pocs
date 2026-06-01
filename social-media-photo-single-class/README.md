# Social Media Photo Single Class

Java 8 POC for a social media photo app with publishing, tags, timeline, and comments.

The production code is intentionally in one class:

```text
src/main/java/com/example/socialphoto/SocialMediaPhotoApp.java
```

## Rules

- Users are required to publish photos and comment.
- Publishing accepts only `image/*` content types.
- Tags are normalized to lowercase and kept unique per photo.
- Timeline returns active photos newest first.
- Timeline can filter by user, tag, or both.
- Deleted photos are hidden from timeline.
- Deleted comments are hidden from photo comments and timeline items.

## Example

```java
SocialMediaPhotoApp app = SocialMediaPhotoApp.withDefaultUsers();

SocialMediaPhotoApp.Photo photo = app.publishPhoto(
        "william",
        "beach.jpg",
        "image/jpeg",
        imageBytes,
        "Beach day");

app.addTags(photo.getId(), "Travel", "Beach");
app.addComment(photo.getId(), "maria", "Great photo!");

List<SocialMediaPhotoApp.TimelineItem> timeline = app.timeline();
```

## Test

```bash
mvn test
```
