<template>
	<div class="middle_ware">
		<!--    暂无权限    -->
		<div class="wrap" v-if="isPermission">
			<img
				src="../../public/images/risk/permission_icon.png"
				alt=""
				class="middle_ware_icon middle_ware_icon_2"
			/>
			<div class="middle_ware_title">暂无权限</div>
			<div class="middle_ware_tip">当前用户无访问权限</div>
		</div>
		<!--    获取授权中……   -->
		<div class="wrap" v-else>
			<img
				src="../../public/images/risk/gain_icon.png"
				alt=""
				class="middle_ware_icon"
			/>
			<div class="middle_ware_title">获取授权中<span>...</span></div>
			<div class="middle_ware_tip">获取权限中，请稍后...</div>
		</div>
	</div>
</template>

<script>
	//access_token，保存
	import { setToken } from "@/utils/auth"
	//接口api
	import { getAuthData } from "@/api/middleWare"

	export default {
		name: "MiddleWare",
		data() {
			return {
				isPermission: false // 是否展示“暂无权限”，默认false不展示
			}
		},
		mounted() {
			//  请求接口之前，默认展示获取授权中……
			this.isPermission = false;

			const params = this.$route.query.params;
			let pageType, ckey, cname, number;
			if (params) {
				const paramsData = JSON.parse(this.getAesString(params));
				pageType = paramsData.pageType;
				ckey = paramsData.data && paramsData.data.ckey ? paramsData.data.ckey : "";
				cname = paramsData.data && paramsData.data.cname ? paramsData.data.cname : "";
				number = paramsData.data && paramsData.data.number ? paramsData.data.number : "";
				setToken(paramsData.token);
				this.setParameter(pageType, ckey, cname, number);
			} else {
				pageType = this.$route.query.pageType;
				ckey = this.$route.query.ckey || "";
				cname = this.$route.query.cname || "";
				number = this.$route.query.number || "";
				//如果传参tokenKey没有值，则直接展示“暂无权限”
				if (!this.$route.query.tokenKey) {
					this.isPermission = true
					return
				}
				//tokenKey有值，调用获取access_token的接口；如果接口报错则直接展示“暂无权限”
				getAuthData({ tokenKey: this.$route.query.tokenKey })
					.then((res) => {
						//如果接口没有获取到access_token，则直接展示“暂无权限”
						if (!res.data.access_token) {
							this.isPermission = true
							return
						}
						//有access_token的值，则保存access_token的值到localStorage里同时跳转到‘搜索页面’
						setToken(res.data.access_token)
						this.setParameter(pageType, ckey, cname, number)
					})
					.catch(() => (this.isPermission = true))
			}
		},
		methods: {
			setParameter(pageType, ckey, cname, number) {
				if (pageType == "1") {
					this.$router.push({ name: "Search" })
				} else if (pageType == "2") {
					this.$router.push({ name: "Dashboard" })
				} else if (pageType == "3") {
					this.$router.push({ name: "MonitorDaily" })
				} else if (pageType == "4") {
					this.$router.push({ name: "MonitoringDynamic" })
				} else if (pageType == "5") {
					this.$router.push({ name: "Monitoring" })
				} else if (pageType == "6") {
					this.$router.push({ name: "AIDueDiligence" })
				} else if (pageType == "7") {
					this.$store.commit("chageBussinessOrindividual", 0)
					this.$router.push({
						name: "WaterUpload",
						query: { ckey, cname, orderNo: "", type: "excel", sizeNum: 30, analysisType: 1 }
					})
				} else if (pageType == "8") {
					this.$router.push({
						name: "FinancialAllFiles",
						query: { ckey, cname }
					})
				} else if (pageType == "9") {
					this.$store.commit("chageBussinessOrindividual", 0)
					this.$router.push({
						name: "WaterUpload",
						query: { ckey, cname, orderNo: "", type: "pdf,image", sizeNum: 10, analysisType: 2 }
					})
				} else if (pageType == "10") {
					this.$router.push({
						name: "JusticeToBestTheCompany",
						query: { recordNumber: number }
					})
				}
			}
		}
	}
</script>

<style lang="less" scoped>
	.middle_ware {
		.wrap {
			height: 100vh;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			background-color: #fff;
		}

		&_icon {
			width: 44px;
			height: 44px;
			margin-bottom: 20px;

			&_2 {
				width: 42px;
			}
		}

		&_title,
		&_tip {
			font-weight: normal;
			margin-bottom: 30px;
		}

		&_title {
			font-size: 34px;
			color: rgba(0, 0, 0, 0.85);
			line-height: 48px;
		}

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

		&_tip {
			font-size: 16px;
			color: rgba(0, 0, 0, 0.6);
			line-height: 22px;
		}

		&_arrow {
			width: 66px;
			height: 66px;
		}
	}

	@keyframes loading {
		33% {
			transform: translateY(-2em);
		}
		66% {
			transform: translateY(-1em);
		}
	}
</style>
