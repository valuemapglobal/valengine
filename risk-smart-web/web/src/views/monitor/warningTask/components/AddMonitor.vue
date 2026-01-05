<template>
  <el-dialog
    :title="dialog.title"
    :visible.sync="dialog.visible"
    :width="dialog.width"
    :before-close="handleClose"
  >
    <div class="addMonitor">
      <div class="tabs">
        <div class="tabs-item" v-for="(item, index) in tabsList" :key="index">
          <div
            class="tabs-item_title"
            :class="{ 'tabs-item_title-active': item.value == activeTabs }"
            @click="handleTabClick(index)"
            v-if="isChange || (!isChange && subjectType == item.value)"
          >
            {{ item.label }}
          </div>
        </div>
      </div>
      <div v-if="activeTabs == 0">
        <el-input
          v-model="queryParams.cname"
          placeholder="请输入企业名称或社会统一信用代码"
          clearable
        />
        <div class="cardList" ref="cardRef">
          <div class="cardItem" v-for="(item, index) in dataList" :key="index">
            <div class="info">
              <div class="title">{{ item?.cname || '' }}</div>
              <div class="flex">
                <span>法人：{{ item?.operName || '' }}</span>
                <span>信用代码：{{ item?.creditCode || '' }}</span>
              </div>
            </div>
            <el-button
              type="primary"
              @click="handleAdd(item)"
              :loading="item.loading"
              :disabled="item.loading"
              >添加监控</el-button
            >
          </div>
          <div class="loadDiv" v-if="loading">
            <i class="el-icon-loading" style="margin-right: 5px" />数据加载中
          </div>
        </div>
      </div>

      <el-form
        v-else
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="客户名称" prop="customerName">
          <el-input
            v-model="formData.customerName"
            placeholder="请输入客户名称"
          />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNumber">
          <el-input v-model="formData.idNumber" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobilePhone">
          <el-input v-model="formData.mobilePhone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer" v-if="activeTabs == 1">
      <el-checkbox v-if="activeTabs == 1" v-model="checked"
        >已获得客户本人信息使用授权</el-checkbox
      >
      <div class="btns">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :disabled="btnLoading">
          {{ btnLoading ? '确认中' : '确认' }}
          <i
            style="margin-left: 4px"
            class="el-icon-loading"
            v-if="btnLoading"
          />
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { RiskSearch } from '@/api/risk/search/index.js'
import { addMonitor } from '../../api'
export default {
  name: 'AddMonitor',
  components: {},
  props: {
    isChange: {
      type: Boolean,
      default: false,
    },
    subjectType: {
      type: [String, Number],
      default: 0,
    },
  },
  data() {
    return {
      tabsList: [
        { label: '企业', value: 0 },
        { label: '个人', value: 1 },
      ],
      dialog: {
        visible: false,
        title: '新增监控名单',
        width: '600px',
      },
      activeTabs: null,

      loading: false,
      queryParams: {
        cname: null,
      },
      params: {
        pageNum: 1,
        pageSize: 10,
      },
      dataList: [],
      total: 0,

      taskId: null,
      btnLoading: false,
      formData: {
        customerName: null,
        idNumber: null,
        mobilePhone: null,
      },
      rules: {
        customerName: {
          required: true,
          message: '请输入客户名称',
          trigger: 'blur',
        },
        idNumber: [
          { required: true, message: '请输入身份证号', trigger: 'blur' },
          {
            pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}[\dXx]$)/,
            message: '请输入正确的身份证号',
            trigger: 'blur',
          },
        ],
        mobilePhone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          {
            pattern:
              /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur',
          },
        ],
      },
      checked: false,
    }
  },
  watch: {
    // subjectType: {
    //   handler(val) {
    //     console.log(
    //       this.isChange ? 0 : val,
    //       'val=============sssssssssssssddddddddddddddddddd'
    //     )
    //     console.log(this.isChange, 'this.isChange')

    //     this.handleTabClick(this.isChange ? 0 : val)
    //   },
    //   immediate: true,
    // },
    activeTabs: {
      handler() {
        this.queryParams = this.$options.data().queryParams
      },
      deep: true,
    },
    queryParams: {
      handler(val) {
        if (val.cname) this.handleDataList()
        else this.dataList = []
      },
      deep: true,
    },
  },
  methods: {
    handleDataList() {
      this.loading = true
      this.dataList = []
      RiskSearch({ ...this.queryParams, ...this.params })
        .then((res) => {
          if (res.code == 200) {
            this.dataList.push(
              ...res.data.list.map((item) => {
                return {
                  ...item.baseinfo,
                  loading: false,
                }
              })
            )
            this.total = res.data.total
          }
          this.loading = false
        })
        .catch((err) => {})
    },

    handleSubmit() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          if (!this.checked) {
            this.$message.warning('请先勾选是否已获得客户本人信息使用授权')
            return
          }
          this.btnLoading = true
          addMonitor({ ...this.formData, taskId: this.taskId })
            .then((res) => {
              if (res.code == 200) {
                this.$message.success('添加成功')
                this.$emit('refresh')
                this.handleClose()
              }
              this.btnLoading = false
            })
            .catch(() => {
              this.btnLoading = false
            })
        }
      })
    },

    handleAdd(data) {
      let form = {
        customerName: data.cname,
        creditCode: data.creditCode,
        taskId: this.taskId,
      }
      data.loading = true
      addMonitor(form)
        .then((res) => {
          if (res.code == 200) {
            this.$message.success('添加成功')
            this.$emit('refresh')
            this.handleClose()
          }
          data.loading = false
        })
        .catch(() => {
          data.loading = false
        })
    },
    handleTabClick(data) {
      this.activeTabs = data
    },
    handleOpen(taskId) {
      this.taskId = taskId
      this.dataList = []
      this.checked = false
      this.handleTabClick(this.isChange ? 0 : this.subjectType)
      this.queryParams = this.$options.data().queryParams
      this.formData = this.$options.data().formData
      this.dialog.visible = true
    },
    handleClose() {
      this.dialog.visible = false
    },
  },
}
</script>
<style lang="less" scoped>
::v-deep .el-dialog {
  border-radius: 12px;
  font-family: PingFang SC-Medium;
  min-height: 260px;
  .el-dialog__header {
    padding: 20px;
    margin-bottom: 0px;
  }
  .el-dialog__body {
    padding: 0px 20px 20px;
  }
}
::v-deep .addMonitor {
  .tabs {
    display: flex;
    margin-bottom: 20px;
    box-sizing: border-box;
    position: relative;
    .tabs-item {
      cursor: pointer;
      .tabs-item_title {
        font-size: 14px;
        padding-bottom: 8px;
        margin-right: 16px;

        &-active {
          color: var(--primary-color);
          border-bottom: 1px solid var(--primary-color);
        }
      }
    }
    &::after {
      content: '';
      width: 100%;
      height: 1px;
      background: rgba(#000, 0.1);
      position: absolute;
      bottom: 0;
      left: 0;
    }
  }

  .el-input {
    .el-input__inner {
      background: rgba(#000, 0.04);
      height: 42px;
    }
  }

  .cardList {
    margin-top: 10px;
    max-height: 390px;
    overflow-y: auto;
    .cardItem {
      padding: 10px;
      background: rgba(#000, 0.04);
      margin-bottom: 10px;
      display: flex;
      align-items: center;
      justify-content: space-between;

      .info {
        width: calc(100% - 90px);
        display: flex;
        flex-direction: column;
        .title {
          color: rgba(#000, 0.85);
          font-size: 14px;
          margin-bottom: 10px;
        }
        .flex {
          display: flex;
          align-items: center;
        }
        span {
          display: block;
          margin-right: 10px;
          max-width: 50%;
          font-size: 12px;
          color: rgba(#000, 0.6);
        }
        .title,
        span {
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }

      .el-button {
        width: 72px;
        height: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
        // background: rgba(#ff8f1f, 0.1);
        // color: #ff8f1f;
        font-size: 12px;
        border-radius: 4px;
      }
    }
    .loadDiv {
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
}

.dialog-footer {
  display: flex;
  align-items: center;
  position: relative;
  height: 36px;
  .btns {
    position: absolute;
    right: 20px;
    .el-button {
      width: 90px;
      border-radius: 3px;
      height: 36px;
    }
  }
}
</style>
