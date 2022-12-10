package club.xiaojiawei.hearthstone.strategy.phase;

import club.xiaojiawei.hearthstone.entity.Card;
import club.xiaojiawei.hearthstone.entity.ExtraEntity;
import club.xiaojiawei.hearthstone.entity.TagChangeEntity;
import club.xiaojiawei.hearthstone.enums.WarPhaseEnum;
import club.xiaojiawei.hearthstone.listen.PowerFileListen;
import club.xiaojiawei.hearthstone.status.War;
import club.xiaojiawei.hearthstone.strategy.PhaseStrategy;
import club.xiaojiawei.hearthstone.utils.PowerLogUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

import static club.xiaojiawei.hearthstone.constant.GameConst.*;
import static club.xiaojiawei.hearthstone.constant.SystemConst.ROBOT;
import static club.xiaojiawei.hearthstone.enums.TagEnum.FIRST_PLAYER;

/**
 * @author 肖嘉威
 * @date 2022/11/27 13:35
 */
@Slf4j
public class DrawnInitCardPhaseStrategy extends PhaseStrategy {

    private String firstPlayerGameId;

    @SneakyThrows
    @Override
    public void dealing(String l) {
        War.setCurrentPhase(WarPhaseEnum.DRAWN_INIT_CARD_PHASE);
        log.info("当前处于：" + War.getCurrentPhase().getComment());
        RandomAccessFile accessFile = PowerFileListen.getAccessFile();
        ExtraEntity extraEntity;
        TagChangeEntity tagChangeEntity;
        while (true) {
            if ((l = accessFile.readLine()) == null){
                if (accessFile.getFilePointer() > accessFile.length() + 10000){
                    accessFile.seek(accessFile.length());
                }
                ROBOT.delay(1000);
            }else if (PowerFileListen.isRelevance(l)){
                PowerFileListen.setMark(System.currentTimeMillis());
                if (l.contains(SHOW_ENTITY)){
                    dealShowEntity(l, accessFile);
                }else if (l.contains(TAG_CHANGE)){
                    tagChangeEntity = PowerLogUtil.parseTagChange(l);
                    if (tagChangeEntity.getTag() == FIRST_PLAYER){
                        firstPlayerGameId = tagChangeEntity.getEntity();
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
//    todo del
    public void dealTagChange(String l){
        String entityId = l.substring(l.indexOf("id") + 3, l.indexOf("zone=") - 1);
        if (War.getRival() == null){
            switch (l.substring(l.lastIndexOf("player") + 7, l.lastIndexOf("]"))){
                case "1" -> {
                    War.setRival(War.getPlayer1());
                    War.setMe(War.getPlayer2());
                }
                case "2" -> {
                    War.setRival(War.getPlayer2());
                    War.setMe(War.getPlayer1());
                }
                default -> log.warn("不支持的playId");
            }
        }
        Card card = War.getRival().getDeckArea().removeByEntityId(entityId);
        War.getRival().getHandArea().add(card);
        log.info("对方起始手牌之一：" + card);
    }

}
