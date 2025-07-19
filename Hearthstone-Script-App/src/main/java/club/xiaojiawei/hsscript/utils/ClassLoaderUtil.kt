package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.config.log
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.ArrayList


/**
 * @author 肖嘉威
 * @date 2024/9/6 22:08
 */
object ClassLoaderUtil {

    fun getClassLoader(path: File): MutableList<ClassLoader?> {
        val classLoaderList: MutableList<ClassLoader?> = ArrayList<ClassLoader?>()
        if (path.exists()) {
            val files = path.listFiles()
            if (files != null) {
                for (file in files) {
                    classLoaderList.add(
                        URLClassLoader(
                            arrayOf<URL>(file.toURI().toURL()),
                            Thread.currentThread().getContextClassLoader()
                        )
                    )
                }
            }
        } else {
            log.warn { "插件目录不存在:$path" }
        }
        return classLoaderList
    }

}
