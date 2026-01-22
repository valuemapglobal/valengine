<template>
	<div class="addTactics">
		<el-form
			ref="form"
			:model="modelForm"
			:rules="rules"
			label-width="80px"
		>
			<el-form-item
				:label="modelFormLabel.name"
				prop="name"
			>
				<el-input
					v-model="modelForm.name"
					placeholder="请输入模型名称"
					v-if="ruleCode == 0"
				/>
				<el-select
					v-model="modelForm.name"
					placeholder="请选择"
					v-else
					clearable
				>
					<el-option
						v-for="(item,index) in industryList"
						:key="index"
						:label="item"
						:value="item"
					></el-option>
				</el-select>
			</el-form-item>
			<el-form-item
				:label="modelFormLabel.descr"
				prop="descr"
			>
				<el-input
					v-model="modelForm.descr"
					type="textarea"
					placeholder="请输入描述"
					:rows='5'
				></el-input>
			</el-form-item>
		</el-form>
		<div
			class="btnBottom"
			v-if="status"
		>
			<el-button
				type="primary"
				@click="submit"
			>确定
			</el-button>
			<el-button @click="handleClose">取消</el-button>
		</div>
	</div>
</template>

<script>
	import { submitModel } from '@/api/dataRisk/riskModel.js'
	import { mapState } from 'vuex'
	import { getDomain } from "@/api/financing";

	export default {
		name: 'addTactics',
		props: {
			formData: {
				type: Object,
				default: {}
			},
			status: {
				type: Boolean,
				default: true
			}
		},
		data() {
			return {
				modelForm: {
					name: '',
					descr: '',
				},
				modelFormLabel: {
					name: '模型名称',
					descr: '模型描述'
				},
				rules: {
					name: [
						{ required: true, message: "不能为空", trigger: "blur" },
						// {
						//   validator: (rule, value, callback) => {
						//     let check = /^[\u4E00-\u9FA5A-Za-z0-9_]+$/
						//     if (!check.test(value)) {
						//       callback('请输入不包含特殊字符的名称')
						//     }
						//     callback()
						//   }, trigger: 'blur'
						// },
						{ max: 30, required: true, message: "模型名称长度不能超过30", trigger: "blur" },
					],
					descr: [
						{ max: 200, message: "模型描述文字长度不能超过200", trigger: "blur" }
					],
				},
				projectCode: null,
				ruleCode: null,
				industryList: []
			}
		},
		watch: {
			'formData': {
				handler(val) {
					this.modelForm = val
				},
				deep: true,
				immediate: true
			},
			'dataRisk.config': {
				handler(val) {
					this.projectCode = val.projectCode
					this.ruleCode = val.ruleCode
					if (val.ruleCode == 0) {
						this.modelFormLabel = {
							name: '模型名称',
							descr: '模型描述'
						}
					} else {
						this.modelFormLabel = {
							name: '应用职业',
							descr: '策略描述'
						}
					}
				}, deep: true, immediate: true
			}
		},
		computed: {
			...mapState(['dataRisk'])
		},
		mounted() {
			//设置应用职业选项
			getDomain("industry_liushui").then(res => {
				if (res.code === 200) {
					this.industryList = res.data.map(item => {
						return item.dictValue
					})
				}
			})
			if (!this.hasButton('productDecision:gutu:show')) {
				this.industryList.shift()
			}
		},
		methods: {
			submit() {
				this.$refs["form"].validate(valid => {
					if (valid) {
						if (this.ruleCode == 2) {
							this.handleClose()
							return
						}
						submitModel({ ...this.modelForm, projectCode: this.projectCode, ruleCode: this.ruleCode }).then((res) => {
							if (res.code == 200) {
								this.$message.success("操作成功");
								this.$emit('resetStep')
								this.handleClose()
							}
						}).catch((err) => {

						});
					}
				});
			},
			handleClose() {
				this.resetForm()
				this.$emit('close', 1)
			},
			resetForm() {
				this.modelForm = {
					name: '',
					descr: '',
				}
				if (this.$refs.form) {
					this.$refs.form.resetFields()
				}
			},
			clearForm() {
				if (this.$refs.form) {
					this.$refs.form.clearValidate()
				}
			}
		},
	}
</script>

<style lang='less' scoped>
	.addTactics {
		padding: 0px 20px;
		box-sizing: border-box;

		.btnBottom {
			width: 100%;
			display: flex;
			align-items: center;
			justify-content: flex-end;
			margin-top: 124px;

			> button {
				border: none;
				font-size: 16px;
				height: 42px;
				border-radius: 6px;
			}

			> button:nth-of-type(1) {
				width: 84px;
			}

			> button:nth-of-type(2) {
				background: #f0f2f5;
				color: rgba(#000, 0.85);
			}
		}

		/deep/ .el-form {
			.el-form-item__label {
				line-height: 48px;
			}

			.el-input {
				.el-input__inner {
					height: 48px;
					padding: 14px;
					background-color: #f4f6f9;
				}
			}

			.el-textarea {
				.el-textarea__inner {
					padding: 14px;
					background-color: #f4f6f9;
				}
			}
		}
	}
</style>
