package everything.core.index;

import everything.core.dao.DataSourceFactory;
import everything.core.dao.FileIndexDao;
import everything.core.dao.impl.FileIndexDaoImpl;
import everything.core.index.Impl.FileScanImpl;
import everything.core.interceptor.FileInterceptor;
import everything.core.interceptor.impl.FileIndexInterceptor;
import everything.core.interceptor.impl.FilePrintInterceptor;
import everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.File;


//快速改类名 Ctrl+shift+F6
//文件扫描
//遍历系统所有文件，并且将文件变成文件对象再变成thing对象，
//再通过Dao层插入到数据库中
public interface FileScan {
    //建立索引
    //遍历Path
    void index(String path);
    //面向接口编程
    //遍历的拦截器
    //给文件扫描接口添加拦截器对象
    void interceptor(FileInterceptor interceptor);

//    public static void main(String[] args) {
//        FileScan fileScan = new FileScanImpl();
//        DataSource dataSource = DataSourceFactory.getInstance();
//        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
//        fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
//        fileScan.interceptor(new FilePrintInterceptor());
//        fileScan.index("D:\\desktop");
//    }
}
