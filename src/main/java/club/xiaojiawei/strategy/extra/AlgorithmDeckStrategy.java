package club.xiaojiawei.strategy.extra;

import club.xiaojiawei.bean.entity.Card;
import club.xiaojiawei.strategy.AbstractDeckStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static club.xiaojiawei.data.CommonBaseDeck.*;
import static club.xiaojiawei.data.CommonBaseDeck.恐狼前锋;
import static club.xiaojiawei.enums.CardTypeEnum.MINION;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2023/11/5 22:21
 */
@Slf4j
public class AlgorithmDeckStrategy extends ActionDeckStrategy{

    protected static double BLOOD_WEIGHT = 0.4;
    protected static double ATC_WEIGHT = 0.6;
    protected static double FREE_EAT_MAX = 5;
    protected boolean cleanTaunt(){
        return cleanTaunt(1.3D, 10D, 0.001D);
    }
    protected boolean cleanBuff(){
        return cleanBuff(1.3D, 4D, 0.3D);
    }
    protected boolean cleanDanger(){
        return cleanDanger(1.3D, 4D, 0.3D);
    }
    protected boolean cleanNormal(){
        return cleanNormal(1.3D, 1.35D, 0.7D);
    }

    protected boolean cleanTaunt(double myAtcWeight, double rivalAtcWeight, double lazyLevel){
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (rivalPlayCards.get(i).isTaunt() && canPointedByRival(rivalPlayCards.get(i)) && rivalPlayCards.get(i).getCardType() == MINION){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        boolean result = true;
        if (rivalCardEnableCount > 0){
            log.info("发现嘲讽怪，思考解嘲讽怪中...");
            result = cleanPlay(myAtcWeight, rivalAtcWeight, copyListAndAddHero(myPlayCards, myPlayArea.getHero()), rivalPlayCards, rivalCardEnable, rivalCardEnableCount, lazyLevel);
        }
        return result && findTauntCard(rivalPlayCards) == -1;
    }

    protected boolean cleanBuff(double myAtcWeight, double rivalAtcWeight, double lazyLevel){
        if (findTauntCard(rivalPlayCards) != -1){
            return false;
        }
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (isBuff(rivalPlayCards.get(i)) && canPointedByRival(rivalPlayCards.get(i))){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        boolean result = true;
        if (rivalCardEnableCount > 0){
            log.info("发现Buff怪，思考解Buff怪中...");
            result = cleanPlay(myAtcWeight, rivalAtcWeight, copyListAndAddHero(myPlayCards, myPlayArea.getHero()), rivalPlayCards, rivalCardEnable, rivalCardEnableCount, lazyLevel);
        }
        return result && !containsBuff(rivalPlayCards);
    }

    protected boolean cleanDanger(double myAtcWeight, double rivalAtcWeight, double lazyLevel){
        if (findTauntCard(rivalPlayCards) != -1){
            return false;
        }
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (isDanger(rivalPlayCards.get(i)) && canPointedByRival(rivalPlayCards.get(i))){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        boolean result = true;
        if (rivalCardEnableCount > 0){
            log.info("发现威胁怪，思考解威胁怪中...");
            result = cleanPlay(myAtcWeight, rivalAtcWeight, copyListAndAddHero(myPlayCards, myPlayArea.getHero()), rivalPlayCards, rivalCardEnable, rivalCardEnableCount, lazyLevel);
        }
        return result && !containsDanger(rivalPlayCards);
    }

    /**
     * @param myAtcWeight 我方随从攻击力权重，大于1表示攻击力比生命值重要
     * @param rivalAtcWeight 敌方随从攻击力权重，rivalAtcWeight/myAtcWeight小于1时认为我方随从更加厉害，防止出现我方2-2解对方2-2的情况
     * @param lazyLevel 懒惰程度，越高则越偏向于随从不动
     */
    protected boolean cleanNormal(double myAtcWeight, double rivalAtcWeight, double lazyLevel){
        if (findTauntCard(rivalPlayCards) != -1){
            return false;
        }
        int rivalCardEnableCount = 0;
        boolean[] rivalCardEnable = new boolean[rivalPlayCards.size()];
        for (int i = 0; i < rivalPlayCards.size(); i++) {
            if (canPointedByRival(rivalPlayCards.get(i))){
                rivalCardEnable[i] = true;
                rivalCardEnableCount++;
            }
        }
        boolean result = true;
        if (rivalCardEnableCount > 0){
            log.info("发现普通怪，思考解普通怪中...");
            result = cleanPlay(myAtcWeight, rivalAtcWeight, copyListAndAddHero(myPlayCards, myPlayArea.getHero()), rivalPlayCards, rivalCardEnable, rivalCardEnableCount, lazyLevel);
        }
        return result;
    }

    private volatile double finalWeight;
    private volatile double initWeight;
    private volatile boolean[][] finalActFlag;

    @SneakyThrows(value = {InterruptedException.class,  ExecutionException.class})
    private boolean cleanPlay(
            double myAtcWeight, double rivalAtcWeight,
            List<Card> myPlayCards, List<Card> rivalPlayCards,
            boolean[] rivalCardEnable, int rivalCardEnableCount,
            double lazyLevel
    ){
//        存储我方哪些随从可以攻击
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
            log.info("我方没有能动的随从");
            return false;
        }
        initWeight = finalWeight = calcInitWeight(myPlayCards, myAtcWeight, rivalPlayCards, rivalAtcWeight, lazyLevel);
        long start = System.currentTimeMillis();
        if (rivalCardEnableCount >= 5 && myCardEnableCount >= 6){
            log.info("场面复杂，已开启多线程计算");
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
                        atc(atcFlag, finalFirstMyCardEnable, myPlayCards, rivalPlayCards, finalRivalPlayIndex, myCard, rivalCard, myAtcWeight, rivalAtcWeight, myCardEnable, rivalCardEnable, lazyLevel);
                        recursionCleanPlay(myAtcWeight, rivalAtcWeight, myPlayCardsCopy, rivalPlayCardsCopy, myCardEnable, rivalCardEnable, atcFlag, finalFirstMyCardEnable + 1, lazyLevel);
                    });
                }
            }
            CompletableFuture.allOf(futures).get();
        }else {
            log.info("单线程计算");
            recursionCleanPlay(myAtcWeight, rivalAtcWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, new boolean[myPlayCards.size()][rivalPlayCards.size()], 0, lazyLevel);
        }
        log.info("思考解怪耗时：" + (System.currentTimeMillis() - start) + "ms");
        return cleanAction(myPlayCards, rivalPlayCards);
    }

    private boolean cleanAction(List<Card> myPlayCards, List<Card> rivalPlayCards){
        if (finalWeight > initWeight){
            HashMap<String, String> entityIdMap = new HashMap<>();
            StringBuilder stringBuilder = new StringBuilder("结果：");
            for (int i = 0; i < finalActFlag.length; i++) {
                boolean[] temp = finalActFlag[i];
                for (int j = 0; j < temp.length; j++) {
                    if (finalActFlag[i][j]){
                        Card myCard = myPlayCards.get(i), rivalCard = rivalPlayCards.get(j);
                        entityIdMap.put(myCard.getEntityId(), rivalCard.getEntityId());
                        stringBuilder.append(myCard.getCardId()).append("攻击").append(rivalCard.getCardId()).append(", ");
                    }
                }
            }
            log.info(stringBuilder.toString());
            if (entityIdMap.isEmpty()){
                log.info("解不动");
            }else {
                for (Map.Entry<String, String> entry : entityIdMap.entrySet()) {
                    int rivalIndex = findByEntityId(this.rivalPlayCards, entry.getValue());
                    if (rivalIndex == -1){
                        log.info("找不到被攻击的敌方随从：" + entry.getValue());
                    }else {
                        int myIndex = findByEntityId(this.myPlayCards, entry.getKey());
                        if (myIndex != -1){
                            myPlayPointToRivalPlay(myIndex, rivalIndex);
                        }else if (Objects.equals(this.myPlayArea.getHero().getEntityId(), entry.getKey())){
                            myHeroPointToRivalPlay(rivalIndex);
                        }else {
                            log.info("找不到攻击的我方随从：" + entry.getKey());
                        }
                    }
                }
                return true;
            }
        }else {
            log.info("没啥好解的");
        }
        return false;
    }

    private void recursionCleanPlay(
            double myAtcWeight, double rivalAtcWeight,
            List<Card> myPlayCards, List<Card> rivalPlayCards,
            boolean[] myCardEnable, boolean[] rivalCardEnable,
            boolean[][] atcFlag, int myPlayIndex, double lazyLevel
    ){
        if (myPlayIndex == myPlayCards.size()){
            double weight = calcWeight(myPlayCards, myAtcWeight) - calcWeight(rivalPlayCards, rivalAtcWeight) + calcAtcWeight(myPlayCards, myAtcWeight, atcFlag, myCardEnable) * lazyLevel;
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
                    atc(atcFlag, myPlayIndex, myPlayCards, rivalPlayCards, rivalIndex, myCard, rivalCard, myAtcWeight, rivalAtcWeight, myCardEnable, rivalCardEnable, lazyLevel);
                    recursionCleanPlay(myAtcWeight, rivalAtcWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, lazyLevel);
                }
            }
        }else {
            recursionCleanPlay(myAtcWeight, rivalAtcWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, lazyLevel);
        }
    }

    private int calcRivalTotalAtcForClean(List<Card> cards){
        int atc = 0;
        for (Card card : cards) {
            atc += card.getAtc() * (card.isWindFury()? 2 : 1);
        }
        atc += rivalPlayArea.getHero().getAtc() * (rivalPlayArea.getHero().isWindFury()? 2 :1);
        return atc;
    }
    private void atc(
            boolean[][]atcFlag, int myPlayIndex,
            List<Card> myPlayCards, List<Card> rivalPlayCards,
            int rivalIndex, Card myCard, Card rivalCard,
            double myAtcWeight, double rivalAtcWeight,
            boolean[] myCardEnable, boolean[] rivalCardEnable, double cleanScale
    ){
        if (calcCardBlood(rivalCard) > 0 && (myPlayIndex != 0 || calcCardBlood(myCard) > calcRivalTotalAtcForClean(rivalPlayCards))){
            atcFlag[myPlayIndex][rivalIndex] = true;
            minusHealth(myCard, rivalCard.getAtc());
            minusHealth(rivalCard, myCard.getAtc());
            recursionCleanPlay(myAtcWeight, rivalAtcWeight, myPlayCards, rivalPlayCards, myCardEnable, rivalCardEnable, atcFlag, myPlayIndex + 1, cleanScale);
            minusHealth(rivalCard, -myCard.getAtc());
            minusHealth(myCard, -rivalCard.getAtc());
            atcFlag[myPlayIndex][rivalIndex] = false;
        }
    }
    private double calcInitWeight(List<Card> myCards, double myAtcWeight, List<Card> rivalCards, double rivalAtcWeight, double cleanScale){
        return calcWeight(myCards, myAtcWeight) - calcWeight(rivalCards, rivalAtcWeight) + calcAtcWeight(myCards, myAtcWeight) * cleanScale;
    }
    @SuppressWarnings("all")
    private int minusHealth(Card card, int health){
        int newHealth = card.getHealth() - health;
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
    private double calcWeight(List<Card> cards, double atcWeight){
        double weight = 0D;
        for (Card card : cards) {
            weight += calcSingleWeight(calcCardBlood(card), card.getAtc(), atcWeight);
        }
        return weight;
    }
    private static final double PLUS_VALUE = 0.1D;
    private double calcSingleWeight(int blood, int atc, double actWeight){
        if (blood > 0){
            atc = Math.max(0, atc);
            return ((blood + PLUS_VALUE) + (atc + PLUS_VALUE) * actWeight) + atc * blood / 8D;
        }
        return 0;
    }
    private double calcAtcWeight(List<Card> cards, double atcWeight){
        double weight = 0D;
        for (Card card : cards) {
            if (canMove(card)) {
                weight += (card.getAtc() + PLUS_VALUE) * atcWeight;
            }
        }
        return weight;
    }
    private double calcAtcWeight(List<Card> cards, double atcWeight, boolean[][] atcFlag, boolean[] cardEnable){
        double weight = 0D;
        for (int i = 0; i < atcFlag.length; i++) {
            boolean[] temp = atcFlag[i];
            if (cardEnable[i]){
                boolean flag = true;
                for (boolean b : temp) {
                    if (b){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    weight += (cards.get(i).getAtc() + PLUS_VALUE) * atcWeight;
                }
            }
        }
        return weight;
    }
    protected List<Card> copyListAndAddHero(List<Card> cards, Card hero){
        List<Card> newCard = copyList(cards);
        newCard.add(0, hero);
        return newCard;
    }
    protected boolean containsDanger(List<Card> cards){
        for (Card card : cards) {
            if (isDanger(card)){
                return true;
            }
        }
        return false;
    }
    protected boolean containsBuff(List<Card> cards){
        for (Card card : cards) {
            if (isBuff(card)){
                return true;
            }
        }
        return false;
    }
    protected boolean isDanger(Card card){
        return  cardContains(card, 对空奥数法师)
                || cardContains(card, 刺豚拳手)
                || cardContains(card, 健谈的调酒师)
                || cardContains(card, 驻锚图腾)
                || cardContains(card, 携刃信使)
                || cardContains(card, 锈水海盗)
                || cardEquals(card, 旗标骷髅)
                || cardEquals(card, 农夫)
                || cardEquals(card, 火焰术士弗洛格尔)
                || cardContains(card, 船载火炮);
    }
    private boolean isBuff(Card card){
        return cardContains(card, 南海船长)
                || cardContains(card, 火舌图腾)
                || cardContains(card, 末日预言者)
                || cardContains(card, 恐狼前锋);
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
            if (canPointedByRival(rivalCard) && rivalCard.getCardType() == MINION && calcCardBlood(rivalCard) <= myAtc && (rivalCard.getAtc() < myBlood || myPlayCard.isDivineShield())){
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
                if (canPointedByRival(card) && card.getCardType() == MINION && calcCardBlood(card) <= myAtc){
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
}
