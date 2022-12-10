package club.xiaojiawei.hearthstone.listen;

import club.xiaojiawei.hearthstone.entity.TagChangeEntity;
import club.xiaojiawei.hearthstone.pool.MyThreadPool;
import club.xiaojiawei.hearthstone.run.Core;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import club.xiaojiawei.hearthstone.utils.SystemUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Objects;

import static club.xiaojiawei.hearthstone.constant.SystemConst.PROPERTIES;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static club.xiaojiawei.hearthstone.enums.StepEnum.FINAL_GAMEOVER;
import static club.xiaojiawei.hearthstone.enums.StepEnum.FINAL_WRAPUP;
import static club.xiaojiawei.hearthstone.enums.TagEnum.MULLIGAN_STATE;
import static club.xiaojiawei.hearthstone.enums.WarPhaseEnum.*;

/**
 * @author 肖嘉威
 * @date 2022/11/26 17:27
 */
@Component
@Slf4j
public class PowerFileListen {

    private static File file;
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
        PowerFileListen.suffix = suffix;
        reset();
    }

    @SneakyThrows
    public static void reset(){
        try {
            File gamepath = new File(PROPERTIES.getProperty("gamepath") + "/Logs");
            if (!gamepath.exists()){
                gamepath.mkdir();
            }
            file = new File(PROPERTIES.getProperty("gamepath") + suffix);
            File backFile = new File(PROPERTIES.getProperty("gamepath") + "/Logs/Power.log_back");
            if (file.renameTo(backFile)){
                if (!backFile.delete()){
                    log.warn("Power.log备份文件删除失败");
                }
            }
            if (!file.exists()){
                try(FileWriter fileWriter = new FileWriter(file)){
                    fileWriter.write("");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                accessFile = new RandomAccessFile(file, "r");
            }else {
                accessFile = new RandomAccessFile(file, "r");
                accessFile.seek(accessFile.length());
            }
            log.info("Power.log 读取正常，指针恢复至：" + accessFile.getFilePointer());
        } catch (FileNotFoundException e) {
            log.error("未找到Power.log文件", e);
        }
    }

    @SneakyThrows
    @Scheduled(fixedRate=1000, initialDelay = 1000)
    public void listenScreenStatus(){
        if (!Core.getPause() && !PhaseStrategy.dealing){
            try {
                if (accessFile.getFilePointer() > accessFile.length() + 10000){
                    accessFile.seek(accessFile.length());
                }
                readPowerLog();
            } catch (IOException e) {
                log.error(file.getName() + "文件读取失败", e);
            }
        }
    }
    private static long mark = System.currentTimeMillis();

    public static void setMark(long mark) {
        PowerFileListen.mark = mark;
    }

    public static final long MAX_ERROR_TIME = 300_000L;

    @SneakyThrows
    @Scheduled(fixedRate=10_000, initialDelay = 2000)
    public void listenError(){
        if (!Core.getPause() && System.currentTimeMillis() - mark > MAX_ERROR_TIME){
            log.info("未知错误，准备重启游戏");
            SystemUtil.notice("未知错误，准备重启游戏");
            mark = System.currentTimeMillis();
            SystemUtil.frontWindow(Core.getGameHWND());
            SystemUtil.killProgram();
            ROBOT.delay(200);
            MyThreadPool.reset();
            PowerFileListen.reset();
        }
    }



    private static void readPowerLog() throws IOException {
        String l;
        while (!Core.getPause() && !PhaseStrategy.dealing && (l = accessFile.readLine()) != null){
            if (isRelevance(l)){
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
                }else if (War.getCurrentPhase() == GAME_OVER_PHASE || l.contains(FINAL_GAMEOVER.getValue()) || l.contains(FINAL_WRAPUP.getValue())){
                    GAME_OVER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                }
            }
        }
    }

    public static boolean isRelevance(String l){
        return l.contains("PowerTaskList.DebugPrintPower()");
    }


}
