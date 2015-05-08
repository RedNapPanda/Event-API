/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of event-api.
 * 
 * event-api can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event.dispatcher;

import io.not2excel.event.context.EventContext;
import io.not2excel.event.subscriber.EventSubscriber;
import io.not2excel.event.subscriber.EventSubscriberPriority;
import io.not2excel.event.subscriber.WrappedEventSubscriber;

import java.util.LinkedList;
import java.util.List;

public class EventDispatcher<E extends EventContext> {

    private final List<EventSubscriber<E>> subscriberList;

    public EventDispatcher() {
        subscriberList = new LinkedList<>();
    }

    public void registerSubscriber(EventSubscriber<E> subscriber) {
        if (!(subscriber instanceof EventSubscriberPriority)) {
            subscriber = new WrappedEventSubscriber<>(subscriber);
        }
        final int priority = ((EventSubscriberPriority) subscriber).getPriority();
        synchronized (this.subscriberList) {
            if (priority >= 0) {
                for(int i = 0; i < this.subscriberList.size(); i++) {
                    EventSubscriber<E> s = this.subscriberList.get(i);
                    int sPriority = ((EventSubscriberPriority) s).getPriority();
                    if(sPriority < 0 || priority < sPriority) {
                        this.subscriberList.add(i, s);
                        return;
                    }
                }
            } else {
                this.subscriberList.add(subscriber);
            }
        }
    }

    public void unregisterSubscriber(EventSubscriber<E> subscriber) {
        synchronized (this.subscriberList) {
            this.subscriberList.remove(subscriber);
        }
    }

    public void clear() {
        synchronized (this.subscriberList) {
            this.subscriberList.clear();
        }
    }

    public void fire(E event) {
        synchronized (this.subscriberList) {
            this.subscriberList.forEach(subscriber -> subscriber.dispatch(event));
        }
    }
}
