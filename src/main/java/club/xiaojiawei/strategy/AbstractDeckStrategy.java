package club.xiaojiawei.strategy;

import club.xiaojiawei.data.GameRationStaticData;
import club.xiaojiawei.bean.BaseCard;
import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.bean.Player;
import club.xiaojiawei.bean.area.HandArea;
import club.xiaojiawei.bean.area.PlayArea;
import club.xiaojiawei.enums.CardRaceEnum;
import club.xiaojiawei.status.War;
import club.xiaojiawei.utils.GameUtil;
import club.xiaojiawei.utils.MouseUtil;
import club.xiaojiawei.utils.RandomUtil;
import club.xiaojiawei.utils.SystemUtil;
import javafx.beans.property.BooleanProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static club.xiaojiawei.data.ScriptStaticData.GAME_RECT;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;
import static club.xiaojiawei.enums.ConfigurationEnum.STRATEGY;
import static club.xiaojiawei.strategy.AbstractDeckStrategy.AbstractDeck.*;

/**
 * 卡牌策略抽象类
 * @author 肖嘉威
 * @date 2022/11/29 17:29
 */
@Slf4j
public abstract class AbstractDeckStrategy{
    @Resource
    protected MouseUtil mouseUtil;
    @Resource
    protected AtomicReference<BooleanProperty> isPause;
    @Resource
    protected Properties scriptConfiguration;
    @Resource
    private GameUtil gameUtil;

    protected static final double[] FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION = new double[]{
            0.033, 0.08, 0.123, 0.167, 0.177, 0.193, 0.203, 0.213, 0.22, 0.227
    };
    protected static final double[] HAND_CARD_HORIZONTAL_CLEARANCE_RATION = new double[]{
            0, 0.09, 0.09, 0.087, 0.07, 0.057, 0.05, 0.042, 0.037, 0.034
    };
    protected static final double HAND_CARD_VERTICAL_TO_BOTTOM_RATION = 0.059;
    protected static final double RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION = 0.801;
    protected static final double MY_HERO_VERTICAL_TO_BOTTOM_RATION = 0.26;
    protected static final double PLAY_CARD_HORIZONTAL_CLEARANCE_RATION = 0.097;
    protected static final double MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = 0.45;
    protected static final double RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION = 0.62;
    protected static final double POWER_VERTICAL_TO_BOTTOM_RATION = 0.23;
    protected static final double POWER_HORIZONTAL_TO_CENTER_RATION = 0.133;
    protected static final double TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.54;
    protected static final double TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION = 0.417;
    public static final double CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION = 0.23;
    protected static double BLOOD_WEIGHT = 0.4;
    protected static double ATC_WEIGHT = 0.6;
    protected static double FREE_EAT_MAX = 5;
    protected PlayArea myPlayArea;
    protected PlayArea rivalPlayArea;
    protected HandArea myHandArea;
    protected HandArea rivalHandArea;
    protected Player me;
    protected Player rival;
    protected List<Card> myHandCards;
    protected List<Card> myPlayCards;
    protected List<Card> rivalHandCards;
    protected List<Card> rivalPlayCards;
    public static class AbstractDeck{
        public static final BaseCard 南海船长 = new BaseCard("NEW1_027");
        public static final BaseCard 恐狼前锋 = new BaseCard("YOD_032");
        public static final BaseCard 火舌图腾 = new BaseCard("EX1_565");
        public static final BaseCard 末日预言者 = new BaseCard("NEW1_021");
        public static final BaseCard 对空奥数法师 = new BaseCard("ULD_240");
        public static final BaseCard 健谈的调酒师 = new BaseCard("REV_513");
        public static final BaseCard 船载火炮 = new BaseCard("GVG_075");
        public static final BaseCard 刺豚拳手 = new BaseCard("TSC_002");
    }
    /**
     * 每次行动后停顿时间
     */
    protected static final int ACTION_INTERVAL = 3500;
    public void changeCard() {
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try {
                log.info("执行换牌策略");
                assign();
                double clearance ,firstCardPos;
                if (myHandCards.size() == 3){
                    clearance  = getFloatCardClearanceForThreeCard();
                    firstCardPos = getFloatCardFirstCardPosForThreeCard();
                }else {
                    clearance  = getFloatCardClearanceForFourCard();
                    firstCardPos = getFloatCardFirstCardPosForFourCard();
                }
                SystemUtil.updateGameRect();
                int size = Math.min(myHandCards.size(), 4);
                for (int index = 0; index < size; index++) {
                    Card card = myHandCards.get(index);
                    if (executeChangeCard(card, index)){
                        clickFloatCard(clearance, firstCardPos, index);
                        log.info("换掉起始卡牌：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
                    }
                }
                log.info("执行换牌策略完毕");
            }finally {
                SystemUtil.updateGameRect();
                //        点击确认按钮
                mouseUtil.leftButtonClick(
                        ((GAME_RECT.right + GAME_RECT.left) >> 1) + RandomUtil.getRandom(-10, 10),
                        (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * CONFIRM_BUTTON_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
                );
            }
        }
    }

    public void outCard(){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            try{
                log.info("执行出牌策略");
                if (log.isDebugEnabled()){
                    log.debug("我方手牌：" + myHandCards);
                    log.debug("我方战场：" + myPlayCards);
                    log.debug("我方英雄：" + myPlayArea.getHero());
                    log.debug("我方武器：" + myPlayArea.getWeapon());
                    log.debug("我方技能：" + myPlayArea.getPower());
                }
                log.info("回合开始可用水晶数：" + getMyUsableResource());
                if (Objects.equals(War.getRival().getGameId(), "SBBaoXue#31568")){
                    gameUtil.surrender();
                    return;
                }
                executeOutCard();
                log.info("执行出牌策略完毕");
            }finally {
                MouseUtil.cancel();
                clickTurnOverButton();
            }
        }
    }
    public void discoverChooseCard(Card...cards){
        if (Boolean.parseBoolean(scriptConfiguration.getProperty(STRATEGY.getKey()))){
            SystemUtil.delay(1000);
            log.info("执行发现选牌策略");
            int index = executeDiscoverChooseCard(cards);
            clickFloatCard(getFloatCardClearanceForThreeCard(), getFloatCardFirstCardPosForThreeCard(), index);
            Card card = cards[index];
            log.info("选择了：【entityId:" + card.getEntityId() + "，entityName:" + card.getEntityName() + "，cardId:" + card.getCardId() + "】");
            log.info("执行发现选牌策略完毕");
        }
    }

