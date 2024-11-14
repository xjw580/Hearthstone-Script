package club.xiaojiawei.hsscript.bean.single.repository

import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.data.PROJECT_NAME

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:20
 */
object GiteeRepository : AbstractRepository() {

    override fun getLatestRelease(isPreview: Boolean): Release? {
        var latestRelease: Release? = null
        if (isPreview) {
            latestRelease = restTemplate.getForObject(
                getLatestReleaseURL(true),
                Release::class.java
            )
        } else {
            val releases: Array<Release>? = restTemplate.getForObject(
                getLatestReleaseURL(false),
                Array<Release>::class.java
            )
            releases?.let {
                for (i in it.indices.reversed()) {
                    val release: Release = it[i]
                    if (!release.isPreRelease) {
                        if (latestRelease == null || release > latestRelease) {
                            latestRelease = release
                        }
                    }
                }
            }
        }
        return latestRelease
    }

    override fun getLatestReleaseURL(isPreview: Boolean): String {
        return if (isPreview) {
            String.format(
                "https://%s/api/v5/repos/%s/%s/releases/latest",
                getDomain(),
                getUserName(),
                PROJECT_NAME
            )
        } else {
            String.format(
                "https://%s/api/v5/repos/%s/%s/releases",
                getDomain(), getUserName(),
                PROJECT_NAME
            )
        }
    }

    override fun getDomain(): String {
        return "gitee.com"
    }

    override fun getUserName(): String {
        return "zergqueen"
    }

}
