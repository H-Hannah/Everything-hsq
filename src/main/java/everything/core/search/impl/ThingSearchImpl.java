package everything.core.search.impl;

import everything.core.dao.FileIndexDao;
import everything.core.interceptor.impl.ThingClearInterceptor;
import everything.core.model.Condition;
import everything.core.model.Thing;
import everything.core.search.ThingSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingSearchImpl implements ThingSearch {
    private final FileIndexDao fileIndexDao;
    private final ThingClearInterceptor interceptor;
    private final Queue<Thing> thingQueue = new ArrayBlockingQueue<Thing>(2048);

    public ThingSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.interceptor = new ThingClearInterceptor(this.fileIndexDao,thingQueue);
        this.backgroundClearThread();
    }

    //实现检索功能的接口
    @Override
    public List<Thing> search(Condition condition) {
        //BUG:
        //如果本地文件系统将文件删除，数据库中仍然存储到索引信息，
        // 此时如果查询结果存在已经在文件系统中删除的文件
        //那么需要在数据库中清除掉该文件的索引信息

        List<Thing> things = this.fileIndexDao.query(condition);
        Iterator<Thing> iterator = things.iterator();
        while (iterator.hasNext()){
            //这里存在效率问题,优化时需要生产者消费者模型
            Thing thing = iterator.next();
            File file = new File(thing.getPath());
            if (!file.exists()){
                //删除结果集
                iterator.remove();
                //IO阻塞
                //删除数据库
                //interceptor.apply(thing);
                this.thingQueue.add(thing);
            }
        }
        if (condition == null){
            return new ArrayList<>();
        }
        return this.fileIndexDao.query(condition);
        //如果一个类依赖了一个数据源，如何让这个数据源填充到这个类中去
        //解决：定义一个属性，属性初始化由构造方法实行
        //数据库的处理逻辑
        // return new ArrayList<>();
    }
    private void backgroundClearThread(){
        //后台清理工作
        //用队列作为容器
        Thread thread = new Thread(this.interceptor);
        thread.setName("Thread-Clear");
        //设置为守护线程
        thread.setDaemon(true);
        thread.start();
    }
}