    /**
     * 执行换牌策略
     * @param card
     * @return 返回true表示换掉该牌
     */
    protected abstract boolean executeChangeCard(Card card, int index);

    /**
     * 执行出牌策略
     */
    protected abstract void executeOutCard();

    /**
     * 执行发现选牌
     * @param cards
     * @return 返回0~2的数字
     */
    protected abstract int executeDiscoverChooseCard(Card...cards);

    private void assign(){
        this.me = War.getMe();
        this.rival = War.getRival();
        this.myHandArea = me.getHandArea();
        this.myPlayArea = me.getPlayArea();
        this.rivalHandArea = rival.getHandArea();
        this.rivalPlayArea = rival.getPlayArea();
        this.myHandCards = myHandArea.getCards();
        this.myPlayCards = myPlayArea.getCards();
        this.rivalHandCards = rivalHandArea.getCards();
        this.rivalPlayCards = rivalPlayArea.getCards();
        if (log.isDebugEnabled()){
            log.debug("我方手牌：" + myHandCards);
        }
    }

    /*calc*/

    /**
     * 计算我方场上法强
     * @return
     */
    protected int calcMySpellPower(){
        int spellPower = 0;
        for (Card playCard : myPlayCards) {
            spellPower += playCard.getSpellPower();
        }
        spellPower += myPlayArea.getHero().getSpellPower();
        if (myPlayArea.getWeapon() != null){
            spellPower += myPlayArea.getWeapon().getSpellPower();
        }
        return spellPower;
    }
    protected int calcCardBlood(Card card){
        return Math.max(0, card.getHealth() + card.getArmor() - card.getDamage());
    }
    protected int calcCardCount(List<Card> cards, String cardId){
        int count = 0;
        for (Card card : cards) {
            if (Objects.equals(card.getCardId(), cardId)){
                count++;
            }
        }
        return count;
    }

