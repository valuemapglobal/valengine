<template>
  <div class="valupMapDrawer">
    <el-form
      ref="formDataRef"
      :model="formData"
      :label-width="isEnglish() ? '200px' : '140px'"
      :rules="rules"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.appKey')"
            prop="appKey"
          >
            <el-input
              v-model="formData.appKey"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.secret')"
            prop="secret"
          >
            <el-input
              v-model="formData.secret"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item :label="$t('departmentManagement.apiUrl')" prop="url">
            <el-input
              v-model="formData.url"
              :placeholder="$t('common.pleaseInput')"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            :label="$t('departmentManagement.judicialDataUpdateTime')"
            prop="cacheTime"
          >
            <el-input
              v-model.number="formData.cacheTime"
              :placeholder="$t('common.pleaseInput')"
            >
              <template slot="append">{{
                $t('departmentManagement.days')
              }}</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="bottomBtns">
      <el-button type="primary" @click="submit">{{
        $t('common.sure')
      }}</el-button>
      <el-button @click="handleCloase">{{ $t('common.cancel') }}</el-button>
    </div>
  </div>
</template>

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
      deptId: null,
    }
  },
  computed: {
    rules() {
      return {
        appKey: [
          {
            required: true,
            message: this.$t('departmentManagement.inputAppKey'),
            trigger: 'blur',
          },
        ],
        secret: [
          {
            required: true,
            message: this.$t('departmentManagement.inputSecret'),
            trigger: 'blur',
          },
        ],
        url: [
          {
            required: true,
            message: this.$t('departmentManagement.inputApiUrl'),
            trigger: 'blur',
          },
        ],
        cacheTime: [
          {
            required: true,
            message: this.$t(
              'departmentManagement.inputJudicialDataUpdateTime'
            ),
            trigger: 'blur',
          },
          {
            validator: (rule, value, callback) => {
              let checkNumber = /^\d+$/
              if (!checkNumber.test(value))
                callback(this.$t('departmentManagement.inputPositiveInteger'))
              callback()
            },
            trigger: 'blur',
          },
        ],
      }
    },
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
                this.$message.success(this.$t('common.success'))
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
