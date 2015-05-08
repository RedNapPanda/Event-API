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
import io.not2excel.event.dispatcher.EventDispatcher;
import io.not2excel.event.subscriber.EventSubscriber;
import io.not2excel.event.subscriber.MethodEventSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;

public final class CoreEventManager implements EventManager {

    private final Logger logger = LogManager.getLogger(CoreEventManager.class.getSimpleName());
    private final Map<Class<? extends EventContext>, EventDispatcher<? extends EventContext>> registeredSubscribers;

    public CoreEventManager() {
        registeredSubscribers = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <E extends EventContext> void subscribe(Class<E> eventContext, EventSubscriber<E> subscriber) {
        EventDispatcher<E> eventDispatcher;
        if (!this.registeredSubscribers.containsKey(eventContext)) {
            eventDispatcher = new EventDispatcher<>();
        } else {
            /*
            This causes an unchecked exception
            Due to compiler not being able to verify the type of the dispatcher in the map to be of type E
            */
            eventDispatcher = (EventDispatcher<E>) this.registeredSubscribers.get(eventContext);
        }
        eventDispatcher.registerSubscriber(subscriber);
        this.registeredSubscribers.put(eventContext, eventDispatcher);
    }

    @Override
    public void subscribe(Object parent) {
        final Map<Class<? extends EventContext>, List<MethodEventSubscriber<? extends EventContext>>> deltaSubscribers = new HashMap<>();
        Arrays.stream(parent.getClass().getDeclaredMethods()).parallel().forEach(method -> {
            if (this.checkMethod(method)) {
                Class<? extends EventContext> eventContext = this.pullContext(method);
                List<MethodEventSubscriber<? extends EventContext>> subscriberList;
                if (deltaSubscribers.containsKey(eventContext)) {
                    subscriberList = deltaSubscribers.get(eventContext);
                } else {
                    subscriberList = new LinkedList<>();
                }
                subscriberList.add(new MethodEventSubscriber<>(eventContext, parent, method));
                deltaSubscribers.put(eventContext, subscriberList);
            }
        });
        deltaSubscribers.forEach((eventContext, subscriberList) ->
                subscriberList.forEach(subscriber -> this.subscribe(eventContext, subscriber)));
    }

    @Override
    public <E extends EventContext> void subscribe(Class<E> eventContext, Object parent) {
        final List<MethodEventSubscriber<? extends EventContext>> deltaSubscribers = new LinkedList<>();
        Arrays.stream(parent.getClass().getDeclaredMethods()).parallel().forEach(method -> {
            if (this.checkMethod(method, eventContext)) {
                deltaSubscribers.add(new MethodEventSubscriber<>(eventContext, parent, method));
            }
        });
        deltaSubscribers.forEach(subscriber -> this.subscribe(eventContext, subscriber));
    }

    @Override
    public <E extends EventContext> void unsubscribe(Class<E> eventContext) {
        synchronized (this.registeredSubscribers) {
            this.registeredSubscribers.get(eventContext).clear();
        }
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

    @Override
    public <E extends EventContext> void fire(E event) {

    }

    private boolean checkMethod(Method method) {
        return this.checkMethod(method, EventContext.class);
    }

    private boolean checkMethod(Method method, Class<? extends EventContext> eventContext) {
        return method.isAnnotationPresent(EventSubscribe.class) &&
                method.getParameterCount() == 1 &&
                eventContext.isAssignableFrom(method.getParameterTypes()[0]);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends EventContext> pullContext(Method method) {
        if (this.checkMethod(method)) {
            return (Class<? extends EventContext>) method.getParameterTypes()[0];
        }
        return null;
    }
}
