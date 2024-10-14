package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.bean.LogRunnable
import club.xiaojiawei.config.EXTRA_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.PROGRAM_ARGS
import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.bean.single.repository.AbstractRepository
import club.xiaojiawei.hsscript.bean.single.repository.GithubRepository
import club.xiaojiawei.hsscript.consts.MAIN_PATH
import club.xiaojiawei.hsscript.consts.TEMP_VERSION_PATH
import club.xiaojiawei.hsscript.enums.ConfigEnum
import club.xiaojiawei.hsscript.enums.VersionTypeEnum
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.utils.ConfigUtil
import club.xiaojiawei.hsscript.utils.FileUtil
import club.xiaojiawei.hsscript.utils.SystemUtil
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


/**
 * 脚本版本监听器，定时查看是否需要更新
 * @author 肖嘉威
 * @date 2023/9/17 21:49
 */
object VersionListener {

    const val VERSION_FILE_FLAG_NAME = "downloaded.flag"

    const val UPDATE_PROGRAM_NAME: String = "update.exe"

    private var checkVersionTask: ScheduledFuture<*>? = null

    val currentRelease: Release = Release()

    var latestRelease: Release? = null

    private val repositoryList: List<AbstractRepository> = listOf()

    /**
     * 能否升级
     */
    private val canUpdateProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)

    val canUpdate: Boolean
        get() = canUpdateProperty.get()

    fun canUpdateReadOnlyProperty(): ReadOnlyBooleanProperty = canUpdateProperty.readOnlyProperty

    /**
     * 正在升级中
     */
    private val updatingProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)

    val updating: Boolean
        get() = updatingProperty.get()

    fun updatingReadOnlyProperty(): ReadOnlyBooleanProperty = updatingProperty.readOnlyProperty

    /**
     * 正在下载中
     */
    private val downloadingProperty: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)

    val downloading: Boolean
        get() = downloadingProperty.get()

    fun downloadingReadOnlyProperty(): ReadOnlyBooleanProperty = downloadingProperty.readOnlyProperty

    fun launch() {
        if (checkVersionTask != null) return
//        打完包后启动this.getClass().getPackage().getImplementationVersion()能读到正确的值
        var version =
            VersionListener::class.java.getPackage().implementationVersion?.let { if (it.isBlank()) VersionTypeEnum.UNKNOWN.name else it }
                ?: VersionTypeEnum.UNKNOWN.name
        currentRelease.tagName = version
        currentRelease.isPreRelease = VersionTypeEnum.getEnum(currentRelease).isPreview

        checkVersionTask = EXTRA_THREAD_POOL.scheduleAtFixedRate(LogRunnable {
            checkVersion()
        }, 500, 1000 * 60 * 60 * 12, TimeUnit.MILLISECONDS)
        log.info { "版本更新检测已启动" }
    }

    /**
     * 下载最新版本
     */
    fun downloadLatestRelease(force: Boolean, progress: DoubleProperty, callback: Consumer<String?>?) {
        latestRelease?.let {
            return downloadRelease(it, force, progress, callback)
        } ?: let {
            EXTRA_THREAD_POOL.submit {
                callback?.accept(null)
            }
        }
    }

    /**
     * 更新版本
     */
    fun execUpdate(versionPath: String) {
        if (updatingProperty.get()) return

        synchronized(updatingProperty) {
            try {
                if (updatingProperty.get()) return
                updatingProperty.set(true)

                val rootPath = MAIN_PATH
                val updateProgramPath = Path.of(rootPath, UPDATE_PROGRAM_NAME).toString()
                Files.copy(
                    Path.of(versionPath, UPDATE_PROGRAM_NAME),
                    Path.of(rootPath, UPDATE_PROGRAM_NAME),
                    StandardCopyOption.REPLACE_EXISTING
                )
                Runtime.getRuntime().exec(
                    String.format(
                        "%s --target='%s' --source='%s' --pause='%s' --pid='%s'",
                        updateProgramPath,
                        rootPath,
                        versionPath,
                        PauseStatus.isPause,
                        ProcessHandle.current().pid()
                    )
                )
            } catch (e: RuntimeException) {
                log.error(e) { "执行版本更新失败" }
            } finally {
                updatingProperty.set(false)
            }
        }
    }

    /**
     * 下载指定版本
     */
    fun downloadRelease(release: Release, force: Boolean, progress: DoubleProperty, callback: Consumer<String?>?) {
        if (downloadingProperty.get()) return

        synchronized(downloadingProperty) {
            if (downloadingProperty.get()) return
            downloadingProperty.set(true)

            EXTRA_THREAD_POOL.submit {
                var path: String? = null
                try {
                    val versionDir: File = Path.of(TEMP_VERSION_PATH, release.tagName, VERSION_FILE_FLAG_NAME).toFile()
                    if (!force && versionDir.exists()) {
                        path = versionDir.parentFile.absolutePath
                    } else {
                        for (repository in repositoryList) {
                            if ((downloadRelease(
                                    release,
                                    repository.getReleaseDownloadURL(release),
                                    progress
                                ).also {
                                    path = it
                                }) == null
                            ) {
                                log.info { "更换下载源重新下载" }
                            } else {
                                break
                            }
                        }
                    }
                } finally {
                    downloadingProperty.set(false)
                    callback?.accept(path)
                }
            }
        }

    }

    /**
     * 检查最新版本
     */
    fun checkVersion() {
//        在idea中启动时就不要检查更新了
        if (Objects.requireNonNull<URL>(javaClass.getResource(""))
                .protocol != "jar" && !PROGRAM_ARGS.contains("--update")
        ) {
            return
        }
        synchronized(canUpdateProperty){
            val updateDev = ConfigUtil.getBoolean(ConfigEnum.UPDATE_DEV)
            log.info { "开始检查更新，更新开发版：$updateDev" }
            for (repository in repositoryList) {
                try {
                    latestRelease = repository.getLatestRelease(updateDev)
                } catch (e: Exception) {
                    log.error(e) { "${repository.getDomain()}检查最新版异常" }
                    continue
                }
                break
            }
            latestRelease?.let {
                if (currentRelease < it && VersionTypeEnum.getEnum(it) !== VersionTypeEnum.TEST) {
                    canUpdateProperty.set(true)
                    log.info { "有更新可用😊，当前版本：${currentRelease.tagName}, 最新版本：${it.tagName}" }
                    SystemUtil.notice(
                        String.format("发现新版本：%s", it.tagName),
                        String.format("更新日志：\n%s", it.body),
                        "查看详情",
                        GithubRepository.getReleasePageURL(it)
                    )
                } else {
                    canUpdateProperty.set(false)
                    log.info { "已是最新，当前版本：${currentRelease.tagName}, 最新版本：${it.tagName}" }
                }
            } ?: {
                canUpdateProperty.set(false)
                log.warn { "没有任何最新版本" }
            }
        }
    }

    private fun downloadRelease(release: Release, url: String, progress: DoubleProperty): String? {
        var rootPath: Path?
        try {
            URI(url)
                .toURL()
                .openConnection()
                .getInputStream().use { inputStream ->
                    ZipInputStream(inputStream).use { zipInputStream ->
                        val startContent = "开始下载<" + release.tagName + ">"
                        log.info { startContent }
                        progress.set(0.0)
                        var nextEntry: ZipEntry?
                        val count = 87.0
                        val step = 0.95 / count
                        rootPath = Path.of(TEMP_VERSION_PATH, release.tagName)
                        val rootFile = rootPath.toFile()
                        if (!FileUtil.createDirectory(rootFile)) {
                            log.error { rootFile.absolutePath + "创建失败" }
                            return null
                        }
                        rootFile.listFiles()?.forEach { file ->
                            file.delete()
                        }
                        while ((zipInputStream.getNextEntry().also { nextEntry = it }) != null) {
                            val entryFile = rootPath.resolve(nextEntry!!.getName()).toFile()
                            if (nextEntry.isDirectory) {
                                if (entryFile.mkdirs()) {
                                    log.info { "created_dir：" + entryFile.path }
                                }
                            } else {
                                val parentFile = entryFile.getParentFile()
                                if (parentFile.exists() || parentFile.mkdirs()) {
                                    BufferedOutputStream(FileOutputStream(entryFile)).use { bufferedOutputStream ->
                                        var l: Int
                                        val bytes = ByteArray(8192)
                                        while ((zipInputStream.read(bytes).also { l = it }) != -1) {
                                            bufferedOutputStream.write(bytes, 0, l)
                                        }
                                    }
                                    log.info { "downloaded_file：" + entryFile.path }
                                }
                            }
                            progress.set(step + progress.get())
                        }
                        writeVersionFileCompleteFlag(rootPath.toString())
                        progress.set(1.0)
                        val endContent = "<" + release.tagName + ">下载完毕"
                        log.info { endContent }
                    }
                }
        } catch (e: RuntimeException) {
            val errorContent = "<" + release.tagName + ">下载失败"
            log.error(e) { "$errorContent,$url" }
            return null
        }
        return rootPath.toString()
    }

    private fun writeVersionFileCompleteFlag(path: String): Boolean {
        try {
            return Path.of(path, VERSION_FILE_FLAG_NAME).toFile().createNewFile()
        } catch (e: IOException) {
            log.error(e) { "" }
        }
        return false
    }

}
