/**
 * imChat jssdk
 */

(function (root, factory) {
  if (typeof define === 'function' && define.amd) {
    define([], factory)
  } else if (typeof exports === 'object') {
    module.exports = factory()
  } else {
    root.returnExports = factory(root, factory) //如果没有AMD/CMD和CommonJS就挂在全局对象下
  }
})(this, function (root, factory) {

  if (!root.ImchatJSBridge) {
     root.ImchatJSBridge = {};
     var JSBridge = root.ImchatJSBridge;
    
    JSBridge.version = '1.0.0';

    // 获取uuid
    function generateUUID() {
      var d = new Date().getTime();
      var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = (d + Math.random()*16)%16 | 0;
        d = Math.floor(d/16);
        return (c=='x' ? r : (r&0x3|0x8)).toString(16);
      });
      return uuid;
    }

    // 是否为imChat浏览器
    function isImChat() {
      var ua = window.navigator.userAgent.toLowerCase();
      
      if(ua.match(/imChatMessenger/i) == 'imchatmessenger'){
          return true;
      }else{
          return false;
      }
    }



    var isConfig = false,
        isAllowReady = false,
        readyFunc;

        // 回调函数集合
    var responseCallbacks = {},
        // 本地注册的方法集合，供原生调用
        messageHandlers = {};


    // 调用native方法
    JSBridge.invoke = function(handlerName, data, callback) {
      // 判断环境，获取不同的 nativeBridge
      var callbackId = generateUUID(); // 获取唯一 id
      responseCallbacks[callbackId] = callback; // 存储 Callback

      var messageData = {
        handlerName: handlerName,
        callbackId: callbackId, // 传到 Native 端
        data: data || {}
      };


      if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent) && isImChat()) {
        // ios环境且在imChat webview内
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.nativeBridge) {
          window.webkit.messageHandlers.nativeBridge.postMessage(messageData);
        }

      } else if (/(Android)/i.test(navigator.userAgent) && isImChat()) {
        // android环境且在imChat webview内
        if (window.nativeBridge) {
          window.nativeBridge.postMessage(JSON.stringify(messageData));
        }

      } else {
        console.log('浏览器中jsbridge无效');
      }
    };


    // 原生调用H5页面注册的方法,或者调用回调方法
    JSBridge._handleMessageFromNative = function(msg) {
      var data;

      if (typeof msg === 'string') {
        data = JSON.parse(msg);
      } else {
        data = msg;
      }


      var responseId = data.responseId;
      var responseData = data.responseData;
      var responseCallback;

      // handlerName 和 responseId 不会同时存在
      if (responseId) {
        // 这里规定,原生执行方法完毕后准备通知h5执行回调时,回调函数id是responseId
        responseCallback = responseCallbacks[responseId];
        if (responseCallback) {
          responseCallback(responseData);
        }

      } else {
        // 否则,代表原生主动执行h5本地的函数，从本地注册的函数中获取
        var handler = messageHandlers[data.handlerName];
        if (handler) {
          handler(responseData);
        }

      }

    };


    // 内部触发jsbridge的方式
    JSBridge._callJsBridge = function (options) {
      var success = options.success;
      var fail = options.fail;
      var handlerName = options.handlerName;
      var data = options.data || {};

      return new Promise(function(resolve, reject) {

        var callbackFun = function(response) {
          if (response.code === 0) {
            // 成功
            if (success && typeof success === 'function') {
              success(response);

              resolve(response);
            }
          } else {
            // 失败
            if (fail && typeof fail === 'function') {
              fail(response);

              reject(response);
            }
          }
        };

        JSBridge.invoke(handlerName, data, callbackFun);
      });

    };


    // 注册js事件，给Native调用
    JSBridge.registerHandler = function(handlerName, handler) {
      messageHandlers[handlerName] = handler;
    };


    /**
     * 通过config接口注入权限验证配置
     */
    JSBridge.config = function (options) {
      if (isConfig) {
        JSBridge.showError('config不允许多次调用: 2002');
      } else {
        isConfig = true;

        var successFun = function() {
          // 如果这时候有ready回调
          if (readyFunc) {
            readyFunc();
          } else {
            // 允许ready直接执行
            isAllowReady = true;
          }
        };

        this._callJsBridge({
          handlerName: 'config',
          data: {
            debug: options.debug || false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: options.appId, // 必填，公众号的唯一标识
            timestamp: options.timestamp, // 必填，生成签名的时间戳
            nonceStr: options.nonceStr, // 必填，生成签名的随机串
            signature: options.signature,// 必填，签名
            jsApiList: options.jsApiList || [] // 必填，需要使用的JS接口列表
          },
          success: function() {
            successFun();
          },
          fail: function(fail) {
            JSBridge.showError(fail);
          }
        });

      }
    };


    /**
     * 通过ready接口处理成功验证
     * 初始化完毕，并且config验证完毕后会触发这个回调
     * 注意，只有config了，才会触发ready，否则无法触发
     */
    JSBridge.ready = function (callback) {
      if (!readyFunc) {
        readyFunc = callback;
        // 如果config先进行，然后才进行ready,这时候恰好又isAllowReady，代表ready可以直接自动执行
        if (isAllowReady) {
            log('ready!');
            isAllowReady = false;
            readyFunc();
        }

      } else {
        JSBridge.showError('ready回调不允许多次设置: 2001');
        
      }
    };

    // 展示全局错误信息
    JSBridge.showError = function(msg) {
      JSBridge.errorFunc(msg);
    };

    // 通过error接口处理失败验证
    JSBridge.error = function (callback) {
      JSBridge.errorFunc = callback;
    };


    // 判断当前客户端版本是否支持指定JS接口
    JSBridge.checkJsApi = function (data) {
      
      this._callJsBridge({
        handlerName: 'jsApiList',
        data: {
          jsApiList: data.jsApiList
        },
        success: data.success,
        fail: data.fail
      });
      
    };


    // 关闭当前网页窗口接口
    JSBridge.closeWindow = function () {
      this._callJsBridge({
        handlerName: 'closeWindow'
      });
    };

    // 批量隐藏功能按钮接口
    JSBridge.hideMenuItems = function (data) {
      this._callJsBridge({
        handlerName: 'hideMenuItems',
        data: {
          menuList: data.menuList
        }
      });
    };

    // 批量显示功能按钮接口
    JSBridge.showMenuItems = function (data) {
      this._callJsBridge({
        handlerName: 'showMenuItems',
        data: {
          menuList: data.menuList
        }
      });
    };

    // imChat支付
    JSBridge.chooseIMCPay = function (data) {
      this._callJsBridge({
        handlerName: 'chooseIMCPay',
        data: {
          timeStamp: data.timeStamp, // 时间戳
          appId: data.appId,
          nonceStr: data.nonceStr,  // 支付签名随机串，不长于 32 位
          package: data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=\*\*\*）
          signType: data.signType, // 签名方式，默认为'SHA1'，使用新版支付需传入'MD5'
          paySign: data.paySign // 支付签名
        },
        success: data.success,
        fail: data.fail
      });
    };

    // 设置横竖屏
    JSBridge.rotateScreen = function (data) {
      this._callJsBridge({
        handlerName: 'rotateScreen',
        data: {
          direction: data.direction // 方向
        },
        success: data.success,
        fail: data.fail
      });
    };

    // 群主页信息分享
    JSBridge.groupHomepageShare = function (data) {
      this._callJsBridge({
        handlerName: 'groupHomepageShare',
        data: {
          title: data.title, // 分享标题
          desc: data.desc, // 分享描述
          link: data.link, // 分享链接，该链接域名或路径必须与当前页面对应的群主页JS安全域名一致
          imgUrl: data.imgUrl // 分享图标
        },
        success: data.success,
        fail: data.fail
      });
    };


  }
  
  root.imc = root.ImchatJSBridge;
})