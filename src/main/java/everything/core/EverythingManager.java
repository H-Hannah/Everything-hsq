package everything.core;

import everything.config.EverythingConfig;
import everything.config.HandlerPath;
import everything.core.dao.DataSourceFactory;
import everything.core.dao.FileIndexDao;
import everything.core.dao.impl.FileIndexDaoImpl;
import everything.core.index.FileScan;
import everything.core.index.Impl.FileScanImpl;
import everything.core.interceptor.impl.FileIndexInterceptor;
import everything.core.interceptor.impl.FilePrintInterceptor;
import everything.core.model.Condition;
import everything.core.model.Thing;
import everything.core.search.ThingSearch;
import everything.core.search.impl.ThingSearchImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

//核心统一调度器
//单例 同一时间有一个就行了
public class EverythingManager {
    private static volatile EverythingManager manager;

    //业务层
    private FileScan fileScan;
    private ThingSearch thingSearch;

    //数据库访问层
    private FileIndexDao fileIndexDao;

    //线程池的执行器
    private final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
    private EverythingConfig config = EverythingConfig.getInstance();

    public EverythingManager(){
        this.fileScan = new FileScanImpl();
        try {
            this.fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(this.fileIndexDao));

        this.thingSearch = new ThingSearchImpl(this.fileIndexDao);
    }

    /**
     * 检索（立即执行，返回结果）
     */
    public List<Thing> search(Condition condition){
        //NOTICE 扩展
        //Condition 用户提供的是：name file-type limit orderby
        condition.setLimit(config.getMaxReturnNumber());
        condition.setOrderByDepthAsc(config.getOrderByDesc());
        return this.thingSearch.search(condition);
    }

    /**
     * 索引（后台线程执行）
     * 建立多少不知道？
     * 所以需要配置：
     * 1、索引目录：包含的目录和排除的目录
     * 2、通过参数设置是否开启索引线程
     * 3、查询是按照depth的升序/降序
     * 4、查询的结果数量多少？限制最大返回数量
     */
    public void buildIndex() throws InterruptedException {
        //目录来源,建立索引
        try {
            DataSourceFactory.databaseInit();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EverythingConfig config = EverythingConfig.getInstance();
        HandlerPath handlerPath = config.getHandlerPath();

        Set<String> includePaths = handlerPath.getIncludePath();
        //使用线程池
//        if (this.executorService==null){
//            this.executorService =
//                    Executors.newFixedThreadPool(includePaths.size(), new ThreadFactory() {
//                        //线程名称
//                        private final AtomicInteger threadId = new AtomicInteger(0);
//                        @Override
//                        public Thread newThread(Runnable r) {
//                            Thread thread = new Thread();
//                            thread.setName("Thread-Scan"+threadId.getAndIncrement());
//                            return thread;
//                        }
//                    });
//        }

        //不知道多线程什么时候开始结束
        final CountDownLatch countDownLatch = new CountDownLatch(includePaths.size());
        System.out.println("Build index start...");

        //没同步，一个接一个
        for (String path : includePaths){
//            //this.fileScan.index(path);
//            //多线程
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    EverythingProManager.this.fileScan.index(path);
//                }
//            }).start();
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    EverythingManager.this.fileScan.index(path);
                    //当前任务完成减一
                    //多线程中的一个API，像开关一样
                    //不能调用多次
                    countDownLatch.countDown(); //减一
                }
            });
        }
        //阻塞直到任务完成，值0
        countDownLatch.await(); //等待
        System.out.println("Build index over...");
        this.fileScan.index("");
    }

    //帮助
    public void help(){
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img|doc|bin|archive|other]");
    }
    //退出
    public void quit(){
        System.out.println("感谢您的使用，再见！");
        System.exit(0);
    }
}
