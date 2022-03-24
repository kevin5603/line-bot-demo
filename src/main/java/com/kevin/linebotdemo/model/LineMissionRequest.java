package com.kevin.linebotdemo.model;

import com.linecorp.bot.model.event.message.MessageContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * @author liyanting
 */
@Data
@AllArgsConstructor
@NonNull
public class LineMissionRequest<S extends MessageContent> {

    private S content;
    private String userId;
}
