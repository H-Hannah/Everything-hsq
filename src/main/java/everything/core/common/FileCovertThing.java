package everything.core.common;

import everything.core.model.FileType;
import everything.core.model.Thing;

import java.io.File;

/**
 * 文件对象转换Thing对象的辅助类
 */
public class FileCovertThing {
    private FileCovertThing(){}
    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        //目录 没有扩展名 ->OTHER
        //文件 有扩展名，通过扩展名获取FileType
        //   无扩展.*
//        int index = name.lastIndexOf(".");
//        String extend = "*";
//        if (index!=-1)
        thing.setDepth(computePathDepth(file.getAbsolutePath()));
        thing.setFileType(computeFileType(file));
        return thing;
    }
    //计算文件深度
    private static int computePathDepth(String path){
        int depth = 0;
        //转义路径分隔符
        //path => D:\a\b => 2
        //path => D:\a\b\hello.java =>3
        //Windows下是反斜杠\，但是Linux下是/正斜杠
        for (char c:path.toCharArray()){
            if (c==File.separatorChar){
                depth+=1;
            }
        }
        return depth;

        //转换器这里有问题！！
//        String[] segments = file.getAbsolutePath().split("\\\\");
//        dept = segments.length;
//        return dept;
    }

    //根据扩展名extend获取文件类型
    private static FileType computeFileType(File file){
        if (file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String extend = "*";
        if (index != -1 && index+1<fileName.length()){
            extend = fileName.substring(index+1);
            return FileType.lookupByExtend(extend);

        }else {
            return FileType.OTHER;
        }
    }

//    public static void main(String[] args) {
//        System.out.println(computeFileDepth("C:\\Users\\hasee\\Desktop\\Documents\\C Language\\Code"));
//    }
}
