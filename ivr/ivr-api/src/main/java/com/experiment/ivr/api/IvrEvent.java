package com.experiment.ivr.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import lombok.Value;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IvrEvent.GreetingMessageChanged.class, name = "greeting-message-changed")
})
public interface IvrEvent {
    String getName();

    @Value
    final class GreetingMessageChanged implements IvrEvent {
        public final String name;
        public final String message;

        @JsonCreator
        public GreetingMessageChanged(String name, String message) {
            this.name = Preconditions.checkNotNull(name, "name");
            this.message = Preconditions.checkNotNull(message, "message");
        }
    }
}
