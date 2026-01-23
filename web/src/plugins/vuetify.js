/*
 * @Author: detectiveBoy
 * @Date: 2022-05-19 17:01:25
 * @LastEditors: detectiveBoy
 * @LastEditTime: 2022-05-19 17:10:50
 * @FilePath: \smart-value-cashflowd:\smart-value-aivisit\src\plugins\vuetify.js
 * @Description: 
 * 
 * Copyright (c) 2022 by 用户/公司名, All Rights Reserved. 
 */
import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'

Vue.use(Vuetify)

export default new Vuetify({
  theme: {
    options: {
      customProperties: true
    },
    themes: {
      light: {
        primary: '#5867dd',
        secondary: '#e8ecfa',
        accent: '#5d78ff',
        error: '#fd397a',
        info: '#5578eb',
        success: '#0abb87',
        warning: '#ffb822'
      }
    }
  }
})