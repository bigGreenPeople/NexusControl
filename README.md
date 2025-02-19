# NexusControl


![Android10](https://img.shields.io/badge/android-10.0%2B-blue.svg)
![GitHub top language](https://img.shields.io/github/languages/top/bigGreenPeople/SharkModTemplate)
![root](https://img.shields.io/badge/root%20require-red.svg?logo=android&labelColor=black)
![Magisk](https://img.shields.io/badge/Magisk-24.0%2B-black.svg)
![NexusControl](https://img.shields.io/github/downloads/bigGreenPeople/NexusControl/total)



NexusControl 是一款基于 Android 系统的 远程群控解决方案，旨在为开发者、企业和 Android 设备管理提供强大且灵活的功能。通过 NexusControl，用户可以实现对大量 Android 设备的远程控制、远程应用安装/卸载、Magisk 模块发布及管理，并且支持 Xposed 框架的远程模块管理。

该系统结合 Xposed 提供的灵活性，支持开发者自定义模块并通过平台进行分发，能够有效提升远程设备管理的效率和灵活性。同时，NexusControl 提供了一系列 私有 API，允许企业级客户根据自身需求进行二次开发与集成，满足不同使用场景的需求。

### [使用文档](https://1243596620.gitbook.io/sharkposed-wen-dang/)
### 联系方式:
- QQ:1243596620

# 快速开始

### 环境要求

*   **Magisk 24+** 版本
*   设备已 **Root**

### 帐号与Key

*   **NexusControl** 系统目前暂时不对外开放注册，所有系统帐号和设备 **Key** 需要联系管理员获取。
*   请确保您已获得必要的帐号和 Key，以便成功接入系统并进行设备管理。


### 脚本自动安装 NexusControl 模块
本模块是一个Magisk模块你可以直接在[releases下载](https://github.com/bigGreenPeople/NexusControl/releases)，手动安装。

如果您希望通过自动化脚本快速安装 **NexusControl** 模块，请按照以下步骤操作：

**前提条件：**

*   确保 **电脑 ADB 环境** 已正确配置。
*   手机通过 **USB 数据线** 连接到电脑，并且已经授权 **ADB** 和 **su** 权限。并且已开启 **Zygisk** 功能。
*   将模块**NexusControl.zip** 文件拷贝到项目**bin**目录下

**运行自动刷机脚本：**

*   在项目**bin**目录下找到并运行 `autoflash.bat `文件（Windows 系统）。
*   该脚本会自动进行以下操作：
    *   将 **NexusControl 模块** 文件刷入设备的 **Magisk 模块**。
    *   自动重启设备，以使模块生效。

**验证安装：**
*   刷入完成后，设备会自动重启。重启后，您可以进入 **Magisk Manager**，在 **模块** 页面查看 **NexusControl 模块** 是否已经启用。

在执行 `autoflash.bat` 脚本时，请确保手机已经 **授权 ADB 调试** 和 **Root 权限**，否则可能无法正常刷入模块

### **刷入授权信息**

为确保 **NexusControl** 模块能够正确连接并进行设备管理，您需要配置相关的授权信息。请按照以下步骤操作：

**步骤：**

1.  **修改** `key.text` **文件：**
    *   打开项目目录下的 `key.text` 文件。
    *   将文件中的内容替换为您获得的 **设备授权 Key**。确保 **Key** 内容正确无误。

2.  **修改** `config.json` **配置文件：**
    *   打开项目目录下的 `config.json` 文件。
    *   修改文件中的配置项：
        

``` json
{
  "hostIp": "8.154.23.1",
  "hostPort": "8080",
  "user": ""
}
```

将 `"user"` 字段修改为您 **注册使用的手机号码**（该手机号将与授权系统进行关联）。

3.  **运行授权脚本：**
    *   在项目**bin**目录下，找到并运行 `auth.bat` 文件（Windows 系统）。
    *   该脚本会自动：
        *   刷入您配置的 **Key** 和 **用户信息**。
        *   完成与服务器的授权连接。

4.  **验证授权：**
    *   运行脚本后，您可以查看命令行输出，确保授权过程没有出现错误。如果授权成功，设备会获得相应的权限并能够正常连接到 **NexusControl** 系统。
