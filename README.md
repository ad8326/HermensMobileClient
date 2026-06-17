# HermensMobileClient

原生 iOS 移动客户端骨架，包含：

- Swift / UIKit
- Xcode 项目结构
- 主界面 `Main.storyboard`
- `HermensService` 远程通讯占位实现

## 目录结构

- `HermensMobileClient/`
  - `AppDelegate.swift`
  - `SceneDelegate.swift`
  - `ViewController.swift`
  - `HermensService.swift`
  - `Info.plist`
  - `LaunchScreen.storyboard`
  - `Base.lproj/Main.storyboard`
  - `Assets.xcassets/Contents.json`
- `HermensMobileClient.xcodeproj/project.pbxproj`

## 使用方法

1. 打开 `HermensMobileClient.xcodeproj`
2. 选择一个 iOS 设备或模拟器
3. 构建并运行

## 远程 Hermens 通信

`HermensService` 当前使用占位 URL：
`https://hermens.example.com/api/status`

请根据远程接口修改 `baseURL` 或请求路径，并补充认证、参数等逻辑。
