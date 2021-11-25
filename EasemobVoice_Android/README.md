# EasemobVoice_Android

## 开始前，请确保你的开发环境满足如下条件：
- Android Studio 4.0.0 或以上版本。
- Android 4.4 或以上版本的设备。部分模拟机可能无法支持本项目的全部功能，所以推荐使用真机。

## 操作步骤

### 获取示例项目
前往 GitHub 下载或克隆 EasemobVoice_Android 示例项目。

### 注册环信
前往[环信官网](https://console.easemob.com/user/register)注册项目，生成appKey 替换工程app中AndroidManifest.xml里配置的appkey
<meta-data
    android:name="EASEMOB_APPKEY"
    android:value="#your appkey#" /> 


替换环信CLIENT_ID和CLIENT_SECRET：将src/main/java/io/agora/agoravoice/im/service/IMService.java 第14--15行CLIENT_ID和CLIENT_SECRET的值换成自己的

### 注册Agora
前往 [agora.io](https://dashboard.agora.io/signin/) 注册项目，替换src/main/res/values/strings.xml 第15--17行 app_id、customer_id和customer_certificate

### 数据存储
Leanclould
前往 [Leancloud官网](https://www.leancloud.cn/)  注册项目，生产 appId、appKey、server_url。
替换工程 src/main/java/io/agora/agoravoice/AgoraApplication.java  第57行


## 运行示例项目

- 开启 Android 设备的开发者选项，通过 USB 连接线将 Android 设备接入电脑。

- 在 Android Studio 中，点击 Sync Project with Gradle Files 按钮，同步项目。

- 在 Android Studio 左下角侧边栏中，点击 Build Variants 选择对应的平台。

- 点击 Run app 按钮。运行一段时间后，应用就安装到 Android 设备上了。

- 打开应用，即可使用。

## 联系我们
 - 如果你遇到了困难，可以先参阅 [常见问题](https://docs-im.easemob.com/) 
 - 如果你想了解更多官方示例，可以参考
   [官方SDK示例](https://www.easemob.com/download/im)
  - 如果你想了解环信SDK在多个场景下的应用，可以参考
   [官方场景案例](https://www.easemob.com/download/demo)
   - 如果你想了解环信的一些社区开发者维护的项目，可以查看 [社区开源项目](https://www.imgeek.org/code/) 完整的
   - API 文档见 [文档中心](https://docs-im.easemob.com/) 
   - 若遇到问题需要开发者帮助，你可以到
   [开发者社区](https://www.imgeek.org/) 提问 
   - 如果发现了示例代码的 bug，欢迎提交
   [issue](https://github.com/easemob/EasemobVoice/issues)
   
   ## 代码许可
The MIT License (MIT)

# 来吧 老铁

