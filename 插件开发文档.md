## [插件市场](https://github.com/xjw580/Deck-Plugin-Market/tree/master)



## 插件开发文档

1. [模板](https://github.com/xjw580/Deck-Plugin-Market/blob/master/Deck-Plugin-Template/src/main/kotlin/TemplateStrategyDeck.kt)

2. [基础策略](https://github.com/xjw580/Hearthstone-Script/blob/master/Hearthstone-Script-Base-Deck/src/main/kotlin/club/xiaojiawei/HsCommonDeckStrategy.kt)

3. [开发套牌策略插件时对API的使用 #29](https://github.com/xjw580/Hearthstone-Script/issues/29)

4. [项目运行的方式如何加载插件 #54](https://github.com/xjw580/Hearthstone-Script/issues/54)

   ## 

## 插件开发步骤

#### 套牌策略插件

1. ##### 拉取模板项目

   ```cmd
   git clone https://github.com/xjw580/Deck-Plugin-Market.git
   ```

2. ##### 新建子模块

3. ##### 修改子模块

   - 创建插件类并实现`DeckPlugin`接口，模板项目中对应为`TemplatePlugin`

     > 该类用于描述插件信息

   - 创建套牌策略类并继承`DeckStrategy`类，模板项目中对应为`TemplateStrategyDeck`

     > 所有出牌逻辑都按此类规定执行
     >
     >
     >
     > 连接模板项目下的[hs_cards.db](https://github.com/xjw580/Deck-Plugin-Market/blob/master/hs_cards.db)（sqlite数据库）可查看卡牌信息
     > ```sql
     > select * from cards
     > ```


- 在resources目录下新建`META-INF/services`目录

- 创建两个文件`club.xiaojiawei.DeckPlugin`和`club.xiaojiawei.DeckStrategy`置于`META-INF/services`目录下

- `club.xiaojiawei.DeckPlugin`内容为插件类的全限定类名

- `club.xiaojiawei.DeckStrategy`内容为套牌策略类的全限定类名，如有多个套牌策略类可都写入，一行写一个类

4. ##### 打包

   ```cmd
   mvn clean package
   ```

5. ##### 将生成的jar包放入软件的plugin目录下

6. ##### 启动软件

7. ##### 可在软件的插件设置页中看到新加的插件信息

8. ##### 软件首页也可以选择插件中的套牌策略

9. ##### 自己写的插件如果想分享出来可以向[套牌插件市场项目](https://github.com/xjw580/Deck-Plugin-Market)提PR



#### 卡牌插件

> 描述卡牌的行为

类似`套牌策略插件`，参考[基础卡牌插件](https://github.com/xjw580/Hearthstone-Script/tree/master/Hearthstone-Script-Base-Card)

自`4.3.1-DEV`版本增加`mcts策略`，该策略将联动卡牌插件，卡牌插件中描述的卡牌可以被该策略理解。

##### 示例

1. [斩杀](Hearthstone-Script-Base-Card/src/main/kotlin/club/xiaojiawei/bean/warrior/Execute.kt)

   ```kotlin
   package club.xiaojiawei.bean.warrior
   
   import club.xiaojiawei.CardAction
   import club.xiaojiawei.bean.PlayAction
   import club.xiaojiawei.bean.Player
   import club.xiaojiawei.enums.CardTypeEnum
   import club.xiaojiawei.bean.War
   
   /**
    * [斩杀](https://hearthstone.huijiwiki.com/wiki/Card/69535)
    * @author 肖嘉威
    * @date 2025/1/18 8:19
    */
   
   private val cardIds = arrayOf<String>(
       "%CS2_108",
   )
   
   class Execute : CardAction.DefaultCardAction() {
   
       override fun generatePlayActions(war: War, player: Player): List<PlayAction> {
           val result = mutableListOf<PlayAction>()
           war.rival.playArea.cards.forEach { rivalCard ->
               if (rivalCard.cardType === CardTypeEnum.MINION && rivalCard.canBeTargetedByRivalSpells() && rivalCard.isInjured()) {
                   result.add(PlayAction({ newWar ->
                       findSelf(newWar)?.action?.power(rivalCard.action.findSelf(newWar))
                   }, { newWar ->
                       spendSelfCost(newWar)
                       removeSelf(newWar)?.let {
                           rivalCard.action.findSelf(newWar)?.let { rivalCard->
                               rivalCard.damage = rivalCard.bloodLimit()
                           }
                       }
                   }))
               }
           }
           return result
       }
   
       override fun createNewInstance(): CardAction {
           return Execute()
       }
   
       override fun getCardId(): Array<String> {
           return cardIds
       }
   
   }
   ```

   - 继承`CardAction.DefaultCardAction`类，`CardAction.DefaultCardAction`是`CardAction`的子类
   - `Execute`类重写了`generatePlayActions`方法，该方法表示生成可用的打出行为，`Execute`将遍历敌方可被法术指向且受伤的随从，并为每个符合该条件的随从生成一个`Action`，最后将这些`Action`返回，`mcts策略`将根据这些`Action`计算最好的打法

2. [焦油爬行者](Hearthstone-Script-Base-Card/src/main/kotlin/club/xiaojiawei/bean/warrior/TarCreeper.kt)

   ```kotlin
   package club.xiaojiawei.bean.warrior
   
   import club.xiaojiawei.CardAction
   import club.xiaojiawei.bean.War
   
   /**
    * [焦油爬行者](https://hearthstone.huijiwiki.com/wiki/Card/41418)
    * @author 肖嘉威
    * @date 2025/1/18 20:31
    */
   private val cardIds = arrayOf<String>(
       "%UNG_928",
   )
   
   class TarCreeper : CardAction.DefaultCardAction() {
   
       override fun triggerTurnStart(war: War) {
           super.triggerTurnStart(war)
           findSelf(war)?.let { card ->
               if (card.area.player === war.currentPlayer) {
                   card.atc -= 2
               }
           }
       }
   
       override fun triggerTurnEnd(war: War) {
           super.triggerTurnEnd(war)
           findSelf(war)?.let { card ->
               if (card.area.player === war.currentPlayer) {
                   card.atc += 2
               }
           }
       }
   
       override fun createNewInstance(): CardAction {
           return TarCreeper()
       }
   
       override fun getCardId(): Array<String> {
           return cardIds
       }
   
   }
   ```
   
   - 继承`CardAction.DefaultCardAction`类，`CardAction.DefaultCardAction`是`CardAction`的子类
   - `DeathwingMadAspect`类重写了`triggerTurnStart`和`triggerTurnEnd`方法，`triggerTurnStart`表示触发回合开始`triggerTurnEnd`表示触发回合结束，上面`triggerTurnStart`将此随从的攻击力降低两点，`triggerTurnEnd`将此随从的攻击力提高两点
