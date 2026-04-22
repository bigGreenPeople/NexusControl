<div align="center">

# NexusControl

**Enterprise Android Cloud Device Management Platform**

[![Vue](https://img.shields.io/badge/Vue-2.6-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Element UI](https://img.shields.io/badge/Element_UI-2.15-409EFF?logo=element)](https://element.eleme.io/)
[![RuoYi](https://img.shields.io/badge/RuoYi--Vue--Plus-4.8.0-red)](https://gitee.com/dromara/RuoYi-Vue-Plus)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

[Website](https://sharkposed.cn/) · [Manual](docs/manual/NexusControl使用手册_v2.md) · [中文](README.md)

</div>

---

## Still visiting devices on-site?

When your devices are spread across the country, a single on-site visit to fix a minor issue is costly. NexusControl lets you manage all Android devices from a browser — live status, remote debugging, app distribution, and framework management in one platform.

![Device Dashboard](docs/images/dashboard.png)

---

## Core Features

### 📡 Device Dashboard

Real-time visibility across all devices: green for online, blue for debug, gray for offline. Card-level operations for instant access to any device. Keyword search, group filtering, and batch group management built in.

<div align="center">
<img src="docs/images/devices.png" width="49%" />
<img src="docs/images/devices-card-menu.png" width="49%" />
</div>

---

### 🔧 Remote Debug

Click **Debug** on any device — the platform auto-establishes an FRP tunnel and generates a ready-to-copy ADB command. Paste it into your terminal and you're connected. Run logcat, push files, execute shell commands — exactly like sitting in front of the device. Click **Close** when done and the tunnel tears down cleanly.

<div align="center">
<img src="docs/images/device-detail.png" width="80%" />
</div>

---

### 📦 App Distribution

Drag and drop an APK — the platform automatically parses the app name, package name, and version. Push to individual devices or entire groups. Installation status is tracked in real time. Remote launch and restart are available from the device detail page.

<div align="center">
<img src="docs/images/app-pkg.png" width="49%" />
<img src="docs/images/app-upload.png" width="49%" />
</div>

---

### 🛡️ Framework Management

System-level control via Magisk modules. Manage SharkController, WLPosed, and LSPosed versions from the Core Management page. Publish a new version once — all online devices pull the update automatically, no manual intervention needed.

<div align="center">
<img src="docs/images/core-mgmt.png" width="49%" />
<img src="docs/images/device-detail-magisk.png" width="49%" />
</div>

---

### 📋 Task Center

Every operation generates a task record with real-time delivery and completion tracking. View results by batch or by individual device — execution status is never a black box.

<div align="center">
<img src="docs/images/task-center.png" width="80%" />
</div>

---

## Who It's For

| Scenario | Description |
|----------|-------------|
| Device ops teams | Remote access replaces on-site visits, cutting travel costs significantly |
| Unified app deployment | Push to hundreds of devices at once — no USB drives, no manual installs |
| Framework management | Centralized Magisk module versioning with automatic device-side updates |
| Device monitoring | Real-time online rate, debug status, and app runtime tracking |

---

## Device Installation

NexusControl uses a Magisk module installed on Android devices to gain system-level control capabilities.

### Prerequisites

| Requirement | Details |
|-------------|---------|
| Android version | Android 10 or above |
| Root framework | Magisk installed |

### Setup Steps

1. **Install the SharkController module**
   Flash the SharkController module package via Magisk, then reboot the device for the module to take effect.

2. **Activate via QR code**
   After reboot, generate an activation QR code from the platform management console and scan it on the device. The device will appear in the cloud device list immediately after scanning.

3. **Confirm online status**
   Return to the platform **Device Dashboard** — a green card indicates the device has connected successfully.

---

## Contact

- 🌐 Website: [https://sharkposed.cn/](https://sharkposed.cn/)
- 📖 Manual: [NexusControl User Manual](docs/manual/NexusControl使用手册_v2.md)

For more information or to request a trial, please reach out via our website.
