<template>
  <div class="description" style="background-color: #eee;">
    <div class="head">说明</div>
    <p>
      <span class="title">本轮投注时间：</span><br />
      {{gameInfo.startTime| parseTime('{m}月{d}日 {h}:{i}')}} - {{gameInfo.endTime | parseTime('{h}:{i}')}}
    </p>
    <p>
      <span class="title">每轮开启时间：</span><br />
      上轮结果区块出块1分钟后
    </p>
    <p>
      <span class="title">投注限制：</span><br />
      每轮仅可投注一次，仅可投注FORCE
    </p>
    <p>
      <span class="title">结果标准：</span><br />
      {{gameInfo.endTime| parseTime('{m}月{d}日 {h}:{i}')}} 后诞生的第3个以太坊主链区块的HASH值倒数第9位，0~7为小，8~F为大
    </p>
    <p>
      <span class="title">奖池分配：</span><br />
      每轮押对的用户根据投注按比例瓜分整个奖池，通过群内红包发放
    </p>
  </div>
</template>

<script>
export default {
  name: "Description",
  props: {
    gameInfo: Object
  }
};
</script>

<style scoped lang="scss">
.description {
  padding: 10px;
  border: 1px solid #ddd;
  text-align: left;
  font-size: 14px;
  background-color: #fff;
  .head {
    font-size: 16px;
    font-weight: bold;
    padding-top: 10px;
  }
  .title {
    color: #999;
    font-size: 12px;
  }
}
</style>
