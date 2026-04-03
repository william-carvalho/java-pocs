package com.example.socialmediaphotoapp.dto;

import java.util.List;

public class TimelineResponse {

    private Integer count;
    private List<TimelineItemResponse> items;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<TimelineItemResponse> getItems() {
        return items;
    }

    public void setItems(List<TimelineItemResponse> items) {
        this.items = items;
    }
}

