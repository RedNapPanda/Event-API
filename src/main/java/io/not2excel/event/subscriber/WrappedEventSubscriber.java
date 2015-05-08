/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of event-api.
 * 
 * event-api can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event.subscriber;

import io.not2excel.event.context.EventContext;
import lombok.Value;

@Value
public class WrappedEventSubscriber<E extends EventContext> implements EventSubscriber<E>, EventSubscriberPriority {

    private final EventSubscriber<E> subscriber;

    @Override
    public void dispatch(E event) {
        this.subscriber.dispatch(event);
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
