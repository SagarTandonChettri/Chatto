package com.peto.peto.Controller;

import com.peto.peto.Message.ChatMessage;
import com.peto.peto.Message.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
        String username = (String) headerAccessor
                .getSessionAttributes()
                .get("username");
        chatMessage.setSender(username);
        chatMessage.setType(MessageType.CHAT);

        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ){
        String sender = chatMessage.getSender();

        if(sender == null || sender.isBlank()){
            log.error("JSON failed = missing sender field");
            return null;
        }
        //Add username in web socket session
        headerAccessor.getSessionAttributes().put("username",sender);

        log.info("User JOINED chat: {}", sender);
        return chatMessage;
    }

}
