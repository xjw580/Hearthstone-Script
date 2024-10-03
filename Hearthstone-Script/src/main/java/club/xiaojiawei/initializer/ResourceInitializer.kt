package club.xiaojiawei.initializer

import club.xiaojiawei.config.SpringBeanConfig
import club.xiaojiawei.config.log
import club.xiaojiawei.data.ScriptStaticData
import java.io.*
import java.util.*

/**
 * 创建系统资源文件夹
 * @author 肖嘉威
 * @date 2023/9/17 1:59
 */
object ResourceInitializer : AbstractInitializer() {

    override fun exec() {
        val imgFile =
            File(SpringBeanConfig.springData.resourcePath + File.separator + ScriptStaticData.MAIN_IMG_PNG_NAME)
        if (imgFile.exists()) {
            return
        }
        val resourceDir = imgFile.parentFile
        if (!resourceDir.exists() && !resourceDir.mkdirs()) {
            log.warn { "资源文件夹创建失败" }
        }
        try {
            BufferedInputStream(
                Objects.requireNonNull(
                    ResourceInitializer::class.java.getResourceAsStream(
                        "/fxml/img/" + ScriptStaticData.MAIN_IMG_PNG_NAME
                    )
                )
            ).use { bufferedInputStream ->
                BufferedOutputStream(FileOutputStream(imgFile)).use { bufferedOutputStream ->
                    val bytes = ByteArray(1024)
                    var size: Int
                    while ((bufferedInputStream.read(bytes).also { size = it }) > 0) {
                        bufferedOutputStream.write(bytes, 0, size)
                    }
                    log.info { "资源文件复制成功" }
                }
            }
        } catch (e: IOException) {
            log.error(e) { "资源文件复制异常" }
        }
    }

}
