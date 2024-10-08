package club.xiaojiawei.hsscript.listener

import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.bean.single.repository.GiteeRepository
import club.xiaojiawei.hsscript.bean.single.repository.GithubRepository
import club.xiaojiawei.hsscript.utils.SystemUtil
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.SimpleBooleanProperty
import org.springframework.web.client.RestTemplate
import java.io.File
import java.net.URL
import java.util.*


/**
 * 脚本版本监听器，定时查看是否需要更新
 * @author 肖嘉威
 * @date 2023/9/17 21:49
 */
object VersionListener {

    private val restTemplate:RestTemplate = RestTemplate()

    fun init() {
        /*
            用idea启动时springData.getVersion()能读到正确的值
            打完包后启动this.getClass().getPackage().getImplementationVersion()能读到正确的值
        */
        var version = VersionListener::class.java.getPackage().implementationVersion
        currentRelease = Release()
        if (version == null) {
            currentRelease.setTagName(springData.version.also { version = it })
        } else {
            currentRelease.setTagName(version)
        }
        if (!version!!.endsWith("GA")) {
            currentRelease.setPreRelease(true)
        }
    }

    private val UPDATING_PROPERTY: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)

    var updating: Boolean
        get() = UPDATING_PROPERTY.get()
        set(value) = UPDATING_PROPERTY.set(value)

    fun updatingProperty(): ReadOnlyBooleanProperty = UPDATING_PROPERTY.readOnlyProperty

    fun downloadRelease(release: Release, force: Boolean, callback: Consumer<String?>?) {
        if (UPDATING_PROPERTY.get()) {
            return
        }
        UPDATING_PROPERTY.set(true)
        ThreadPoolConfigKt.getEXTRA_THREAD_POOL().submit {
            var path: String? = null
            try {
                val file: File = Path.of(TEMP_VERSION_PATH, release.getTagName(), VERSION_FILE_FLAG_NAME).toFile()
                if (!force && file.exists()) {
                    path = file.parentFile.absolutePath
                } else if ((downloadRelease(release, GiteeRepository.getInstance().getReleaseURL(release)).also {
                        path = it
                    }) == null) {
                    Platform.runLater { staticNotificationManger.showInfo("更换下载源重新下载", 3) }
                    path = downloadRelease(release, GithubRepository.getInstance().getReleaseURL(release))
                }
            } finally {
                UPDATING_PROPERTY.set(false)
                if (callback != null) {
                    callback.accept(path)
                }
            }
        }
    }

    @Scheduled(initialDelay = 500, fixedDelay = 1000 * 60 * 60 * 12)
    fun checkVersion() {
//        在idea中启动时就不要检查更新了
        if (Objects.requireNonNull<URL>(javaClass.getResource(""))
                .getProtocol() != "jar" && !ScriptApplication.getArgs().contains("--update")
        ) {
            return
        }
        val updateDev = scriptConfiguration!!.getProperty(ConfigEnum.UPDATE_DEV.getKey()) == "true"
        log.info("开始从Gitee检查更新")
        log.info("更新dev：$updateDev")
        try {
            if (updateDev) {
                latestRelease = restTemplate.getForObject(
                    String.format(
                        "https://gitee.com/api/v5/repos/zergqueen/%s/releases/latest",
                        ScriptStaticData.PROJECT_NAME
                    ),
                    Release::class.java
                )
            } else {
                val releases: Array<Release> = restTemplate.getForObject(
                    String.format(
                        "https://gitee.com/api/v5/repos/zergqueen/%s/releases",
                        ScriptStaticData.PROJECT_NAME
                    ),
                    Array<Release>::class.java
                )
                if (releases != null) {
                    for (i in releases.indices.reversed()) {
                        val release: Release = releases[i]
                        if (!release.isPreRelease()) {
                            if (latestRelease == null || release.compareTo(latestRelease) > 0) {
                                latestRelease = release
                            }
                        }
                    }
                }
            }
        } catch (e: RuntimeException) {
            log.warn("从Gitee检查更新异常", e)
            log.info("开始从Github检查更新")
            try {
                if (updateDev) {
                    val releases: Array<Release> = restTemplate.getForObject(
                        String.format("https://api.github.com/repos/xjw580/%s/releases", ScriptStaticData.PROJECT_NAME),
                        Array<Release>::class.java
                    )
                    if (releases != null && releases.size > 0) {
                        latestRelease = releases[0]
                    }
                } else {
                    latestRelease = restTemplate.getForObject(
                        String.format(
                            "https://api.github.com/repos/xjw580/%s/releases/latest",
                            ScriptStaticData.PROJECT_NAME
                        ),
                        Release::class.java
                    )
                }
            } catch (e2: RuntimeException) {
                log.warn("从Github检查更新异常", e2)
            }
        }
        if (latestRelease != null) {
            if (currentRelease.compareTo(latestRelease) < 0) {
                canUpdate.set(true)
                log.info("有更新可用😊，当前版本：" + currentRelease + ", 最新版本：" + latestRelease)
                SystemUtil.notice(
                    java.lang.String.format("发现新版本：%s", getLatestRelease().getTagName()),
                    java.lang.String.format("更新日志：\n%s", getLatestRelease().getBody()),
                    "查看详情",
                    java.lang.String.format(
                        "https://gitee.com/zergqueen/%s/releases/tag/%s",
                        PROJECT_NAME,
                        getLatestRelease().getTagName()
                    )
                )
            } else {
                canUpdate.set(false)
                log.info("已是最新，当前版本：" + currentRelease + ", 最新版本：" + latestRelease)
            }
        } else {
            canUpdate.set(false)
            log.warn("没有任何最新版本")
        }
    }

    @Getter
    private var latestRelease: Release? = null

    @Getter
    private var currentRelease: Release? = null
    private val canUpdate: ReadOnlyBooleanWrapper = ReadOnlyBooleanWrapper(false)

    @JvmStatic
    fun isCanUpdate(): Boolean {
        return canUpdate.get()
    }

    @JvmStatic
    fun canUpdateReadOnlyProperty(): ReadOnlyBooleanProperty {
        return canUpdate.getReadOnlyProperty()
    }
}
