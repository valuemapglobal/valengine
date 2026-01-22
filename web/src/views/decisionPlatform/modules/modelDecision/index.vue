<template>
	<div class="productDecision">
		<div class="left">
			<product-list @getDueActive="dueActive = $event"/>
		</div>
		<div class="right">
			<business-tabs :dueActive="dueActive"/>
		</div>
	</div>
</template>

<script>
	import productList from './components/productList.vue'
	import businessTabs from './components/businessTabs.vue'

	export default {
		name: 'productDecision',
		components: {
			productList,
			businessTabs,
		},
		data() {
			return {
				dueActive: 0
			}
		},
		computed: {},
		watch: {},
		created() {

		},
		beforeDestroy() {

		},
		methods: {},
	}
</script>

<style lang="less" scoped>
	.productDecision {
		display: flex;
		min-width: 1200px;
		width: 100%;
		height: calc(var(--bgvh) - 42px);
		padding: 20px;
		// overflow: auto;

		.left {
			flex-shrink: 0;
			width: 200px;
			height: 100%;
			background-color: #fff;
			overflow: hidden;
			transition: width 0.2s linear;
		}

		.full-screen {
			width: 74px;
		}

		.right {
			flex: auto;
			margin-left: 20px;
			height: 100%;
			overflow: hidden;
		}
	}
</style>
