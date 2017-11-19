package com.act.quzhibo.widget;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;


public class SerialExecutor {
    final Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
    final Executor executor;
    Runnable active;

    public SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    public void addrun(Runnable r) {
        tasks.add(r);
    }

    public void execute(final Runnable r) {
        try {
            r.run();
        } finally {
            scheduleNext();
        }
    }

    public void scheduleNext() {
        if ((active = tasks.poll()) != null) {
            this.execute(active);
        }
    }
}
