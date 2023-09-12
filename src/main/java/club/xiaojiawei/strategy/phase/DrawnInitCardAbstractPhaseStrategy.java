package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.StepEnum;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.listener.ScreenFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.TagEnum.ZONE;
import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class DrawnInitCardAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {

    @Getter
    private static String firstPlayerGameId;

    @SneakyThrows
    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
        TagChangeEntity tagChangeEntity;
        while (true) {
            if (isPause.get().get()){
                return;
            }else if ((l = accessFile.readLine()) == null){
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ScriptStaticData.ROBOT.delay(1000);
                }
            }else if (powerFileListener.isRelevance(l)){
                ScreenFileListener.setMark(System.currentTimeMillis());
                if (l.contains(GameStaticData.SHOW_ENTITY)){
                    verifyPlayer(PowerLogUtil.dealShowEntity(l, accessFile).getPlayerId(), false);
                }else if (l.contains(GameStaticData.TAG_CHANGE)){
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (tagChangeEntity.getTag() == TagEnum.FIRST_PLAYER){
                        log.info("先手玩家：" + (firstPlayerGameId = new String(tagChangeEntity.getEntity().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
                    }else if (Objects.equals(tagChangeEntity.getValue(), StepEnum.FINAL_GAMEOVER.getValue())){
                        War.setCurrentPhase(GAME_OVER_PHASE, l);
                        break;
                    }else {
                        PowerLogUtil.dealTagChange(tagChangeEntity);
                        if (tagChangeEntity.getTag() == ZONE){
                            verifyPlayer(tagChangeEntity.getPlayerId(), true);
                        }
                    }
                }else if (l.contains(GameStaticData.FULL_ENTITY)){
                    Card coin = PowerLogUtil.dealFullEntity(l, accessFile);
                    coin.setEntityName("幸运币");
                    if (Strings.isNotBlank(coin.getCardId())){
                        War.getRival().setGameId(firstPlayerGameId);
                        log.info("对方：" + firstPlayerGameId);
                    }else {
                        War.getMe().setGameId(firstPlayerGameId);
                        log.info("我方：" + firstPlayerGameId);
                    }
                }else if (l.contains("BLOCK_END")){
                    break;
                }
            }
        }
    }

    /**
     * SHOW_ENTITY解析来的reverse为false
     * TAG_CHANGE解析来的reverse为true
     * @param playerId
     * @param reverse
     */
    public void verifyPlayer(String playerId, boolean reverse){
        if (reverse){
            playerId = Objects.equals(playerId, "1")? "2" : "1";
        }
        if (War.getMe() == null && Strings.isNotBlank(playerId)) {
            switch (playerId) {
                case "1" -> {
                    War.setMe(War.getPlayer1());
                    War.setRival(War.getPlayer2());
                }
                case "2" -> {
                    War.setMe(War.getPlayer2());
                    War.setRival(War.getPlayer1());
                }
                default -> log.warn("不支持的playId");
            }
        }
    }
}
