package club.xiaojiawei.hsscript.bean.single.repository

import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.consts.PROJECT_NAME
import club.xiaojiawei.hsscript.consts.SCRIPT_NAME

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:18
 */
abstract class AbstractRepository {

    fun getReleaseDownloadURL(isPreview: Boolean = false): String?{
        val latestRelease = getLatestRelease(isPreview)
        latestRelease?.let {
            return getReleaseDownloadURL(latestRelease)
        }?:let {
            return null
        }
    }

    open fun getReleaseDownloadURL(release: Release): String{
        return String.format(
            "https://%s/%s/%s/releases/download/%s/%s_%s.zip",
            getDomain(),
            getUserName(),
            PROJECT_NAME,
            release.tagName,
            SCRIPT_NAME,
            release.tagName
        )
    }

    open fun getReleasePageURL(release: Release): String{
        return String.format(
            "https://%s/%s/%s/releases/tag/%s",
            GiteeRepository.getDomain(),
            GiteeRepository.getUserName(),
            PROJECT_NAME,
            release.tagName
        )
    }

    abstract fun getLatestRelease(isPreview: Boolean = false): Release?

    abstract fun getLatestReleaseURL(isPreview: Boolean = false): String

    abstract fun getDomain(): String?

    abstract fun getUserName(): String?
}
