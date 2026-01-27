package com.github.duskmage2009.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationDto implements Serializable {

    private String subject;
    private String content;
    private String recipient;
}