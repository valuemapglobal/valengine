<template>
  <div>
    <el-drawer
      :visible.sync="visible"
      direction="rtl"
      :with-header="false"
      size='40%'
    >
      <div class="drawer">
        <Headline @click="resetFields">{{ title }}</Headline>
        <el-form
          :model="form"
          :rules="rules"
          ref="form"
          label-width="100px"
        >

          <el-form-item
            label="审批结果："
            prop="approvalStatus"
          >
            <el-radio-group v-model="form.approvalStatus">
              <el-radio :label="1">通过</el-radio>
              <el-radio :label="2">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item
            label="审批意见："
            prop="approvalRemark"
          >
            <el-input
              type="textarea"
              v-model="form.approvalRemark"
              :autosize="{ minRows: 3 }"
            ></el-input>
          </el-form-item>
          <el-form-item style="float: right">
            <el-button
              type="primary"
              @click="submit('ruleForm')"
            >通过</el-button>
            <el-button @click="resetFields()">取消</el-button>
          </el-form-item>
        </el-form>
      </div>

    </el-drawer>
  </div>
</template>

<script>
import Headline from "@/components/Headline";
import { getApproval } from '../../api/platformEngine'
export default {
  components: {
    Headline
  },
  props: {
    title: {
      type: String,
      default: '审核'
    },
    info: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      form: {
        approvalStatus: 1,
        approvalRemark: ''
      },
      visible: false,
      rules: {
        approvalStatus: { required: true, message: '请选择审批结果', trigger: 'change' }
      }

    }
  },
  methods: {
    resetFields() {
      this.visible = false
      this.form = {}
    },
    submit() {
      if (this.form.approvalStatus == 2) {
        if (!this.form.approvalRemark && !this.form.approvalRemark.length <= 200) {
          return this.$message.error('请输入200字以下的意见')
        }
      }
      let data = {
        nodeId: this.info.nodeId + 1,
        processTask: this.info.processTask,
        id: this.info.nId
      }
      getApproval({ ...data, ...this.form }).then((res) => {
        if (res.code == 200) {

          this.$emit('success')
          this.resetFields()
        }
      })
    },
  }
}
</script>

<style lang="less" scoped>
.drawer {
  padding: 30px 20px;
  box-sizing: border-box;
}

::v-deep .el-textarea__inner {
  background: #f4f6f9;
  border: none;
}
</style>