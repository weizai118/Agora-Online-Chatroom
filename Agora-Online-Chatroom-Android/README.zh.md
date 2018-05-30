# Agora Online Chatroom

*Read this in another language: [English](README.md)*

Agora Online Chatroom 开源示例项目支持以下平台：

- [iOS](https://github.com/vissidarte/Agora-Online-Chatroom/tree/master/Agora-Online-Chatroom-iOS)
- [Android](https://github.com/vissidarte/Agora-Online-Chatroom/tree/master/Agora-Online-Chatroom-Android)

本文简要介绍了这个开源示例项目的功能以及演示该项目所需的准备工作。

## 介绍

Agora Online Chatroom 演示了如何通过声网 SDK 构建语音聊天室场景。功能主要包括：

- 创建和加入房间
- 文字消息聊天
- 连麦
- 更换聊天室的背景图片和音乐（仅限房主）

NOTE: 本示例项目基于 Agora Video SDK v2.2.0 和 Agora Signaling SDK v 1.3.0 创建。

## 运行环境
- Android Studio 2.0 +
- 真实 Android 设备 (Nexus 5X 或者其它设备)

NOTE: 部分模拟器会存在功能缺失或者性能问题，所以推荐使用真机。

## 运行示例程序
###1. 在 [Agora.io](https://dashboard.agora.io/signin/) 注册账号、创建自己的测试项目，并获取一个 App ID。

   NOTE: 更多详情请见 [获取 App ID](https://docs.agora.io/en/2.2/addons/Signaling/Agora%20Basics/key_signaling?platform=All%20Platforms).

###2. 在 **app/src/main/res/values/strings.xml** 文件内填写App ID。

```
<string name="agora_app_id"><#YOUR APP ID#></string>
```


###3. 集成 Agora Video SDK

集成 Agora Video SDK 有以下两种方式：

- 首选集成方式：在项目对应的模块的 "app/build.gradle" 文件的依赖属性中加入通过 JCenter 自动集成 Agora SDK 的地址：

```
implementation 'io.agora.rtc:full-sdk:2.2.0'
```
NOTE: 如果要在自己的应用中集成 Agora Video SDK，添加链接地址是最重要的一步。

- 手动集成方式：

   * 在 [Agora.io](https://www.agora.io/cn/download/) 下载最新版 **视频通话／视频直播 SDK**，
   * 解压后，将：
     - 其中 **libs** 文件夹下的 ***.jar** 复制到本项目的 **app/libs** 下。
     - 其中 **libs** 文件夹下的 **arm64-v8a**/**x86**/**armeabi-v7a** 复制到本项目的 **app/src/main/jniLibs** 下。

   * 在本项目的 **app**/**build.gradle** 文件依赖属性中添加如下依赖关系：

```
compile fileTree(dir: 'libs', include: ['*.jar'])
```

###4. 集成 Agora Signaling SDK


   * 在 [Agora.io](https://www.agora.io/cn/download/) 下载最新版 **信令 SDK**，
   * 解压后，将：
     - 其中 **libs** 文件夹下的 ***.jar** 复制到本项目的 **app**/**libs** 下。
     - 其中 **libs** 文件夹下的 **arm64-v8a**/**x86**/**armeabi-v7a** 复制到本项目的 **app/src/main/jniLibs** 下。

   * 在本项目的 **app**/**build.gradle** 文件依赖属性中添加如下依赖关系：

```
compile fileTree(dir: 'libs', include: ['*.jar'])
```

###5 用 Android Studio 打开本项目、连上设备、编译并运行。

NOTE: 你也可以使用 `Gradle` 直接编译运行本项目。

## 联系我们
- 完整的 API 文档见 [文档中心](https://docs.agora.io/cn/)
- 如果在集成中遇到问题, 你可以到 [开发者社区](https://dev.agora.io/cn/) 提问
- 如果有售前咨询问题, 可以拨打 400 632 6626，或加入官方Q群 12742516 提问
- 如果需要售后技术支持, 你可以在 [Agora Dashboard](https://dashboard.agora.io) 提交工单
- 如果发现了示例代码的错误, 欢迎提交 [issue](https://github.com/AgoraIO/Agora-Android-Tutorial-1to1/issues)

## 代码许可
The MIT License (MIT).