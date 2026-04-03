package com.example.calendarsystem.service;

import com.example.calendarsystem.dto.CreateMeetingRequest;
import com.example.calendarsystem.dto.MeetingResponse;
import com.example.calendarsystem.dto.PersonResponse;
import com.example.calendarsystem.entity.Meeting;
import com.example.calendarsystem.entity.MeetingStatus;
import com.example.calendarsystem.entity.Person;
import com.example.calendarsystem.exception.ConflictException;
import com.example.calendarsystem.exception.ResourceNotFoundException;
import com.example.calendarsystem.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final PersonService personService;

    public MeetingService(MeetingRepository meetingRepository, PersonService personService) {
        this.meetingRepository = meetingRepository;
        this.personService = personService;
    }

    @Transactional
    public MeetingResponse create(CreateMeetingRequest request) {
        validateMeetingWindow(request.getStartDateTime(), request.getEndDateTime());

        Person organizer = personService.findEntityById(request.getOrganizerId());
        Set<Person> participants = resolveParticipants(request.getParticipantIds(), organizer.getId());
        List<Long> peopleToValidate = buildPeopleIdsForConflictValidation(organizer.getId(), participants);
        validateNoConflict(peopleToValidate, request.getStartDateTime(), request.getEndDateTime());

        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle().trim());
        meeting.setDescription(request.getDescription());
        meeting.setOrganizer(organizer);
        meeting.setParticipants(participants);
        meeting.setStartDateTime(request.getStartDateTime());
        meeting.setEndDateTime(request.getEndDateTime());
        meeting.setStatus(MeetingStatus.SCHEDULED);

        return toResponse(meetingRepository.save(meeting));
    }

    @Transactional(readOnly = true)
    public List<MeetingResponse> list(Long personId, MeetingStatus status, LocalDate date) {
        List<Meeting> meetings;

        if (personId != null && status != null) {
            meetings = meetingRepository.findByPersonIdAndStatus(personId, status);
        } else if (personId != null) {
            meetings = meetingRepository.findByPersonId(personId);
        } else if (status != null && date != null) {
            meetings = filterByDate(meetingRepository.findByStatusAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                    status,
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            ), date);
        } else if (status != null) {
            meetings = meetingRepository.findByStatusOrderByStartDateTimeAsc(status);
        } else if (date != null) {
            meetings = filterByDate(meetingRepository.findByStartDateTimeBetweenOrderByStartDateTimeAsc(
                    date.atStartOfDay(),
                    date.plusDays(1).atStartOfDay()
            ), date);
        } else {
            meetings = meetingRepository.findAllByOrderByStartDateTimeAsc();
        }

        return meetings.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MeetingResponse findById(Long id) {
        return toResponse(findEntityById(id));
    }

    @Transactional
    public void cancel(Long id) {
        Meeting meeting = findEntityById(id);
        if (meeting.getStatus() != MeetingStatus.CANCELLED) {
            meeting.setStatus(MeetingStatus.CANCELLED);
            meetingRepository.save(meeting);
        }
    }

    @Transactional(readOnly = true)
    public Meeting findEntityById(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<Meeting> findScheduledConflicts(List<Long> personIds, LocalDateTime searchStart, LocalDateTime searchEnd) {
        return meetingRepository.findConflictingMeetings(personIds, MeetingStatus.SCHEDULED, searchStart, searchEnd);
    }

    private void validateMeetingWindow(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            throw new IllegalArgumentException("startDateTime and endDateTime are required");
        }
        if (!endDateTime.isAfter(startDateTime)) {
            throw new IllegalArgumentException("endDateTime must be greater than startDateTime");
        }
    }

    private Set<Person> resolveParticipants(List<Long> participantIds, Long organizerId) {
        Set<Long> uniqueIds = new LinkedHashSet<>(participantIds);
        if (uniqueIds.contains(organizerId)) {
            throw new IllegalArgumentException("organizer cannot be duplicated in participantIds");
        }

        Set<Person> participants = new LinkedHashSet<>();
        for (Long participantId : uniqueIds) {
            participants.add(personService.findEntityById(participantId));
        }
        return participants;
    }

    private List<Long> buildPeopleIdsForConflictValidation(Long organizerId, Set<Person> participants) {
        List<Long> ids = new ArrayList<>();
        ids.add(organizerId);
        for (Person participant : participants) {
            ids.add(participant.getId());
        }
        return ids;
    }

    private void validateNoConflict(List<Long> personIds, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Meeting> conflicts = findScheduledConflicts(personIds, startDateTime, endDateTime);
        if (!conflicts.isEmpty()) {
            throw new ConflictException("One or more people already have a meeting in the selected time range");
        }
    }

    private List<Meeting> filterByDate(List<Meeting> meetings, LocalDate date) {
        return meetings.stream()
                .filter(meeting -> meeting.getStartDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public MeetingResponse toResponse(Meeting meeting) {
        MeetingResponse response = new MeetingResponse();
        response.setId(meeting.getId());
        response.setTitle(meeting.getTitle());
        response.setDescription(meeting.getDescription());
        response.setOrganizer(personService.toResponse(meeting.getOrganizer()));
        response.setParticipants(meeting.getParticipants()
                .stream()
                .map(personService::toResponse)
                .collect(Collectors.toList()));
        response.setStartDateTime(meeting.getStartDateTime());
        response.setEndDateTime(meeting.getEndDateTime());
        response.setStatus(meeting.getStatus());
        response.setCreatedAt(meeting.getCreatedAt());
        return response;
    }
}

