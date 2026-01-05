<template>
  <div class="riskEval">
    <el-form
      ref="formRef"
      :model="form"
      label-width="auto"
      :rules="rules"
      :validate-on-rule-chang="false"
    >
      <el-form-item label="使用流程">
        <el-select
          clearable
          v-model="form.processStrategyId"
          @change="handleWorkfolw"
          placeholder="请选择"
        >
          <el-option
            v-for="item in workflowList"
            :key="item.value"
            :label="item.processStrategy"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <div class="card">
        <span class="title">请求参数</span>
        <div class="card_content" v-loading="dataLoading">
          <el-form-item
            v-for="(item, index) in dynamicDataList"
            :label="item.nameZh + '：'"
            :prop="item.nameEn"
            :key="index"
          >
            <div style="display: flex">
              <el-input
                placeholder="请输入"
                v-model="form[item.nameEn]"
              ></el-input>
            </div>
          </el-form-item>
        </div>
      </div>
    </el-form>
    <div class="bottomBtns">
      <el-button @click="handleSubmit" :disabled="btnLoading"
        >{{ btnLoading ? '提交中' : '确定' }}
        <i v-if="btnLoading" class="el-icon-loading"
      /></el-button>
      <el-button @click="handleClose">取消</el-button>
    </div>
    <div class="tips">
      <span>注意</span><br />
      <span>
        1.确认后将进行系统实名验证和用户授权认证，认证通过后将生成“风险评估报告”，您可在操作列表中点击“详情”查看报告。<br />
        2.由于客户授权的不确定性，报告生成时间可能较长，建议稍后查看报告。您也可以联系客户尽快完成授权。<br />
        3.若客户一小时内未完成授权认证，“风险评估报告”将被视为生成失败，您可重新申请风险评估。
      </span>
    </div>
  </div>
</template>

<script>
import { getPramById, submitPolicyTask } from '../api/platformEngine'
export default {
  props: {
    workflowList: {
      type: Array,
      default: () => {
        return []
      },
    },
  },
  data() {
    return {
      form: {
        processStrategyId: null,
      },
      rules: {
        number: [
          {
            validator: (rule, value, callback) => {
              if (!value) callback('请输入身份证号')
              let check = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
              if (!check.test(value)) callback('请输入正确的身份证号')
              callback()
            },
            trigger: 'blur',
          },
        ],
        mobile: [
          {
            validator: (rule, value, callback) => {
              if (!value) callback('请输入手机号')
              let check =
                /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
              if (!check.test(value)) callback('请输入正确的手机号')
              callback()
            },
            trigger: 'blur',
          },
        ],
        name: [
          {
            validator: (rule, value, callback) => {
              if (!value) callback('请输入姓名')
              callback()
            },
            trigger: 'blur',
          },
        ],
      },
      dynamicDataList: [],
      dataLoading: false,
      btnLoading: false,
    }
  },
  watch: {
    workflowList: {
      handler(val) {
        if (val && val.length) this.form.processStrategyId = val[0].id
        this.handleWorkfolw(val[0].id)
      },
      immediate: true,
    },
  },
  methods: {
    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.btnLoading = true
          submitPolicyTask({ ...this.form })
            .then((res) => {
              if (res.code == 200) {
                this.$message('操作成功')
                this.$emit('success')
                this.reset()
              }
              this.btnLoading = false
            })
            .catch((err) => {
              this.btnLoading = false
            })
        }
      })
    },
    handleWorkfolw(data) {
      this.form = this.$options.data().form
      this.dynamicDataList = []
      if (!data) return
      this.form.processStrategyId = data
      this.dataLoading = true
      getPramById({ moduleId: data })
        .then((res) => {
          if (res.code == 200) {
            let filterField = ['name', 'number', 'mobile']
            this.dynamicDataList = res.data
              .map((item) => item.parameterInfo)
              .filter((item) => filterField.includes(item.nameEn))
          }
          this.dataLoading = false
        })
        .catch(() => {
          this.dataLoading = false
        })
    },
    handleClose() {
      this.reset()
      this.$emit('close')
    },
    reset() {
      this.form = this.$options.data().form
      this.dynamicDataList = []
    },
  },
}
</script>

<style lang="less" scoped>
.riskEval {
  padding: 20px;
  box-sizing: border-box;
  .card {
    width: 100%;
    box-sizing: border-box;
    overflow: auto;
    border-radius: 6px;
    background: #ffffff;
    box-sizing: border-box;
    border: 1px solid #ebebeb;
    .title {
      width: 100%;
      height: 50px;
      // line-height: 50px;
      background: #f6f9fd;
      font-size: 16px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
      padding: 14px 20px;
      box-sizing: border-box;
    }
  }
  .card_content {
    min-height: 200px;
    padding: 20px;
  }
  .bottomBtns {
    width: 100%;
    text-align: right;
    margin-top: 20px;
    /deep/.el-button {
      //width: 70px;
      height: 36px;
      font-size: 14px;
      background-color: rgba(#3d7fff, 0.1);
      color: #3d7fff;
    }
    .el-button:first-of-type {
      background-color: #3d7fff;
      color: #fff;
    }
  }
  .tips {
    font-size: 12px;
    line-height: 18px;
    margin-top: 20px;
    span {
      color: rgba(#000, 0.6);
    }
    span:first-of-type {
      color: #ff8f1f;
    }
  }
}
</style>
