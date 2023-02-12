package club.xiaojiawei.strategy.phase;

import club.xiaojiawei.entity.Card;
import club.xiaojiawei.entity.ExtraEntity;
import club.xiaojiawei.entity.TagChangeEntity;
import club.xiaojiawei.enums.WarPhaseEnum;
import club.xiaojiawei.listener.PowerFileListener;
import club.xiaojiawei.status.War;
import club.xiaojiawei.strategy.AbstractPhaseStrategy;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.util.Objects;

import static club.xiaojiawei.constant.GameKeyWordConst.*;
import static club.xiaojiawei.constant.SystemConst.ROBOT;
import static club.xiaojiawei.enums.TagEnum.FIRST_PLAYER;
import static club.xiaojiawei.enums.WarPhaseEnum.GAME_OVER_PHASE;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
public class DrawnInitCardAbstractPhaseStrategy extends AbstractPhaseStrategy {

    private String firstPlayerGameId;

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.DRAWN_INIT_CARD_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        RandomAccessFile accessFile = PowerFileListener.getAccessFile();
        ExtraEntity extraEntity;
        TagChangeEntity tagChangeEntity;
        while (true) {
            if ((l = accessFile.readLine()) == null){
                if (accessFile.getFilePointer() > accessFile.length()){
                    accessFile.seek(0);
                }else {
                    ROBOT.delay(1000);
                }
            }else if (PowerFileListener.isRelevance(l)){
                PowerFileListener.setMark(System.currentTimeMillis());
                if (l.contains(SHOW_ENTITY)){
                    dealShowEntity(l, accessFile);
                }else if (l.contains(TAG_CHANGE)){
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (tagChangeEntity.getTag() == FIRST_PLAYER){
//                        炉石日志抽风，先手玩家游戏id打印出错，导致无法正常完成后续对局，所以直接投降
                        if(Objects.equals(firstPlayerGameId = tagChangeEntity.getEntity(), "UNKNOWN HUMAN PLAYER")){
                            GameUtil.surrender();
                            log.info(War.getCurrentPhase().getComment() + " -> 结束");
                            GAME_OVER_PHASE.getPhaseStrategySupplier().get().afterInto(l);
                            break;
                        }
                    }else {
                        PowerLogUtil.dealTagChange(tagChangeEntity);
                    }
                }else if (l.contains(FULL_ENTITY)){
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
                    log.info(War.getCurrentPhase().getComment() + " -> 结束");
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
