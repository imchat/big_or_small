<template>
  <div
    class="el-progress"
    :class="[
      'el-progress--' + type,
      status ? 'is-' + status : '',
      {
        'el-progress--without-text': !showText,
        'el-progress--text-inside': textInside,
      }
    ]"
    role="progressbar"
    :aria-valuenow="percentage"
    aria-valuemin="0"
    aria-valuemax="100"
  >
    <div class="el-progress-bar" v-if="type === 'line'">
      <div class="el-progress-bar__outer" :style="{height: strokeWidth + 'px'}">
        <div class="el-progress-bar__inner progress-bar-striped active" :style="barStyle">
          <div class="el-progress-bar__innerText" v-if="showText && textInside">{{percentage}}%</div>
        </div>
      </div>
    </div>
    <div class="el-progress-circle" :style="{height: width + 'px', width: width + 'px'}" v-else>
      <svg viewBox="0 0 100 100">
        <path class="el-progress-circle__track" :d="trackPath" stroke="#e5e9f2" :stroke-width="relativeStrokeWidth" fill="none"></path>
        <path class="el-progress-circle__path" :d="trackPath" stroke-linecap="round" :stroke="stroke" :stroke-width="relativeStrokeWidth" fill="none" :style="circlePathStyle"></path>
      </svg>
    </div>
    <div
      class="el-progress__text"
      v-if="showText && !textInside"
      :style="{fontSize: progressTextSize + 'px'}"
    >
      <template v-if="!status">{{percentage}}%</template>
      <i v-else :class="iconClass"></i>
    </div>
  </div>
</template>
<script>
  export default {
    name: 'Progress',
    props: {
      type: {
        type: String,
        default: 'line',
        validator: val => ['line', 'circle'].indexOf(val) > -1
      },
      percentage: {
        type: Number,
        default: 0,
        required: true,
        validator: val => val >= 0 && val <= 100
      },
      status: {
        type: String
      },
      strokeWidth: {
        type: Number,
        default: 6
      },
      textInside: {
        type: Boolean,
        default: false
      },
      width: {
        type: Number,
        default: 126
      },
      showText: {
        type: Boolean,
        default: true
      },
      color: {
        type: String,
        default: ''
      }
    },
    computed: {
      barStyle() {
        const style = {};
        style.width = this.percentage + '%';
        style.backgroundColor = this.color;
        return style;
      },
      relativeStrokeWidth() {
        return (this.strokeWidth / this.width * 100).toFixed(1);
      },
      trackPath() {
        const radius = parseInt(50 - parseFloat(this.relativeStrokeWidth) / 2, 10);

        return `M 50 50 m 0 -${radius} a ${radius} ${radius} 0 1 1 0 ${radius * 2} a ${radius} ${radius} 0 1 1 0 -${radius * 2}`;
      },
      perimeter() {
        const radius = 50 - parseFloat(this.relativeStrokeWidth) / 2;
        return 2 * Math.PI * radius;
      },
      circlePathStyle() {
        const perimeter = this.perimeter;
        return {
          strokeDasharray: `${perimeter}px,${perimeter}px`,
          strokeDashoffset: (1 - this.percentage / 100) * perimeter + 'px',
          transition: 'stroke-dashoffset 0.6s ease 0s, stroke 0.6s ease'
        };
      },
      stroke() {
        let ret;
        if (this.color) {
          ret = this.color;
        } else {
          switch (this.status) {
            case 'success':
              ret = '#13ce66';
              break;
            case 'exception':
              ret = '#ff4949';
              break;
            default:
              ret = '#20a0ff';
          }
        }
        return ret;
      },
      iconClass() {
        if (this.type === 'line') {
          return this.status === 'success' ? 'el-icon-circle-check' : 'el-icon-circle-close';
        } else {
          return this.status === 'success' ? 'el-icon-check' : 'el-icon-close';
        }
      },
      progressTextSize() {
        return this.type === 'line'
          ? 12 + this.strokeWidth * 0.4
          : this.width * 0.111111 + 2 ;
      }
    }
  };
</script>

<style scoped lang="scss">
.el-progress {
  position: relative;
  line-height: 1;
  display: inline-block;
  .el-progress-bar {
    // padding-right: 50px;
    display: inline-block;
    vertical-align: middle;
    width: 100%;
    // margin-right: -50px;
    box-sizing: border-box;
    .el-progress-bar__outer {
      height: 18px;
      border-radius: 100px;
      background-color: #ebeef5;
      overflow: hidden;
      position: relative;
      vertical-align: middle;
      .el-progress-bar__inner {
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        background-color: #409eff;
        text-align: right;
        // border-radius: 100px;
        line-height: 1;
        white-space: nowrap;
        transition: width .6s ease;
        .el-progress-bar__innerText {
          display: inline-block;
          vertical-align: middle;
          color: #fff;
          font-size: 12px;
          margin: 0 5px;
        }
      }
      .el-progress-bar__inner:after {
        display: inline-block;
        content: "";
        height: 100%;
        vertical-align: middle;
      }
    }
  }
  .el-progress--text-inside .el-progress-bar {
    padding-right: 0;
    margin-right: 0;
  }
  .el-progress__text {
    font-size: 14px;
    color: #606266;
    display: inline-block;
    vertical-align: middle;
    margin-left: 10px;
    line-height: 1;
    display: none;
  }
}
  
.demo-box.demo-progress .el-progress--line {
  margin-bottom: 15px;
  width: 350px;
}


@-webkit-keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
@-o-keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
@keyframes progress-bar-stripes {
  from {
    background-position: 40px 0;
  }
  to {
    background-position: 0 0;
  }
}
.progress-bar-striped {
    background-image: -webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);
    background-image: -o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);
    background-image: linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);
    -webkit-background-size: 40px 40px;
    background-size: 40px 40px;
}
.el-progress-bar__inner{
  transition: width .6s ease;
}
.el-progress-bar__inner.active {
    -webkit-animation: progress-bar-stripes 2s linear infinite;
    -o-animation: progress-bar-stripes 2s linear infinite;
    animation: progress-bar-stripes 2s linear infinite;
}

</style>