import { Button, Toast } from 'vant'
import CountTo from 'vue-count-to'
import Countdown from '@/components/Countdown.vue'
import Progress from '@/components/Progress.vue'

import Description from '@/components/Description.vue'
import request from '@/utils/request'
import { mapGetters } from 'vuex'
import { getQueryString } from '@/utils/index'

export default {
  name: 'home',
  components: {
    Description,
    [Button.name]: Button,
    CountTo,
    Countdown,
    [Progress.name]: Progress
  },
  data() {
    return {
      log: '', // test log用
      logNum: 10, // test 用
      blockOutPercent: 0, // 出块百分比，默认10%
      ticket: '', // jssdk 签名信息
      prepayId: '', // 用户调起支付用
      mchSignInfo: {}, // 商户签名信息
      openId: '', // 用户openId
      // 游戏基本信息
      gameInfo: {
        status: 0, // 0: 竞猜中  1: 出块中  2: 结果公示
        hash: '', // 结果公示hash
        height: '', // 当前块
        block: '', // 结果块
        winning: 0, // 0: 未开奖  1: 小  2: 大
        winningNum: '', // hash倒数第9为字母
        userCount: 0, // 参与总人数
        minUserCount: 0, // 猜小人数
        maxUserCount: 0, // 猜大人数
        minAmount: 0, // 猜小金额
        maxAmount: 0, // 猜大金额
        totalAmount: 0, // 奖池总数
        blockTime: 0, // 出块时间
        sequence: 0, // 游戏轮次
        startTime: 0, // 开始时间
        endTime: 0, // 结束时间
        currTime: 0 // 服务器当前时间
      },
      // 我的中奖信息
      myInfo: {
        status: 0, // 0: 未参与  1: 等待公布中奖  2: 已中奖未发放  3: 未中奖  4: 已退款  5: 中奖已发放  6: 已支付待确认
        productId: 0, // 产品ID，对于goodsId
        reward: 0, // 中奖force
        amount: 0, // 投注数
        guessType: 0 // 1: 小  2: 大
      },
      // 上轮中奖结果
      prevRoundInfo: {
        userCount: 0, // 参与总人数
        minUserCount: 0, // 猜小人数
        maxUserCount: 1, // 猜大人数
        totalAmount: 0, // 奖池总数
        minAmount: 0, // 猜小金额
        maxAmount: 0, // 猜大金额
        hash: '', // 中奖hash
        block: '', // 结果块
        winningNum: '', // hash倒数第9为字母
        winning: 0, // 0: 未开奖  1: 小  2: 大
      },
      goodsList: [
        {
          "goodsId": 1,
          "name": "押大",
          "amount": 5
        },
        {
          "goodsId": 2,
          "name": "押小",
          "amount": 5
        },
        {
          "goodsId": 3,
          "name": "押大",
          "amount": 10
        },
        {
          "goodsId": 4,
          "name": "押小",
          "amount": 10
        },
        {
          "goodsId": 5,
          "name": "押大",
          "amount": 15
        },
        {
          "goodsId": 6,
          "name": "押小",
          "amount": 15
        }
      ]
    }
  },
  computed: {
    ...mapGetters([
      'appConfig'
    ]),
    percentageProgress: function() {

      let percentDiff

      if (this.gameInfo.status === 0) {
        // 竞猜中 5分钟
        let timeDiff = this.gameInfo.endTime - this.gameInfo.currTime
        percentDiff = 100 - (parseInt((timeDiff) / 1000) / (5 * 60) * 100)
        if (percentDiff >= 100) {
          percentDiff = 100
        }

      } else if (this.gameInfo.status === 1) {
        // 出块中，每次递增2%
        this.blockOutPercent = this.blockOutPercent + 2
        if (this.blockOutPercent >= 95) {
          this.blockOutPercent = 95
        }
        percentDiff = this.blockOutPercent

      } else if(this.gameInfo.status === 2) {
        // 结果公示 1分钟
        let timeDiff = this.gameInfo.blockTime - this.gameInfo.currTime
        percentDiff = 100 - (parseInt(timeDiff / 1000) / (1 * 60) * 100)

        if (percentDiff >= 100) {
          percentDiff = 99
        }
      }
      
      return parseInt(percentDiff)
    }
  },
  mounted() {
    // this.getGameDetail()

    /*
      授权拿openId
      ##WEB_AUTH##
    */
    if (this.isImChat() && !getQueryString('code')) {
      window.location.href = this.appConfig.baseUrl + '/oauth2/authorize?appid='+ this.appConfig.appId +'&redirect_uri='+ encodeURIComponent(window.location.href.split('#')[0]) +'&response_type=code&scope=snsapi_group,snsapi_userinfo&group_id='+ this.appConfig.groupId +'&state=silent#imchat_redirect';
    }

    // 授权成功
    if (this.isImChat() && getQueryString('code')) {
      // 获取openId
      this.getOpenId(getQueryString('code'))

      // 获取商户签名
      this.getConfigSign()

      // 还原至初始url
      history.pushState('', '', location.origin + location.pathname)

      Toast.loading({
        mask: true,
        message: '加载中...'
      });
    }

  },
  methods: {
    // 获取游戏详细
    async getGameDetail() {
      const res = await request({
        url: '/ChatGuess/game/detail',
        method: 'get',
        params: {
          openid: this.openId
        }
      })

      Toast.clear()

      this.gameInfo = {
        // ...this.gameInfo,
        ...res.gameInfo
      }

      this.myInfo = {
        // ...this.myInfo,
        ...res.myInfo
      }

      this.prevRoundInfo = {
        // ...this.prevRoundInfo,
        ...res.upRound
      }


      if (this.gameInfo.status !== 0) {
        // 非竞猜中
        this.goodsList.forEach((item) => {
          this.$set(item, 'disabled', true)
        })
      }

      if (this.gameInfo.status === 0) {
        // 竞猜中
        this.goodsList.forEach((item) => {
          this.$set(item, 'disabled', false)
          this.$set(item, 'isPay', false)
        })
      }


      // 用户所选
      if (this.myInfo.status > 0 && this.myInfo.productId) {
        this.goodsList.forEach((item) => {
          if (item.goodsId === this.myInfo.productId) {
            this.$set(item, 'disabled', false)
            this.$set(item, 'isPay', true)
          } else {
            this.$set(item, 'disabled', true)
            this.$set(item, 'isPay', false)
          }
        })
      }

      if (this.gameInfo.status === 0 || this.gameInfo.status === 2) {
        // 重启定时器
        this.$nextTick(() => {
          if (this.$refs.timeComponent) {
            this.$refs.timeComponent.startCountdown('restart')
          }
        })
      }

      setTimeout(() => {
        this.getGameDetail()
      }, 1500)


      // this.log = '游戏详情: ' + JSON.stringify(res)
    },
    // 获取用户openId
    async getOpenId(code) {
      const res = await request({
        url: '/ChatGuess/auth/userInfo',
        method: 'get',
        params: {
          appId: this.appConfig.appId,
          code,
          response_type: 'code',
        }
      })

      this.openId = res.openid
      
      // this.log = '获取openId: ' + JSON.stringify(res)
      // this.log = this.logNum++

      this.getGameDetail()
    },
    // 下单 ##PREPAY##
    async unifiedOrder(pid) {
      const res = await request({
        url: '/ChatGuess/game/unifiedOrder',
        method: 'post',
        data: {
          openid: this.openId,
          productId: pid,
        }
      })

      this.prepayId = res.prepay_id
      // this.log = '下单prepayId: ' + JSON.stringify(res)

      this.getMerchantSign()
    },
    // 获取商户签名
    async getMerchantSign() {
      const res = await request({
        url: '/ChatGuess/auth/paySign',
        method: 'get',
        params: {
          package: encodeURIComponent('prepay_id=' + this.prepayId)
        }
      })
      
      this.mchSignInfo = res;
      
      // this.log = '商户签名: ' + JSON.stringify(res)

      this.toPay()
    },
    // 确认订单
    async confirmOrder(tradeNo) {
      const res = await request({
        url: '/ChatGuess/game/confirmOrder',
        method: 'post',
        data: {
          out_trade_no: tradeNo
        }
      })

      // this.getGameDetail()
    },
    // 去支付
    toPay() {
      Toast.clear()

      // this.log = '支付信息: ' + JSON.stringify(this.mchSignInfo)

      // ##PAY_CHOOSE##
      imc.chooseIMCPay({
        timeStamp: this.mchSignInfo.timeStamp,
        appId: this.appConfig.appId,
        nonceStr: this.mchSignInfo.nonceStr,
        package: encodeURIComponent('prepay_id=' + this.prepayId),
        signType: this.mchSignInfo.signType, // 注意：新版支付接口使用 MD5 加密
        paySign: this.mchSignInfo.paySign,
        success: (res) => {
          // ##PAY_CALLBACK##
          if (res.code === 0) {
            // 支付成功
            // this.log = '支付成功: ' + JSON.stringify(res)

            Toast.loading({
              mask: true,
              message: '加载中...'
            });

            // 重新获取游戏数据
            this.confirmOrder(res.out_trade_no)
          } else {
            // 支付失败
            this.log = '支付失败' + JSON.stringify(res)
          }
        }
      })
    },
    // 支付商品
    payGoods(data) {
      // 已支付
      if (data.isPay) {
        // Toast('您已经押过了，等待下一轮吧～')
        return
      } 

      Toast.loading({
        mask: true,
        message: '支付中...'
      });

      this.unifiedOrder(data.goodsId)

    },
    // 倒计时结束
    countDownEnd(vm) {
      // console.log('countdown end', vm);
      if (this.gameInfo.status === 1) {
        this.blockOutPercent = 100
      } else {
        this.blockOutPercent = 0
      }
    },
    // 获取config签名，用于jssdk调用
    async getConfigSign() {
      const res = await request({
        url: '/ChatGuess/auth/jsSign',
        method: 'get',
        params: {
          appId: this.appConfig.appId,
          url: window.location.href.split('#')[0]
        }
      })

      this.ticket = res.ticket
      this.initJSSDK()

      // this.log = 'jssdk config: ' + JSON.stringify(res)
    },
    initJSSDK() {
      imc.config({
        debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印（暂未实现）
        appId: this.ticket.appid, // 必填，公众号的唯一标识
        timestamp: this.ticket.timestamp, // 必填，生成签名的时间戳
        nonceStr: this.ticket.nonceStr, // 必填，生成签名的随机串
        signature: this.ticket.signature,// 必填，签名
        jsApiList: [] // 必填，需要使用的JS接口列表 （暂未实现）
      });

      imc.ready(() => {
        // this.log = 'config信息验证成功，执行ready'

      })

      imc.error((res) => {
        // this.log = 'config信息验证失败:' + JSON.stringify(res)
      });
    },
    closeWindow() {
      imc.closeWindow()
    },
    rotateScreen(direction) {
      imc.rotateScreen({
        direction
      });
    },
    authorizeGroup() {
      window.location.href = this.appConfig.baseUrl + '/oauth2/authorize?appid='+ this.appConfig.appId +'&redirect_uri='+ encodeURIComponent(window.location.href.split('#')[0]) +'&response_type=code&scope=snsapi_group&group_id='+ this.appConfig.groupId +'&state=silent#imchat_redirect';
    },
    printCurrUrl() {
      this.log = window.location.href
    },
    isImChat: function() {
      const ua = window.navigator.userAgent.toLowerCase()
      
      if (ua.match(/imChatMessenger/i) == 'imchatmessenger'){
          return true
      } else {
          return false
      }
    }
  }
};