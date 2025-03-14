![GitHub release](https://img.shields.io/github/release/xjw580/Hearthstone-Script.svg)  ![GitHub](https://img.shields.io/github/license/xjw580/Hearthstone-Script?style=flat-square)

# 🎮 炉石传说脚本（雷火服封禁力度大）
<img src="Hearthstone-Script/src/main/resources/resources/img/favicon.png" alt="Image description" width="100" height="100">

---

## ⚠️ 免责申明

本项目仅供学习交流 **`Java`**、**`Kotlin`** 以及 **《炉石传说》玩法**，不得用于任何违反法律法规及游戏协议的地方！🚨😡  

---

## ❤️ 捐赠支持  

> 您的捐赠不仅是对代码的认可，更是对自由软件理念的支持。  

<img src="payment-code.jpg" alt="Image description" height="300">  

---

## 🛠 [提Bug](https://github.com/xjw580/Hearthstone-Script/issues/new?template=01_bug_report.yml) · [提点子](https://github.com/xjw580/Hearthstone-Script/issues/new?template=02_feature_request.yml)  

---

## 🎯 开发计划  

- [ ] **测试封禁行为是否与鼠标事件模拟有关**（此项测试完成前不会增加其他防封禁手段）  

---

## 📩 问题反馈  

1. **任何问题请通过 issue 提问，并详细描述** 📌  
2. 在 issue 讨论过程中，请及时回复作者的消息 📬  
3. **尽量附上日志文件**，日志位于软件根目录的 `log` 目录下 📜  

---

## 💻 使用环境  

- 🖥 **Windows**（仅测试过 Win10 & Win11）  
- ☕ **JDK 21**（若未安装，首次启动脚本时会自动下载并安装）  

---

## 🎮 支持的策略（更智能的策略请自行编写插件）  

- ⚡ **秒投策略**  
- 🏹 **基础策略**：通用，未对卡牌和卡组适配，自行组一套无战吼无法术的套牌即可  
- 🔥 **激进策略**：在基础策略的基础上**无差别释放法术**并**打出战吼牌**  
- 🤖 **MCTS 策略**：通用，需要卡牌插件支持  

---

## 🚀 使用步骤  

### 📥 **下载软件**  

📌 在 [Gitee release](https://gitee.com/zergqueen/Hearthstone-Script/releases) 或 [Github release](https://github.com/xjw580/Hearthstone-Script/releases) 下载（Gitee 源仅发布稳定版）  
📌 **下载 `hs-script.zip`（包含完整依赖）**  

### 🛠 **初次使用**  

✅ 阅读 [常见问题](常见问题.md)  
✅ **配置卡组**：游戏内将要使用的卡组移动到**一号卡组位**  
✅ **选择策略**：软件内选择要使用的卡组策略（先选择模式）  

### ▶ **启动软件**  

1️⃣ 执行 `hs-script.exe` 🖱️  
2️⃣ 点击 **开始按钮** 或使用快捷键 `Ctrl + P` 🎯  
3️⃣ **运行后可关闭软件窗口**，软件将以**托盘模式**运行，此时只能检测到 Java 进程 📦  

### ❌ **关闭软件**  

🔹 执行 `force-stop.exe` 🛑  
🔹 使用快捷键 `Alt + P` ⏹  
🔹 程序托盘**右键退出**  

### 🔄 **更新软件**  

📌 **软件内更新**  

1️⃣ 软件检测到新版本后，可点击下载更新  
2️⃣ 如需更新 **开发版**，请在设置里打开 **更新开发版** 选项  

📌 **手动更新**  

1️⃣ 在 [Gitee release](https://gitee.com/zergqueen/Hearthstone-Script/releases) 或 [Github release](https://github.com/xjw580/Hearthstone-Script/releases) 下载所需版本的 `hs-script.zip`，并放置在旧版软件根目录下  
2️⃣ **删除** 旧版软件根目录下的 `new_version_temp` 文件夹（如果存在）  
3️⃣ 双击 `update.exe` 完成更新  

---

## 📌 版本说明  

软件版本分为 **稳定版** 和 **开发版**  

✅ **稳定版**  

- **GA** 🏆 最稳定的版本（bushi）  
- **PATCH** 🔧 GA 版本的紧急修复  

🛠 **开发版**  

- **DEV** ✅ 在游戏和本地测试过  
- **BETA** 🛠 仅在本地测试过  
- **TEST** 🚧 未进行任何测试  

---

## 📚 相关文档  

📖 **[常见问题](常见问题.md)**  
📖 **[插件开发文档](插件开发文档.md)**  
📖 **[项目文档](https://hearthstone-script-documentation.vercel.app/)**  
📖 **[更新记录](更新记录.md)**  

---

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=xjw580/Hearthstone-Script&type=Date)](https://www.star-history.com/#xjw580/Hearthstone-Script&Date)