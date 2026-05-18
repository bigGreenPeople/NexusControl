# NexusFlow（一念流）— AI 驱动的移动端 RPA 平台

> **Think Once, Flow Everywhere · 一念触发，流转万机**
> 
> 基于 NexusControl 系统级能力，融合 LLM 智能决策，打造下一代移动端自动化平台。

---

## 目录

1. [产品定位](#1-产品定位)
2. [核心优势](#2-核心优势)
3. [整体架构](#3-整体架构)
4. [核心功能设计](#4-核心功能设计)
   - 4.1 脚本引擎（基础层）
   - 4.2 AI 魔法生成（生成层）
   - 4.3 运行时 AI 兜底（智能层）
   - 4.4 Vision Agent 模式（未来层）
5. [JS API 设计（Auto.js 兼容）](#5-js-api-设计autojs-兼容)
6. [AI 集成设计](#6-ai-集成设计)
7. [数据模型](#7-数据模型)
8. [前端页面规划](#8-前端页面规划)
9. [落地计划](#9-落地计划)
10. [竞品对比](#10-竞品对比)

---

## 1. 产品定位

### 是什么

NexusFlow 是 NexusControl 平台的 AI 自动化模块，让用户通过**自然语言描述**或**编写 JS 脚本**，批量控制多台 Android 设备完成复杂自动化任务。

### 目标用户

| 用户类型 | 使用方式 | 典型场景 |
|---------|---------|---------|
| 运营人员 | 自然语言 → AI 生成脚本 → 执行 | 抖音批量发布、电商操作 |
| 开发者 | 直接编写 JS 脚本 | 定制复杂业务逻辑 |
| 企业用户 | 任务模板 + 参数化配置 | 定时批量任务 |

### 一句话价值

**用说话的方式，同时操控一百台手机。**

---

## 2. 核心优势

与市面产品的根本差异：

```
权限层级对比：

影刀RPA      ADB 级         需 PC 保持连接，设备被动执行
Auto.js      AccessibilityService 级  需用户授权，可被系统杀死
触动精灵     Root 级         Lua 脚本，无远程管理
NexusFlow   system_process 级  无需授权、批量管理、AI 驱动
               ↑
           最高权限，无可比拟
```

| 能力 | 影刀 | Auto.js | NexusFlow |
|------|------|---------|-----------|
| 执行位置 | PC 端控设备 | 设备端 | 设备端 |
| 权限深度 | ADB | Accessibility | **system_process** |
| AI 生成脚本 | ✅ 但不可编辑 | ❌ | ✅ **可编辑** |
| 运行时 AI 兜底 | ❌ | ❌ | ✅ |
| UI 结构获取 | UIAutomator dump | Accessibility | **IAccessibilityManager（最快）** |
| 多设备批量 | 有限 | ❌ | ✅ MQTT 批量下发 |
| Xposed 集成 | ❌ | ❌ | ✅ |
| 断网可运行 | ❌ | ✅ | ✅（主流程） |

---

## 3. 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户侧                               │
│   shark-vue NexusFlow 页面                                  │
│   ┌──────────────┐  ┌──────────────┐  ┌─────────────────┐  │
│   │ 任务模板编辑器 │  │  脚本编辑器  │  │  执行监控面板   │  │
│   └──────────────┘  └──────────────┘  └─────────────────┘  │
└─────────────────────────────┬───────────────────────────────┘
                              │ HTTP / WebSocket
┌─────────────────────────────▼───────────────────────────────┐
│                      shark-server                            │
│                                                             │
│  ┌─────────────────┐   ┌──────────────────────────────────┐ │
│  │  脚本管理模块    │   │         AI 调度模块              │ │
│  │  - 存储/版本    │   │  ┌─────────────────────────────┐ │ │
│  │  - 参数化模板   │   │  │  Phase1: 自然语言→脚本生成  │ │ │
│  │  - 定时任务     │   │  │  Phase2: 运行时异常处理     │ │ │
│  └─────────────────┘   │  │  Phase3: Vision Agent 模式  │ │ │
│                        │  └─────────────────────────────┘ │ │
│  ┌─────────────────┐   │         │ LLM API 调用            │ │
│  │  执行结果收集    │   │   Claude / GPT-4V / 文心 等      │ │
│  │  - 日志存储     │   └──────────────────────────────────┘ │
│  │  - 截图归档     │                                        │
│  └─────────────────┘                                        │
└─────────────────────────────┬───────────────────────────────┘
                              │ MQTT
┌─────────────────────────────▼───────────────────────────────┐
│                     Android 设备（SharkPosed）               │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                  NexusFlow 脚本引擎                   │  │
│  │                                                      │  │
│  │  Rhino JS 引擎                                       │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────────┐ │  │
│  │  │ 触控 API   │  │ 截图 API   │  │  文件/HTTP API │ │  │
│  │  │ tap/swipe  │  │ capture    │  │  files/http    │ │  │
│  │  └────────────┘  └────────────┘  └────────────────┘ │  │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────────┐ │  │
│  │  │ 控件树 API │  │ 图像识别   │  │  AI 回调 API   │ │  │
│  │  │ IAccMgr    │  │ findImage  │  │  ai.fix()      │ │  │
│  │  └────────────┘  └────────────┘  └────────────────┘ │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  底层能力：InputManager.injectInputEvent / ScreenShot       │
│            IAccessibilityManager / ActivityManagerService   │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. 核心功能设计

### 4.1 脚本引擎（基础层）

**技术选型：Rhino（Mozilla JS 引擎，纯 Java）**

选择理由：
- 纯 Java 实现，无需 JNI，直接嵌入 Daemon
- Auto.js 同款引擎，API 兼容性最好
- Java 对象与 JS 互调零成本

**嵌入方式：**

```java
// ServiceManager.java 中初始化
Context rhino = Context.enter();
rhino.setOptimizationLevel(-1); // 解释模式，避免 dex 限制
Scriptable scope = rhino.initStandardObjects();

// 注入 NexusFlow API
ScriptableObject.putProperty(scope, "tap",
    new NativeJavaMethod(NexusFlowApi.class.getMethod("tap", int.class, int.class), "tap"));
// ... 注入所有 API

// 执行脚本
rhino.evaluateString(scope, scriptCode, "nexusflow", 1, null);
```

**脚本执行流程：**

```
MQTT 下发 {type: "run_script", scriptId: "xxx", params: {...}}
    ↓
ScriptExecutor.run(scriptId, params)
    ↓
从本地缓存/下载脚本内容
    ↓
Rhino 引擎执行
    ↓
执行结果/日志通过 MQTT 上报
```

---

### 4.2 AI 魔法生成（生成层）

用户用自然语言描述任务，LLM 生成可编辑的 JS 脚本。

**生成流程：**

```
用户输入：
"进入抖音发布作品 文案：{{caption}} 作品：{{video_url}}"

          ↓ POST /api/nexusflow/generate

发给 LLM 的 Prompt：
┌────────────────────────────────────────────────────┐
│ System:                                            │
│ 你是 NexusFlow 脚本生成专家。                      │
│ 根据用户描述生成 Android 自动化 JS 脚本。          │
│ 可用 API：tap(x,y) swipe(...) input(text)         │
│           app.launch(pkg) sleep(ms)               │
│           captureScreen() ai.findElement(desc)    │
│           ai.findAndTap(desc) ai.fix(context)     │
│ 对于不确定位置的元素，使用 ai.findElement() 替代  │
│ 硬编码坐标。参数用 {{paramName}} 表示。           │
│                                                    │
│ User:                                              │
│ 进入抖音发布作品 文案：{{caption}} 作品：{{url}}  │
└────────────────────────────────────────────────────┘

          ↓ LLM 返回

生成的脚本（用户可编辑）：
```

```javascript
/**
 * 发布抖音作品
 * @param {string} caption - 文案内容
 * @param {string} video_url - 视频链接
 */
function main({ caption, video_url }) {
    // Step 1: 下载视频
    var videoPath = "/sdcard/nexusflow/tmp_video.mp4"
    http.download(video_url, videoPath)

    // Step 2: 打开抖音
    app.launch("com.ss.android.ugc.aweme")
    sleep(3000)

    // Step 3: 点击发布
    ai.findAndTap("底部发布/加号按钮")
    sleep(2000)

    // Step 4: 选择视频
    ai.findAndTap("从相册选择视频")
    sleep(1000)
    selectFile(videoPath)
    sleep(3000)

    // Step 5: 填写文案
    ai.findAndTap("添加作品描述输入框")
    input(caption)

    // Step 6: 发布
    ai.findAndTap("发布按钮")
    sleep(2000)

    return { success: true, message: "发布完成" }
}
```

---

### 4.3 运行时 AI 兜底（智能层）

脚本执行过程中，遇到未知情况时实时调用 LLM 处理。

**核心 API：`ai.fix()` / `ai.findElement()` / `ai.findAndTap()`**

**设备端实现：**

```javascript
// 脚本内调用
ai.findAndTap("发布按钮")

// 等价于设备端执行：
function findAndTap(description) {
    // 1. 收集上下文
    var context = {
        screenshot: captureScreen().toBase64(),
        ui_tree: getUITree(),        // IAccessibilityManager 获取
        description: description,
        task_prompt: GLOBAL_TASK_PROMPT
    }

    // 2. 上报给服务端 AI 模块
    var result = http.post(SERVER + "/api/nexusflow/ai/find", context)

    // 3. 执行返回的操作
    // result: { action: "tap", x: 540, y: 1780, reason: "找到底部加号按钮" }
    if (result.action == "tap") {
        tap(result.x, result.y)
    }
}
```

**后台弹窗守护线程：**

```javascript
// 脚本启动时自动开启弹窗监控
threads.start(function() {
    while (SCRIPT_RUNNING) {
        var ui = getUITree()
        // 简单规则优先（快）
        var closeBtn = ui.findByText(["关闭", "×", "取消", "我知道了", "拒绝"])
        if (closeBtn && isPopup(closeBtn)) {
            tap(closeBtn.x, closeBtn.y)
            log("弹窗守护：自动关闭弹窗")
            sleep(500)
            continue
        }
        sleep(1500)
    }
})
```

**服务端 AI 处理逻辑：**

```
POST /api/nexusflow/ai/find
{
  screenshot: "base64...",
  ui_tree: {...},
  description: "发布按钮",
  task_prompt: "在抖音发布作品",
  system_prompt: "遇到弹窗关闭，遇到权限申请点允许"
}

          ↓

发给 Vision LLM：
[截图] + [UI树文本] + "请找到「发布按钮」的坐标，返回 JSON"

          ↓ LLM 返回

{
  "action": "tap",
  "x": 540,
  "y": 1780,
  "reason": "右下角加号图标，即发布入口",
  "confidence": 0.95
}
```

---

### 4.4 Vision Agent 模式（未来层）

完全由 LLM 驱动，无需预写脚本，纯自然语言→执行。

```
用户："帮我点赞抖音首页前10个视频"

Server 循环：
  截图 → LLM("当前是什么界面？下一步应该怎么做？") → 执行 → 截图 → ...

适用场景：
  - 探索性任务（不知道具体步骤）
  - 应急处理（脚本跑飞了手动 AI 接管）
  - 复杂判断任务（内容审核、智能回复）
```

此模式作为**高级功能**，默认关闭，按需启用。

---

## 5. JS API 设计（Auto.js 兼容）

### Phase 1 实现（优先级最高）

```javascript
// ===== 触控操作 =====
tap(x, y)                          // 点击
longPress(x, y, duration)          // 长按（默认1000ms）
swipe(x1, y1, x2, y2, duration)   // 滑动
press(x, y, duration)              // 按住

// ===== 按键 =====
back()                             // 返回键
home()                             // Home键
recents()                          // 最近任务
keyCode(code)                      // 任意按键，如 keyCode("VOLUME_UP")

// ===== 截图 =====
captureScreen()                    // 返回 Image 对象
Image.toBase64()
Image.saveTo(path)

// ===== 文字输入 =====
input(text)                        // 输入文字（当前焦点）
clearInput()                       // 清空输入框

// ===== 应用控制 =====
app.launch(packageName)
app.kill(packageName)
app.info(packageName)              // 返回版本/安装状态

// ===== 文件 =====
files.read(path)
files.write(path, content)
files.exists(path)
files.list(path)
files.copy(from, to)
files.remove(path)

// ===== HTTP =====
http.get(url, headers)
http.post(url, body, headers)
http.download(url, savePath)
// Response: { code, body: { string(), json() } }

// ===== 设备信息 =====
device.width
device.height
device.imei
device.brand
device.model
device.isScreenOn()
device.getBrightness()

// ===== 线程/时间 =====
sleep(ms)
threads.start(fn)                  // 返回 Thread 对象
Thread.interrupt()
setTimeout(fn, ms)
setInterval(fn, ms)
clearTimeout(id)
clearInterval(id)

// ===== 日志 =====
log(msg)                           // 输出到执行日志面板
toast(msg)                         // 设备端 Toast（可选）
```

### Phase 2 实现（图像识别）

```javascript
// ===== 图像操作 =====
images.read(path)
images.pixel(img, x, y)           // 返回颜色值 "#RRGGBB"
images.detectsColor(img, color, x, y, threshold)
images.findImage(img, template, options)
// options: { threshold: 0.8, region: [x,y,w,h] }
// 返回: Point{x, y} 或 null

images.matchTemplate(img, template, options)
// 返回: [{x,y,score}, ...]
```

### Phase 3 实现（UI 控件选择器）

```javascript
// ===== 控件选择器（基于 IAccessibilityManager）=====
id("resource_id").findOne(timeout)
text("文字内容").findOne(timeout)
className("android.widget.Button").find()
desc("内容描述").findOne(timeout)

// 链式调用
id("login_btn").text("登录").findOne(3000).click()

// UiObject 方法
UiObject.click()
UiObject.longClick()
UiObject.setText(text)
UiObject.getText()
UiObject.bounds()                  // 返回 {left,top,right,bottom}
UiObject.center()                  // 返回 {x, y}
UiObject.exists()
UiObject.scrollUp()
UiObject.scrollDown()
```

### NexusFlow 专有 API（Auto.js 没有的）

```javascript
// ===== AI 辅助 =====
ai.findElement(description)        // 返回 Point
ai.findAndTap(description)         // 找到并点击
ai.fix(context)                    // 异常修复，返回执行结果
ai.ask(question)                   // 问 LLM，返回文字答案

// ===== UI 结构（系统级）=====
getUITree()                        // 获取完整控件树 JSON
getUITree(packageName)             // 获取指定 App 的控件树

// ===== 文件下载（带进度）=====
http.download(url, path, onProgress)

// ===== 系统操作 =====
system.installApk(apkPath)
system.uninstallApp(packageName)
system.reboot()
system.setVolume(level)

// ===== 多媒体 =====
media.selectFromGallery(path)      // 向文件选择器注入文件
```

---

## 6. AI 集成设计

### LLM 接入方案

支持多模型配置，优先使用性价比最高的 Vision 模型：

```yaml
# shark-server 配置
nexusflow:
  ai:
    provider: claude              # claude / openai / qwen / wenxin
    model: claude-opus-4-7        # 支持视觉的模型
    api_key: ${AI_API_KEY}
    
    # 备用模型（主模型失败时）
    fallback_provider: qwen
    fallback_model: qwen-vl-max
    
    # 超时与重试
    timeout_ms: 10000
    max_retries: 2
```

### 系统提示词设计

```
【脚本生成 System Prompt】
你是 NexusFlow 脚本生成专家，专门为 Android 设备生成自动化脚本。
输出格式：标准 JS 函数，main(params) 为入口。
对不确定的 UI 元素位置，使用 ai.findElement("描述") 而非硬编码坐标。
参数使用 {{paramName}} 占位符。
只输出代码，不要解释。

【运行时异常 System Prompt】
你是 Android 自动化专家，正在辅助执行一个自动化脚本。
当前任务：{task_description}
用户偏好：{user_system_prompt}
可用函数：tap(x,y), swipe(x1,y1,x2,y2,ms), input(text), back(), home()
返回格式：{"action":"tap","x":100,"y":200,"reason":"点击原因"}
只返回 JSON，不要解释。
```

### AI 调用链路

```
设备端 ai.findAndTap("发布按钮")
    ↓
上报到 shark-server
POST /api/nexusflow/ai/action
{
  imei: "xxxxx",
  type: "find_and_tap",
  description: "发布按钮",
  screenshot: "base64...",     // 截图
  ui_tree: {...},               // 控件树
  context: "正在执行：发布抖音"
}
    ↓
shark-server 构建 Prompt → 调用 LLM API
    ↓
返回操作指令
{action: "tap", x: 540, y: 1780}
    ↓
设备端执行
```

---

## 7. 数据模型

### 脚本模板表

```sql
CREATE TABLE nf_script (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100),       -- 脚本名称
    description TEXT,               -- 功能描述
    code        LONGTEXT,           -- JS 脚本内容
    params      JSON,               -- 参数定义
    -- [{"name":"caption","label":"文案","type":"text","required":true}]
    system_prompt TEXT,             -- AI 系统提示词
    tags        VARCHAR(200),       -- 标签
    version     INT DEFAULT 1,      -- 版本号
    create_time DATETIME,
    update_time DATETIME,
    create_by   BIGINT
);
```

### 执行任务表

```sql
CREATE TABLE nf_task (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    script_id    BIGINT,            -- 关联脚本
    imei         VARCHAR(20),       -- 目标设备
    params       JSON,              -- 实际参数值
    status       TINYINT,          -- 0待执行 1执行中 2成功 3失败
    start_time   DATETIME,
    end_time     DATETIME,
    result       JSON,              -- 执行结果
    log          LONGTEXT,          -- 执行日志
    error_msg    VARCHAR(500)
);
```

### 定时任务表

```sql
CREATE TABLE nf_schedule (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    script_id   BIGINT,
    device_ids  JSON,              -- 目标设备 IMEI 列表
    params      JSON,              -- 固定参数
    cron        VARCHAR(50),       -- Cron 表达式
    enabled     TINYINT DEFAULT 1,
    last_run    DATETIME,
    next_run    DATETIME
);
```

---

## 8. 前端页面规划

### 新增页面（shark-vue）

```
src/views/nexusflow/
├── scripts/
│   └── index.vue          -- 脚本库列表（卡片形式）
├── editor/
│   └── index.vue          -- 脚本编辑器（Monaco Editor）
├── task/
│   └── index.vue          -- 执行任务管理
├── schedule/
│   └── index.vue          -- 定时任务配置
└── monitor/
    └── index.vue          -- 实时执行监控（日志流+截图）
```

### 脚本编辑器页面功能

```
┌────────────────────────────────────────────────────────┐
│  NexusFlow 脚本编辑器                                   │
├──────────────┬─────────────────────────────────────────┤
│  基础信息     │  [脚本名称]  [标签]                     │
│              │  [功能描述]                              │
├──────────────┼─────────────────────────────────────────┤
│  参数配置     │  + 添加参数                             │
│              │  文案 [文本] [必填]  [删除]             │
│              │  视频URL [文本] [必填] [删除]           │
├──────────────┼─────────────────────────────────────────┤
│  AI 系统提示词│  [遇到弹窗自动关闭                     │
│              │   遇到权限申请点允许...]                │
├──────────────┼─────────────────────────────────────────┤
│              │  [✨ AI生成脚本]  [自然语言描述...]     │
│  JS 脚本     │  ┌──────────────────────────────────┐  │
│              │  │  Monaco Editor（代码编辑器）       │  │
│              │  │  // 自动完成、语法高亮             │  │
│              │  └──────────────────────────────────┘  │
├──────────────┼─────────────────────────────────────────┤
│  操作         │  [保存]  [立即执行]  [定时设置]         │
└──────────────┴─────────────────────────────────────────┘
```

### 执行监控页面

```
┌────────────────────────────────────────────────────────┐
│  执行监控 - 发布抖音作品 / 设备 IMEI:86981003...        │
├─────────────────────┬──────────────────────────────────┤
│    实时截图          │  执行日志                        │
│  ┌───────────────┐  │  [10:23:01] ✅ 打开抖音          │
│  │               │  │  [10:23:04] ✅ 点击发布按钮      │
│  │   (设备截图)   │  │  [10:23:06] ⚠️ 遇到弹窗         │
│  │               │  │  [10:23:07] 🤖 AI处理中...      │
│  │               │  │  [10:23:08] ✅ 已关闭隐私弹窗   │
│  └───────────────┘  │  [10:23:10] ✅ 填写文案          │
│  [刷新截图]          │  [10:23:12] ✅ 发布成功          │
└─────────────────────┴──────────────────────────────────┘
```

---

## 9. 落地计划

### Phase 1：脚本引擎（预计 2-3 周）

**目标：** 脚本能在设备上跑起来，完成基础自动化

```
Week 1:
  [ ] daemon 集成 Rhino 引擎
  [ ] 实现基础 API：tap / swipe / longPress / back / home / sleep
  [ ] 实现 captureScreen / files / http 基础操作
  [ ] MQTT 下发脚本并执行，结果上报

Week 2:
  [ ] 实现 app.launch / app.kill / device.* 信息
  [ ] 实现 threads.start / setTimeout / setInterval
  [ ] 脚本执行日志实时上报
  [ ] 执行异常捕获与上报

Week 3:
  [ ] shark-server 脚本管理 CRUD 接口
  [ ] shark-vue 脚本列表页 + 基础编辑页
  [ ] 立即执行 + 查看执行日志
  [ ] 手工测试：简单点击脚本跑通
```

### Phase 2：图像识别（预计 2-3 周）

**目标：** 支持截图判断，脚本具备基本"看屏幕"能力

```
  [ ] 集成 Android Bitmap 操作（pixel / detectsColor）
  [ ] 实现模板匹配（OpenCV Android 或纯 Java 实现）
  [ ] images.findImage / matchTemplate
  [ ] shark-vue 编辑器升级（Monaco Editor）
  [ ] 参数化模板配置 UI
```

### Phase 3：AI 魔法生成（预计 2 周）

**目标：** 自然语言生成可编辑脚本

```
  [ ] shark-server 接入 LLM API（Claude / GPT-4V）
  [ ] 脚本生成 Prompt 工程
  [ ] 生成接口 POST /api/nexusflow/generate
  [ ] shark-vue 编辑器加"AI生成"按钮
  [ ] AI 系统提示词配置 UI
```

### Phase 4：运行时 AI 兜底（预计 2-3 周）

**目标：** 脚本遇到异常时 AI 自动修复

```
  [ ] 实现 getUITree()（IAccessibilityManager 接入）
  [ ] 设备端 ai.fix() / ai.findElement() / ai.findAndTap() API
  [ ] 服务端 AI Action 处理接口
  [ ] 后台弹窗守护线程
  [ ] 执行监控页面（实时截图 + 日志流）
```

### Phase 5：调度与运营（预计 1-2 周）

**目标：** 定时任务、批量执行、执行统计

```
  [ ] 定时任务（Cron 配置）
  [ ] 批量设备执行
  [ ] 执行历史 / 成功率统计
  [ ] 脚本市场（内置常用脚本模板）
```

### 总时间线

```
Month 1:   Phase 1 + Phase 2   →  能跑脚本，支持图像识别
Month 2:   Phase 3 + Phase 4   →  AI生成 + AI兜底完整闭环
Month 3:   Phase 5 + 优化       →  生产可用，开始推广
```

---

## 10. 竞品对比

```
功能矩阵

                NexusFlow   影刀RPA   Auto.js   触动精灵   Appium
─────────────────────────────────────────────────────────────────
执行权限          ●●●●●      ●●        ●●●       ●●●●       ●●
批量设备管理       ●●●●●      ●●●       ✗         ✗          ●●●
AI 生成脚本       ●●●●●      ●●●       ✗         ✗          ✗
运行时 AI 兜底    ●●●●●      ✗         ✗         ✗          ✗
UI 结构获取       ●●●●●      ●●●       ●●●●      ●●●        ●●●●●
图像识别          ●●●●       ●●●●      ●●●       ●●●●       ●●●
Auto.js 兼容      ●●●●●      ✗         原版        ✗          ✗
Xposed 集成       ●●●●●      ✗         ✗         ✗          ✗
断网可用          ●●●●       ✗         ●●●●●     ●●●●●      ✗
开源/可定制       ●●●●●      ✗         ●●●●      ✗          ●●●●●

● = 1分，最高5分
```

---

## 附录：常用脚本模板

```javascript
// 模板1：抖音发布作品
function main({ caption, video_url, tags }) {
    var videoPath = "/sdcard/nexusflow/" + Date.now() + ".mp4"
    http.download(video_url, videoPath)
    app.launch("com.ss.android.ugc.aweme")
    sleep(3000)
    ai.findAndTap("底部发布按钮")
    sleep(2000)
    ai.findAndTap("选择视频")
    media.selectFromGallery(videoPath)
    sleep(3000)
    ai.findAndTap("文案输入框")
    input(caption + " " + tags)
    ai.findAndTap("发布")
    sleep(2000)
    return { success: true }
}

// 模板2：微信发消息
function main({ contact, message }) {
    app.launch("com.tencent.mm")
    sleep(2000)
    ai.findAndTap("搜索")
    input(contact)
    sleep(1000)
    ai.findAndTap(contact + " 联系人")
    sleep(1000)
    ai.findAndTap("输入框")
    input(message)
    ai.findAndTap("发送")
    return { success: true }
}

// 模板3：定时截图上报
function main({ interval_seconds }) {
    while (true) {
        var img = captureScreen()
        http.post(SERVER + "/api/nexusflow/screenshot", {
            imei: device.imei,
            image: img.toBase64(),
            timestamp: Date.now()
        })
        sleep(interval_seconds * 1000)
    }
}
```

---

*NexusFlow — Think Once, Flow Everywhere*

*基于 NexusControl 平台 | 版本 v1.0 规划文档*
