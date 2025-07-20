package club.xiaojiawei.hsscript.bean

/**
 * @author 肖嘉威
 * @date 2025/3/18 16:35
 */
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.Services
import java.io.File
import java.net.URLClassLoader
import org.jetbrains.kotlin.config.JVMConfigurationKeys as KotlinJVMConfigurationKeys

class KotlinCompilerHelper {

    /**
     * 使用 Kotlin Compiler API 编译外部 .kt 文件
     *
     * @param kotlinFile 要编译的 Kotlin 文件
     * @param outputDir 目标 class 文件路径
     * @return 是否编译成功
     * @throws IllegalArgumentException 如果文件不存在或不可读
     */
    fun compileKotlinFile(kotlinFile: File, outputDir: File): Boolean {
        require(kotlinFile.exists() && kotlinFile.canRead()) {
            "Kotlin 文件不存在或不可读: ${kotlinFile.absolutePath}"
        }
        require(kotlinFile.extension == "kt") {
            "文件必须是 .kt 文件: ${kotlinFile.absolutePath}"
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val messageCollector = object : MessageCollector {
            private var hasErrors = false

            override fun clear() {}

            override fun hasErrors(): Boolean = hasErrors

            override fun report(
                severity: CompilerMessageSeverity,
                message: String,
                location: CompilerMessageSourceLocation?
            ) {
                if (severity == CompilerMessageSeverity.ERROR) {
                    hasErrors = true
                    println("Kotlin 编译错误[$severity]: $message at $location")
                } else {
                    println("Kotlin 编译信息[$severity]: $message at $location")
                }
            }
        }

        val configuration = CompilerConfiguration().apply {
            put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
            put(KotlinJVMConfigurationKeys.OUTPUT_DIRECTORY, outputDir)

            val classpath = System.getProperty("java.class.path")
                .split(File.pathSeparator)
                .map { File(it) }
                .filter { it.exists() }
            addJvmClasspathRoots(classpath)

            put(CommonConfigurationKeys.MODULE_NAME, kotlinFile.nameWithoutExtension)
        }

        // 创建编译器参数
        val arguments = K2JVMCompilerArguments().apply {
            freeArgs = listOf(kotlinFile.absolutePath)
            destination = outputDir.absolutePath
            classpath = System.getProperty("java.class.path")
        }

        val compiler = K2JVMCompiler()
        val exitCode = compiler.exec(messageCollector, Services.EMPTY, arguments)

        return exitCode == org.jetbrains.kotlin.cli.common.ExitCode.OK && !messageCollector.hasErrors()
    }

    /**
     * 加载编译后的 class 文件
     *
     * @param outputDir 目标 class 文件路径
     * @param className 类名
     * @return 动态加载的类对象
     * @throws ClassNotFoundException 如果类无法加载
     * @throws IllegalArgumentException 如果输出目录无效
     */
    fun loadClass(outputDir: File, className: String): Class<*> {
        require(outputDir.exists() && outputDir.isDirectory) {
            "输出目录无效: ${outputDir.absolutePath}"
        }
        require(className.isNotBlank()) { "类名不能为空" }

        return URLClassLoader(arrayOf(outputDir.toURI().toURL()),
            Thread.currentThread().contextClassLoader).use { classLoader ->
            classLoader.loadClass(className)
        }
    }
}