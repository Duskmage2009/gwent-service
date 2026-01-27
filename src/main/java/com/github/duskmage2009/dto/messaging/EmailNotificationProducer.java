package com.github.duskmage2009.dto.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.email}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.email}")
    private String routingKey;

    @Value("${email.admin:admin@gwent-service.com}")
    private String adminEmail;


    public void sendCardCreatedNotification(String cardName, String deckName,
                                            Integer power, String faction, String type) {
        EmailNotificationDto notification = EmailNotificationDto.builder()
                .subject("New Gwent Card Added: " + cardName)
                .content(buildCardCreatedContent(cardName, deckName, power, faction, type))
                .recipient(adminEmail)
                .build();

        sendNotification(notification);
    }


    public void sendNotification(EmailNotificationDto notification) {
        try {
            log.info("Sending email notification to RabbitMQ: subject={}, recipient={}",
                    notification.getSubject(), notification.getRecipient());

            rabbitTemplate.convertAndSend(exchangeName, routingKey, notification);

            log.info("Email notification sent successfully to RabbitMQ");
        } catch (Exception e) {
            log.error("Failed to send email notification to RabbitMQ: {}", e.getMessage(), e);
        }
    }

    private String buildCardCreatedContent(String cardName, String deckName,
                                           Integer power, String faction, String type) {
        return String.format(
                "A new Gwent card has been added to the collection!\n\n" +
                        "Card Details:\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Name:       %s\n" +
                        "Deck:       %s\n" +
                        "Faction:    %s\n" +
                        "Type:       %s\n" +
                        "Power:      %d\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "This notification was sent automatically by Gwent Service.\n" +
                        "Check your Gwent collection for more details!",
                cardName, deckName, faction, type, power
        );
    }
}