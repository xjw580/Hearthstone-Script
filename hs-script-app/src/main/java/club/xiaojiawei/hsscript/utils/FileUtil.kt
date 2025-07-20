package club.xiaojiawei.hsscript.utils

import java.io.File

/**
 * @author 肖嘉威
 * @date 2024/10/11 11:37
 */
object FileUtil {

    /**
     * 清空指定目录（不删除该目录）
     */
    fun clearDirectory(directory: File?) {
        if (directory == null || !directory.exists() || !directory.isDirectory) return
        deleteFile(directory)
    }

    /**
     * 删除文件或目录
     */
    fun deleteFile(file: File?) {
        file ?: return
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                deleteFile(it)
            }
        }
        file.delete()
    }

    fun createDirectory(directory: File?): Boolean {
        directory ?: return false
        if (directory.exists()) {
            if (directory.isFile) {
                deleteFile(directory)
                return directory.mkdirs()
            } else {
                clearDirectory(directory)
                return true
            }
        } else {
            return directory.mkdirs()
        }
    }

}