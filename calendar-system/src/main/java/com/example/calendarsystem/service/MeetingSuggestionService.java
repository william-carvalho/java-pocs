package com.example.calendarsystem.service;

import com.example.calendarsystem.dto.SuggestMeetingRequest;
import com.example.calendarsystem.dto.SuggestMeetingResponse;
import com.example.calendarsystem.entity.Meeting;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class MeetingSuggestionService {

    private final PersonService personService;
    private final MeetingService meetingService;

    public MeetingSuggestionService(PersonService personService, MeetingService meetingService) {
        this.personService = personService;
        this.meetingService = meetingService;
    }

    @Transactional(readOnly = true)
    public SuggestMeetingResponse suggest(SuggestMeetingRequest request) {
        validateRequest(request);

        personService.findEntityById(request.getPersonOneId());
        personService.findEntityById(request.getPersonTwoId());

        List<Meeting> meetings = meetingService.findScheduledConflicts(
                Arrays.asList(request.getPersonOneId(), request.getPersonTwoId()),
                request.getSearchStart(),
                request.getSearchEnd()
        );

        LocalDateTime candidateStart = request.getSearchStart();
        List<Meeting> sortedMeetings = meetings.stream()
                .sorted(Comparator.comparing(Meeting::getStartDateTime))
                .collect(java.util.stream.Collectors.toList());

        for (Meeting meeting : sortedMeetings) {
            LocalDateTime candidateEnd = candidateStart.plusMinutes(request.getDurationMinutes());
            if (!candidateEnd.isAfter(meeting.getStartDateTime())) {
                return buildFoundResponse(request, candidateStart, candidateEnd);
            }

            if (candidateStart.isBefore(meeting.getEndDateTime()) && candidateEnd.isAfter(meeting.getStartDateTime())) {
                candidateStart = meeting.getEndDateTime();
            }
        }

        LocalDateTime candidateEnd = candidateStart.plusMinutes(request.getDurationMinutes());
        if (!candidateEnd.isAfter(request.getSearchEnd())) {
            return buildFoundResponse(request, candidateStart, candidateEnd);
        }

        SuggestMeetingResponse response = new SuggestMeetingResponse();
        response.setPersonOneId(request.getPersonOneId());
        response.setPersonTwoId(request.getPersonTwoId());
        response.setDurationMinutes(request.getDurationMinutes());
        response.setMessage("No available slot found in the informed window");
        return response;
    }

    private void validateRequest(SuggestMeetingRequest request) {
        if (request.getPersonOneId().equals(request.getPersonTwoId())) {
            throw new IllegalArgumentException("personOneId and personTwoId must be different");
        }
        if (!request.getSearchEnd().isAfter(request.getSearchStart())) {
            throw new IllegalArgumentException("searchEnd must be greater than searchStart");
        }
        if (request.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("durationMinutes must be greater than zero");
        }
    }

    private SuggestMeetingResponse buildFoundResponse(SuggestMeetingRequest request, LocalDateTime start, LocalDateTime end) {
        SuggestMeetingResponse response = new SuggestMeetingResponse();
        response.setPersonOneId(request.getPersonOneId());
        response.setPersonTwoId(request.getPersonTwoId());
        response.setSuggestedStart(start);
        response.setSuggestedEnd(end);
        response.setDurationMinutes(request.getDurationMinutes());
        response.setMessage("First available slot found");
        return response;
    }
}

