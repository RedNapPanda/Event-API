/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of server-core.
 * 
 * server-core can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event.subscriber;

import io.not2excel.event.annotation.EventSubscribe;
import io.not2excel.event.context.EventContext;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Value
public class MethodEventSubscriber<E extends EventContext> implements EventSubscriber<E>, EventSubscriberPriority {

    private final Logger logger = LogManager.getLogger(MethodEventSubscriber.class.getSimpleName());

    private final Class<E> eventContextClass;
    private final Object parent;
    private final Method subscriber;
    private final int priority;

    public MethodEventSubscriber(Class<E> eventContextClass, Object parent, Method subscriber) {
        this.eventContextClass = eventContextClass;
        this.parent = parent;
        this.subscriber = subscriber;
        EventSubscribe annotation = subscriber.getAnnotation(EventSubscribe.class);
        if(annotation == null) {
            logger.error(subscriber.getName() + " Method Subscriber doesn't have @EventSubscribe annotation. " +
                    "Reaching this point should never happen. Please debug immediately.", new NullPointerException());
            priority = -1;
            return;
        }
        this.priority = annotation.priority();
    }

    @Override
    public void dispatch(E event) {
        try {
            subscriber.setAccessible(true);
            subscriber.invoke(parent, event);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(String.format("Failed to invoke event subscriber: %s#%s",
                    parent.getClass().getSimpleName(), subscriber.getName()), e);
        }
    }

    @Override
    public int getPriority() {
        return this.priority;
    }
}
