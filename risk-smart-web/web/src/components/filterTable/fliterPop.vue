<template>
	<transition>
		<div
			id="fliterPop"
			v-show="show"
			@click="handleClose"
		>
			<div
				@click.stop
				class="filterCard"
				:style="cardStyle"
			>
				<div class="search">
					<div class="sort">
						<template v-for="(item,index) in operateBtn">
							<div
								:key="index"
								:class="{'fixedEnd':item.fixedEnd,'sortActive':sortClick == item.id}"
								@click="mine[item.function]()"
								v-show="item.show"
							>
								<img :src="item.img">
								<span>{{ item.label }}</span>
							</div>
						</template>
					</div>
					<el-input
						v-model="remark"
						prefix-icon="el-icon-search"
						:placeholder="`请输入${position?position.data.label:''}`"
						@input="handleRemark"
					></el-input>
				</div>
				<div class="checkData">
					<div
						class="checkBox"
						v-show="!noClick"
					>
						<el-checkbox
							v-model="allSelected"
							@change="selectAll"
						>全选
						</el-checkbox>
						<el-checkbox
							v-model="invert"
							@change="selectInvert"
						>反选
						</el-checkbox>
					</div>
					<div
						class="virtualScroll"
						v-loading="scrollLoading"
					>
						<div
							class="wrapper"
							ref="wrapper"
							@scroll="onScroll"
						>
							<div
								class="background"
								:style="{height:`${total_height}px`}"
							></div>
							<div
								class="list"
								ref="container"
							>
								<div
									v-for="(item,index) in runList"
									:key="index"
								>
									<div
										class="rowData"
										:class="{'active':item.check}"
									>
										<el-checkbox
											v-show="!noClick"
											v-model="item.check"
										/>
										<div
											class="rowLabel"
											@click="handleRowClick(item)"
										>{{ item.name }}{{ item.count != undefined ? `（${item.count}）` : "" }}
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="btnList">
						<el-button
							type="primary"
							@click="handleSelectedData"
							:disabled='submitBtn'
						>确定
						</el-button>
						<el-button
							type=""
							@click="handleClose"
						>取消
						</el-button>
					</div>
				</div>
			</div>
		</div>
	</transition>
</template>

