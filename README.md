# Big or Small 

-- a sample for imChat partners



## 简介

“猜大小”(Big or Small)游戏是一个基于imChat“群主页”的Light Web App。imChat DevTeam 开发并开源了该游戏，其目的是为了较为完整地向开发者演示如何基于imChat提供的接口开发嵌入imChat的轻应用。



## 游戏设计

⚠ 本项目主要目的是为了演示第三方开发者如何接入imChat系统，游戏本身并非重点。这里介绍游戏设计，仅仅为了便于开发者理解相关代码的上下文。

说明：FORCE是imChat奖励用户活跃度的积分，用于imChat系统内流通。

游戏分为三个阶段循环进行，包括投注阶段、等待开奖阶段和局间休息阶段。

### 投注阶段

- 用户可以选择投注方向（大/小）和投注FORCE数，输入支付密码后完成投注；每局中用户只能投注一次；
- 投注阶段时长5分钟，到点自动进入“等待开奖阶段”（如果任一方向上无人投注，则判定为“流局”，投注FORCE自动退还投注者，并自动进入“局间休息阶段”）；

### 等待开奖阶段

- 进入本阶段后，后端自动从etherscan.io获取以太坊主网当前区块，该区块后的第三个区块将作为本局的结果块；
- 后端检测到结果块出块后，以结果块的block hash作为开奖依据，判定本局猜中者；
- 所有投注FORCE扣除手续费后构成奖池（如输方FORCE总数不足于支付手续费，即赢方获得的FORCE数小于其投注数，则判定为“流局”，所有FORCE退还给投注者）；
- 赢方按投注FORCE数等比例分配奖池中的FORCE，分配的FORCE将有群机器人以“专属红包”的方式发放到群会话内，用户可以点击领取；
- 每局结果将由群机器人发送文本消息到群会话内；
- 开奖阶段时间不固定（取决于以太坊主网出块速度），开奖完成后自动进入“局间休息”阶段；

### 局间休息阶段

- 显示本局中奖结果，等待2分钟，自动进入下一局的投注阶段



## 代码文件说明

前端：

- 页面布局：big_or_small/webclient/src/views/home.vue
- 页面交互：big_or_small/webclient/src/views/home.js
- 游戏所用配置参数：big_or_small/webclient/.env.*

后端：

