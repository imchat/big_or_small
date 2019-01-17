import Vue from 'vue'

Vue.directive('hash',{
  update(el, binding){
    if (binding.value.hash.length !== 66) {
      el.innerHTML = 'hash不合法'
      return
    }
    let formatHash
    formatHash = binding.value.hash

    formatHash = formatHash.substr(0, 6) + '...' + formatHash.substr(50, 16)

    let letterArr = formatHash.split('')
    let stashChat = letterArr[16]
    let style = ''

    if (binding.value.type === 1) {
      // 小
      style = 'color: #f44;'
    } else if (binding.value.type === 2) {
      // 大
      style = 'color: #4b0;'
    }

    letterArr.splice(16, 1, `<span style="font-size: 20px; ${style}">${stashChat}</span>`)

    formatHash = letterArr.join('')

    el.innerHTML = formatHash
  }
})