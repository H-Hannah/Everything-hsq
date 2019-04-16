package everything.core.model;

//检索条件的模型类型

import lombok.Data;

@Data
public class Condition {
    //文件名
    private String name;
    //文件类型
    private String fileType;
    //限制数量
    private Integer limit;
    //是否按照dept升序
    public Boolean orderByDepthAsc;

}
