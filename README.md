框架介绍
niucloud-Admin-Java（niucloud框架）是一款快速开发SaaS通用管理系统后台框架，基于SpringBoot+MyBatisPlus+Shiro 技术架构精心设计，易读易懂，没有任何其它重度依赖，架构设计小巧灵活，没有采用过度设计模式。 前端采用最新的技术栈Vite+TypeScript+Vue3+ElementPlus最流行技术架构。是一款快速可以开发企业级应用的软件系统。【您不需要重复造轮子 – 框架内置已经实现基础组件功能，您只需要开发业务模块即可 】!

Gitee star Gitee fork bootstrap spring-boot bootstrap spring-boot mybatis-plus license Apache 2.0 

快速链接
gitee下载地址：https://gitee.com/niucloud-team/niucloud-admin-java

github下载地址（镜像）：https://gitee.com/niucloud-team/niucloud-admin-java

演示地址：https://www.niucloud.com

文档地址：https://www.niucloud.com

快速启动
全栈工程师推荐idea

前端支撑
插件	版本	用途
node.js	≥18	JavaScript运行环境
启动前端
npm install
npm run dev
后端支撑
插件	版本	用途
jdk	17	java环境
lombok	idea内	代码简化插件
maven	最新版	包管理工具
redis	最新版	缓存库
mysql	8.0 / 5.7	数据库
启动后端
开发工具内配置好maven并在代码中配置数据库即可启动

代码结构
niucloud-Admin-Java框架对代码以插件化的模式进行分包，使得包层级结构更加清晰合理，同时降低了耦合度，关于插件模块化开发的规范请查阅文档【niucloud-Admin-Java开源文档——前端手册or后端手册——开发规范】板块。

snowy
  |-snowy-admin-web == 前端
    |-public == 基础静态文件
    |-src == 前端源代码
      |-api == API接口转发
      |-assets == 静态文件
      |-components == VUE组件
      |-config == 基础配置
      |-layout == 基础布局
      |-locales == 多语言配置
      |-router == 基础路由配置
      |-store == Pinia缓存配置
      |-style == 样式风格配置
      |-utils == 工具类
      |-views == 所有视图界面
  |-snowy-common == 基础通用模块
  |-snowy-plugin == 插件包
    |-snowy-plugin-auth == 登录鉴权插件
    |-snowy-plugin-biz == 业务功能插件
    |-snowy-plugin-client == C端功能插件
    |-snowy-plugin-dev == 开发工具插件
    |-snowy-plugin-gen == 代码生成插件
    |-snowy-plugin-mobile == 移动端管理插件
    |-snowy-plugin-sys == 系统功能插件
  |-snowy-plugin-api == 插件api包
    |-snowy-plugin-auth-api == 登录鉴权插件api接口
    |-snowy-plugin-biz-api == 业务功能插件api接口
    |-snowy-plugin-client-api == C端功能插件api接口
    |-snowy-plugin-dev-api == 开发工具插件api接口
    |-snowy-plugin-gen == 代码生成插件api接口
    |-snowy-plugin-mobile == 移动端管理插件api接口
    |-snowy-plugin-sys-api == 系统功能插件api接口
  |-snowy-web-app == 主启动模块
分支说明
master
正式稳定版本，具体版本升级内容看更新标签

dev
团队开发的分支（代码可能随时会推，不保证运行和使用）

snowy1.8
1.x分支，目前已停止新增功能，只限于bug的维护，推荐使用2x版本

snowy2.5
2.x分支，目前已停止新增功能，只限于bug的维护，可以平滑过渡至3x版本

视频教程
教程地址（免费开放）：https://space.bilibili.com/50101698/channel/collectiondetail?sid=739071


作者也在上班工作，所以在利用休息时间为大家创作，录制视频的目的也是为各位小伙伴提供文档跟技术交流群聊之外的上手学习资料

视频由小诺开源技术团队王同学（每天一点）进行录制

架构原理
业务架构


应用架构


数据架构


技术架构


部署架构


效果展示
	
	
	
	
密码分步
功能	算法类型
登录	SM2前端加密，后端解密
登录登出日志	SM2对登录登出日志做签名完整性保护存储
操作日志	SM2对操作日志做签名完整性保护存储
用户密码	SM3完整性保护存储，登录时做完整性校验
用户手机号	SM4（cbc模式）加解密使用字段脱敏
官方群聊
QQ技术群：732230670（已满）、685395081

微信技术群：因群达到200人以上，需加微信拉群

微信群	
代码贡献
近期有很多热心开源的小伙伴陆续为咱们Snowy框架提交PR或者提出好的建议，基本合格的PR我们都接受，这样您的头像就列入到咱们Snowy仓库的贡献者列表啦！

如何贡献

1、fork一份代码至自己的账号下，本地修改您要提的代码，提交至您fork的仓库

2、登录gitee后到Snowy仓库下创建Pull Requests,选择您的仓库到Snowy的dev分支，提交即可

因为dev分支是团队开发分支，并不是统一发版本的测试过的，所以我们建议提代码至dev即可

外包开发
如果您（或您公司）有外包开发需求，可以通过联系客服方式，提交您的需求，经过工作量分析，出具合适的报价，合作开发并交付。

本团队具备雄厚的技术人才力量，均跟随小诺团队发展至今，已完全熟练每一处细节代码，您的产品自然也是由Snowy平台进行开发并交付。

1、工期保障 2、源码质量保障 3、节点按时汇报

团队成员
成员	技术	昵称
俞宝山	全栈	俞宝山
徐玉祥	全栈	就是那个锅
董夏雨	全栈	阿董
王鹏	全栈	每天一点
陈心雨	前端	.
曾获荣誉


版权说明
Snowy生态技术框架全系版本采用 Apache License2.0协议

代码可用于个人项目等接私活或企业项目脚手架使用，Snowy全系开源版完全免费

二次开发如用于开源竞品请先联系群主沟通，禁止任何变相的二开行为，未经审核视为侵权

请不要删除和修改Snowy源码头部的版权与作者声明及出处

#### 介绍
niucloud-Admin-Java 程序员二开神器！

### 官网地址
https://www.niucloud.com

#### 使用须知

1. 允许用于个人学习、毕业设计、教学案例、公益事业、商业使用;
2. 如果商用必须授权后使用，保留版权信息，请自觉遵守;
3. 禁止将本开源的代码和资源进行任何形式任何名义的出售，否则产生的一切任何后果责任由侵权者自负;
4. 本版本源码全部开源;包括前端，后端，无任何加密;