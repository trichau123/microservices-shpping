package org.amex;

import lombok.extern.slf4j.Slf4j;
import org.amex.event.OrderPlaceEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@Slf4j
@EnableEurekaClient
public class NotificationServiceAppliation
{
    public static void main( String[] args )
    {
        SpringApplication.run(NotificationServiceAppliation.class,args);
    }

    @KafkaListener(topics = "notificationTopic")
    public void handleNotifcation(OrderPlaceEvent orderPlaceEvent){
        //send out an email notification
        log.info("received notification for Order - {}", orderPlaceEvent.getOrderNumber());
    }
}
