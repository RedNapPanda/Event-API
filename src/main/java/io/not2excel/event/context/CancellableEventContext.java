/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of event-api.
 * 
 * event-api can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package io.not2excel.event.context;

import lombok.Getter;
import lombok.Setter;

public class CancellableEventContext extends SimpleEventContext {

    @Getter
    @Setter
    private boolean cancelled;
}
