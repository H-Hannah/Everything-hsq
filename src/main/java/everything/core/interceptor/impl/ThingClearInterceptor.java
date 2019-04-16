package everything.core.interceptor.impl;

import everything.core.dao.FileIndexDao;
import everything.core.interceptor.ThingInterceptor;
import everything.core.model.Thing;

import java.util.Queue;

public class ThingClearInterceptor implements ThingInterceptor,Runnable {

    private final FileIndexDao fileIndexDao;
    private final Queue<Thing> thingQueue;

    public ThingClearInterceptor(FileIndexDao fileIndexDao,Queue thingQueue) {
        this.fileIndexDao = fileIndexDao;
        this.thingQueue = thingQueue;
    }

    @Override
    public void apply(Thing thing) {
        this.fileIndexDao.delete(thing);
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thing thing = this.thingQueue.poll();
            if (thing!=null){
                this.apply(thing);
            }
        }
    }
}
