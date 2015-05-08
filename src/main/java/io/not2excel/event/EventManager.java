/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of server-core.
 * 
 * server-core can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event;

import io.not2excel.event.context.EventContext;
import io.not2excel.event.subscriber.EventSubscriber;

public interface EventManager {

    <E extends EventContext> void subscribe(Class<E> eventContext, EventSubscriber<E> subscribers);

    void subscribe(Object parent);

    <E extends EventContext> void subscribe(Class<E> eventContext, Object parent);

    <E extends EventContext> void unsubscribe(Class<E> eventContext);

    void unsubscribe(EventSubscriber<?> subscriber);

    void unsubscribe(Object parent);

    <E extends EventContext> void unsubscribe(Class<E> eventContext, EventSubscriber<E> subscriber);

    <E extends EventContext> void unsubscribe(Class<E> eventContext, Object parent);

    <E extends EventContext> void fire(E event);
}
