package com.example.producer;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.domain.MessageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class MessageEventProducer {
 
	@Autowired
	private KafkaTemplate<Integer,String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private String topic="Message-Event";
	
	public void sendMessage(MessageEvent messageEvent) throws JsonProcessingException {
		Integer key=messageEvent.getMessageEventId();
		String value=objectMapper.writeValueAsString(messageEvent);
		ListenableFuture<SendResult<Integer, String>> listenableFuture= kafkaTemplate.sendDefault(key, value);
		
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>(){

			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key,value, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key,value,result);
				
			}	
			
		});
		
	}
	
	public void sendMessageWithTopic(MessageEvent messageEvent) throws JsonProcessingException {
		Integer key=messageEvent.getMessageEventId();
		String value=objectMapper.writeValueAsString(messageEvent);
		
		ProducerRecord<Integer, String> producerRecord=buildProducerRecord(key,value,topic);
		//ListenableFuture<SendResult<Integer, String>> listenableFuture= kafkaTemplate.send("Message-Event",key, value);
		ListenableFuture<SendResult<Integer, String>> listenableFuture= kafkaTemplate.send(producerRecord);
		listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>(){

			@Override
			public void onFailure(Throwable ex) {
				handleFailure(key,value, ex);
			}

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				handleSuccess(key,value,result);
				
			}	
			
		});
		
	}
	
	
	
	private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic2) {
		// TODO Auto-generated method stub
		return new ProducerRecord<>(topic,null,key,value,null);
	}

	public void handleSuccess(Integer key,String value,SendResult<Integer,String> result) {
		log.info("Message Sent Successfully for the key:{}, and the value is {},partition is {}:",key,value,result.getRecordMetadata().partition());
	}
	
	public void handleFailure(Integer key,String value,Throwable e) {
		log.error("Error Sending the message and the exception is :",e.getMessage());
		try {
				throw e;
		}catch(Throwable  e1) {
			log.error("Error in OnFailure {}",e1.getMessage());
		}
	}
	
	public SendResult<Integer,String> sendMessageSynchronous(MessageEvent messageEvent) throws JsonProcessingException {
		
		Integer key=messageEvent.getMessageEventId();
		String value=objectMapper.writeValueAsString(messageEvent);
		SendResult<Integer,String> sendResult=null;
		try {
			
			sendResult=kafkaTemplate.sendDefault(key, value).get();
			
		} catch (InterruptedException | ExecutionException e) {
			log.error("InterruptedException | ExecutionException Error Sending the message and the exception is :",e.getMessage());
			e.printStackTrace();
		}
		return sendResult;
	}
}
