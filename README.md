# 懒得找了 · 落地 App（v1.0.0）

一个独立的原生 Android App，不是对网站套 WebView 壳。
对标的是懒得找了网站 v1.1.44 的核心导航功能，不是把网站全部页面都搬进来。

## v1 包含什么

- 首页：标题 + 收录总数 + NEW 数量，浅深两套水蓝主题（浅 `#0088FF`、深 `#0091FF`）
- 搜索：搜站点名、介绍、链接，搜的时候忽略分类、在全部 21 个分类里找
- 分类：21 个分类横向切换，每个分类下的子类（全部 + 子类）再筛一次
- 站点卡片：首字头像、标题、介绍、链接、NEW / HOT 角标，点卡片用系统浏览器打开对应网站
- 数据：内置 `app/src/main/assets/landezhaole-data.json`，21 分类、2603 个站点、174 个 NEW、4 个 HOT，和网站 v1.1.44 的 data.json 对过数

## v1 刻意没做的

- 没有点击排行：网站的排行是按真实点击统计的，App 里没有这套统计，就不编一个假排行
- 没有食谱页、ChatGPT 玩法页、快捷 Dock、云端同步、收藏：这些是网站的扩展页，不是导航核心，下一版再按需加
- 没有真液态折射：v1 用 Material3 半透明卡片做玻璃感，没有引 Backdrop 2.0.1，因为它会把 compileSdk 顶到 37、Compose 顶到 1.12，和这套已验证的 AGP 9.1.1 / compileSdk 36 / Compose BOM 2024.09 工具链不对齐。想上真折射时再单独升工具链，别在 v1 里混着升。

## 包名注意

- 本 App：`com.landezhaole.landing`，version 1.0.0（code 1）
- 已有的懒得找了 App：`com.landezhaole.app`
- 两个包名不同，可以同时装，互不覆盖。这个新工程也没有动已有工程的任何文件。

## 编译

本地这台开发机没有 Android SDK，所以这里没有直接出 APK，APK 走下面两条路之一：

Android Studio：打开本工程，Sync 后 Run，或 `./gradlew :app:assembleDebug`。

GitHub Actions：把工程推到一个新仓库的 main 分支，`.github/workflows/build.yml` 会自动编 debug 包，在 Actions 的 Artifacts 里下 `landezhaole-landing-debug`。

```bash
./gradlew :app:assembleDebug
# 产物：app/build/outputs/apk/debug/app-debug.apk
```

## 更新数据

网站 data.json 有新版时，把新文件覆盖 `app/src/main/assets/landezhaole-data.json`，再把 `app/build.gradle.kts` 里的 versionCode +1、versionName 递增，重新编译即可。数据格式不变：`cats[].{id,name,desc,subs[].{id,name},cards[].{t,u,d,b,s}}`。

## 文件清单

- `app/src/main/java/com/landezhaole/landing/MainActivity.kt`：入口
- `app/src/main/java/com/landezhaole/landing/LandingApp.kt`：首页、搜索、分类、卡片
- `app/src/main/java/com/landezhaole/landing/SiteData.kt`：数据模型 + org.json 解析
- `app/src/main/java/com/landezhaole/landing/Theme.kt`：浅深水蓝主题 + 头像色
- `app/src/main/assets/landezhaole-data.json`：2603 站点数据
