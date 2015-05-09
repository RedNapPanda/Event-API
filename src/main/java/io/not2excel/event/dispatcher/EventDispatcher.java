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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EventDispatcher<E extends EventContext> {

    private final List<EventSubscriber<E>> subscriberList;

    public EventDispatcher() {
        subscriberList = new LinkedList<>();
    }

    @SuppressWarnings("unchecked")
    public void registerSubscriber(EventSubscriber<? extends EventContext> subscriber) {
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
                this.subscriberList.add((EventSubscriber<E>) subscriber);
            }
        }
    }

    public void unregisterSubscriber(EventSubscriber<? extends EventContext> subscriber) {
        synchronized (this.subscriberList) {
            Iterator<EventSubscriber<E>> iterator = this.subscriberList.iterator();
            while(iterator.hasNext()) {
                EventSubscriber<E> s = iterator.next();
                if(s.equals(subscriber)) {
                    iterator.remove();
                }
            }
        }
    }

    public void clear() {
        synchronized (this.subscriberList) {
            this.subscriberList.clear();
        }
    }

    public void dispatch(E event) {
        synchronized (this.subscriberList) {
            this.subscriberList.forEach(subscriber -> subscriber.dispatch(event));
        }
    }
}
