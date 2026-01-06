/*
 * @Date: 2022-07-11 11:20:42
 * @LastEditors: Seastar 1507136388@qq.com
 * @LastEditTime: 2024-07-17 09:14:12
 * @name:
 * @FilePath: \vm-micro-middleground\vue.config.js
 */
const { defineConfig } = require('@vue/cli-service')
// const UglifyPlugin = require('uglifyjs-webpack-plugin') webpack5已弃用
const TerserPlugin = require('terser-webpack-plugin')
const CompressionPlugin = require('compression-webpack-plugin')
const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin')
require('events').EventEmitter.defaultMaxListeners = 0 //防止EventEmitter内存泄漏
const Timestamp = new Date().getTime()

module.exports = defineConfig({
  transpileDependencies: true,
  productionSourceMap: false, //去除生产环境的productionSourceMap
  publicPath: process.env.VUE_APP_ENV === 'production' ? './' : '/',
  // 在npm run build 或 yarn build 时 ，生成文件的目录名称（要和baseUrl的生产环境路径一致）（默认dist）
  outputDir: 'dist',
  // 用于放置生成的静态资源 (js、css、img、fonts) 的；（项目打包之后，静态资源会放在这个文件夹下）
  assetsDir: 'static',
  configureWebpack: (config) => {
    const plugins = []
    if (process.env.VUE_APP_ENV == 'production') {
      // 为生产环境修改配置
      config.mode = 'production'
      // 替换为 terser-webpack-plugin
      // let optimization = {
      //   minimizer: [
      //     new UglifyPlugin({
      //       uglifyOptions: {
      //         warnings: false,
      //         compress: {
      //           drop_console: true,
      //           drop_debugger: false,
      //           pure_funcs: ['console.log'],
      //         },
      //       },
      //     }),
      //   ],
      // }
      // Object.assign(config, {
      //   optimization,
      // })

      const terserOptions = {
        compress: {
          drop_console: true, // 移除所有 console.*
          drop_debugger: false, // 保留 debugger
          pure_funcs: ['console.log'], // 安全移除 console.log (可选)
          defaults: false, // 禁用默认压缩设置
          arguments: true, // 优化函数参数使用
          dead_code: true, // 移除未使用代码
          unsafe_math: true, // 优化算术运算
          toplevel: true, // 顶级作用域变量压缩
        },
        format: {
          comments: false, // 移除注释
          beautify: false, // 禁用美化输出
        },
        mangle: {
          toplevel: true, // 混淆顶层变量
          safari10: true, // 解决 Safari 10 循环迭代问题
        },
      }
      config.optimization.minimizer.push(
        new TerserPlugin({
          terserOptions,
          parallel: true, // 启用多进程压缩
          // extractComments: false,   // 不提取注释到单独文件
          minify: TerserPlugin.swcMinify, // 使用 SWC 代替 Terser (性能更快)
        })
      )

      plugins.push(
        // 为静态资源准备压缩版本，在服务器也要开启相应配置
        new CompressionPlugin({
          test: /\.(js|css|json|ico|svg)$/, // 匹配文件格式
          algorithm: 'gzip',
          minRatio: 1, //默认为0.8，能压缩到小于等于原来的80%才压缩，小编这里觉得能压缩就行，其实有些文件压缩反而变大的
          filename: '[path][base].gz', // 压缩后的文件名，默认值是 [path][base].gz
          deleteOriginalAssets: true, // 不删除源文件，true 则只保留压缩后的文件
        })
      )
      plugins.push(new SimpleProgressWebpackPlugin())
    } else {
      // 为开发环境修改配置
      config.mode = 'development'
      config.performance = {
        hints: false,
      }
      config.cache = {
        type: 'filesystem', // 使用文件系统缓存
        allowCollectingMemory: true,
      }
    }
    config.plugins = [...config.plugins, ...plugins]
    config.output.filename = `static/js/[name].${Timestamp}.js`
    config.output.chunkFilename = `static/js/[name].${Timestamp}.js`
  },
  chainWebpack: (config) => {
    // 🔑 关键：让 Webpack 处理 .jsx 文件
    config.module
      .rule('jsx') // 新增一个名为 'jsx' 的规则
      .test(/\.jsx$/) // 匹配 .jsx 文件
      .use('babel-loader')
      .loader('babel-loader') // 明确使用 Babel 处理 JSX
      .end()
    config.plugin('html').tap((args) => {
      args[0].title = process.env.VUE_APP_WEBTITLE
      return args
    })
    //打包隐藏console
    config.optimization.minimizer('terser').tap((args) => {
      args[0].terserOptions.compress.drop_console = true
      return args
    })
  },
  devServer: {
    hot: true,
    host: '0.0.0.0',
    // port: port,
    proxy: {
      '/dev-api': {
        // target: 'http://192.168.1.29:6019',
        // target: 'http://124.222.29.106:8088',
        target: 'http://localhost:8080', // 本地网关
        // target: 'https://www.risksmart.valuemap.cn',
        changeOrigin: true,
        pathRewrite: {
          ['^/dev-api']: '',
        },
      },
      '/ht': {
        target: 'http://192.168.1.10:9306',
        changeOrigin: true,
        pathRewrite: {
          ['^/ht']: '',
        },
      },
      '/api': {
        target: 'https://restapi.amap.com',
        changeOrigin: true,
        pathRewrite: {
          ['^/api']: '',
        },
      },
    },

    compress: false, //禁止开发环境启动压缩
  },
})
