# 倒计时笔记 App 📅

一个精美的倒计时 + 日历笔记应用，支持云端同步。

## 功能特点

- ⏱️ **倒计时** - 距离目标日期的精确倒计时
- 📅 **日历视图** - 直观的月历界面
- 📝 **每日笔记** - 记录每天的进度和感悟
- ☁️ **云端同步** - 登录后数据自动同步
- 📖 **时光轴** - 回顾所有笔记
- 🖼️ **图片上传** - 支持添加图片到笔记

## 技术栈

- Vue 3 + Vite
- Tailwind CSS
- PocketBase (后端)
- Capacitor (安卓打包)

## 本地开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

## 构建安卓 APK

### 方式一：GitHub Actions (推荐)

1. 推送代码到 GitHub
2. 进入仓库的 Actions 页面
3. 等待构建完成后下载 APK

### 方式二：本地构建

```bash
# 构建 Web
npm run build

# 添加安卓平台
npx cap add android

# 同步到安卓项目
npx cap sync android

# 在 Android Studio 中打开
npx cap open android
```

## 许可证

MIT
