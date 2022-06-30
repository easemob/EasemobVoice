# Easemob Voice iOS

## 先决条件
- Xcode 10.0+
- 物理 iOS 设备 (iPhone)
- 支持iOS模拟器
## 快速开始
本节向您展示如何准备、构建和运行应用程序。

## 下载 AgoraRte 框架
下载 [AgoraRte](https://github.com/AgoraIO-Usecase/AgoraVoice/releases/download/ios_1.1.0/AgoraRte.framework.zip) 并解压，然后将 "AgoraRte.framework" 文件移动到 "EasemobVoice-iOS/AgoraRte" 文件夹下。

## 获取 Keys
### 获取环信Kyes
1. 在[环信官网](https://console.easemob.com/user/register)创建一个开发者账号。
2. 创建一个项目,然后获取一组 **orgName**, **appName**, **clientID**, **clientSecret**。 

### 获取声网Keys
1. 在[agora.io](https://dashboard.agora.io/signin/)创建一个开发者账号。
2. 创建一个项目,  然后获取一组 **AppId**, **CustomerId**（头像-RESTful API-客户 ID）, **customerCertificate**。 

### 更新Keys文件
用环信的 **orgName**, **appName**, **clientID**, **clientSecret** 和声网的 **AppId**, **CustomerId**, **customerCertificate** 去更新 "Keys" 文件。

## Run
进入[EasemobVoice-iOS/EasemobVoice](https://github.com/easemob/EasemobVoice/tree/dev/EasemobVoice_iOS/EaseMobVoice/EaseMobVoice)路径，使用“pod install”命令链接所有依赖的框架和库。

打开[EasemobVoice.xcworkspace](https://github.com/easemob/EasemobVoice/tree/dev/EasemobVoice_iOS/EaseMobVoice/EaseMobVoice.xcworkspace)，连接你的iPhone设备并运行项目。确保应用了有效的配置文件，否则您的项目将无法运行。

## 关于 EaseMobVoice pod install 问题？

从 github 下载或者clone 的原始项目，执行 pod install 之前不能删除原始的 ‘Podfile.lock’ 文件！

直接执行 pod install 。

pod 安装完成之后，打开 ‘EaseMobVoice.xcworkspace’ ，build 之后会有报错！

解决方式：

​		1、Xcode 打开文件 ‘ArUtils.swift’  

<img src="/Users/zchong/Library/Application Support/typora-user-images/image-20220629164016482.png" alt="image-20220629164016482" style="zoom:50%;" />

​		将报错处进行如下替换：

```swift
struct OptionsDescription {
    static func any<T>(_ any: T?) -> String where T : CustomStringConvertible {
        return any != nil ? any!.description : "nil"
    }
}
```

​		2、Xcode 打开文件 ‘AGELog.swift’

<img src="/Users/zchong/Library/Application Support/typora-user-images/image-20220629164443334.png" alt="image-20220629164443334" style="zoom:50%;" />

​		将报错处进行如下替换：

```swift
struct OptionsDescription {
    static func any<T>(_ any: T?) -> String where T : CustomStringConvertible {
        return any != nil ? any!.description : "nil"
    }
}
```

修改两处报错之后，再次进行编译即可。


## 联系我们

- 如果你遇到了困难，可以先参阅 [常见问题](https://docs-im.easemob.com/)
- 如果你想了解更多官方示例，可以参考 [官方SDK示例](https://www.easemob.com/download/im)
- 如果你想了解声网SDK在复杂场景下的应用，可以参考 [官方场景案例](https://www.easemob.com/download/demo)
- 如果你想了解环信的一些社区开发者维护的项目，可以查看 [社区开源项目](https://www.imgeek.org/code/)
- 完整的 API 文档见 [文档中心](https://docs-im.easemob.com/) 
- 若遇到问题需要开发者帮助，你可以到  [开发者社区](https://www.imgeek.org/) 提问 
- 如果发现了示例代码的 bug，欢迎提交 [issue](https://github.com/easemob/EasemobVoice/issues)

## 代码许可
The MIT License (MIT)

# 来吧 老铁

