package everything.config;

import lombok.Getter;
import lombok.Setter;


public class EverythingConfig {
    private static volatile EverythingConfig config;
    //索引目录
    @Getter
    private HandlerPath handlerPath;
    //最大检索返回的结果数

    @Getter
    @Setter
    private Integer maxReturnNumber = 30;

    //默认程序运行关闭构建索引
    //运行程序时，指定参数
    //通过功能命令 index
    @Getter
    @Setter
    private Boolean enableBuildIndex = true;

    //检索是depth深度的排序规则，true 升序  false 降序
    @Setter
    @Getter
    private Boolean orderByDesc = false;

    private EverythingConfig(){

    }
    public static EverythingConfig getInstance(){
        //检查
        if (config==null){
            synchronized (EverythingConfig.class){
                if (config == null){
                    config = new EverythingConfig();
                    //遍历的目录
                }
            }
        }
        return config;
    }
}
