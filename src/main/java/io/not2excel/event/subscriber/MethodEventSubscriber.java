/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of server-core.
 * 
 * server-core can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event.subscriber;

import io.not2excel.event.context.EventContext;
import lombok.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Value
public class MethodEventSubscriber<E extends EventContext> implements EventSubscriber<E> {

    private final Logger logger = LogManager.getLogger(MethodEventSubscriber.class.getSimpleName());

    private final Class<E> eventContextClass;
    private final Object parent;
    private final Method subscriber;

    @Override
    public void dispatch(E eventContext) {
        try {
            subscriber.setAccessible(true);
            subscriber.invoke(parent, eventContext);
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(String.format("Failed to invoke event subscriber: %s#%s",
                    parent.getClass().getSimpleName(), subscriber.getName()), e);
        }
    }
}
