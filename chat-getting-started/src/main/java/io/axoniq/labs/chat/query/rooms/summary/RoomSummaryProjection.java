package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.eventhandling.*;
import org.axonframework.queryhandling.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @EventHandler
    public void onRoomCreated(RoomCreatedEvent roomCreatedEvent) {
        this.roomSummaryRepository.save(new RoomSummary(roomCreatedEvent.getRoomId(), roomCreatedEvent.getName()));
    }

    @EventHandler
    public void onParticipantJoined(ParticipantJoinedRoomEvent participantJoinedRoomEvent) {
        RoomSummary roomSummary = this.roomSummaryRepository.findById(participantJoinedRoomEvent.getRoomId())
                                                            .get();
        roomSummary.addParticipant();
        roomSummaryRepository.save(roomSummary);
    }

    @EventHandler
    public void onParticipantLeft(ParticipantLeftRoomEvent participantLeftRoomEvent) {
        RoomSummary roomSummary = this.roomSummaryRepository.findById(participantLeftRoomEvent.getRoomId())
                                                            .get();
        roomSummary.removeParticipant();
        roomSummaryRepository.save(roomSummary);
    }


    @QueryHandler
    public List<RoomSummary> handleRoomSummaryQuery(AllRoomsQuery allRoomsQuery) {
        return this.roomSummaryRepository.findAll();
    }
}
