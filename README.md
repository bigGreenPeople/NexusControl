# SharkMvvm



![GitHub Workflow Status](https://img.shields.io/github/workflow/status/bigGreenPeople/SharkMvvm/SharkMvvmPublish) ![GitHub top language](https://img.shields.io/github/languages/top/bigGreenPeople/SharkMvvm) ![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/bigGreenPeople/SharkMvvm?label=SharkMvvm) ![GitHub language count](https://img.shields.io/github/languages/count/bigGreenPeople/SharkMvvm)

### 什么是SharkMvvm？

SharkMvvm是对android mvvm架构的一种封装实现，其中提供了很多注解与接口，帮助开发者快速建立标准架构、低代码、低耦合的Android应用,

### [SharkMvvm使用文档](https://1243596620.gitbook.io/sharkmvvm-wen-dang/)

## Supported Language

Kotlin

## Install

> 您可以直接参照本项目app工程的配置方式进行安装

1. 添加maven仓库到您项目的**build.gradle**

```
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
        mavenLocal()
        maven {
            name = "gitpack"
            url = uri("https://maven.pkg.github.com/bigGreenPeople/sharkmvvm")
            credentials {
                username = project.properties['GITHUB_USER'] ?: System.getenv('GITHUB_USER')
                password = project.properties['GITHUB_中配置您的githubPERSONAL_ACCESS_TOKEN'] ?: System.getenv('GITHUB_PERSONAL_ACCESS_TOKEN')
            }
        }
    }
}
```

2\. 在**gradle.properties** **username**和**token**

```
GITHUB_USER=sharkchilli7
GITHUB_PERSONAL_ACCESS_TOKEN=ghp_f2***********************
```

> 这里建议你将信息配置到系统环境变量中

3\. 最后在你的模块**build.gradle**的**dependencies**块中引入**SharkMvvm**即可

```
implementation "org.jetbrains.kotlin:kotlin-reflect"
implementation 'com.shark.android:mvvm:1.2.+'
```
