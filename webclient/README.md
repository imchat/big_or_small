# Big or Small

> 猜大小（前端）
> 版本：v0.1.0

<p align="left">
  <a href="https://github.com/vuejs/vue">
    <img src="https://img.shields.io/badge/vue-2.5.21-brightgreen.svg" alt="vue">
  </a>
  <a href="https://youzan.github.io/vant/#/zh-CN/intro">
    <img src="https://img.shields.io/badge/vant-1.5.1-green.svg" alt="element-ui">
  </a>
  <a href="https://github.com/imchat/big_or_small/blob/master/webclient/LICENSE" rel="nofollow">
    <img src="https://img.shields.io/badge/license-MIT-brightgreen.svg" alt="License">
  </a>
</p>

## 开发及构建

### 项目目录结构

```
├── vue.config.js
├── README.md
├── postcss.config.js
├── package.json
├── LICENSE
├── cypress.json
├── favicon.ico
├── babel.configjs
├── .gitignore
├── .eslintrc.js
├── .env.dev.local
├── .env.dev
├── .env.prod
├── .browserslistrc
├── dist                    // 存放build后的文件目录
├── public                  // 存放静态资源
└── src                     // 源文件
    ├── filters             // 过滤器
    ├── assets              // 资源
    ├── components          // 组件
    ├── icons               // 图标
    ├── router              // 路由
    ├── store               // 数据管理
    ├── styles              // 样式
    ├── utils               // 工具库
    └── views               // 视图
```

### 项目构建

需要环境如下：

- [nodejs](http://nodejs.org/) 运行环境
- 构建工具 [vue-cli3](https://cli.vuejs.org/)

#### Clone 项目文件

```bash
git clone https://github.com/imchat/big_or_small.git
```

#### 进入项目目录，安装项目依赖：

```bash
cd big_or_small/webclient
yarn
```

#### 预览项目:

```bash
yarn serve:dev
```

#### 编译项目：

```bash
yarn build:dev
```

#### 发布项目

```bash
yarn deploy:dev
```