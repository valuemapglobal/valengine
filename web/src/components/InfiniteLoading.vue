<!--
 * @Author: sam
 * @Date: 2023-02-09 18:03:29
 * @LastEditors: Do not edit
 * @LastEditTime: 2023-04-14 09:09:34
 * @FilePath: \vmkj\src\home\components\InfiniteLoading.vue
-->
<template>
	<!--  滚动到底部 加载更多组件-->
	<div class="InfiniteLoading">
		<div class="InfiniteLoading-wrapper">
			<slot></slot>
		</div>
		<div class="InfiniteLoading-tips" id="load-more">
			<div v-if="loadingMore">正在获取更多内容<span>...</span></div>
			<div v-if="noMore">{{ copywritBottom }}</div>
		</div>
	</div>
</template>

<script>
export default {
	name: 'InfiniteLoading',
	props: {
		//如果是循环使用，则必传（对应下标）
		loadKey:{
			type:Number,
			default: 0
		},
		loadingMore: { type: Boolean }, //正在获取更多内容……
		noMore: { type: Boolean }, //没有更多了
		copywritBottom: {
			type: String,
			default: '没有更多了',
		},
	},
	mounted() {
		this.load()
	},
	methods: {
		load() {
			const intersectionObserver = new IntersectionObserver(
				(entries) => {
					if (entries[0].isIntersecting) {
						//  调用接口
						this.$emit('load')
					}
				},
				{
					threshold: [0, 0.25, 0.5, 0.75, 1], //当目标元素 0%、25%、50%、75%、100% 可见时，会触发回调函数。
				}
			)
			intersectionObserver.observe(document.querySelectorAll('#load-more')[this.loadKey])
		},
	},
}
</script>

<style lang="less" scoped>
.InfiniteLoading {
	&-wrapper {
	}

	.InfiniteLoading-tips {
		height: 1px;
		text-align: center;
		margin: 14px 0 43px 0;
		font-size: 16px;
		color: rgba(0, 0, 0, 0.3);
		line-height: 26px;
		//border: 1px solid #000;

		span {
			display: inline-block;
			height: 1em;
			line-height: 1;
			text-align: left;
			vertical-align: -0.25em;
			overflow: hidden;

			&::before {
				display: block;
				content: '...\A..\A.';
				white-space: pre-wrap;
				animation: loading 3s infinite step-start both;
			}
		}
	}
}

//正在获取更多内容点点动画
@keyframes loading {
	33% {
		transform: translateY(-2em);
	}
	66% {
		transform: translateY(-1em);
	}
}
</style>
