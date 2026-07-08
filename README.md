# DeepSeek View - Android App（烂尾）

一个 Android 原生外壳应用，封装 DeepSeek 网页体验，同时提供余额查询、Token 用量图表、API Key 管理和在线充值功能。

## 📱 核心功能

| 功能 | 说明 |
|------|------|
| **手机号登录** | Firebase Phone Auth，短信验证码登录 |
| **微信登录** | 集成微信开放平台 SDK 授权登录 |
| **余额查询** | 调用 DeepSeek `/user/balance` API，可视化展示余额 |
| **Token 用量** | 调用 `/user/usage` API，圆环图展示输入/输出 Token 分布 |
| **API Key 管理** | AES-256 加密本地存储，支持添加/查看/删除 |
| **在线充值** | WebView 内嵌 platform.deepseek.com 充值页面 |
| **AI 对话** | WebView 封装 chat.deepseek.com，保持完整聊天体验 |

## 🛠️ 技术栈

- **语言**: Kotlin 2.1
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM (ViewModel + StateFlow)
- **网络**: Retrofit 2 + OkHttp 4
- **安全**: AndroidX Security Crypto (EncryptedSharedPreferences)
- **登录**: Firebase Authentication + WeChat SDK
- **最低 SDK**: Android 8.0 (API 26)
- **目标 SDK**: Android 15 (API 35)

## 🚀 快速开始

### 1. 环境要求

- Android Studio Hedgehog (2024.1+) 或更新版本
- JDK 17
- Gradle 8.7+

### 2. 克隆项目

```bash
git clone <your-repo-url>
cd DeepSeek-View
```

用 Android Studio 打开项目目录，等待 Gradle 同步完成。

### 3. 配置 Firebase

1. 前往 [Firebase Console](https://console.firebase.google.com/) 创建新项目
2. 添加 Android 应用，包名为 `com.deepseek.view`
3. 下载 `google-services.json` 并替换 `app/google-services.json`
4. 在 Firebase Console 中启用 **Phone Authentication**

### 4. 配置微信登录 (可选)

1. 前往 [微信开放平台](https://open.weixin.qq.com/) 注册应用
2. 获取 AppID
3. 修改 [Constants.kt](app/src/main/java/com/deepseek/view/util/Constants.kt) 中的 `WX_APP_ID`

### 5. 运行

1. 连接 Android 设备或启动模拟器
2. 点击 **Run 'app'** (▶️)

## 📁 项目结构

```
app/src/main/java/com/deepseek/view/
├── DeepSeekApp.kt              # Application 初始化
├── MainActivity.kt             # 主 Activity (Compose 入口)
├── data/
│   ├── api/
│   │   ├── DeepSeekApi.kt     # Retrofit API 接口定义
│   │   └── RetrofitClient.kt  # HTTP 客户端单例
│   ├── local/
│   │   ├── SecureStorage.kt   # 加密存储 (API Key)
│   │   └── PreferencesManager.kt # 普通设置存储
│   ├── model/
│   │   └── Models.kt          # 数据模型
│   └── repository/
│       └── DeepSeekRepository.kt # 数据仓库
├── navigation/
│   └── NavGraph.kt            # 导航图
├── ui/
│   ├── components/
│   │   ├── BalanceCard.kt     # 余额卡片组件
│   │   └── UsageChart.kt      # Token 用量图表组件
│   ├── screens/
│   │   ├── LoginScreen.kt     # 登录界面
│   │   ├── HomeScreen.kt      # 首页仪表盘
│   │   ├── ApiKeyScreen.kt    # API Key 管理
│   │   ├── RechargeScreen.kt  # 充值 WebView
│   │   └── ChatScreen.kt      # AI 对话 WebView
│   └── theme/
│       ├── Color.kt           # 调色板
│       ├── Theme.kt           # Material 3 主题
│       └── Type.kt            # 字体排版
├── util/
│   ├── Constants.kt           # 应用常量
│   ├── WeChatHelper.kt        # 微信 SDK 助手
│   └── WeChatAuthReceiver.kt  # 微信回调广播
├── viewmodel/
│   ├── LoginViewModel.kt      # 登录逻辑
│   ├── MainViewModel.kt       # 首页数据逻辑
│   └── ApiKeyViewModel.kt     # API Key 管理逻辑
└── wxapi/
    └── WXEntryActivity.kt     # 微信回调 Activity
```

## 🔑 API 对接说明

应用通过 Retrofit 调用 DeepSeek 官方 API：

- **余额**: `GET https://api.deepseek.com/user/balance` (Header: `Authorization: Bearer <key>`)
- **用量**: `GET https://api.deepseek.com/user/usage`

API Key 由用户在应用内输入，使用 AES-256-GCM 加密存储在设备本地。

## 📝 待办 / 自定义

- [ ] 替换 `google-services.json` 为实际 Firebase 配置
- [ ] 替换 `WX_APP_ID` 为实际微信 AppID
- [ ] 生成正式版签名密钥 (Release Keystore)
- [ ] 配置 ProGuard 规则（release 构建）
- [ ] 添加 DeepSeek API Key 的有效性验证
- [ ] 实现推送通知（余额低预警等）

## 📄 License

MIT
