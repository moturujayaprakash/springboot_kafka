package com.example.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.MessageEvent;
import com.example.domain.MessageEventType;
import com.example.producer.MessageEventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class MessageEventsController {

	@Autowired
	private MessageEventProducer messageEventProducer;
	
	
	@PostMapping("/v1/me")
	@ResponseBody
	public ResponseEntity<MessageEvent> createMessageEvent(@RequestBody  MessageEvent messageEvent) throws JsonProcessingException {
		log.info("createMessage from Controller Entry point");
		System.out.println(messageEvent);
		
		log.info("MessageEventController before sendMessage method");
		messageEvent.setMessageEventType(MessageEventType.NEW);
		SendResult<Integer,String> sendResult=messageEventProducer.sendMessageSynchronous(messageEvent);
		//messageEventProducer.sendMessageWithTopic(messageEvent);
		log.info(sendResult.toString());
		log.info("MessageEventController after sendMessage method");
		return ResponseEntity.status(HttpStatus.CREATED).body(messageEvent);
	}
	
	
	@PutMapping("/v1/me")
	
	public ResponseEntity<?> updateMessageEvent(@RequestBody MessageEvent messageEvent) throws JsonProcessingException {
		log.info("updateMessage from Controller Entry point");
		System.out.println(messageEvent);
		if(messageEvent.getMessageEventId()==null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the messageEventId");
		}
		log.info("MessageEventController before sendMessage method");
		messageEvent.setMessageEventType(MessageEventType.UPDATE);
		SendResult<Integer,String> sendResult=messageEventProducer.sendMessageSynchronous(messageEvent);
		//messageEventProducer.sendMessageWithTopic(messageEvent);
		log.info(sendResult.toString());
		log.info("MessageEventController after sendMessage method");
		return ResponseEntity.status(HttpStatus.OK).body(messageEvent);
	}
}
