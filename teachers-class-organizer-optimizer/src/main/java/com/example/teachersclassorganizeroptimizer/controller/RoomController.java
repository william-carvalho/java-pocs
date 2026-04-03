package com.example.teachersclassorganizeroptimizer.controller;

import com.example.teachersclassorganizeroptimizer.dto.RoomRequest;
import com.example.teachersclassorganizeroptimizer.dto.RoomResponse;
import com.example.teachersclassorganizeroptimizer.service.RoomService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> list() {
        return ResponseEntity.ok(roomService.listAll());
    }
}

