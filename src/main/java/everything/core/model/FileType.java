package everything.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//FileType模型类表示文件的扩展名进行归类，跟安卓软件比较像

//写个枚举类
//放到集合里面Set
public enum FileType {
    IMG("jpg","jpeg","png","bmp","gif"),
    DOC("doc","docx","pdf","ppt","pptx","xls"),
    BIN("exe","jar","sh","msi"),
    ARCHIVE("zip","rar"),
    OTHER("*");

    private Set<String> extend = new HashSet<>();
    //写个构造方法，将上面的枚举类型放进集合里面
    //传入可变参数
    FileType(String... extend){
        //数组转集合
        this.extend.addAll(Arrays.asList(extend));
    }
    //给上一个扩展名去判断文件类型
    public static FileType lookupByExtend(String extend){
        //遍历循环
        for (FileType fileType:FileType.values()){
            //如果包含这个扩展名，就返回它的文件类型
            if (fileType.extend.contains(extend)){
                return fileType;
            }
        }
        //否则就返回other
        return FileType.OTHER;
    }

    //给一个fileType 返回它的FileType
    public static FileType looupByName(String name){
        for (FileType fileType:FileType.values()){
            if (fileType.name().equals(name)){
                return fileType;
            }
        }
        return FileType.OTHER;
    }
}