<script>

	export default {
		name: "fliterPop",
		components: {},
		props: {
			show: {
				type: Boolean,
				default: false
			},
			position: {
				type: Object
			},
			cache_screens: { // 缓冲的屏幕数量
				type: Number,
				default: 1
			},
			rowHeight: {
				type: Number,
				default: 50
			},
			dataList: {
				type: Array,
				default: () => []
			},
			change: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				mine: this,
				showPosition: {},
				screenWidth: 0,
				screenHeight: 0,
				operateBtn: [
					{
						img: require("@/assets/images/components/asc.png"),
						label: "升序",
						function: "handleAsc",
						show: false,
						id: 0
					},
					{
						img: require("@/assets/images/components/desc.png"),
						label: "降序",
						function: "handleDesc",
						show: false,
						id: 1
					},
					{
						img: require("@/assets/images/components/del.png"),
						label: "清空条件",
						fixedEnd: true,
						function: "handleDel",
						show: true,
						id: 2
					}
				],
				remark: null,
				allSelected: true,
				invert: false,

				list: [],//存储处理过的源数据
				runList: [], // 运行时的列表
				total_height: 0, // 列表总高度
				maxNum: 0,// 一屏幕容纳的最大数量
				distance: 0, // 存储滚动的距离
				scroll_scale: [],
				scrollLoading: false,
				selectIndex: {},//存储进行筛选的列的选中数据的index，用于回显在页面上
				selectMap: new Map(),
				selectIndexBack: {},
				backUp: false,
				selectForm: {},//存储进行筛选的列的选中的数据，用于返回给外部的组件
				submitBtn: false,
				sortClick: null,
				currentRow: null,
				remarkStatus: false,//是否模糊搜索
				noClick: false
			}
		},
		watch: {
			show(val) {
				if (val) {
					this.init()
				}
			},
			dataList: {
				handler(val) {
					this.$nextTick(() => {
						this.handleSourceData()
					})
				}, deep: true, immediate: true
			},
			list: {
				handler(val) {
					let number = 0
					val.forEach(item => {
						if (!item.check) {
							this.allSelected = false
						} else {
							number++
						}
					})
					this.submitBtn = false
					if (number == 0) {
						this.submitBtn = true
					}
					if (number == val.length) {
						this.allSelected = true
					}
				}, deep: true
			}
		},
		computed: {
			cardStyle() {
				if (this.position) {
					let event = this.position.event
					let click_x = event.x
					let click_y = event.y
					let mouse_x = click_x
					let mouse_y = click_y + 10
					let divHeight = document.getElementById("fliterPop").offsetHeight
					if (click_x + 350 > this.screenWidth) {
						mouse_x = click_x - 320
					}
					if (click_y + divHeight > this.screenHeight) {
						mouse_y = click_y - divHeight - 10
					}
					return { top: `${mouse_y}px`, left: `${mouse_x}px` }
				}
			}
		},
		mounted() {
			this.updateWindow()
			window.addEventListener("resize", this.updateWindow);
		},
		methods: {
			updateWindow() {
				this.screenWidth = document.body.clientWidth
				this.screenHeight = document.body.clientHeight
			},
			handleSelectedData() {
				if (this.backUp) this.backUp = false
				let rowData = this.currentRow
				let list = []
				this.selectMap = new Map()
				this.selectForm = {}
				let selectMap = this.selectMap
				selectMap.set(rowData.prop, [])

				this.list.forEach((item, index) => {
					if (selectMap.get(rowData.prop) && item.check) {
						list.push(index)
					}
				})
				if (list.length == this.list.length) {
					selectMap.delete(rowData.prop)
				} else {
					selectMap.set(rowData.prop, list)
				}
				if (!this.selectForm.hasOwnProperty(rowData.prop)) {
					this.$set(this.selectForm, rowData.prop, [])
				}
				this.list.forEach((item, index) => {
					if (item.check) {
						if (item.name === "【空】") {
							this.selectForm[rowData.prop].push("")
						} else {
							this.selectForm[rowData.prop].push(item.name)
						}
					}
				})
				this.selectForm[rowData.prop] = [...new Set(this.selectForm[rowData.prop])]
				if (this.selectForm[rowData.prop].length == this.list.length && !this.remarkStatus) {
					this.selectForm[rowData.prop] = []
				} else {
					this.remarkStatus = false
				}
				/**
				 * 返回数据
				 * selectedRow{ 选中的所以的列的prop:对应列的表格参数和选中的数据(data) }
				 * currentRow: 当前操作的列
				 */
				this.$emit("clickSubmit", { selectedRow: this.selectForm, currentRow: rowData, status: this.selectMap })
				this.handleClose()
			},
			showSort(status) {
				this.operateBtn[0].show = status
				this.operateBtn[1].show = status
			},
			selectAll(val) {
				this.invert = false
				this.list.forEach(item => {
					item.check = val
				})
			},
			selectInvert() {
				this.list.forEach(item => {
					item.check = !item.check
				})
			},
			handleRemark(data) {
				if (data) this.remarkStatus = true
				console.log(this.remarkStatus, "this.remarkStatus");
				this.$emit("remarkData", {
					selectedRow: this.selectForm,
					currentRow: this.currentRow,
					status: this.selectMap,
					remark: data
				})
			},
			handleClose() {
				this.$emit("update:show", false)
			},
			handleAsc() {
				this.sortClick = 0
				this.currentRow.sort = 0
				this.$emit("sortFun", { selectedRow: this.selectForm, currentRow: this.currentRow, status: this.selectMap })
			},
			handleDesc() {
				this.sortClick = 1
				this.currentRow.sort = 1
				this.$emit("sortFun", { selectedRow: this.selectForm, currentRow: this.currentRow, status: this.selectMap })
			},
			handleDel() {
				this.sortClick = null
				if (this.currentRow.hasOwnProperty("sort")) {
					this.currentRow.sort = null
				}
				this.selectMap.delete(this.currentRow.prop)
				this.selectForm[this.currentRow.prop] = []

				this.handleClose()
				this.$emit("delParams", { selectedRow: this.selectForm, currentRow: this.currentRow, status: this.selectMap })
			},

			init() {
				this.runList = []
				this.scrollLoading = true
				this.remark = null
				this.$nextTick(() => {
					const containerHeight = parseInt(getComputedStyle(this.$refs.wrapper).height);
					this.maxNum = Math.ceil(containerHeight / this.rowHeight);
					this.$refs.wrapper.scrollTop = 0
				})

				//如何需要筛选，传入的表格列数据里就需要有 hasSort，并且需要同时传入sort，sort的作用是 是否默认选中升序或者降序（升序：0，降序：1）
				this.currentRow = JSON.parse(JSON.stringify(this.position.data))
				if (this.currentRow.hasSort) {
					this.showSort(this.currentRow.hasSort)
				} else {
					this.showSort(false)
				}
				if (this.currentRow.hasOwnProperty("sort")) {
					this.sortClick = this.currentRow.sort
				} else {
					this.sortClick = null
				}

				this.allSelected = true
				this.invert = false
			},
			handleRowClick(data) {
				if (data.hasOwnProperty("checkStatus")) return
				this.list[data.index].check = !this.list[data.index].check
			},
			onScroll(e) {
				const distance = e.target.scrollTop;
				this.distance = distance;
				this.getRunData(distance);
			},
			getRunData(distance = null) {

				//滚动的总距离
				const scrollTop = distance ? distance : this.$refs.container ? this.$refs.container.scrollTop : 0;

				//在哪个范围内不执行滚动
				if (this.scroll_scale) {
					if (scrollTop > this.scroll_scale[0] && scrollTop < this.scroll_scale[1]) {
						return;
					}
				}

				if (this.list.length < this.maxNum) {
					this.runList = this.list
					this.scrollLoading = false
					return
				}
				//起始索引
				let start_index = this.getStartIndex(scrollTop);
				start_index = start_index < 0 ? 0 : start_index;
				//上屏索引
				let upper_start_index = start_index - this.maxNum * this.cache_screens;
				upper_start_index = upper_start_index < 0 ? 0 : upper_start_index;
				// 调整offset
				if (this.$refs.container)
					this.$refs.container.style.transform = `translate3d(0,${this.list[upper_start_index].top}px,0)`;
				//中间屏幕的元素
				const mid_list = this.list.slice(start_index, start_index + this.maxNum);
				// 上屏
				const upper_list = this.list.slice(upper_start_index, start_index);
				// 下屏元素
				let down_start_index = start_index + this.maxNum;
				down_start_index = down_start_index > this.list.length - 1 ? this.list.length : down_start_index;
				this.scroll_scale = [this.list[Math.floor(upper_start_index + this.maxNum / 2)].top, this.list[Math.ceil(start_index + this.maxNum / 2)].top];
				const down_list = this.list.slice(down_start_index, down_start_index + this.maxNum * this.cache_screens);
				this.runList = [...upper_list, ...mid_list, ...down_list];
				this.scrollLoading = false
			},

			getStartIndex(scrollTop) {
				let start = 0, end = this.list.length - 1;
				while (start < end) {
					const mid = Math.floor((start + end) / 2);
					const { top, height } = this.list[mid];
					if (scrollTop >= top && scrollTop < top + height) {
						start = mid;
						break;
					} else if (scrollTop >= top + height) {
						start = mid + 1;
					} else if (scrollTop < top) {
						end = mid - 1;
					}
				}
				return start;
			},

			handleSourceData() {
				let selectMap = [...this.selectMap]
				this.scrollLoading = true
				if (this.$refs.wrapper)
					this.$refs.wrapper.scrollTop = 0
				if (this.dataList && this.dataList.length > 0) {
					this.noClick = false
					let total_height = 0;
					const list = this.dataList.map((data, index) => {
						const height = this.rowHeight;
						const ob = {
							height,
							top: total_height,
							...data,
							check: true,
							index
						}
						index++
						total_height += height;
						return ob;
					})

					if (selectMap.length > 0 && selectMap[selectMap.length - 1][0] == this.currentRow.prop) {
						list.map(itemX => itemX.check = false)
						selectMap[selectMap.length - 1][1].forEach(item => {
							list[item].check = true
						})
					}

					this.total_height = total_height; //  列表总高度
					this.list = list;
				} else {
					this.noClick = true
					this.list = [{
						check: false,
						name: "当前筛选无数据",
						checkStatus: false,
						top: 0,
						height: this.rowHeight
					}]
					this.total_height = 0;
				}
				this.getRunData()
			}

		}
	}
