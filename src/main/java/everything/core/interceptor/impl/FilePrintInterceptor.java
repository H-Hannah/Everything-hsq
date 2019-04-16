package everything.core.interceptor.impl;

import everything.core.interceptor.FileInterceptor;

import java.io.File;

public class FilePrintInterceptor implements FileInterceptor {
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
