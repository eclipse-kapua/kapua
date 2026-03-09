/*******************************************************************************
 * Copyright (c) 2021, 2026 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.commons.localevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Generic class to allow different component to interact with a blocking queue.
 * This is usable to distribute internal events without involving brokers.
 * This class provide a consumer that polling for new messages in the queue and a producer method to put new messages in that queue.
 * O generic object
 */
public abstract class EventHandler<O> {

    private static Logger logger = LoggerFactory.getLogger(EventHandler.class);

    private boolean running;
    private ExecutorWrapper executorWrapper;
    private static final int MAX_ONGOING_OPERATION = 10;

    private BlockingQueue<O> eventQueue = new LinkedBlockingDeque<>(MAX_ONGOING_OPERATION);
    private EventProcessor<O> eventProcessor;

    private Counter enqueuedEvent;

    /**
     * Default constructor. Warn: doesn't start without start() is called
     * @param name Event handler name useful to distinguish between multiple instances (and used while logging information)
     * @param initialDelay initial delay before executor start, after start() is called (in seconds!)
     * @param pollTimeout poll timeout (in milliseconds!)
     * @param enqueuedEvent counter for enqueued events
     * @param dequeuedEvent counter for dequeued events
     * @param processedEvent counter for processed events
     */
    public EventHandler(String name, long initialDelay, long pollTimeout,
            Counter enqueuedEvent, Counter dequeuedEvent, Counter processedEvent) {
        this.enqueuedEvent = enqueuedEvent;
        executorWrapper = new ExecutorWrapper(name, () -> {
            while (isRunning()) {
                try {
                    O eventBean = eventQueue.poll(pollTimeout, TimeUnit.MILLISECONDS);
                    if (eventBean != null) {
                        dequeuedEvent.inc();
                        eventProcessor.processEvent(eventBean);
                        processedEvent.inc();
                    }
                } catch (InterruptedException e) {
                    //do nothing...
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    //do nothing
                    logger.error("Error while processing event: {}", e.getMessage(), e);
                    //TODO add metric?
                }
            }
        }, initialDelay, TimeUnit.SECONDS);
    }

    public void enqueueEvent(O eventBean) {
        eventQueue.add(eventBean);
        enqueuedEvent.inc();
    }

    public void registerConsumer(EventProcessor<O> eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    public void start() {
        running = true;
        executorWrapper.start();
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}