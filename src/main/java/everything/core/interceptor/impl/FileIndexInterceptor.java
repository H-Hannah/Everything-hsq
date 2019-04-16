package everything.core.interceptor.impl;

import everything.core.common.FileCovertThing;
import everything.core.dao.FileIndexDao;
import everything.core.interceptor.FileInterceptor;
import everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {
    //文件打印，转换和写入数据库  三者独立
    //写入要thing对象，但传入的是file对象
    //所以讲转换和写入合在一起
    //则需要实现的是：如何把file对象转换为thing对象

    //写数据库必须和DAO有关系了
    private FileIndexDao fileIndexDao;
    public FileIndexInterceptor(FileIndexDao fileIndexDao){
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileCovertThing.convert(file);
        System.out.println("这是Thing的操作"+thing);
        this.fileIndexDao.insert(thing);
    }
}
