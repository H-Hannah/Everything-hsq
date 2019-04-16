package everything.core.index.Impl;


import everything.config.EverythingConfig;
import everything.core.index.FileScan;
import everything.core.interceptor.FileInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FileScanImpl implements FileScan {
    //DAO

    private EverythingConfig config = EverythingConfig.getInstance();
    //调用拦截器
    private final LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path){
        //遍历文件，目录及子目录，获取文件对象
        Set<String> excludePaths = config.getHandlerPath().getExcludePath();
        //其实可以对文件直接Scan->File->Ineterceptor 一遍历直接处理 不用存入集合中  因为是一个递归方法
        //List<File> fileList = new ArrayList<>();
        //或者判断是目录 isDirectory怎么样
        //判断A Path是否在B Path中，建立队列直接一一比较
        for (String excludePath:excludePaths) {
            if (path.startsWith(excludePath)){
                return;
            }
        }
        File file = new File(path);
//        if (file.isFile()){
//            if (config.contains(file.getParent())) {
//                return;
//            }
////            }else {
////                fileList.add(file);
////            }
//        } else {
//            if (config.getExcludePath().contains(path)) {
//                return;
//            }else {
//                File[] files = file.listFiles();
//                if (files != null) {
//                    for (File f : files) {
//                        index(f.getAbsolutePath());
//                    }
//                }
//            }
//        }
        //判断目录
        if (file.isDirectory()){
            File[] files = file.listFiles();
            if (files != null){
                for (File f: files){
                    index(f.getAbsolutePath());
                }
            }
        }

        //两种情况 目录或索引  directory或file 每个都拦截
//        for (FileInterceptor intercept : this.interceptors){
//            intercept.apply(file);
//        }

//        //文件变成thing->写入数据库
//        for (File f : fileList){
//            //File
//        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

    //添加intercept的方法  添加拦截器
//    public void addFileInterceptor(FileInterceptor fileIntercept){
//        this.interceptors.add(fileIntercept);
//    }
//    public static void main(String[] args) {
////        FileScan scan = new FileScanImpl();
////        System.out.println("D:\\Documents");
//    }
}
