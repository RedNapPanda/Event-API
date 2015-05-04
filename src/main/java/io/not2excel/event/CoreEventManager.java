/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of server-core.
 * 
 * server-core can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event;

import io.not2excel.event.annotation.EventSubscribe;
import io.not2excel.event.context.EventContext;
import io.not2excel.event.subscriber.EventSubscriber;
import io.not2excel.event.subscriber.MethodEventSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

public final class CoreEventManager implements EventManager {

    private final Logger logger = LogManager.getLogger(CoreEventManager.class.getSimpleName());
    private final Map<Class<? extends EventContext>, List<EventSubscriber<? extends EventContext>>> registeredSubscribers;

    public CoreEventManager() {
        registeredSubscribers = new HashMap<>();
    }

    @Override
    public <E extends EventContext> void subscribe(Class<E> eventContext, EventSubscriber<E> subscriber) {
        List<EventSubscriber<? extends EventContext>> subscriberList;
        if (this.registeredSubscribers.containsKey(eventContext)) {
            subscriberList = this.registeredSubscribers.get(eventContext);
        } else {
            subscriberList = new ArrayList<>();
        }
        if (!subscriberList.isEmpty() &&
                subscriberList.contains(subscriber)) {
            subscriberList.add(subscriber);
            this.registeredSubscribers.put(eventContext, subscriberList);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void subscribe(Object parent) {
        Arrays.stream(parent.getClass().getDeclaredMethods()).parallel().forEach(method -> {
            if (method.isAnnotationPresent(EventSubscribe.class) &&
                    method.getParameterCount() == 1 &&
                    EventContext.class.isAssignableFrom(method.getParameterTypes()[0])) {
                this.subscribeMethod((Class<? extends EventContext>) method.getParameterTypes()[0], parent, method);
            }
        });
    }

    @Override
    public <E extends EventContext> void subscribe(Class<E> eventContext, Object parent) {
        Arrays.stream(parent.getClass().getDeclaredMethods()).parallel().forEach(method -> {
            if (method.isAnnotationPresent(EventSubscribe.class) &&
                    method.getParameterCount() == 1 &&
                    eventContext.equals(method.getParameterTypes()[0])) {
                this.subscribeMethod(eventContext, parent, method);
            }
        });
    }

    private void subscribeMethod(Class<? extends EventContext> eventContext, Object parent, Method method) {
        this.subscribe(eventContext, new MethodEventSubscriber<>(eventContext, parent, method));
    }

    @Override
    public <E extends EventContext> void unsubscribe(Class<E> eventContext) {

    }

    @Override
    public void unsubscribe(EventSubscriber<?> subscriber) {

    }

    @Override
    public void unsubscribe(Object parent) {

    }

    @Override
    public <E extends EventContext> void unsubscribe(Class<E> eventContext, EventSubscriber<E> subscriber) {

    }

    @Override
    public <E extends EventContext> void unsubscribe(Class<E> eventContext, Object parent) {

    }
}