</script>

<style lang="less" scoped>
	.flexCenter {
		display: flex;
		align-items: center;
	}

	.flexCenterBt {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	#fliterPop {
		position: fixed;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		overflow: auto;
		margin: 0;
		z-index: 9999;

		.filterCard {
			position: fixed;
			width: 320px;
			min-height: 390px;
			background-color: #fff;
			border-radius: 6px;
			box-shadow: 0px 4px 16px 0px rgba(0, 0, 0, 0.16);

			.search {
				width: 100%;
				padding: 20px 20px 14px 20px;
				border-bottom: 1px solid rgba(#000, 0.08);

				.sort {
					.flexCenter();
					width: 100%;
					height: 24px;
					margin-bottom: 16px;

					> div {
						.flexCenter();
						padding: 2px 4px;
						cursor: pointer;
						box-sizing: border-box;
						margin-right: 10px;
						user-select: none;
						border-radius: 6px;

						> img {
							width: 20px;
							height: 20px;
							margin-right: 2px;
						}

						> span {
							font-size: 14px;
							font-family: PingFang SC-Regular, PingFang SC;
							font-weight: 400;
							color: rgba(0, 0, 0, 0.85);
						}
					}

					.fixedEnd {
						position: absolute;
						right: 0;
					}

					.sortActive {
						background-color: rgba(#58a5f1, 0.2);
					}
				}

				/deep/ .el-input {
					.el-input__inner {
						height: 34px;
						background-color: rgba(#000, 0.04);
					}

					.el-input__prefix {
						.el-input__icon {
							line-height: 34px;
						}
					}
				}
			}

			.checkData {
				width: 100%;
				height: 280px;
				padding: 6px 20px 12px 20px;

				.checkBox {
					width: 100%;
					height: 36px;
					padding: 6px 12px;

					/deep/ .el-checkbox {
						.el-checkbox__inner {
							width: 16px;
							height: 16px;
						}

						.el-checkbox__label {
							font-size: 16px;
							font-family: PingFang SC-Regular, PingFang SC;
							font-weight: 400;
							color: rgba(0, 0, 0, 0.9);
							padding-left: 8px;
						}
					}
				}

				.virtualScroll {
					width: 100%;
					height: calc(100% - 70px);
					position: relative;

					.wrapper {
						position: absolute;
						left: 0;
						right: 0;
						bottom: 0;
						top: 0;
						overflow-y: scroll;

						.background {
							position: absolute;
							top: 0;
							left: 0;
							right: 0;
							z-index: -1;
						}

						.list {
							position: absolute;
							top: 0;
							left: 0;
							right: 0;

							.rowData {
								// width: 100%;
								height: 36px;
								margin-bottom: 2px;
								display: flex;
								align-items: center;
								border-radius: 3px;
								padding: 6px 12px;
								cursor: pointer;

								/deep/ .el-checkbox {
									.el-checkbox__inner {
										width: 16px;
										height: 16px;
									}
								}

								.rowLabel {
									display: inline-block;
									white-space: nowrap;
									padding: 0px 8px;
								}
							}

							.active {
								// background-color: #f2f3ff;
								color: #0052d9;
							}
						}
					}
				}

				.btnList {
					width: 100%;
					display: flex;
					justify-content: flex-end;
					margin-top: 6px;

					/deep/ .el-button {
						width: 60px;
					}
				}

				::-webkit-scrollbar {
					width: 3px; /* 滚动条宽度 */
					height: 6px;
				}

				::-webkit-scrollbar-track {
					width: 6px;
					background: rgba(#101f1c, 0.1);
					-webkit-border-radius: 2em;
					-moz-border-radius: 2em;
					border-radius: 2em;
				}

				::-webkit-scrollbar-thumb {
					background-color: rgba(144, 147, 153, 0.5);
					background-clip: padding-box;
					min-height: 28px;
					-webkit-border-radius: 2em;
					-moz-border-radius: 2em;
					border-radius: 2em;
					transition: background-color 0.3s;
					cursor: pointer;
				}

				::-webkit-scrollbar-thumb:hover {
					background-color: rgba(144, 147, 153, 0.3);
				}
			}
		}
	}
</style>
