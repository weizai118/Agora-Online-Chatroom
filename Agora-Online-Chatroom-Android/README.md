# Agora Online Chat Room

*其他语言版本： [简体中文](README.zh.md)*

The Agora Android Online Chat Room sample app supports the following platforms: 

- [iOS](https://github.com/vissidarte/Agora-Online-Chatroom/tree/master/Agora-Online-Chatroom-iOS)
- [Android](https://github.com/vissidarte/Agora-Online-Chatroom/tree/master/Agora-Online-Chatroom-Android)

This readme describes how to run the Agora Android Online Chat Room sample app.

## Introduction

Built upon the Agora Video SDK v2.2.0 and the Agora Signaling SDK v1.3.0, the Agora Android Online Chat Room sample app is an open-source demo that integrates the voice chat-room feature into your Android apps.

This sample app allows you to:

- Create and join a chat room
- Send text messages
- Join the host
- Change the background image and music of the chat room (owner only)


## Development Environment

- Android Studio 2.0 or later
- Real Android devices, for example Nexus 5X

NOTE: Agora recommends using a real device instead of an emulator.

## Running the App
###1. Create a developer account at [Agora.io](https://dashboard.agora.io/signin/), and obtain an App ID.

   NOTE: For more information, see [Obtaining an App ID](https://docs.agora.io/en/2.2/addons/Signaling/Agora%20Basics/key_signaling?platform=All%20Platforms).

###2. Fill in your App ID in **app/src/main/res/values/strings.xml**.

```
<string name="agora_app_id"><#YOUR APP ID#></string>
```
###3. Use one of the following options to integrate the Agora Video SDK.

- The recommended option: Add the Agora SDK information as a Gradle compile dependency to the dependencies block of **app**/**build.gradle** in your project:

             implementation 'io.agora.rtc:full-sdk:2.2.0'

- The manual option:

   * Download the Agora Video SDK v2.2.0 from [agora.io](https://www.agora.io/en/download/).
   * Unzip the downloaded SDK package and:

         - copy ***.jar** under **/libs** to **app/libs**
         - copy **libs**/**arm64-v8a**/**x86**/**armeabi-v7a** to **app/src/main/jniLibs**.
   * Add the following command line to the dependency property of **app**/**build.gradle**. 

             compile fileTree(dir: 'libs', include: ['*.jar'])

###4 Integrate the Agora Signaling SDK.

   * Download the Agora Signaling SDK v1.3.0 from [agora.io](https://www.agora.io/en/download/).
   * Unzip the downloaded SDK package and:

         - copy ***.jar** under **/libs** to **app/libs**
         - copy **libs**/**arm64-v8a**/**x86**/**armeabi-v7a** to **app/src/main/jniLibs**.
   * Add the following command line to the dependency property of **app**/**build.gradle**. 

            compile fileTree(dir: 'libs', include: ['*.jar'])

###6. Open your project using Android Studio.
###7. Connect your Android device to your computer.
###8. Build and run the sample app.

   NOTE: You can also use `Gradle` to build and run the sample app.


## Contact Us

- You can find the API documentation at [Developer Center](https://docs.agora.io/en/).
- You can report issues about this demo at [issue](https://github.com/AgoraIO-Community/Agora-Online-Chatroom/issues).

## License
The MIT License (MIT).