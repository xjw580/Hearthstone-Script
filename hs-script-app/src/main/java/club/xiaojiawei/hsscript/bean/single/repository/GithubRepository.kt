package club.xiaojiawei.hsscript.bean.single.repository

import club.xiaojiawei.hsscript.bean.Release
import club.xiaojiawei.hsscript.consts.PROJECT_NAME
import club.xiaojiawei.hsscript.utils.NetUtil

/**
 * @author 肖嘉威
 * @date 2024/5/23 19:19
 */
object GithubRepository : AbstractRepository() {

    override fun getLatestRelease(isPreview: Boolean): Release? {
        var latestRelease: Release? = null
        if (isPreview) {
            val releases: Array<Release>? = NetUtil.buildRestTemplate().getForObject(
                getLatestReleaseURL(true),
                Array<Release>::class.java
            )
            if (!releases.isNullOrEmpty()) {
                latestRelease = releases[0]
            }
        } else {
            latestRelease = NetUtil.buildRestTemplate().getForObject(
                getLatestReleaseURL(false),
                Release::class.java
            )
        }
        return latestRelease
    }

    override fun getLatestReleaseURL(isPreview: Boolean): String {
        return if (isPreview) {
            String.format(
                "https://api.%s/repos/%s/%s/releases",
                getDomain(),
                getUserName(),
                PROJECT_NAME
            )
        } else {
            String.format(
                "https://api.%s/repos/%s/%s/releases/latest",
                getDomain(), getUserName(),
                PROJECT_NAME
            )
        }
    }

    override fun getDomain(): String {
        return "github.com"
    }

    override fun getUserName(): String {
        return "xjw580"
    }

}
