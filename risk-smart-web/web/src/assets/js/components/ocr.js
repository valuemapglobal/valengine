//ocr解析tableview组件表格功能抽离
'use strict'
export class OcrMethods {
	// 控制动画效果
	constructor() {
		this.loading = false //控制插入行添加行的动画加载
	}
	animationEffects() {
		this.loading = true
		setTimeout(() => {
			this.loading = false
		}, 500)
	}
}
