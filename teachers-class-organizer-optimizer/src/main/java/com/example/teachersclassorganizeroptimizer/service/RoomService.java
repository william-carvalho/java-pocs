package com.example.teachersclassorganizeroptimizer.service;

import com.example.teachersclassorganizeroptimizer.dto.RoomRequest;
import com.example.teachersclassorganizeroptimizer.dto.RoomResponse;
import com.example.teachersclassorganizeroptimizer.entity.Room;
import com.example.teachersclassorganizeroptimizer.exception.ResourceNotFoundException;
import com.example.teachersclassorganizeroptimizer.repository.RoomRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public RoomResponse create(RoomRequest request) {
        Room room = new Room();
        room.setName(request.getName().trim());
        room.setCapacity(request.getCapacity());
        return toResponse(roomRepository.save(room));
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> listAll() {
        return roomRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Room findEntity(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<Room> findCandidateRooms(long minimumCapacity) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getCapacity() >= minimumCapacity)
                .sorted(Comparator.comparing(Room::getCapacity).thenComparing(Room::getName))
                .collect(Collectors.toList());
    }

    public RoomResponse toResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        response.setName(room.getName());
        response.setCapacity(room.getCapacity());
        return response;
    }
}
