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

@FunctionalInterface
public interface EventSubscriber<E extends EventContext> {

    void dispatch(E event);
}
