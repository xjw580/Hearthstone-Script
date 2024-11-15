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

> 未来会有较大变动，不建议编写
