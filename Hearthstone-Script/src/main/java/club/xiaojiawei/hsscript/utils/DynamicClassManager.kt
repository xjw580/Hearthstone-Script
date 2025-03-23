package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.bean.JavaClassLoader
import club.xiaojiawei.hsscript.bean.KotlinCompilerHelper
import java.io.File

/**
 * 动态类加载管理器
 * @author 肖嘉威
 * @date 2025/3/18 16:04
 */
class DynamicClassManager {

    private val classLoader = JavaClassLoader(Thread.currentThread().contextClassLoader)

    private val kotlinCompilerHelper by lazy { KotlinCompilerHelper() }

    /**
     * 加载外部Java文件
     *
     * @param javaFilePath Java文件路径
     * @return 加载的类对象
     */
    fun loadJavaFile(javaFilePath: String): Class<*> {
        val javaFile = File(javaFilePath)
        if (!javaFile.exists()) {
            throw IllegalArgumentException("Java文件不存在: $javaFilePath")
        }

        return classLoader.loadExternalJavaClass(javaFile)
    }

    /**
     * 动态加载 Kotlin 文件
     *
     * @param filePath Kotlin 文件路径
     * @return 加载的类
     */
    fun loadKotlinFile(filePath: String): Class<*> {
        val kotlinFile = File(filePath)

        if (!kotlinFile.exists() || !kotlinFile.name.endsWith(".kt")) {
            throw IllegalArgumentException("无效的 Kotlin 文件: $filePath")
        }

        // 编译输出目录
        val outputDir = File(kotlinFile.parentFile, "out")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        // 编译 Kotlin 文件
        val compileSuccess = kotlinCompilerHelper.compileKotlinFile(kotlinFile, outputDir)

        if (!compileSuccess) {
            throw RuntimeException("Kotlin 文件编译失败: $filePath")
        }

        println("Kotlin 文件编译成功！")

        // 动态加载类
        val className = kotlinFile.nameWithoutExtension
        return kotlinCompilerHelper.loadClass(outputDir, className)
    }

    /**
     * 实例化加载的类
     *
     * @param loadedClass 已加载的类
     * @return 类的实例
     */
    fun instantiate(loadedClass: Class<*>): Any {
        return loadedClass.getDeclaredConstructor().newInstance()
    }

    /**
     * 添加额外的类路径
     */
    fun addClassPath(path: String) {
        classLoader.addClassPath(File(path))
    }
}
