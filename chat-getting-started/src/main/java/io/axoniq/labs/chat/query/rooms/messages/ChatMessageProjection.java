package io.axoniq.labs.chat.query.rooms.messages;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.eventhandling.*;
import org.axonframework.queryhandling.*;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;

@Component
public class ChatMessageProjection {

    private final ChatMessageRepository repository;
    private final QueryUpdateEmitter updateEmitter;

    public ChatMessageProjection(ChatMessageRepository repository, QueryUpdateEmitter updateEmitter) {
        this.repository = repository;
        this.updateEmitter = updateEmitter;
    }

    @EventHandler
    public void onMessagePosted(MessagePostedEvent messagePostedEvent, @Timestamp Instant timestamp){
        ChatMessage savedMessage = repository.save(new ChatMessage(messagePostedEvent.getParticipant(), messagePostedEvent.getRoomId(), messagePostedEvent.getMessage(), timestamp.getEpochSecond()));
        updateEmitter.emit(RoomMessagesQuery.class, query -> query.getRoomId().equals(messagePostedEvent.getRoomId()) , savedMessage);
    }

    @QueryHandler
    public List<ChatMessage> handleChat(RoomMessagesQuery roomMessagesQuery){
        return this.repository.findAllByRoomIdOrderByTimestamp(roomMessagesQuery.getRoomId());
    }




}
