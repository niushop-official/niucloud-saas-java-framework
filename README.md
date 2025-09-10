README.md文件内容已生成如下：

---

# niucloud-admin-java

![输入图片说明](https://www.niucloud.com/_nuxt/login_logo.650a27e2.png) 一款支持插件化、云安装、云编译的快速开发SaaS多用户系统后台管理框架！

## 框架介绍

**niucloud-admin-java** 是一款快速开发SaaS通用管理系统后台框架，基于 **SpringBoot + MyBatisPlus + Sa-Token** 技术架构设计，前端采用 **Vite + TypeScript + Vue3 + ElementPlus** 技术栈，支持多语言开发，适用于构建企业级应用系统。

框架内置了完整的底层功能模块，包括权限管理、用户管理、菜单管理、日志管理、代码生成器等，开发者只需专注于业务模块开发，无需重复造轮子。

## 快速链接

- **Gitee 下载地址**: [https://gitee.com/niucloud-team/niucloud-admin-java](https://gitee.com/niucloud-team/niucloud-admin-java)
- **GitHub 镜像地址**: [https://gitee.com/niucloud-team/niucloud-admin-java](https://gitee.com/niucloud-team/niucloud-admin-java)
- **演示地址**: [http://java.niucloud-admin.com/admin/](http://java.niucloud-admin.com/admin/)  
  - 账号：`admin`  
  - 密码：`123456`
- **文档地址**: [https://doc.niucloud.com/java](https://doc.niucloud.com/java)
- **云应用市场**: [https://www.niucloud.com/app](https://www.niucloud.com/app)

## 开发者生态圈

niucloud-admin-java 是 **NIUCLOUD 开发者生态圈** 的一部分，生态圈已拥有3W+用户，支持插件和应用的发布、交易和分销。开发者只需专注于插件开发，并发布到 NIUCLOUD 云应用市场，即可获得收益。

## 设计理念

- **插件化架构**：所有功能模块均可作为插件存在，支持多插件共存、组合使用。
- **多租户支持**：适用于 SaaS 多用户系统，支持多站点、多商户、多门店等场景。
- **一键云安装/编译/升级**：框架内置一键式云安装、云编译、云升级功能，开发者只需遵循标准规范，即可实现插件的在线发布和更新。

## 技术亮点

- **后端技术栈**：SpringBoot + MyBatisPlus + Sa-Token + MySQL8 + Redis
- **前端技术栈**：Vue3 + TypeScript + Vite + ElementPlus + UniApp
- **RESTful API**：严格遵循 RESTful 规范，支持国际化多语言接口。
- **多语言支持**：前后端均支持多语言，包括接口返回、数据验证、错误提示等。
- **代码生成器**：根据数据库表结构一键生成前后端代码，提升开发效率。
- **DIY装修系统**：支持前端页面自定义装修，开发者可扩展组件库。

## 内置功能

1. **用户管理**：系统操作者管理
2. **权限管理**：菜单权限、按钮权限、数据范围权限
3. **角色管理**：角色分配与权限配置
4. **字典管理**：系统常用固定数据维护
5. **参数管理**：动态配置系统参数
6. **消息通知**：短信、微信公众号、小程序、邮件等消息模板管理
7. **操作日志**：记录系统操作日志
8. **计划任务**：在线任务调度管理
9. **代码生成**：支持前后端代码生成（Java、HTML、XML、SQL）
10. **系统接口**：自动生成API文档
11. **服务监控**：监控CPU、内存、磁盘等系统资源
12. **在线构建器**：拖拽式表单构建HTML代码
13. **素材管理**：支持图像、图标、视频、音频的存储与管理，集成七牛云、阿里云、腾讯云存储接口
14. **会员管理**：会员信息、账户、储值、佣金提现等
15. **微信公众号管理**：菜单、自动回复、素材管理
16. **小程序管理**：发布、更新、云编译
17. **支付管理**：集成微信支付、支付宝支付
18. **第三方登录**：支持微信公众号、小程序自动登录
19. **短信管理**：支持第三方短信发送
20. **文章管理**：内置CMS文章发布系统
21. **DIY装修**：前后端灵活装修，支持组件扩展
22. **微信开放平台**：支持多站点小程序发布
23. **云安装/云编译插件**：支持在线动态安装、卸载插件
24. **一键在线更新**：支持插件一键更新，开发者只需按规范开发并发布到云市场

## 目录结构说明

```
niucloud-admin-java
  |- niucloud-addon        # 插件模块 (JAVA)
  |- niucloud-boot         # 框架启动模块 (JAVA)
  |- niucloud-core         # 核心业务模块 (JAVA)
  |- niucloud-web-app      # Web应用模块 (JAVA)
  |- uni-app               # 手机端源代码 (UniApp)
  |- web                   # PC端源代码 (Vite + Vue3)
  |- admin                 # 管理后台源代码 (Vue3 + Vite)
  |- webroot               # 生产环境发布目录
    |- addon               # 插件目录
    |- jar                 # JAR包目录
    |- public              # 前端编译输出目录
    |- resource            # 资源文件目录
    |- runtime             # 运行时前端源码目录
    |- niucloud.bat        # Windows启动脚本
    |- niucloud.sh         # Linux启动脚本
```

## 前端支撑

| 插件    | 版本要求 | 说明           |
|---------|----------|----------------|
| Node.js | >=18     | 前端编译环境   |

## 后端支撑

| 插件    | 版本要求 | 说明           |
|---------|----------|----------------|
| JDK     | 17       | Java运行环境   |
| Maven   | 最新版   | 包管理工具     |
| Redis   | 最新版   | 缓存服务       |
| MySQL   | 8.0 / 5.7 | 数据库支持     |

## 界面截图

![界面截图1](https://niucloud-bucket.oss-cn-beijing.aliyuncs.com/gitee_saas/saas/saas_kj2.png)  
![界面截图2](https://niucloud-bucket.oss-cn-beijing.aliyuncs.com/gitee_saas/saas/saas_kj3.png)  
![界面截图3](https://niucloud-bucket.oss-cn-beijing.aliyuncs.com/gitee_saas/saas/saas_kj4.png)

## 商城插件（SHOP）

![商城插件截图](https://niucloud-bucket.oss-cn-beijing.aliyuncs.com/gitee_saas/saas/saas_shop1.png)

商城插件支持商品管理、订单处理、会员系统、支付接口等完整电商功能，开发者可直接集成使用。

## 官网地址

[https://www.niucloud.com](https://www.niucloud.com)

## 技术交流群

扫码加入技术交流群（加客服拉您入群）  
![技术交流群二维码](qun1.jpg)

## 开源使用须知

1. 本框架允许用于个人学习、教学、公益、商业项目等；
2. 框架源码所有权归 niucloud 所有，基于框架开发的应用归开发者所有；
3. 禁止修改框架并发布衍生版本；
4. 框架前后端源码全部开源，无加密；
5. 商业使用请自行审查代码安全性；
6. 本开源项目旨在分享技术，欢迎支持，不喜勿喷。

## 版权说明

本项目采用 **Apache License 2.0** 协议。

版权所有 © 2015-2025 niucloud-admin 版权所有  
技术支持：杭州数字云动科技有限公司 & 杭州牛之云科技有限公司

## 联系我们

如需技术支持或商务合作，请联系官方客服或访问官网。

---

如需进一步了解项目结构、模块功能或部署方式，请参考项目源码及文档。