    /**
     * 计算指定卡的数量
     * @param cards
     * @param card
     * @return
     */
    protected int calcCardCount(List<Card> cards, BaseCard card){
        return calcCardCount(cards, card.cardId());
    }
    /**
     * 计算对方英雄血量
     * @return
     */
    protected int calcRivalHeroBlood(){
        return calcCardBlood(rivalPlayArea.getHero());
    }
    protected int calcMyHeroAtc(){
        if (myPlayArea.getHero().isFrozen() || myPlayArea.getHero().isExhausted()){
            return 0;
        }
        Card hero = myPlayArea.getHero();
        Card weapon = myPlayArea.getWeapon();
        return hero.getAtc() * (weapon != null && weapon.isWindFury() & calcCardBlood(weapon) > 1 ? 2 : 1);
    }
    protected int calcMyHeroBlood(){
        return calcCardBlood(myPlayArea.getHero());
    }
    protected int calcMyPlayAtc(){
        return calcAtc(myPlayCards);
    }
    protected int calcMyTotalAtc(){
        return calcMyPlayAtc() + calcMyHeroAtc();
    }
    protected int calcAtc(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            atc += card.getAtc() * (card.isWindFury()? 2 : 1);
        }
        return atc;
    }
    protected boolean cleanTaunt(){
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (rivalPlayCards.get(i).isTaunt() && canPointedToRival(rivalPlayCards.get(i)) && rivalPlayCards.get(i).getCardType() == MINION){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        boolean result = true;
        if (rivalCardEnableCount > 0){
            log.info("发现嘲讽，解嘲讽");
            result = cleanPlay(1.3D, canMove(myPlayArea.getHero())? 2D : 1.5D, 1000D, myPlayCards, rivalPlayCards, rivalCardEnable, rivalCardEnableCount, false, 0.01D);
        }
        for (int rivalIndex = rivalPlayCards.size() - 1; rivalIndex >= 0; rivalIndex--) {
            if (rivalIndex < rivalPlayCards.size()){
                Card rivalCard = rivalPlayCards.get(rivalIndex);
                if (
                        rivalCard.getCardType() == MINION
                                && rivalCard.isTaunt()
                                && canPointedToRival(rivalCard)
                ){
                    int heroAtc = calcMyHeroAtc();
                    List<Integer> list = calcEatRivalCard(rivalCard, Integer.MAX_VALUE, (heroAtc == 0 || myPlayArea.getHero().isExhausted())? 0 : heroAtc);
                    if (list != null){
//                攻击嘲讽怪
                        for (int j = list.size() - 2; j >= 0; j--) {
                            myPlayPointToRivalPlay(list.get(j), rivalIndex);
                        }
                        if (!(heroAtc == 0 || myPlayArea.getHero().isExhausted())){
                            myHeroPointToRivalPlay(rivalIndex);
                        }
                    }else {
                        result = false;
                    }
                }
            }
        }
        return result;
    }
    private void resetClean(List<Card> myCards, double myAtcWeight, List<Card> rivalCards, double rivalAtcWeight, boolean forceClean, double cleanScale){
        finalWeight = forceClean? 0 : calcCanMoveWeight(myCards, myAtcWeight) * cleanScale + calcWeight(myCards, myAtcWeight) - calcWeight(rivalCards, rivalAtcWeight);
        finalActFlag = new boolean[myPlayCards.size()][rivalPlayCards.size()];
    }
    private double calcCanMoveWeight(List<Card> cards, double atcWeight){
        double weight = 0D;
        for (Card card : cards) {
            if (canMove(card)) {
                weight += card.getAtc() * atcWeight;
            }
        }
        return weight;
    }

    protected void cleanPlay(){
        cleanPlay(1.3D, 1.25D, false, 0.2D, 8D);
    }
    protected void cleanPlay(double myAtcWeight, double rivalAtcWeight, boolean forceClean, double cleanScale, double maxOverWeight){
        if (findTauntCard(rivalPlayCards) != -1){
            return;
        }
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (isBuff(rivalPlayCards.get(i)) && canPointedToRival(rivalPlayCards.get(i))){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        if (rivalCardEnableCount > 0){
            log.info("发现buff，解buff");
            if (!cleanPlay(myAtcWeight, rivalAtcWeight + 1D, maxOverWeight + 7D, myPlayCards, rivalPlayCards, rivalCardEnable, rivalCardEnableCount, true, 0.05D)){
                log.info("解buff失败");
            }
        }

        rivalCardEnable = new boolean[rivalPlayCards.size()];
        rivalCardEnableCount = 0;
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (canPointedToRival(rivalPlayCards.get(i))){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        if (rivalCardEnableCount > 0){
            log.info("解普通怪");
            if (!cleanPlay(myAtcWeight, rivalAtcWeight,  maxOverWeight, myPlayCards, rivalPlayCards, rivalCardEnable, rivalCardEnableCount, forceClean, cleanScale)){
                log.info("解普通怪失败");
            }
        }


    }
    private volatile double finalWeight;
    private volatile boolean[][] finalActFlag;
    @SneakyThrows(value = {InterruptedException.class,  ExecutionException.class})
    private boolean cleanPlay(
            double myAtcWeight, double rivalAtcWeight, double maxOverWeight,
            List<Card> myPlayCards, List<Card> rivalPlayCards,
            boolean[] rivalCardEnable, int rivalCardEnableCount,
            boolean forceClean, double cleanScale
    ){
//        存储我方哪些随从可以攻击；存储敌方随从哪些可以被攻击
        boolean[] myCardEnable = new boolean[myPlayCards.size()];
        int myCardEnableCount = 0, firstMyCardEnable = -1;
        for (int i = 0; i < myPlayCards.size(); i++) {
            if (myCardEnable[i] = canMove(myPlayCards.get(i))){
                if (firstMyCardEnable == -1){
                    firstMyCardEnable = i;
                }
                myCardEnableCount++;
            }
        }
//        我方没有能动的，直接结束
        if (firstMyCardEnable == -1){
            return false;
        }
        resetClean(myPlayCards, myAtcWeight, rivalPlayCards, rivalAtcWeight, forceClean, cleanScale);
        long start = System.currentTimeMillis();
        if (rivalCardEnableCount >= 5 && myCardEnableCount >= 6){
            log.info("开启多线程计算");
            CompletableFuture[] futures = new CompletableFuture[rivalCardEnableCount];
            int index = 0, finalFirstMyCardEnable = firstMyCardEnable;
            for (int rivalPlayIndex = 0; rivalPlayIndex < rivalPlayCards.size(); rivalPlayIndex++) {
                if (rivalCardEnable[rivalPlayIndex]){
//                存储是否攻击：atcFlag[i][j] = true 表示我方i下标的随从攻击敌方j下标的随从
                    boolean[][] atcFlag = new boolean[myPlayCards.size()][rivalPlayCards.size()];
                    List<Card> myPlayCardsCopy = copyList(myPlayCards), rivalPlayCardsCopy = copyList(rivalPlayCards);
                    Card myCard = myPlayCardsCopy.get(finalFirstMyCardEnable), rivalCard = rivalPlayCardsCopy.get(rivalPlayIndex);
                    int finalRivalPlayIndex = rivalPlayIndex;
                    futures[index++] = CompletableFuture.runAsync(() -> {
                        if ( (calcWeight(calcCardBlood(myCard), myCard.getAtc(), myAtcWeight) - calcWeight(calcCardBlood(rivalCard), rivalCard.getAtc(), rivalAtcWeight) < maxOverWeight || forceClean)){
                            atcFlag[finalFirstMyCardEnable][finalRivalPlayIndex] = true;
                            reduceHealth(myCard, rivalCard.getAtc());
                            reduceHealth(rivalCard, myCard.getAtc());
                            recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCardsCopy, rivalPlayCardsCopy, myCardEnable, rivalCardEnable, atcFlag, finalFirstMyCardEnable + 1, forceClean, cleanScale);
                            reduceHealth(rivalCard, -myCard.getAtc());
                            reduceHealth(myCard, -rivalCard.getAtc());
                            atcFlag[finalFirstMyCardEnable][finalRivalPlayIndex] = false;
                        }
                        recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCardsCopy, rivalPlayCardsCopy, myCardEnable, rivalCardEnable, atcFlag, finalFirstMyCardEnable + 1, forceClean, cleanScale);
                    });
                }
            }
            CompletableFuture.allOf(futures).get();
        }else {
            log.info("单线程计算");
            recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, new boolean[myPlayCards.size()][rivalPlayCards.size()], 0, forceClean, cleanScale);
        }
        log.info("思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms");
        if (finalWeight > (forceClean? 0 : calcCanMoveWeight(myPlayCards, myAtcWeight) * cleanScale) + calcWeight(myPlayCards, myAtcWeight) - calcWeight(rivalPlayCards, rivalAtcWeight)){
            //        todo 攻击
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < finalActFlag.length; i++) {
                boolean[] temp = finalActFlag[i];
                for (int j = 0; j < temp.length; j++) {
                    if (finalActFlag[i][j]){
                        map.put(myPlayCards.get(i).getEntityId(), rivalPlayCards.get(j).getEntityId());
                    }
                }
            }
            if (map.isEmpty()){
                log.info("全都不动");
                return false;
            }
            for (String s : map.keySet()) {
                int myIndex = findByEntityId(myPlayCards, s);
                if (myIndex != -1){
                    int rivalIndex = findByEntityId(rivalPlayCards, map.get(s));
                    if (rivalIndex != -1){
                        myPlayPointToRivalPlay(myIndex, rivalIndex);
                    }
                }
            }
            return true;
        }
        return false;
    }
    private void recursionCleanPlay(
            double myAtcWeight, double rivalAtcWeight, double maxOverWeight,
            List<Card> myPlayCards, List<Card> rivalPlayCards,
            boolean[] myCardEnable, boolean[] rivalCardEnable,
            boolean[][] atcFlag, int myPlayIndex, boolean forceClean, double cleanScale
    ){
        if (myPlayIndex == myPlayCards.size()){
            double weight = calcWeight(myPlayCards, myAtcWeight) - calcWeight(rivalPlayCards, rivalAtcWeight) + (forceClean? 0 : calcCanMoveWeight(atcFlag, myCardEnable, myPlayCards, myAtcWeight) * cleanScale);
            if (weight >= finalWeight){
                synchronized (AbstractDeckStrategy.class){
                    if (weight >=finalWeight){
                        finalWeight = weight;
                        finalActFlag = copyAtcFlag(atcFlag);
                    }
                }
            }
            return;
        }
        if (myCardEnable[myPlayIndex]){
            for (int rivalIndex = 0; rivalIndex < rivalPlayCards.size(); rivalIndex++) {
                Card rivalCard = rivalPlayCards.get(rivalIndex);
                if (rivalCardEnable[rivalIndex]){
                    Card myCard = myPlayCards.get(myPlayIndex);
                    if (
                            calcCardBlood(rivalCard) > 0
                            &&
                            (calcWeight(calcCardBlood(myCard), myCard.getAtc(), myAtcWeight) - calcWeight(calcCardBlood(rivalCard), rivalCard.getAtc(), rivalAtcWeight) < maxOverWeight || forceClean)
                    ){
                        atcFlag[myPlayIndex][rivalIndex] = true;
                        reduceHealth(myCard, rivalCard.getAtc());
                        reduceHealth(rivalCard, myCard.getAtc());
                        recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, forceClean, cleanScale);
                        reduceHealth(rivalCard, -myCard.getAtc());
                        reduceHealth(myCard, -rivalCard.getAtc());
                        atcFlag[myPlayIndex][rivalIndex] = false;
                    }
                    recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, forceClean, cleanScale);
                }
            }
        }else {
            recursionCleanPlay(myAtcWeight, rivalAtcWeight, maxOverWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, forceClean, cleanScale);
        }
    }
    private double calcWeight(List<Card> cards, double atcWeight){
        double weight = 0D;
        for (Card card : cards) {
            weight += calcWeight(calcCardBlood(card), card.getAtc(), atcWeight);
        }
        return weight;
    }
    private int reduceHealth(Card card, int damage){
        int newHealth = card.getHealth() - damage;
        card.setHealth(newHealth);
        return newHealth;
    }
    private boolean[][] copyAtcFlag(boolean[][] atcFlag){
        boolean[][] atcFlagCopy = new boolean[atcFlag.length][atcFlag[0].length];
        for (int i = 0; i < atcFlag.length; i++) {
            atcFlagCopy[i] = Arrays.copyOf(atcFlag[i], atcFlag[i].length);
        }
        return atcFlagCopy;
    }
    private List<Card> copyList(List<Card> cards){
        ArrayList<Card> cardsCopy = new ArrayList<>();
        for (Card card : cards) {
            cardsCopy.add(card.clone());
        }
        return cardsCopy;
    }
    private double calcWeight(int blood, int atc, double actWeight){
        if (blood > 0){
            atc = Math.max(0, atc);
            return (blood + atc * actWeight) + atc * blood / 8D;
        }
        return 0;
    }
    private double calcCanMoveWeight(boolean[][] atcFlag, boolean[] myCardEnable, List<Card> cards, double myAtcWeight){
        double weight = 0D;
        for (int i = 0; i < atcFlag.length; i++) {
            boolean[] temp = atcFlag[i];
            if (myCardEnable[i]){
                boolean flag = true;
                for (boolean b : temp) {
                    if (b){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    weight += cards.get(i).getAtc() * myAtcWeight;
                }
            }
        }
        return weight;
    }
    private boolean isBuff(Card card){
        return cardEquals(card, 南海船长)
                || cardEquals(card, 火舌图腾)
                || cardEquals(card, 对空奥数法师)
                || cardEquals(card, 恐狼前锋)
                || cardEquals(card, 刺豚拳手)
                || cardEquals(card, 健谈的调酒师)
                || cardEquals(card, 船载火炮);
    }

    /**
     * 计算指定种族的数量
     * @param cards
     * @param cardRace
     * @return
     */
    protected int calcCardRaceCount(List<Card> cards, CardRaceEnum cardRace, boolean canMove){
        int count = 0;
        for (Card card : cards) {
            if ((card.getCardRace() == cardRace || card.getCardRace() == CardRaceEnum.ALL) && (!canMove || canMove(card))){
                count++;
            }
        }
        return count;
    }
    /**
     * 计算我的一个怪如何白吃对面一个怪
     * @param myPlayCard
     * @param allowDeath 允许不白吃
     * @return
     */
    protected int calcMyCardFreeEat(Card myPlayCard, boolean allowDeath){
        int rivalIndex = -1;
        double weight = 0;
        int myAtc = myPlayCard.getAtc();
        int myBlood = calcCardBlood(myPlayCard);
        double myWeight = myAtc * ATC_WEIGHT + myBlood * BLOOD_WEIGHT;
//        寻找能白吃的
        for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
            Card rivalCard = rivalPlayCards.get(i);
            if (canPointedToRival(rivalCard) && rivalCard.getCardType() == MINION && calcCardBlood(rivalCard) <= myAtc && (rivalCard.getAtc() < myBlood || myPlayCard.isDivineShield())){
                double newWeight = calcCardBlood(rivalCard) * BLOOD_WEIGHT + rivalCard.getAtc() * ATC_WEIGHT;
//                寻找最优白吃方法，既要白吃又不能白吃过头忽略打脸，如55白吃11这种
                if (newWeight > weight && myWeight - newWeight < FREE_EAT_MAX){
                    weight = newWeight;
                    rivalIndex = i;
                }
            }
        }
//            白吃不了，寻找比较赚的解法
        if (allowDeath && rivalIndex == -1){
            double myWeightPlus = myBlood * BLOOD_WEIGHT + (myAtc + 1) * ATC_WEIGHT;
            weight = 0;
            for (int i = rivalPlayCards.size() - 1; i >= 0 ; i--) {
                Card card = rivalPlayCards.get(i);
                if (canPointedToRival(card) && card.getCardType() == MINION && calcCardBlood(card) <= myAtc){
                    double newWeight = calcCardBlood(card) * BLOOD_WEIGHT + card.getAtc() * ATC_WEIGHT;
                    if (newWeight >= myWeightPlus && newWeight > weight){
                        rivalIndex = i;
                        weight = newWeight;
                    }
                }
            }
        }
        return rivalIndex;
    }

    /**
     * 计算我的多个怪如何吃对面一个怪
     * @param rivalCard
     * @param maxDeathCount
     * @param subtractRivalBlood 需要减少的敌方怪的血量
     * @return 储存下标的list，最后一位存储总攻击力
     */
    protected List<Integer> calcEatRivalCard(Card rivalCard, int maxDeathCount, int subtractRivalBlood){
        if (rivalCard == null){
            return null;
        }
        TreeSet<List<Integer>> result = new TreeSet<>(Comparator.comparingInt(o -> o.get(o.size() - 1)));
        calcFreeEatRivalCardRecursive(result, myPlayCards, new ArrayList<>(), calcCardBlood(rivalCard) - subtractRivalBlood, rivalCard.getAtc(), 0, 0, maxDeathCount);
        return result.isEmpty() ? null : result.first();
    }
    protected List<Integer> calcEatRivalCard(Card rivalCard){
        return calcEatRivalCard(rivalCard, Integer.MAX_VALUE, 0);
    }
    protected List<Integer> calcFreeEatRivalCard(Card rivalCard){
        return calcEatRivalCard(rivalCard, 0, 0);
    }
    /**
     * 穷举法
     * @param result
     * @param myPlayCards
     * @param indexList 存储卡组下标，最后一位存储总攻击力
     * @param rivalBlood
     * @param rivalAtc
     * @param atcSum
     * @param index
     */
    private void calcFreeEatRivalCardRecursive(TreeSet<List<Integer>> result, List<Card> myPlayCards, List<Integer> indexList, int rivalBlood, int rivalAtc, int atcSum, int index, int maxDeathCount){
//        终止条件
        if (atcSum >= rivalBlood){
            indexList.add(atcSum);
            ArrayList<Integer> tempIndexList = new ArrayList<>(indexList);
            indexList.remove(indexList.size() - 1);
//            由于重写了compare方法，所以这里比较的是总攻击力是否相等
            if (result.contains(tempIndexList)){
                double oldFreeEatCount = 0, newFreeEatCount = 0;
                List<Integer> oldList = null;
                for (List<Integer> lists : result) {
//                取出总攻击力相等的那个旧list
                    if (lists.get(lists.size() - 1) == atcSum){
                        oldList = lists;
//                计算旧list的白吃数
                        for (int i = 0; i < lists.size() - 1; i++) {
                            Card card = myPlayCards.get(lists.get(i));
                            if (calcCardBlood(card) > rivalAtc || card.isDivineShield() || card.isImmune()){
                                oldFreeEatCount++;
                            }
                        }
                        break;
                    }
                }
//                计算新list的白吃数
                for (Integer integer : indexList) {
                    Card card = myPlayCards.get(integer);
                    if (calcCardBlood(card) > rivalAtc || card.isDivineShield() || card.isImmune()){
                        newFreeEatCount++;
                    }
                }
                assert oldList != null;
//                比较新旧list白吃率
                if (oldFreeEatCount / oldList.size() < newFreeEatCount / tempIndexList.size()){
                    result.remove(tempIndexList);
                    result.add(tempIndexList);
                }
            }else {
                if (maxDeathCount != Integer.MAX_VALUE){
                    int deathCount = 0;
                    for (Integer integer : indexList) {
                        Card card = myPlayCards.get(integer);
//                        保证全部白吃才能添加
                        if (calcCardBlood(card) <= rivalAtc && !card.isDivineShield() && !card.isImmune()){
                            if (++deathCount > maxDeathCount){
                                return;
                            }
                        }
                    }
                }
                result.add(tempIndexList);
            }
            return;
        }else if (index == myPlayCards.size()){
            return;
        }

        Card card = myPlayCards.get(index);
        if (canMove(card)){
//            选
            indexList.add(index);
            atcSum += card.getAtc();
            calcFreeEatRivalCardRecursive(result, myPlayCards, indexList, rivalBlood, rivalAtc, atcSum, index + 1, maxDeathCount);
            indexList.remove(indexList.size() - 1);
            atcSum -= card.getAtc();
        }
//        不选
        calcFreeEatRivalCardRecursive(result, myPlayCards, indexList, rivalBlood, rivalAtc,  atcSum, index + 1, maxDeathCount);
    }
    /*action*/
    
    private void myHandPointTo(int handIndex, int[] endPos){
        if (handIndex < 0){
            return;
        }
        int[] handPos = getMyHandCardPos(me.getHandArea().getCards().size(), handIndex);
        mouseUtil.leftButtonDrag(
                handPos,
                endPos
        );
    }
    protected boolean myHandPointToMyPlay(int handIndex){
        if (handIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(handIndex);
        if (myPlayArea.isFull() || card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(handIndex, myPlayCards.size(), true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }
    protected boolean myHandPointToMyPlay(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (myPlayArea.isFull() || card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }
    protected boolean myHandPointToMyPlayNoPlace(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToMe(myPlayCards.get(myPlayIndex))){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }

    private int[] myHandPointToMyPlayForBase(int handIndex, int playIndex, boolean insertGap){
        SystemUtil.updateGameRect();
        int[] playPos;
        if (playIndex < 0){
            playPos = new int[]{
                    (GAME_RECT.right + GAME_RECT.left) >> 1,
                    (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION) + RandomUtil.getRandom(-5, 5)
            };
        }else {
            playPos = getMyPlayCardPos(me.getPlayArea().getCards().size() + (insertGap? 1 : 0), playIndex);
        }
        myHandPointTo(handIndex, playPos);
        return playPos;
    }

    protected boolean myHandPointToRivalPlayNoPlace(int myHandIndex, int rivalPlayIndex){
        if (myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToRival(rivalPlayCards.get(rivalPlayIndex))){
            return false;
        }
        SystemUtil.updateGameRect();
        myHandPointTo(myHandIndex, getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex));
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }
    protected boolean myHandPointToRivalHeroNoPlace(int myHandIndex){
        if (myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource() || !canSpellPointedToRival(rivalPlayArea.getHero())){
            return false;
        }
        SystemUtil.updateGameRect();
        myHandPointTo(myHandIndex, getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }
    protected boolean myHandPointToNoPlace(int myHandIndex){
        if (myHandIndex >= myHandCards.size()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        if (card.getCost() > getMyUsableResource()){
            return false;
        }
        myHandPointToMyPlayForBase(myHandIndex, -1, false);
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }

    protected boolean myHeroPointToRivalHero(){
        if (!canPointedToRival(rivalPlayArea.getHero()) || !canMove(myPlayArea.getHero()) || calcMyHeroAtc() <= 0){
           return false;
        }
        SystemUtil.updateGameRect();
        myHeroPointTo(getRivalHeroPos());
        SystemUtil.delay(ACTION_INTERVAL);
        return true;
    }
    protected boolean myHeroPointToRivalPlay(int rivalPlayIndex){
        if (
                rivalPlayIndex >= rivalPlayCards.size()
                || !canPointedToRival(rivalPlayCards.get(rivalPlayIndex))
                || !canMove(myPlayArea.getHero())
                || calcMyHeroAtc() <= 0
                || calcCardBlood(myPlayArea.getHero()) <= rivalPlayCards.get(rivalPlayIndex).getAtc()
        ){
            return false;
        }
        SystemUtil.updateGameRect();
        int[] rivalPlayPos = getRivalPlayCardPos(rivalPlayCards.size(), rivalPlayIndex);
        myHeroPointTo(rivalPlayPos);
        SystemUtil.delay(ACTION_INTERVAL + 750);
        return true;
    }
    private void myHeroPointTo(int[] endPos){
        mouseUtil.leftButtonDrag(
                getMyHeroPos(),
                endPos
        );
    }

    protected void allAtcRivalHero(){
        for (int i = myPlayCards.size() - 1; i >= 0; i--) {
            Card card = myPlayCards.get(i);
            myPlayPointToRivalHero(i);
            if (card.isWindFury()){
                myPlayPointToRivalHero(i);
            }
        }
    }
    protected boolean myPlayPointToRivalHero(int myPlayIndex){
        if (myPlayIndex >= myPlayCards.size()){
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedToRival(rivalPlayArea.getHero()) || !canMove(card)){
            return false;
        }
        SystemUtil.updateGameRect();
        int[] rivalHeroPos = getRivalHeroPos();
        mouseUtil.leftButtonDrag(
                getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex),
                rivalHeroPos
        );
        SystemUtil.delay(ACTION_INTERVAL - 700);
        return true;
    }

    /**
     * 从我方战场指向对方战场
     * @param myPlayIndex
     * @param rivalPlayIndex
     */
    protected boolean myPlayPointToRivalPlay(int myPlayIndex, int rivalPlayIndex){
        if (myPlayIndex >= myPlayCards.size() || rivalPlayIndex >= rivalPlayCards.size()){
            return false;
        }
        Card card = myPlayCards.get(myPlayIndex);
        if (!canPointedToRival(rivalPlayCards.get(rivalPlayIndex)) || !canMove(card)){
            return false;
        }
        SystemUtil.updateGameRect();
        int[] myPlayPos = getMyPlayCardPos(me.getPlayArea().getCards().size(), myPlayIndex);
        int[] rivalPlayPos = getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex);
        mouseUtil.leftButtonDrag(
                myPlayPos[0],
                myPlayPos[1],
                rivalPlayPos[0],
                rivalPlayPos[1]
        );
        SystemUtil.delay(ACTION_INTERVAL + 750);
        return true;
    }

    /**
     * 点击我方技能
     */
    protected boolean clickPower(){
        if (myPlayArea.getPower().isExhausted() || getMyUsableResource() < myPlayArea.getPower().getCost() || myPlayArea.isFull()){
            return false;
        }
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(getMyPowerPos());
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        return true;
    }

    /**
     * 点击回合结束按钮
     */
    protected void clickTurnOverButton(){
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * TURN_OVER_BUTTON_HORIZONTAL_TO_CENTER_RATION + RandomUtil.getRandom(-6, 6)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * TURN_OVER_BUTTON_VERTICAL_TO_BOTTOM_RATION  + RandomUtil.getRandom(-3, 3))
        );
    }

    protected boolean myHandPointToMyPlayThenPointToMyPlay(int myHandIndex, int myPlayIndex, int thenMyPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayArea.isFull() || myPlayCards.get(thenMyPlayIndex).isDormantAwakenConditionEnchant() || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        if (myPlayIndex <= thenMyPlayIndex){
            thenMyPlayIndex++;
        }
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getMyPlayCardPos(me.getPlayArea().getCards().size() + 1, thenMyPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalPlay(int myHandIndex, int myPlayIndex, int rivalPlayIndex){
        if (myHandIndex >= myHandCards.size() || rivalPlayIndex >= rivalPlayCards.size() || myPlayArea.isFull() || !canPointedToRival(rivalPlayCards.get(rivalPlayIndex)) || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalPlayCardPos(rival.getPlayArea().getCards().size(), rivalPlayIndex)
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }

    protected boolean myHandPointToMyPlayThenPointToRivalHero(int myHandIndex, int myPlayIndex){
        if (myHandIndex >= myHandCards.size() || myPlayArea.isFull() || !canPointedToRival(rivalPlayArea.getHero()) || myHandCards.get(myHandIndex).getCost() > getMyUsableResource()){
            return false;
        }
        Card card = myHandCards.get(myHandIndex);
        int[] playPos = myHandPointToMyPlayForBase(myHandIndex, myPlayIndex, true);
        SystemUtil.delayMedium();
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonMoveThenClick(
                playPos,
                getRivalHeroPos()
        );
        SystemUtil.delay(ACTION_INTERVAL);
        log.info("当前可用水晶数：" + getMyUsableResource());
        if (findByEntityId(myHandCards, card) == -1){
            return true;
        }
        MouseUtil.cancel();
        return false;
    }
    /**
     * 点击悬浮卡牌，如发现,
     * @param clearance
     * @param firstCardPos
     * @param doubleCardIndex 0~2
     */
    protected void clickFloatCard(double clearance, double firstCardPos, int doubleCardIndex){
        SystemUtil.updateGameRect();
        mouseUtil.leftButtonClick(
                (int) (firstCardPos + doubleCardIndex * clearance) + RandomUtil.getRandom(-10, 10),
                (GAME_RECT.bottom + GAME_RECT.top >> 1) + RandomUtil.getRandom(-15, 15)
        );
        SystemUtil.delayShort();
    }
    /*exist*/
    /**
     * 是否存在指定费用的牌
     * @param cards
     * @param cost
     * @return
     */
    protected boolean existByCost(List<Card> cards, int cost){
        return findByCost(cards, cost) != -1;
    }
    /*find*/
    /**
     * 寻找指定血量随从数量
     * @return
     */
    protected int findCountByBlood(List<Card> cards, int blood){
        int count = 0;
        for (Card card : cards) {
            if (calcCardBlood(card) == blood){
                count++;
            }
        }
        return count;
    }
    /**
     * 寻找在大于等于指定攻击力中攻击力最大的
     * @param cards
     * @param atk
     * @return
     */
    protected int findMaxAtcByGEAtk(List<Card> cards, int atk){
        int index = -1, attackNum = 0;
        for (int i = 0; i < cards.size(); i++) {
            int atc = cards.get(i).getAtc();
            if (atc >= atk && atc > attackNum){
                index = i;
                attackNum = atc;
            }
        }
        return index;
    }

    /**
     *
     * @param cards
     * @param atcLine
     * @return
     */
    protected int findMaxAtcByGEAtkNotWindFury(List<Card> cards, int atcLine){
        int index = -1, atcMax = 0;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            int atc = card.getAtc();
            if ((atc > Math.max(atcMax, atcLine) || (atc == Math.max(atcMax, atcLine) && (index != -1 && calcCardBlood(card) > calcCardBlood(cards.get(index))))) && !card.isWindFury()){
                index = i;
                atcMax = atc;
            }
        }
        return index;
    }
    protected int findCardByCardRace(List<Card> cards, CardRaceEnum ...cardRace){
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            for (CardRaceEnum cardRaceEnum : cardRace) {
                if (cardRaceEnum == card.getCardRace()){
                    return i;
                }
            }
        }
        return -1;
    }
    /**
     * 寻找在等于指定血量中攻击力最大的
     * @param cards
     * @param blood 生命值减去伤害等得出
     * @return
     */
    protected int findMaxAtcByBlood(List<Card> cards, int blood, int maxAtc, boolean canLTBlood){
        int atk = 0;
        int index = -1;
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (
                    (calcCardBlood(card) == blood || (canLTBlood && calcCardBlood(card) < blood))
                    && card.getAtc() > atk
                    && card.getAtc() <= maxAtc
            ){
                    atk = card.getAtc();
                    index = i;
            }
        }
        return index;
    }
    protected int findMaxAtcByBlood(List<Card> cards, int blood, boolean canLTBlood){
        return findMaxAtcByBlood(cards, blood, Integer.MAX_VALUE, canLTBlood);
    }
    /**
     * 寻找能动的怪
     * @param cards
     * @return
     */
    protected int findCanMove(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (canMove(cards.get(i))){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找指定费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByCost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() == cost){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找小于等于费用的卡牌
     * @param cards
     * @param cost
     * @return
     */
    protected int findByLECost(List<Card> cards, int cost){
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCost() <= cost){
                return i;
            }
        }
        return -1;
    }

    /**
     * 寻找指定cardId的card
     * @param cards
     * @param cardId
     * @return
     */
    protected int findByCardId(List<Card> cards, String cardId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, Card card){
        String cardId = card.getCardId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(cardId)){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, Card card){
        String entityId = card.getEntityId();
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByEntityId(List<Card> cards, String entityId){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (Objects.equals(entityId, cards.get(i).getEntityId())){
                return i;
            }
        }
        return -1;
    }
    protected int findByCardId(List<Card> cards, BaseCard baseCard){
        for (int i = cards.size() - 1; i >= 0; i--) {
            String id = cards.get(i).getCardId();
            if (id != null && id.contains(baseCard.cardId())){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找所有指定费用的card
     * @param cards
     * @param cost
     * @return
     */
    protected List<Card> findAllByCost(List<Card> cards, int cost){
        ArrayList<Card> list = new ArrayList<>();
        for (Card card : cards) {
            if (card.getCost() == cost){
                list.add(card);
            }
        }
        return list;
    }
    /**
     * 寻找第一个有嘲讽的随从
     * @param cards
     * @return
     */
    protected int findTauntCard(List<Card> cards){
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).isTaunt()){
                return i;
            }
        }
        return -1;
    }
    /**
     * 寻找第一个非疲劳卡牌
     * @param cards
     * @return
     */
    protected int findNotExhaustedCard(List<Card> cards){
        for (int i = 0; i < cards.size(); i++) {
            if (!cards.get(i).isExhausted()){
                return i;
            }
        }
        return -1;
    }

    /*get*/
    /**
     * 获取可用水晶数
     * @return
     */
    protected int getMyUsableResource(){
        return me.getResources() - me.getResourcesUsed() + me.getTempResources();
    }
    protected double getFloatCardClearanceForFourCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_FOUR_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected double getFloatCardClearanceForThreeCard(){
        return GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * GameRationStaticData.CARD_HORIZONTAL_CLEARANCE_WHEN_THREE_CARD * (GAME_RECT.bottom - GAME_RECT.top);
    }
    protected double getFloatCardFirstCardPosForFourCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_FOUR_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }
    protected double getFloatCardFirstCardPosForThreeCard(){
        return (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.FIRST_CARD_HORIZONTAL_TO_CENTER_WHEN_THREE_CARD * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
    }

    /**
     * 获取指定手牌位置
     * @param size
     * @param handIndex
     * @return
     */
    protected int[] getMyHandCardPos(int size, int handIndex){
        double clearance = GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * HAND_CARD_HORIZONTAL_CLEARANCE_RATION[size - 1] * (GAME_RECT.bottom - GAME_RECT.top),
                firstCardPos = (GAME_RECT.left + GAME_RECT.right >> 1) - (GAME_RECT.bottom - GAME_RECT.top) * FIRST_HAND_CARD_HORIZONTAL_TO_CENTER_RATION[size - 1] * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO;
        return new int[]{
                (int) (firstCardPos + handIndex * clearance) + RandomUtil.getRandom(-5, 5),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * HAND_CARD_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5, 5))
        };
    }
    /**
     * 获取指定战场位置
     * @param size
     * @param playIndex
     * @return
     */
    private int[] getMyPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, MY_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }
    private int[] getRivalPlayCardPos(int size, int playIndex){
        return calcPlayCardPos(size, playIndex, RIVAL_PLAY_CARD_VERTICAL_TO_BOTTOM_RATION);
    }

    private int[] calcPlayCardPos(int size, int playIndex, double playCardVerticalToBottomRation) {
        double clearance = (GAME_RECT.bottom - GAME_RECT.top) * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * PLAY_CARD_HORIZONTAL_CLEARANCE_RATION;
        int x;
        if ((size & 1) == 0){
            x =  (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + 0.5 + playIndex) * clearance);
        }else {
            x = (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + (-(size >> 1) + playIndex) * clearance);
        }
        return new int[]{
                x,
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * playCardVerticalToBottomRation) + RandomUtil.getRandom(-5, 5)
        };
    }

    private int[] getRivalHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * RIVAL_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    private int[] getMyHeroPos(){
        return new int[]{
                (GAME_RECT.right + GAME_RECT.left >> 1) + RandomUtil.getRandom(-10 , 10),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * MY_HERO_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-15 , 15))
        };
    }
    private int[] getMyPowerPos(){
        return new int[]{
                (int) ((GAME_RECT.right + GAME_RECT.left >> 1) + POWER_HORIZONTAL_TO_CENTER_RATION * GameRationStaticData.GAME_WINDOW_ASPECT_TO_HEIGHT_RATIO * (GAME_RECT.bottom - GAME_RECT.top) + RandomUtil.getRandom(-5 , 5)),
                (int) (GAME_RECT.bottom - (GAME_RECT.bottom - GAME_RECT.top) * POWER_VERTICAL_TO_BOTTOM_RATION + RandomUtil.getRandom(-5 , 5))
        };
    }

    /**
     * 卡id是否相同
     * @param longCard
     * @param baseCard
     * @return
     */
    protected boolean cardEquals(Card longCard, BaseCard baseCard){
        return cardEquals(longCard, baseCard.cardId());
    }
    protected boolean cardEquals(Card longCard, String baseCardId){
        return longCard != null && longCard.getCardId() != null && longCard.getCardId().contains(baseCardId);
    }

    /**
     * 能不能被对方法术指向
     * @param card
     * @return
     */
    protected boolean canSpellPointedToRival(Card card){
        return canPointedToRival(card) && !isImmunityMagic(card);
    }

    /**
     * 能不能被本方法术指向
     * @param card
     * @return
     */
    protected boolean canSpellPointedToMe(Card card){
        return canPointedToMe(card) && !isImmunityMagic(card);
    }

    /**
     * 能不能被对方指向
     * @param card
     * @return
     */
    protected boolean canPointedToRival(Card card){
        return !(card.isImmune() || card.isStealth() || card.isDormantAwakenConditionEnchant());
    }

    /**
     * 能不能被本方指向
     * @param card
     * @return
     */
    protected boolean canPointedToMe(Card card){
        return !(card.isImmune() || card.isDormantAwakenConditionEnchant() || isImmunityMagic(card));
    }

    /**
     * 是不是魔免
     * @param card
     * @return
     */
    protected boolean isImmunityMagic(Card card){
        return card.isCantBeTargetedByHeroPowers() && card.isCantBeTargetedBySpells();
    }

    /**
     * 能不能动
     * @param card
     * @return
     */
    protected boolean canMove(Card card){
        return !(card.isExhausted() || card.isFrozen() || card.isDormantAwakenConditionEnchant() || card.getAtc() <= 0);
    }
}
