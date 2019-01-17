<template>
  <div>

    <div class="home" v-if="gameInfo.sequence">
    <!-- <div class="home"> -->
      <div style="word-break: break-all; margin-bottom: 10px;" v-if="log">{{log}}</div>
      <!---猜奖上轮结果-->
      <div class="prev-result" style="background-color: #eee;" v-if="prevRoundInfo.sequence && prevRoundInfo.status !== 3">
        <strong class="title">第{{prevRoundInfo.sequence}}期 结果：</strong>
        <br><span class="user-count">{{prevRoundInfo.userCount}}</span>人投注，
        <span class="user-count" v-if="prevRoundInfo.winning === 1">{{prevRoundInfo.minUserCount}}</span>
        <span class="user-count" v-else-if="prevRoundInfo.winning === 2">{{prevRoundInfo.maxUserCount}}</span>
        人猜中：
        <span class="txt" v-if="prevRoundInfo.winning === 1">小</span>
        <span class="txt" v-if="prevRoundInfo.winning === 2">大</span>
        <br/>
        一起分：
        <span class="amount">{{prevRoundInfo.totalAmount}}</span>
        FORCE
        <br>
        结果区块 #{{prevRoundInfo.block}}<br/>
        <div style="padding: 0px 10px; line-height: 16px; margin-top: 5px;">
          HASH:<span class="hash-txt" v-if="prevRoundInfo.hash" v-hash="{'hash': prevRoundInfo.hash, 'type': prevRoundInfo.winning}"></span>
          <span class="status small" v-if="prevRoundInfo.winning === 1">小</span>
          <span class="status big" v-if="prevRoundInfo.winning === 2">大</span>
        </div>
      </div>
      <!--流局-->
      <div class="prev-result" style="background-color: #eee;" v-if="prevRoundInfo.sequence && prevRoundInfo.status === 3">
        <strong class="title">第{{prevRoundInfo.sequence}}期 结果：流局</strong>
      </div>

      <!--当前猜奖状态-->
      <div class="guess-box">
        <div class="title">
          第{{gameInfo.sequence}}期（
          <span v-if="gameInfo.status === 0">
            竞猜中<span class="dotting"></span>
          </span>
          <span v-if="gameInfo.status === 1">
            出块中<span class="dotting"></span>
          </span>
          <span v-if="gameInfo.status === 2">
            结果公示
          </span>
          ）

        </div>


        <!--进度条-->
        <div style="overview: hidden;">
          <Progress :percentage="percentageProgress" color="rgba(255, 68, 68, 0.9)" style="width: 70%"/>
          <!--本轮竞猜技术倒计时-->
          <countdown v-if="gameInfo.status === 0" ref="timeComponent" :startTime="gameInfo.currTime" :endTime="gameInfo.endTime" @onFinish="countDownEnd">
            <span
              class="countdown"
              slot="process"
              slot-scope="{ timeObj }">
              <span class="txt">{{ `${timeObj.m}:${timeObj.s}` }}</span>
            </span>
            <span slot="finish"></span>
          </countdown>
          <!--下轮开启倒计时-->
          <countdown v-if="gameInfo.status === 2" ref="timeComponent" :startTime="gameInfo.currTime" :endTime="gameInfo.blockTime" @onFinish="countDownEnd">
            <span
              class="countdown"
              slot="process"
              slot-scope="{ timeObj }">
              <span class="txt">{{ `${timeObj.m}:${timeObj.s}` }}</span>
            </span>
            <span slot="finish"></span>
          </countdown>
        </div>


        <!--当前区块、结果区块-->
        <div class="hash-box" v-if="gameInfo.height && gameInfo.status === 1">
          <span v-if="gameInfo.height">当前区块 #{{gameInfo.height}}</span>
          <span style="margin-left: 30px;" v-if="gameInfo.block">结果区块 #{{gameInfo.block}}</span>
        </div>
        <!--实际结果-->
        <div class="real-success" v-show="gameInfo.status === 2">
          <span class="hash-txt" v-if="gameInfo.hash" v-hash="{'hash': gameInfo.hash, 'type': gameInfo.winning}"></span>
          <span class="status small" v-show="gameInfo.winning === 1">小</span>
          <span class="status big" v-show="gameInfo.winning === 2">大</span>
        </div>
        <!--当前玩家-->
        <div class="curr-player" v-show="gameInfo.status === 1">
          <span class="txt">? ? ?</span> 人
        </div>
        <div class="curr-player" v-show="gameInfo.status === 2">
          #{{gameInfo.block}}区块
          <span class="txt" v-show="gameInfo.winning === 1">{{gameInfo.minUserCount}}</span>
          <span class="txt" v-show="gameInfo.winning === 2">{{gameInfo.maxUserCount}}</span> 人猜中
        </div>
        <!--当前，瓜分奖池-->
        <div class="curr-pool">
          <span v-show="gameInfo.status === 0">当前奖池</span>
          <span v-show="gameInfo.status === 1 || gameInfo.status === 2">瓜分奖池</span>
          <span class="amount" v-if="gameInfo.totalAmount >= 0">
            <count-to :start-val="0" :end-val="gameInfo.totalAmount" :duration="1500"/>
          </span>
          FORCE
        </div>
        <!--押注比率-->
        <div
          class="pool-rate"
          v-bind:class="{
            // 所有都压大
            'big-win-all': gameInfo.maxAmount  ===  gameInfo.totalAmount,
            // 所有都压小
            'small-win-all': gameInfo.minAmount === gameInfo.totalAmount,
            // 押大赢
            'big-win': (gameInfo.maxAmount > gameInfo.minAmount) && gameInfo.minAmount !== 0,
            // 押小赢
            'small-win': (gameInfo.maxAmount < gameInfo.minAmount) && gameInfo.maxAmount !== 0,
            // 平局
            'average': gameInfo.maxAmount === gameInfo.minAmount
          }"
          v-if="gameInfo.maxAmount || gameInfo.minAmount">
          <span class="item left">
            <span>{{parseInt(gameInfo.maxAmount / gameInfo.totalAmount * 100)}}%猜大</span>
          </span>
          <span class="item right">
            <span>{{100 - parseInt(gameInfo.maxAmount / gameInfo.totalAmount * 100)}}%猜小</span>
          </span>
        </div>
        <!--押注人数-->
        <div class="pool-usercout" v-if="gameInfo.maxAmount || gameInfo.minAmount">
          <span class="left">{{gameInfo.maxUserCount}}人</span>
          <span class="right">{{gameInfo.minUserCount}}人</span>
        </div>
        <!--产品列表-->
        <div class="goods-list">
          <van-button class="item"
            :type="(item.goodsId % 2) === 0 ? 'danger' : 'primary'"
            v-for="(item, index) in goodsList" :key="index"
            :disabled="item.disabled"
            size="large"
            @click="payGoods(item)">
            {{item.name}}：{{item.amount}}
          </van-button>
        </div>
        <!--选择结果-->
        <div class="select-result">
          <div v-if="myInfo.status !== 0">
            你已投注：
            <span class="status small" v-if="myInfo.guessType === 1">小</span>
            <span class="status big" v-if="myInfo.guessType === 2">大</span>，
            <span class="amount">{{myInfo.amount}}</span> FORCE
          </div>
          <div class="tips" v-if="myInfo.status === 2 || myInfo.status === 5">
            猜对啦！获得<span class="amount">{{myInfo.reward}}</span> FORCE～
          </div>
          <div class="tips" v-if="myInfo.status === 3">
            猜错啦！继续努力💪～
          </div>
          <div class="tips" v-if="myInfo.status === 4">
            已退款
          </div>
        </div>
      </div>

      <!--说明-->
      <Description :gameInfo="gameInfo"/>

      <!-- <div>
        <van-button plain type="primary" style="width: 80%; margin-bottom: 15px;" @click="authorizeGroup">授权进群</van-button>
        <van-button plain type="danger" @click="closeWindow">关闭窗口</van-button>
        <van-button type="primary" @click="rotateScreen('horizontal')">设置横屏</van-button>
        <van-button type="danger" @click="rotateScreen('vertical')">设置竖屏</van-button>
      </div> -->
    </div>

    <!--test 用-->
    <!-- <div v-if="log" style="word-break: break-all; margin-bottom: 10px; color: #f30;">{{log}}</div> -->

  </div>
</template>

<script src="./home.js"></script>


<style scoped lang="scss" src="./home.scss"></style>
