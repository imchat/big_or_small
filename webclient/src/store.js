import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    app: {
      state: {
        config: {
          baseUrl: process.env.VUE_APP_BASE_API,
          appId: process.env.VUE_APP_APPID,
          mchId: process.env.VUE_APP_MCHID,
          groupId: process.env.VUE_APP_GROUPID
        }
      },
      mutations: {},
      actions: {}
    }
  },
  getters: {
    appConfig: state => state.app.config
  }
});
