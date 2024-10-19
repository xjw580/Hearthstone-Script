![GitHub release](https://img.shields.io/github/release/xjw580/Hearthstone-Script.svg)  ![GitHub](https://img.shields.io/github/license/xjw580/Hearthstone-Script?style=flat-square)

# 炉石传说脚本（雷火服封禁力度较严，有时间再研究优化下）
![favicon.ico](Hearthstone-Script/src/main/resources/static/img/favicon.ico)



### 免责申明

本项目仅供学习交流 `Java`，`Kotlin` 和 `炉石传说` 玩法，不得用于任何违反法律法规及游戏协议的地方😡！！！。



### 问题反馈

1. **任何问题都要通过提issue的方式提问，详细描述**
2. 在提issue时，请及时地回复作者的消息
3. 可以的话附上日志文件，日志在log目录下



### 使用环境

- Windows（仅在Win10和Win11上测试过）
- JDK 21（**如无安装，第一次启动脚本会打开浏览器下载，然后安装就行了**）



### 支持的策略（**需要更智能的策略自行编写插件或者提PR，目前只维护软件运行**）

- 秒投策略
- 基础策略(通用，未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可)
- 激进策略：在基础策略的基础上无差别释放法术和打出战吼牌


### 使用步骤

- **下载软件**
  - 在[release](https://gitee.com/zergqueen/Hearthstone-Script/releases)处下载
  - hs-script.zip包含完整依赖，一般下载此文件
  
- **初次使用**：
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



### [常见问题](QUESTION.md)



### [插件](PLUGIN_DEV.md )



### [项目文档](https://hearthstone-script-documentation.vercel.app/)



### [更新历史](HISTRORY.md)