- 前端请求响应：big_or_small/server/src/main/java/com/guess/controller
- 授权接口封装：big_or_small/server/src/main/java/com/guess/service/AuthService.java
- 支付接口封装：big_or_small/server/src/main/java/com/guess/service/PayService.java
- 游戏逻辑：big_or_small/server/src/main/java/com/guess/service/GameService.java
- 接口封装及逻辑实现：big_or_small/server/src/main/java/com/guess/service/impl/*.java

注：本项目中使用RockDB作为持久化数据存储，在实际项目中建议使用MySQL、REDIS等大型数据存储系统



## imChat开放平台若干概念

- imChat开放平台：
  - imChat open platform，支持第三方应用接入imChat，
  - 开放平台管理工具 http://open.imchat.com/ ；
- 应用ID（appId）：
  - 每个群可以新建一个appId，用于网页授权，也可以多个群共用一个appId；
  - 在开放平台管理工具对应群的“网页授权”菜单中，可以设置或获取appId和对应的secret；
- Bot ID（botId）：
  - 每个Bot有一个BotId；
  - 在开放平台管理工具对应群的“机器人”菜单中，可以获取botId和对应的secret；
- 商户ID（mchId）：
  - 支付的收款方必须是一个商户；
  - 一个商户可以绑定到多个群/公号（“收费群专用商户”只能绑定到一个收费群）；
  - 目前商户只支持“预付单支付”方式;
  - 用户支付后，会处于“待收款”中，进行确认收款操作后，进入商户现金；
  - 在开放平台管理工具对应商户的“API使用”菜单中，可以获取appId和对应的secret；
- 群ID（groupId）：
  - 普通群、虚拟群及虚拟群的子群均有唯一的群ID‘
  - 在开放平台管理工具对应群的“概览”菜单中，可以获取groupId；
- code：
  - 网页授权中，静默授权或用户确认授权后，会获得code，使用该一次性的code换取accessToken
- accessToken：
  - 网页授权过程中，通过code/appId/secret换取网页授权的accessToken，使用accessToken可获取用户相关信息；
  - 支付过程中，通过mchId/secret换取支付等accessToken, 使用accessToken进行后续操作



## imChat 开放平台接口的使用

imChat开放平台接口包括网页授权接口、支付接口、Bot API和JS-SDK。

### 网页授权接口

用户每次打开游戏网页时，调用“网页授权接口”获取用户的openID（sns_base)、userInfo(sns_userinfo，获取昵称、头像、性别等)，该接口与微信网页授权接口兼容，但在scope中增加了sns_mobile（用户手机号）和sns_group（引导用户自动加入指定群，须配合groupid参数）；

第三方开发者可以使用openID作为用户在第三方应用中的唯一标识，也可使用手机号建立imChat账号与第三方应用账号的对应关系；

网页授权接口的调用包括前端JS和后端Java代码，请在以下文件中搜索“##WEB_AUTH##"，查看示例代码：

- 前端：big_or_small/webclient/src/views/home.js

- 后端：big_or_small/server/src/main/java/com/guess/controller/AuthController.java



### 支付接口

支付接口兼容于”微信支付”。本项目中在用户下注时使用了“预付单支付”方法，在流局时使用了“退款”方法，在开奖结算时使用了“确认收款”方法。

#### A. 用户下注（预付单支付）

流程如下:

1. JS使用Ajax调用后端生成预付单，获取prepay_id；后端生成（或使用已有的）out_trade_no(外部交易编号)；##PREPAY##
2. JS中调用JS-SDK支付（传入prepay_id），弹出支付界面；##PAY_CHOOSE##
3. 用户输入支付密码完成支付，JS-SDK触发“支付成功”回调；##PAY_CALLBACK##
4. JS调用后端设置“已支付待确认”，并切换该用户界面上为“已投注”状态（disable各投注按钮）；
5. 后端使用out_trade_no检查支付状态，并修改奖池总投注FORCE数；##PAY_CHECK##
6. JS中轮询后端，显示奖池总投注FORCE数及“大/小“比例；

支付接口预付单支付方法的调用包括前端JS和后端Java代码，请在以下文件中搜索流程中对应标签（上文中的“##<tag>##”）查看示例代码：

- 前端：big_or_small/webclient/src/views/home.js
- 后端：big_or_small/server/src/main/java/com/guess/controller/???java 


#### B. 流局（退款）

“任一方向无人下注”或“输方下注额小于手续费”时，该局被判定为流局，此时调用支付接口的“退款”方法（refund，传入out_trade_no）对每个投注者退款。

支付接口退款方法的调用为后端Java代码,请在以下文件中搜索“##PAY_REFUND##"，查看示例代码：

- big_or_small/server/src/main/java/com/guess/controller/???.java 

#### C. 开奖结算（确认收款）

开奖结算时，调用“确认收款”方法。确认收款后，用户支付款项会从商户的“待收款”转到“现金”，并扣除支付手续费。



### Bot API

流局时或开奖结算后，会调用Bot API向群会话发送“流局”消息、开奖结果以及中奖专属红包。

发送文本消息（含“流局”消息、开奖结果）示例代码，在以下后端Java代码文件中搜索“##TEXT_MESSAGE##"（有多处）；发送“中奖专属红包”示例代码，在以下后端Java代码文件中搜索“##RED_PACKAGE##"：

- big_or_small/server/src/main/java/com/guess/service/GameService.java

注1：向用户返奖可选择以下两种方法之一：

1. “专属红包”：（如上所示的）确认收款后，按开奖结果，在群会话内向每个中奖用户发送一份“专属红包”，该红包所有群成员可见，但仅指定用户可领取；
2. 确认收款后，使用支付接口的refund方法向指定用户直接返还对应数量FORCE（此时用户会在系统公号“imChat支付”中收到“支付返还”模版消息，与此同时使用Bot API发送一条文本消息（本局开奖结果）到群会话。

注2: 在未调用“确认收款”方法前，调用refund只能按用户支付数量返还款项，但不产生支付手续费；在调用“确认收款”方法后，调用refund可以向已支付用户返还任意数量，但商户在确认收款时会被扣除支付手续费。