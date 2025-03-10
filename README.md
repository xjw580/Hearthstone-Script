![GitHub release](https://img.shields.io/github/release/xjw580/Hearthstone-Script.svg)  ![GitHub](https://img.shields.io/github/license/xjw580/Hearthstone-Script?style=flat-square)

# 炉石传说脚本（雷火服封禁力度大）
<img src="Hearthstone-Script/src/main/resources/resources/img/favicon.png" alt="Image description" width="100" height="100">



### 免责申明

本项目仅供学习交流 `Java`，`Kotlin` 和 `炉石传说` 玩法，不得用于任何违反法律法规及游戏协议的地方😡！！！。



### 捐赠

> 您的捐赠不仅是对代码的认可，更是对自由软件理念的支持。

<img src="payment-code.jpg" alt="Image description"  height="300">



### **[提Bug](https://github.com/xjw580/Hearthstone-Script/issues/new?template=01_bug_report.yml) · [提点子](https://github.com/xjw580/Hearthstone-Script/issues/new?template=02_feature_request.yml)**



### 开发计划

- [ ] 测试封禁行为与鼠标事件的模拟是否有关（此项测试完成前不会增加其他防封禁手段）



### 问题反馈

1. **任何问题都要通过提issue的方式提问，详细描述**
2. 在提issue时，请及时地回复作者的消息
3. 尽量附上日志文件，日志在软件根目录的log目录下



### 使用环境

- Windows（仅在Win10和Win11上测试过）
- JDK 21（**如无安装，第一次启动脚本会打开浏览器下载，然后安装就行了**）



### 支持的策略（更加智能的策略请自行编写插件）

- 秒投策略
- 基础策略：通用，未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可
- 激进策略：在基础策略的基础上无差别释放法术和打出战吼牌
- mcts策略：通用，需要卡牌插件支持




### 使用步骤

- **下载软件**
  
  - 在 [Gitee release](https://gitee.com/zergqueen/Hearthstone-Script/releases) 或 [Github release](https://github.com/xjw580/Hearthstone-Script/releases) 处下载（Gitee源只发布稳定版）
  - hs-script.zip包含完整依赖，下载此文件
  
- **初次使用**：
  
  - 阅读 [常见问题](常见问题.md)
  - 配置卡组：游戏里将要使用的卡组移动到一号卡组位
  - 选择卡组：软件里选择要使用的卡组策略(先选择模式)
  
- **启动软件**
  
  1. 执行 `hs-script.exe`
  
  2. 点击开始按钮或者使用热键 `Ctrl` + `P`
  
  3. 软件开始运行后可以关闭软件窗口，这将以托盘形式运行，此时只能检测到Java进程 
  
- **关闭软件**
  - 执行`force-stop.exe` 
  - 使用热键 `Alt` + `P`  
  - 程序托盘处点击退出

- **更新软件**
  - 软件内更新
    1. 软件内检测到新版本后可点击下载然后更新，如需更新开发版需到设置里打开更新开发版
  - 手动更新
    1. 在 [Gitee release](https://gitee.com/zergqueen/Hearthstone-Script/releases) 或 [Github release](https://github.com/xjw580/Hearthstone-Script/releases) 处下载所需版本的hs-script.zip包放置在旧版软件根目录下
    2. 手动删除根目录下的new_version_temp文件夹如果有的话
    3. 双击根目录下的update.exe



### 版本说明

> 软件版本分为稳定版和开发版

1. 稳定版

   - GA

     > 最稳定的版本(bushi)

   - PATCH

     > 对GA版本的紧急修复

2. 开发版

   - DEV

     > 在游戏和本地测试过

   - BETA

     > 仅在本地测试过

   - TEST

     > 未进行任何测试



### [常见问题](常见问题.md)



### [插件开发文档](插件开发文档.md )



### [项目文档](https://hearthstone-script-documentation.vercel.app/)



### [更新记录](更新记录.md) 
