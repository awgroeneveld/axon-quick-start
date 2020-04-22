package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import io.axoniq.labs.chat.restapi.*;
import org.axonframework.commandhandling.*;
import org.axonframework.eventhandling.*;
import org.axonframework.eventsourcing.*;
import org.axonframework.modelling.command.*;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.*;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private List<String> participants = new LinkedList<>();
    private List<String> messages = new LinkedList<>();

    @CommandHandler
    public ChatRoom(CreateRoomCommand createRoomCommand) {
        apply(new RoomCreatedEvent(createRoomCommand.getRoomId(), createRoomCommand.getName()));
    }

    @CommandHandler
    public void handle(JoinRoomCommand joinRoomCommand) {
        if (!participants.contains(joinRoomCommand.getParticipant()))
            apply(new ParticipantJoinedRoomEvent(this.roomId, joinRoomCommand.getParticipant()));
    }

    @CommandHandler
    public void handle(PostMessageCommand postMessageCommand) {
        if (!participants.contains(postMessageCommand.getParticipant()))
            throw new IllegalStateException("Participant not in the room");

        apply(new MessagePostedEvent(postMessageCommand.getRoomId(), postMessageCommand.getParticipant(), postMessageCommand.getMessage()));
    }

    @CommandHandler
    public void handle(LeaveRoomCommand leaveRoomCommand) {
        if (participants.contains(leaveRoomCommand.getParticipant()))
            apply(new ParticipantJoinedRoomEvent(this.roomId, leaveRoomCommand.getParticipant()));

    }

    @EventSourcingHandler
    public void onRoomCreated(RoomCreatedEvent roomCreatedEvent) {
        this.roomId = roomCreatedEvent.getRoomId();
    }

    @EventSourcingHandler
    public void onParticipantJoined(ParticipantJoinedRoomEvent participantJoinedRoomEvent) {
        this.participants.add(participantJoinedRoomEvent.getParticipant());
    }

    @EventSourcingHandler
    public void onMessagePosted(MessagePostedEvent messagePostedEvent) {
        this.messages.add(messagePostedEvent.getMessage());
    }

    @EventSourcingHandler
    public void onParticipantLeft(ParticipantLeftRoomEvent participantLeftRoomEvent) {
        this.participants.remove(participantLeftRoomEvent.getParticipant());
    }

    protected ChatRoom() {

    }
}
