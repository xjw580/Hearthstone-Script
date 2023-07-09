package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.PowerLogUtil;
import club.xiaojiawei.data.GameStaticData;
import club.xiaojiawei.data.ScriptStaticData;
import club.xiaojiawei.enums.TagEnum;
import club.xiaojiawei.enums.WarPhaseEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
@Component
public class DrawnInitCardAbstractPhaseStrategy extends AbstractPhaseStrategy<String> {

    private String firstPlayerGameId;

    @SneakyThrows
    @Override
    protected void execute(String l, RandomAccessFile accessFile) {
        ExtraEntity extraEntity;
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
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains(GameStaticData.SHOW_ENTITY)){
                    dealShowEntity(l, accessFile);
                }else if (l.contains(GameStaticData.TAG_CHANGE)){
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (tagChangeEntity.getTag() == TagEnum.FIRST_PLAYER){
//                        炉石日志抽风，先手玩家游戏id打印出错，导致无法正常完成后续对局，所以直接投降
                        if(Objects.equals(firstPlayerGameId = tagChangeEntity.getEntity(), "UNKNOWN HUMAN PLAYER")){
                            log.info("power.log输出的玩家id有误，无法完成后续流程，准备投降");
                            gameUtil.surrender();
                            War.setCurrentPhase(WarPhaseEnum.GAME_OVER_PHASE, l);
                            break;
                        }
                    }else {
                        PowerLogUtil.dealTagChange(tagChangeEntity);
                    }
                }else if (l.contains(GameStaticData.FULL_ENTITY)){
                    extraEntity = PowerLogUtil.parseExtraEntity(l, accessFile);
                    extraEntity.setEntityName("幸运币");
                    Card card = new Card(extraEntity);
//                    向后手玩家手牌添加幸运币，确定后手玩家的gameId
                    switch (extraEntity.getPlayerId()){
                        case "1" -> {
                            War.getPlayer1().getHandArea().add(card);
                            War.getPlayer2().setGameId(firstPlayerGameId);
                        }
                        case "2" -> {
                            War.getPlayer2().getHandArea().add(card);
                            War.getPlayer1().setGameId(firstPlayerGameId);
                        }
                    }
                    firstPlayerGameId = null;
                }else if (l.contains("BLOCK_END")){
                    break;
                }
            }
        }
    }

    public void dealShowEntity(String l, RandomAccessFile accessFile){
        ExtraEntity extraEntity = PowerLogUtil.dealShowEntity(l, accessFile);
        if (War.getMe() == null) {
            switch (extraEntity.getPlayerId()) {
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
