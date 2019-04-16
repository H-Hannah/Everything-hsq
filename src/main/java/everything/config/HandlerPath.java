package everything.config;


import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;


@Getter
@ToString
public class HandlerPath {

    //建立索引的路径集合，包含的目录
    private Set<String> includePath = new HashSet<>();
    //排除索引的路径，排除的目录
    //new后就不会空指针了
    private Set<String> excludePath = new HashSet<>();

    private HandlerPath(){

    }

    public void addIncludePath(String path){
        this.includePath.add(path);
    }
    public void addExcludePath(String path){
        this.excludePath.add(path);
    }

    public static HandlerPath getDefaultHandlerPath(){
        HandlerPath handlerPath = new HandlerPath();
        Iterable<Path> paths = FileSystems.getDefault().getRootDirectories();
        //默认包含的目录及一些不想被处理的目录
        paths.forEach(path -> {
            handlerPath.addIncludePath(path.toString());

            String osname = System.getProperty("os.name");
            if (osname.startsWith("Windows")){
                handlerPath.addExcludePath("C:\\Program Files (x86)");
                handlerPath.addExcludePath("C:\\Program Files");
                handlerPath.addExcludePath("C:\\Windows");
                handlerPath.addExcludePath("C:\\ProgramData");
            }   //不然就是Linux系统
            else{
                handlerPath.addExcludePath("/temp");
                handlerPath.addExcludePath("/etc");
                handlerPath.addExcludePath("/root");
            }
        });
        return handlerPath;
    }

//    public static void main(String[] args) {
//        HandlerPath.getDefaultHandlerPath();
//    }
}
