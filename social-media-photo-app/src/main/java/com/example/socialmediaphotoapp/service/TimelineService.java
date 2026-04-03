package com.example.socialmediaphotoapp.service;

import com.example.socialmediaphotoapp.dto.TimelineItemResponse;
import com.example.socialmediaphotoapp.dto.TimelineResponse;
import com.example.socialmediaphotoapp.entity.PhotoPost;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TimelineService {

    private final PhotoService photoService;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;

    public TimelineService(PhotoService photoService,
                           TagService tagService,
                           CommentService commentService,
                           UserService userService) {
        this.photoService = photoService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public TimelineResponse timeline(Long userId, String tag, int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("page must be >= 0 and size must be > 0");
        }

        List<PhotoPost> all = photoService.listForTimeline(userId, tag);
        int fromIndex = Math.min(page * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());
        List<TimelineItemResponse> items = all.subList(fromIndex, toIndex).stream()
                .map(this::toTimelineItem)
                .collect(Collectors.toList());

        TimelineResponse response = new TimelineResponse();
        response.setCount(items.size());
        response.setItems(items);
        return response;
    }

    private TimelineItemResponse toTimelineItem(PhotoPost photoPost) {
        TimelineItemResponse item = new TimelineItemResponse();
        item.setPhotoId(photoPost.getId());
        item.setUser(userService.toResponse(photoPost.getUser()));
        item.setCaption(photoPost.getCaption());
        item.setCreatedAt(photoPost.getCreatedAt());
        item.setTags(tagService.toNames(photoPost.getTags()));
        item.setComments(commentService.listByPhoto(photoPost.getId()));
        return item;
    }
}

