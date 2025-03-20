package club.xiaojiawei.hsscript.utils

import club.xiaojiawei.hsscript.dll.CSystemDll
import club.xiaojiawei.hsscript.enums.ConfigEnum
import com.sun.jna.Memory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI
import java.net.URLConnection


/**
 * 网络工具
 * @author 肖嘉威
 * @date 2025/2/9 21:23
 */
object NetUtil {

    fun getSystemProxy(): Proxy? {
        val proxyPointer = Memory(256)
        CSystemDll.INSTANCE.getWindowsProxy(proxyPointer, proxyPointer.size().toInt())
        val proxyUrl = proxyPointer.getString(0)
        val split = proxyUrl.split(":")
        return if (split.size == 2) {
            Proxy(Proxy.Type.HTTP, InetSocketAddress(split[0], split[1].toInt()))
        } else {
            null
        }
    }

    fun buildConnection(url: String, useProxy: Boolean = ConfigUtil.getBoolean(ConfigEnum.USE_PROXY)): URLConnection {
        val toURL = URI(url).toURL()

        val connection = if (useProxy) {
            getSystemProxy()?.let {
                toURL.openConnection(it)
            } ?: toURL.openConnection()
        } else {
            toURL.openConnection()
        }
        return connection.apply {
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"
            )
        }
    }

    fun buildRestTemplate(useProxy: Boolean = ConfigUtil.getBoolean(ConfigEnum.USE_PROXY)): RestTemplate {
        if (!useProxy) RestTemplate()
        return getSystemProxy()?.let {
            val factory = SimpleClientHttpRequestFactory()
            val proxy = Proxy(Proxy.Type.HTTP, it.address())
            factory.setProxy(proxy)
            RestTemplate(factory)
        } ?: RestTemplate()
    }


}