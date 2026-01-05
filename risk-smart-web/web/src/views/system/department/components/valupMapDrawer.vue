<template>
  <div class="valupMapDrawer">
    <el-form
      ref="formDataRef"
      :model="formData"
      label-width="140px"
      :rules="rules"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="appkey" prop="appKey">
            <el-input v-model="formData.appKey" placeholder="请输入"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="秘钥" prop="secret">
            <el-input v-model="formData.secret" placeholder="请输入"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="接口地址" prop="url">
            <el-input v-model="formData.url" placeholder="请输入"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="司法数据更新时间" prop="cacheTime">
            <el-input v-model.number="formData.cacheTime" placeholder="请输入">
              <template slot="append">天</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="bottomBtns">
      <el-button type="primary" @click="submit">确 定</el-button>
      <el-button @click="handleCloase">取 消</el-button>
    </div>
  </div>
</template>
{ required: true, message: '请输入appkey', trigger: 'blur' }
<script>
import { getValueMapInfo, submitValueMapInfo } from '@/api/system/department.js'
export default {
  props: {
    dataInfo: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      formData: {
        id: null,
        appKey: null,
        deptName: null,
        secret: null,
        cacheTime: null,
        url: null,
      },
      rules: {
        appKey: [{ required: true, message: '请输入appkey', trigger: 'blur' }],
        secret: [{ required: true, message: '请输入秘钥', trigger: 'blur' }],
        url: [{ required: true, message: '请输入接口地址', trigger: 'blur' }],
        cacheTime: [
          {
            required: true,
            message: '请输入司法数据更新时间',
            trigger: 'blur',
          },
          {
            validator: (rule, value, callback) => {
              let checkNumber = /^\d+$/
              if (!checkNumber.test(value)) callback('请输入正整数')
              callback()
            },
            trigger: 'blur',
          },
        ],
      },
      deptId: null,
    }
  },
  watch: {
    dataInfo: {
      handler(val) {
        if (val.hasOwnProperty('deptId')) {
          this.deptId = val.deptId
          this.formData.deptName = val.deptName
          this.getDataInfo()
        }
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getDataInfo() {
      getValueMapInfo({ deptId: this.deptId, pageSize: 1, pageNum: 1 })
        .then((res) => {
          if (res.code == 200 && res.data.list.length) {
            let data = res.data.list[0]
            for (let key in this.formData) {
              if (data[key] != null) this.formData[key] = data[key]
            }
          }
        })
        .catch((err) => {})
    },
    submit() {
      this.$refs.formDataRef.validate((valid) => {
        if (valid) {
          submitValueMapInfo({ deptId: this.deptId, ...this.formData })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('操作成功')
                this.handleCloase()
              }
            })
            .catch((err) => {})
        }
      })
    },
    handleCloase() {
      this.formData = this.$options.data().formData
      this.deptId = null
      this.$emit('close')
    },
  },
}
</script>

<style lang="less" scoped>
.valupMapDrawer {
  padding: 0px 20px 20px;

  .bottomBtns {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    .el-button {
      padding: 0px 24px;
      height: 42px;
      font-size: 14px;
      border-radius: 8px;
    }
  }
}
</style>
