package club.xiaojiawei.hsscript.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 肖嘉威
 * @date 2024/9/6 22:08
 */
@Slf4j
public class ClassLoaderUtil {

    public static List<ClassLoader> getClassLoader(File path) throws Exception {
        List<ClassLoader> classLoaderList = new ArrayList<>();
        if (path.exists()){
            File[] files = path.listFiles();
            if (files != null){
                for (File file : files) {
                    classLoaderList.add(new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader()));
                }
            }
        }else {
            log.warn("插件目录不存在:" + path);
        }
        return classLoaderList;
    }

}
