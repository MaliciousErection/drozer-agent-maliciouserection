package com.WithSecure.jsolar.api;

import java.util.Locale;
import com.WithSecure.jsolar.api.Protobuf.Message.MessageType;

public class UnexpectedMessageException extends RuntimeException {

    private static final long serialVersionUID = 2323712339351270587L;

    private MessageType type;

    public UnexpectedMessageException(MessageType type) {
        this.type = type;
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "Unexpected MessageType: %d", this.type.getNumber());
    }

}
