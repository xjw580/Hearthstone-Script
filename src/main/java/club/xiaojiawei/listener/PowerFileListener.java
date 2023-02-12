package club.xiaojiawei.listener;

import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.run.Core;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.utils.SystemUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;

import static club.xiaojiawei.constant.SystemConst.GAME_LOG_PATH_SUFFIX;
import static club.xiaojiawei.constant.SystemConst.PROPERTIES;
import static club.xiaojiawei.enums.StepEnum.FINAL_GAMEOVER;
import static club.xiaojiawei.enums.StepEnum.FINAL_WRAPUP;
import static club.xiaojiawei.enums.TagEnum.MULLIGAN_STATE;
import static club.xiaojiawei.enums.WarPhaseEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:27
 */
@Component
@Slf4j
public class PowerFileListener {

    private static File file;
    private static File backFile;
    private static RandomAccessFile accessFile;
    private static String suffix;
    public static RandomAccessFile getAccessFile() {
        return accessFile;
    }

    public static String getSuffix() {
        return suffix;
    }

    @SneakyThrows
    @Value("${game.log.path.power}")
    public void setFile(String suffix){
        PowerFileListener.suffix = suffix;
    }

    public void init(){
        reset();
    }

    @SneakyThrows
    public static void reset(){
        try {
            if (accessFile != null){
                accessFile.close();
                log.info("文件流已关闭");
            }
            File gamepath = new File(PROPERTIES.getProperty("gamepath") + GAME_LOG_PATH_SUFFIX);
            if (!gamepath.exists() && !gamepath.mkdirs()){
                log.error("游戏日志目录不存在且创建失败，path：" + gamepath);
            }
            if (file == null){
                file = new File(PROPERTIES.getProperty("gamepath") + suffix);
            }
            if (backFile == null){
                backFile = new File(PROPERTIES.getProperty("gamepath") + GAME_LOG_PATH_SUFFIX + "/Power.log_back");
            }
            if (file.renameTo(backFile)){
                if (!backFile.delete()){
                    log.warn(backFile.getName() + "文件删除失败");
                }
            }else {
                log.warn(file.getName() + "文件重命名失败，因为游戏正在运行");
            }
            if (!file.exists()){
                try(FileWriter fileWriter = new FileWriter(file)){
                    fileWriter.write("");
                    log.info(file.getName() + "文件创建成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            accessFile = new RandomAccessFile(file, "r");
            accessFile.seek(accessFile.length());
            log.info(file.getName() + "读取正常，指针恢复至：" + accessFile.getFilePointer());
        } catch (FileNotFoundException e) {
            log.error("未找到" + file.getName() +  "文件", e);
        }
    }

    private static long mark = System.currentTimeMillis();

    public static void setMark(long mark) {
        PowerFileListener.mark = mark;
    }

    public static final long MAX_ERROR_TIME = 5 * 60 * 1000L;

    /**
     * Power.log文件超过MAX_ERROR_TIME秒没有新数据则判断为出现错误，需要重启游戏
     */
    @SneakyThrows
    @Scheduled(fixedRate=10_000, initialDelay = 4000)
    public void listenError(){
        if (!Core.getPause() && System.currentTimeMillis() - mark > MAX_ERROR_TIME){
            SystemUtil.shutdownGame();
        }
    }

    private volatile boolean reading;
    @SneakyThrows
    @Scheduled(fixedRate=1000, initialDelay = 3000)
    public void listenScreenStatus(){
        if (!reading){
            try {
                readPowerLog();
            } catch (IOException e) {
                log.error(file.getName() + "文件读取失败", e);
            }
        }
    }

    private void readPowerLog() throws IOException {
        reading = true;
        try {
            String l;
            while (!Core.getPause() && !AbstractPhaseStrategy.dealing){
                if ((l = accessFile.readLine()) == null){
                    if (accessFile.getFilePointer() > accessFile.length()){
                        accessFile.seek(0);
                    }
                }else if (isRelevance(l)){
                    setMark(System.currentTimeMillis());
                    if (War.getCurrentPhase() == null && l.contains("CREATE_GAME")){
                        FILL_DECK_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                    }else if (War.getCurrentPhase() == FILL_DECK_PHASE && l.contains("BLOCK_START")){
                        DRAWN_INIT_CARD_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                    }else if (War.getCurrentPhase() == DRAWN_INIT_CARD_PHASE && l.contains("TAG_CHANGE")){
                        TagChangeEntity tagChangeEntity = PowerLogUtil.parseTagChange(l);
                        if (tagChangeEntity.getTag() == MULLIGAN_STATE && Objects.equals(tagChangeEntity.getValue(), "INPUT")){
                            REPLACE_CARD_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                        }
                    }else if (War.getCurrentPhase() == REPLACE_CARD_PHASE && l.contains("BLOCK_END")){
                        SPECIAL_EFFECT_TRIGGER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                    }else if (War.getCurrentPhase() == SPECIAL_EFFECT_TRIGGER_PHASE && l.contains("MAIN_READY")){
                        GAME_TURN_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                    }else if (War.getCurrentPhase() == GAME_OVER_PHASE
                            || (War.getCurrentPhase() != null
                                    && (l.contains(FINAL_GAMEOVER.getValue()) || l.contains(FINAL_WRAPUP.getValue()))
                    )){
                        GAME_OVER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                    }
                }
            }
        }finally {
            reading = false;
        }
    }

    public static boolean isRelevance(String l){
        return l.contains("PowerTaskList.DebugPrintPower()");
    }

}
