先决条件
Xcode 10.0+
物理 iOS 设备 (iPhone)
支持iOS模拟器
快速开始
本节向您展示如何准备、构建和运行应用程序。

下载 AgoraRte 框架
下载AgoraRte，解压并移动“AgoraRte.framework”到“AgoraVoice-iOS/AgoraRte”。

获取密钥
在agora.io创建一个开发者账户。完成注册过程后，您将被重定向到仪表板。
创建一个项目，并获取一个AppId，一个CustomerId，一个customerCertificate。
使用AppId、CustomerId、customerCertificate更新“密钥”文件。
跑
进入“EasemobVoice-iOS/EasemobVoice”路径，使用“pod install”命令链接所有依赖的框架和库。

打开“AgoraVoice.xcworkspace”，连接你的iPhone设备并运行项目。确保应用了有效的配置文件，否则您的项目将无法运行。

联系我们
对于潜在问题，请先查看我们的常见问题解答
深入Agora SDK Samples查看更多教程
查看Agora 用例以获得更复杂的实际用例
开发者社区管理的仓库可以在Agora 社区找到
您可以在文档中心找到完整的 API 文档
如果在集成过程中遇到问题，可以在Stack Overflow 中提问
您可以提交有关此问题示例的错误
执照
麻省理工学院许可证 (MIT)













# Agora Voice iOS

*[English](README.md) | 中文*

## 环境准备

- XCode 10.0 +
- iOS 真机设备
- 支持模拟器

## 运行示例程序

这个段落主要讲解了如何编译和运行实例程序。

### 下载 AgoraRte Framework
下载 [AgoraRte](https://github.com/AgoraIO-Usecase/AgoraVoice/releases/download/ios_1.1.0/AgoraRte.framework.zip) 并解压，然后将 "AgoraRte.framework" 文件移动到 "AgoraVoice-iOS/AgoraRte" 文件夹下。

### 获取 Keys

1. 在[agora.io](https://dashboard.agora.io/signin/)创建一个开发者账号。
2. 创建一个项目,  然后获取一组 **AppId**, **CustomerId**, **customerCertificate**。 
3. 用 **AppId**, **CustomerId**, **customerCertificate** 去更新 "Keys" 文件。


### Run
1. 在 "AgoraVoice-iOS/AgoraVoice" 路径下，使用 "pod install" 命令去链接所有需要依赖的库。
2. 最后使用 Xcode 打开 AgoraVoice.xcworkspace，连接 iPhone／iPad 测试设备，设置有效的开发者签名后即可运行。

## 联系我们

- 如果你遇到了困难，可以先参阅 [常见问题](https://docs.agora.io/cn/faq)
- 如果你想了解更多官方示例，可以参考 [官方SDK示例](https://github.com/AgoraIO)
- 如果你想了解声网SDK在复杂场景下的应用，可以参考 [官方场景案例](https://github.com/AgoraIO-usecase)
- 如果你想了解声网的一些社区开发者维护的项目，可以查看 [社区](https://github.com/AgoraIO-Community)
- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 若遇到问题需要开发者帮助，你可以到 [开发者社区](https://rtcdeveloper.com/) 提问
- 如果需要售后技术支持，你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的 bug，欢迎提交 [issue](https://github.com/AgoraIO-Usecase/AgoraVoice/issues)

## 代码许可

The MIT License (MIT)
