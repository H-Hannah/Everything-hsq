package everything.core.model;

import lombok.Data;

@Data
public class Thing {
    //文件名称（不含路径）
    private String name;

    //文件路径
    private String path;

    //文件路径深度
    private Integer depth;

    //文件类型
    private FileType fileType;
}
