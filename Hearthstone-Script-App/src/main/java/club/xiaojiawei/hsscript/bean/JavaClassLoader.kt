package club.xiaojiawei.hsscript.bean

/**
 * 动态类加载器，用于在运行时加载和编译外部Java文件
 * @author 肖嘉威
 * @date 2025/3/18 15:56
 */
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.ToolProvider

class JavaClassLoader(parent: ClassLoader) : URLClassLoader(arrayOf<URL>(), parent) {

    /**
     * 加载并编译外部Java文件
     *
     * @param javaFile 要加载的Java文件
     * @return 加载的类对象
     */
    fun loadExternalJavaClass(javaFile: File): Class<*> {
        // 获取类名
        val className = javaFile.nameWithoutExtension

        // 获取包名（如果有）
        val packageName = extractPackageName(javaFile)

        // 编译Java文件
        compileJavaFile(javaFile)

        // 读取编译后的类文件
        val classFilePath = javaFile.absolutePath.replace(".java", ".class")
        val classFile = File(classFilePath)

        if (!classFile.exists()) {
            throw RuntimeException("找不到编译后的class文件: $classFilePath")
        }

        // 读取类字节码
        val bytes = Files.readAllBytes(classFile.toPath())

        // 构建完整类名
        val fullClassName = if (packageName.isEmpty()) className else "$packageName.$className"

        // 定义类
        return defineClass(fullClassName, bytes, 0, bytes.size)
    }

    /**
     * 从Java文件中提取包名
     */
    private fun extractPackageName(javaFile: File): String {
        val content = javaFile.readText()
        val packageRegex = """package\s+([\w.]+);""".toRegex()
        val matchResult = packageRegex.find(content)
        return matchResult?.groupValues?.get(1) ?: ""
    }

    /**
     * 编译Java文件
     */
    private fun compileJavaFile(javaFile: File) {
        val compiler = ToolProvider.getSystemJavaCompiler()
            ?: throw RuntimeException("无法获取系统Java编译器，请确保在JDK环境下运行")

        // 获取系统类路径
        val classPath = System.getProperty("java.class.path")

        // 捕获编译错误输出
        val errorOutput = ByteArrayOutputStream()

        // 构建编译选项和文件路径作为字符串数组
        val arguments = arrayOf(
            "-classpath",
            classPath,
            javaFile.absolutePath
        )

        // 执行编译，直接传递字符串数组
        val compileResult = compiler.run(
            null,
            null,
            errorOutput,
            *arguments  // 使用展开操作符将数组转换为可变参数
        )

        if (compileResult != 0) {
            throw RuntimeException("编译失败: ${javaFile.absolutePath}\n${errorOutput}")
        }
    }

    /**
     * 添加类路径
     */
    fun addClassPath(path: File) {
        if (path.exists()) {
            addURL(path.toURI().toURL())
        }
    }

}