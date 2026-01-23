module.exports =
{
  presets: [
    '@vue/cli-plugin-babel/preset',
    [
      '@babel/preset-react',
      {
        runtime: 'automatic', // 关键配置：启用自动导入
        // 指定自定义导入源（可选）
        // importSource: '@emotion/react'
      },
    ],
    // ["@babel/preset-env", { "modules": false }]
  ],
  // plugins: [
  //   [
  //     "component",
  //     {
  //       "libraryName": "element-ui",
  //       "styleLibraryName": "theme-chalk"
  //     }
  //   ]
  // ]
}

