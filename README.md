框架介绍
niucloud-Admin-Java（niucloud框架）是一款快速开发SaaS通用管理系统后台框架，基于SpringBoot+MyBatisPlus+Shiro 技术架构精心设计，易读易懂，没有任何其它重度依赖，架构设计小巧灵活，没有采用过度设计模式。 前端采用最新的技术栈Vite+TypeScript+Vue3+ElementPlus最流行技术架构。是一款快速可以开发企业级应用的软件系统。【您不需要重复造轮子 – 框架内置已经实现基础组件功能，您只需要开发业务模块即可 】!

Gitee star Gitee fork bootstrap spring-boot bootstrap spring-boot mybatis-plus license Apache 2.0 

快速链接
gitee下载地址：https://gitee.com/niucloud-team/niucloud-admin-java

github下载地址（镜像）：https://gitee.com/niucloud-team/niucloud-admin-java

演示地址：https://www.niucloud.com

文档地址：https://www.niucloud.com


**niucloud-admin采用的技术亮点** 
后台php采用THINKPHP8+PHP8+MYSQL8,支持composer快速安装扩展，支持redis缓存以及消息队列，支持多语言设计开发，同时开发采用严格的restful的api设计开发。

后台前后端分离采用element-plus、vue3.0、typescript、vite、pina等前端技术,同时使用i18n支持国际化多语言开发。

手机端采用uniapp前后端分离，同时使用uview、vue3.0、typescript、vite、pina等前端技术，同时使用i18n支持国际化多语言开发，可以灵活编译成h5,微信小程序，支付宝小程序，抖音小程序等使用场景。

niucloud-admin采用多租户的saas系统设计，能够提供企业级软件服务运营 ，同时满足用户多站点，多商户，多门店等系统开发需求。

niucloud-admin结合当前市面上很多框架结构不规范，导致基础结构不稳定等情况，严格定义了分层设计的开发规范，同时api接口严格采用restful的开发规范，能够满足大型业务系统或者微服务的开发需求。

niucloud-admin前端以及后端采用严格的多语言开发规范，包括前端展示，api接口返回，数据验证，错误返回等全部使用多语言设计规范，使开发者能够真生意义上实现多语言的开发需求。

niucloud-admin已经搭建好常规系统的开发底层，具体的底层功能包括：管理员管理，权限管理，网站设置，计划任务管理，素材管理，会员管理，会员账户管理，微信公众号以及小程序管理，支付管理，第三方登录管理，消息管理，短信管理，文章管理，前端装修等全面的基础功能，这样开发者不需要开发基础的结构而专心开发业务。

niucloud-admin系统内置支持微信/支付宝支付，微信公众号/小程序/短信消息管理，阿里云/腾讯云短信，七牛云/阿里云存储等基础的功能扩展，后续会根据实际业务不断扩展基础组件。

niucloud-admin结合系统结构特点专门开发了代码生成器，这样开发者根据数据表就可以一键生成基础的业务代码，包括：后台php业务代码以及对应的前端vue代码。

前端采用标准的element-plus，开发者不需要详细了解前端，只需要用标准的element组件就可以。

手机端设计开发了自定义装修，同时提供了基础的开发组件，方便开发者设计开发手机自定义页面装修的开发需求。

手机端使用uniapp ，同时使用uview页面展示，可以开发出丰富的手机样式，同时不需要专门学习小程序，app等开发语言，只需要通过uniapp编译就可以。

**界面截图说明** 

![输入图片说明](https://media.niucloud.com/172431244911349b7ff1e9608035bb6957850ec96d_aliyun.png)
![输入图片说明](https://media.niucloud.com/172431267682a7be77c2c9e61520be08b080cc8ac2_aliyun.png)
![输入图片说明](https://media.niucloud.com/172431267732a3c42a66787bfba67c4fd2962f0aee_aliyun.png)
![输入图片说明](https://media.niucloud.com/17243126769f817f094c61758e79f21de58f844a0a_aliyun.png)
![输入图片说明](https://media.niucloud.com/172431267671489cc9b187ff313ea7e661d0cedd50_aliyun.png)
![输入图片说明](https://media.niucloud.com/17243126771ce619c28cf45db0a65de2e0101f1dfa_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312676e25324758c1624f4e9f09c4ee346441c_aliyun.png)
![输入图片说明](https://media.niucloud.com/17243126765cd61a78df7867dc6dcafe4d5c705be9_aliyun.png)
![输入图片说明](https://media.niucloud.com/17243126778746343ef1447330478d9b29247c4bbc_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312677be455a84766adfe58d046b86a47a4b7b_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312677206aff060b7896851a8d7e4c9de21361_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312677511da97e400dde7de62f11ac86c6d4e0_aliyun.png)
![输入图片说明](https://media.niucloud.com/17243126774e43ffbfbac66cb300594cf4cbfe1272_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312677ad1f9259074de7443f992b46dc3766d6_aliyun.png)
![输入图片说明](https://media.niucloud.com/1724312677bd2c7d91d1293cc51f932b1de5d5c0f9_aliyun.png)


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