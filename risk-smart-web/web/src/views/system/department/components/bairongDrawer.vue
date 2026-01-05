<template>
  <div class="bairongDrawer">
    <el-form
      :model="formData"
      label-width="140px"
      ref="formDataRef"
      :rules="rules"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="appkey"
            prop="appKey"
          >
            <el-input
              v-model="formData.appKey"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="令牌"
            prop="apiCode"
          >
            <el-input
              v-model="formData.apiCode"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="手机号舆情"
            prop="phoneNumberPublicOpinion"
          >
            <el-input
              v-model="formData.phoneNumberPublicOpinion"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12"> <el-form-item
            label="自然人识别"
            prop="naturalPersonIdentification"
          >
            <el-input
              v-model="formData.naturalPersonIdentification"
              placeholder="请输入"
            ></el-input>
          </el-form-item></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="手机在网时长"
            prop="telOnlineTime"
          >
            <el-input
              v-model="formData.telOnlineTime"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12"> <el-form-item
            label="手机在网状态"
            prop="telOnlineStatus"
          >
            <el-input
              v-model="formData.telOnlineStatus"
              placeholder="请输入"
            ></el-input>
          </el-form-item></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="被执行人限高版"
            prop="executionLimit"
          >
            <el-input
              v-model="formData.executionLimit"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="被执行人高级版"
            prop="executionPro"
          >
            <el-input
              v-model="formData.executionPro"
              placeholder="请输入"
            ></el-input>
          </el-form-item></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="特殊名单验证"
            prop="specialList"
          >
            <el-input
              v-model="formData.specialList"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12"> <el-form-item
            label="借贷意向验证"
            prop="loanIntention"
          >
            <el-input
              v-model="formData.loanIntention"
              placeholder="请输入"
            ></el-input>
          </el-form-item></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="偿债压力指数"
            prop="debtServiceStressIndex"
          >
            <el-input
              v-model="formData.debtServiceStressIndex"
              placeholder="请输入"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="团伙欺诈排查"
            prop="gangFraud"
          >
            <el-input
              v-model="formData.gangFraud"
              placeholder="请输入"
            ></el-input>
          </el-form-item></el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            label="百融数据更新时间"
            prop="period"
          >
            <el-input
              v-model.number="formData.period"
              placeholder="请输入"
            >
              <template slot="append">天</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="bottomBtns">
      <el-button
        class="btn"
        @click="submit"
      >确 定</el-button>
      <el-button @click="handleCloase">取 消</el-button>
    </div>
  </div>
</template>

<script>
import {
  getBairongInfo,
  submitBairongInfo
} from '@/api/system/department.js'
export default {
  props: {
    dataInfo: {
      type: Object,
      default: () => { }
    }
  },
  data() {
    return {
      formData: {
        id: null,
        appKey: null,
        apiCode: null,
        phoneNumberPublicOpinion: null,
        naturalPersonIdentification: null,
        telOnlineTime: null,
        telOnlineStatus: null,
        executionLimit: null,
        executionPro: null,
        specialList: null,
        loanIntention: null,
        debtServiceStressIndex: null,
        gangFraud: null,
        period: null,
      },
      rules: {
        appKey: [{ required: true, message: '请输入appkey', trigger: 'blur' }],
        apiCode: [{ required: true, message: '请输入令牌', trigger: 'blur' }],
        phoneNumberPublicOpinion: [{ required: true, message: '请输入手机号舆情', trigger: 'blur' }],
        naturalPersonIdentification: [{ required: true, message: '请输入自然人识别', trigger: 'blur' }],
        telOnlineTime: [{ required: true, message: '请输入手机在网时长', trigger: 'blur' }],
        telOnlineStatus: [{ required: true, message: '请输入手机在网状态', trigger: 'blur' }],
        executionLimit: [{ required: true, message: '请输入被执行人限高版', trigger: 'blur' }],
        executionPro: [{ required: true, message: '请输入被执行人高级版', trigger: 'blur' }],
        specialList: [{ required: true, message: '请输入特殊名单验证', trigger: 'blur' }],
        loanIntention: [{ required: true, message: '请输入借贷意向验证', trigger: 'blur' }],
        debtServiceStressIndex: [{ required: true, message: '请输入偿债压力指数', trigger: 'blur' }],
        gangFraud: [{ required: true, message: '请输入团伙欺诈排查', trigger: 'blur' }],
        period: [
          { required: true, message: '请输入百融数据更新时间', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              let checkNumber = /^\d+$/
              if (!checkNumber.test(value)) callback('请输入正整数')
              callback()
            }, trigger: 'blur'
          },
        ],
      },
      deptId: null
    }
  },
  watch: {
    dataInfo: {
      handler(val) {
        if (val.hasOwnProperty('deptId')) {
          this.deptId = val.deptId
          this.getDataInfo()
        }
      }, deep: true, immediate: true
    }
  },
  methods: {
    getDataInfo() {
      getBairongInfo({ deptId: this.deptId }).then((res) => {
        if (res.code == 200 && res.hasOwnProperty('data')) {
          let data = res.data
          for (let key in this.formData) {
            if (data[key] != null) this.formData[key] = data[key]
          }
        }
      }).catch((err) => { });
    },
    submit() {
      this.$refs.formDataRef.validate((valid) => {
        if (valid) {
          submitBairongInfo({ deptId: this.deptId, ...this.formData }).then((res) => {
            if (res.code == 200) {
              this.$message.success('操作成功')
              this.handleCloase()
            }
          }).catch((err) => {

          });
        }
      })

    },
    handleCloase() {
      this.formData = this.$options.data().formData
      this.deptId = null
      this.$emit('close')
    }
  }
}
</script>

<style lang='less' scoped>
.bairongDrawer {
  padding: 0px 20px 20px;

  .bottomBtns {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    .btn {
      color: #fff;
      border: none;
      background: #409eff;
    }
  }
}
</style>