package club.xiaojiawei.hsscript.listener.log

import club.xiaojiawei.config.LISTEN_LOG_THREAD_POOL
import club.xiaojiawei.config.log
import club.xiaojiawei.hsscript.interfaces.closer.ScheduledCloser
import club.xiaojiawei.hsscript.listener.WorkListener
import club.xiaojiawei.hsscript.status.PauseStatus
import club.xiaojiawei.hsscript.status.TaskManager
import club.xiaojiawei.util.isFalse
import java.io.File
import java.io.FileWriter
import java.io.RandomAccessFile
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 在 [LogListenerConfig.logListener] 添加新的LogListener
 * @author 肖嘉威
 * @date 2023/9/20 16:54
 */
abstract class AbstractLogListener(
    protected var logFileName: String,
    protected var listenInitialDelay: Long,
    protected var listenPeriod: Long,
    protected var listenTimeUnit: TimeUnit
): ScheduledCloser {

    init {
        TaskManager.addTask(this)
    }

    protected var innerLogFile: RandomAccessFile? = null

    val logFile: RandomAccessFile?
        get() = innerLogFile


    private var logScheduledFuture: ScheduledFuture<*>? = null

    private var nextLogListener: AbstractLogListener? = null

    fun setNextLogListener(nextLogListener: AbstractLogListener): AbstractLogListener {
        return nextLogListener.also { this.nextLogListener = it }
    }

    protected abstract fun dealOldLog()

    protected abstract fun dealNewLog()

    private fun listenNextListener() {
        nextLogListener?.listen()
    }

    @Synchronized
    fun listen() {
        logScheduledFuture?.let {
            if (!it.isDone) {
                log.warn { logFileName + "正在被监听，无法再次被监听" }
                listenNextListener()
                return
            }
        }
        closeLogFile()
        val logFile = createLogFile()
        logFile ?: let {
            log.error { logFileName + "初始化失败" }
            return
        }
        log.info { "开始监听日志$logFileName" }
        try {
            this.innerLogFile = RandomAccessFile(logFile, "r")
            dealOldLog()
        } catch (e: Exception) {
            log.error(e) {}
            return
        }
        logScheduledFuture = LISTEN_LOG_THREAD_POOL.scheduleAtFixedRate({
            if (PauseStatus.isPause || !WorkListener.working) {
                close()
            } else {
                try {
                    dealNewLog()
                } catch (e: InterruptedException) {
                    log.warn(e) { logFileName + "监听器sleep中断" }
                } catch (e: Exception) {
                    log.error(e) { logFileName + "监听器发生错误" }
                }
            }
        }, listenInitialDelay, listenPeriod, listenTimeUnit)
        listenNextListener()
    }

    private fun createLogFile(): File? {
        return logPath?.let {
            val logFile = it.resolve(logFileName)
            logFile.exists().isFalse {
                FileWriter(logFile).use { fileWriter ->
                    fileWriter.write("")
                }
            }
            logFile
        }
    }

    private fun closeLogFile() {
        innerLogFile?.let {
            it.close()
            innerLogFile = null
        }
    }

    private fun closeLogListener() {
        logScheduledFuture?.let {
            it.isDone.isFalse {
                it.cancel(true)
            }
        }
    }

    override fun close() {
        closeLogListener()
    }

    companion object{
        var logPath: File? = null
    }

}
