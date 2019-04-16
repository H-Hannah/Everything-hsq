package everything.core.interceptor;

import java.io.File;

public interface FileInterceptor {
    //文件拦截器，处理指定文件
    public void apply(File file);

}
