package com.elijahwaswa.filetracker.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {


    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.reminder.name}")
    private String reminderQueue;

    @Value("${rabbitmq.binding.routing.key}")
    private String reminderRoutingKey;

    //bean for queue
    @Bean
    public Queue reminderQueue(){
        return new Queue(reminderQueue, true);
    }

    //bean for exchange
//    @Bean
//    public TopicExchange exchange(){
//        return new TopicExchange(exchange);
//    }

    @Bean
    public CustomExchange exchange(){
        Map<String,Object> args  = new HashMap<>();
        args.put("x-delayed-type","topic");
        return new CustomExchange(exchange,"x-delayed-message",true,false,args);
    }

    //bean for binding between exchange and queue
    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(reminderQueue())
                .to(exchange())
                .with(reminderRoutingKey).noargs();
    }

    //message converter
    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    //configure RabbitTemplate
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
