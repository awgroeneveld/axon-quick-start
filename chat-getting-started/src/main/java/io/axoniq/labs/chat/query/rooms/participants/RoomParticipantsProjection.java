package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.eventhandling.*;
import org.axonframework.queryhandling.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@Component
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    public RoomParticipantsProjection(RoomParticipantsRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    void onParticipantJoined(ParticipantJoinedRoomEvent participantJoinedRoomEvent) {
        this.repository.save(new RoomParticipant(participantJoinedRoomEvent.getRoomId(), participantJoinedRoomEvent.getParticipant()));
    }

    @EventHandler
    void onParticipantLeft(ParticipantJoinedRoomEvent participantJoinedRoomEvent) {
        repository.deleteByParticipantAndRoomId(participantJoinedRoomEvent.getParticipant(), participantJoinedRoomEvent.getRoomId());
    }

    @QueryHandler
    List<String> handleRoomParticipants(RoomParticipantsQuery roomParticipantsQuery){
        return repository.findRoomParticipantsByRoomId(roomParticipantsQuery.getRoomId()).stream().map(x->x.getParticipant()).collect(Collectors.toList());
    }
}
