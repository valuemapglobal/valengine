<template>
  <el-drawer
    :close-on-click-modal="false"
    :title="drawer.title"
    :visible.sync="drawer.visible"
    :before-close="closeDrawer"
    :size="drawer.size"
    append-to-body
  >
    <div class="addResources">
      <el-form :model="info" :rules="rules" label-width="90px" ref="formRef">
        <el-form-item label="资源名称" prop="name">
          <el-input v-model="info.name" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="info.description"
            type="textarea"
            :rows="3"
            placeholder="请输入"
            clearable
          />
        </el-form-item>
        <el-form-item label="资源分类" prop="type">
          <el-select
            v-model="info.type"
            placeholder="请选择"
            clearable
            :popper-append-to-body="false"
          >
            <el-option
              v-for="(item, index) in collectionList.resourceTypeList"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="绑定角色" prop="roleId">
          <el-select
            v-model="info.roleId"
            placeholder="请选择"
            clearable
            :popper-append-to-body="false"
          >
            <el-option
              v-for="(item, index) in collectionList.roleList"
              :key="index"
              :label="item.roleName"
              :value="item.roleId + ''"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="接口路径" prop="roleId">
          <el-select
            v-model="info.apiId"
            placeholder="请选择"
            clearable
            :popper-append-to-body="false"
          >
            <el-option
              v-for="(item, index) in collectionList.interFaceList"
              :key="index"
              :label="`${item.apiDescription} ${item.apiUrl}`"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="报告类型" prop="reportType">
          <el-select
            v-model="info.reportType"
            placeholder="请选择"
            clearable
            :popper-append-to-body="false"
          >
            <el-option
              v-for="(item, index) in collectionList.reportTypeList"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="客户类型" prop="userType">
          <el-select
            v-model="info.userType"
            placeholder="请选择"
            clearable
            :popper-append-to-body="false"
          >
            <el-option
              v-for="(item, index) in collectionList.userTypeList"
              :key="index"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源图标" prop="icon">
          <MyUpload
            :uploadUrl="uploadUrl"
            :showName="false"
            hasDelete
            :dataList.sync="info.icon"
            :limit="1"
          >
            <div slot="upload" class="uploadImg">
              <img src="@/assets/images/accountResources/upload.png" />
            </div>
          </MyUpload>
        </el-form-item>
      </el-form>
      <div class="bottomBtns">
        <el-button type="primary" @click="handleConfirm">确定</el-button>
        <el-button @click="closeDrawer">取消</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import MyUpload from './myUpload'
import { add, edit } from '@/api/system/resources'
export default {
  props: {
    resourcesInfo: {
      type: Object || null,
      default: null,
    },
    collectionList: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  components: {
    MyUpload,
  },
  data() {
    return {
      drawer: {
        visible: false,
        title: '添加资源',
        size: '35%',
      },
      info: {
        id: null,
        name: null,
        description: null,
        type: null,
        roleId: null,
        apiId: null,
        reportType: null,
        userType: null,
        icon: [],
      },
      elementName: null,
      rules: {
        name: [{ required: true, message: '请输入资源名称', trigger: 'blur' }],
        description: [
          { required: true, message: '请输入资源描述', trigger: 'blur' },
        ],
        type: [{ required: true, message: '请选择资源类别', trigger: 'blur' }],
        roleId: [{ required: true, message: '请输入角色', trigger: 'blur' }],
        icon: [{ required: true, message: '请上传资源图标', trigger: 'blur' }],
      },
      uploadUrl: null,
    }
  },
  watch: {
    resourcesInfo: {
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            for (let key in this.info) {
              if (val[key] != null) {
                this.info[key] = val[key] + ''
                if (key == 'icon') this.info.icon = [val.icon]
              }
            }
          })
        }
      },
      deep: true,
      immediate: true,
    },
    'drawer.visible': {
      handler(val) {
        if (val) {
          this.info = this.$options.data().info
        }
      },
    },
  },
  computed: {},
  mounted() {
    this.uploadUrl =
      process.env.VUE_APP_ENV === 'production'
        ? '/prod-api/finance/common/uploadFile'
        : '/dev-api/prod-api/finance/common/uploadFile'
  },
  methods: {
    initInfo() {},
    handleConfirm() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          let params = { ...this.info }
          let URL = params.id ? edit : add
          params.resourceId = params.id
          params.icon = params.icon.map((item) => item.url).join(',')
          // console.log(params, 'params----------')
          // return
          URL(params)
            .then((res) => {
              if (res.code == 200) {
                this.$message.success = '操作成功'
                this.closeDrawer()
                this.$emit('success')
              }
            })
            .catch((err) => {})
        }
      })
    },
    handleCancel() {
      this.$emit('cancel')
    },
    openDrawer() {
      this.drawer.visible = true
    },
    closeDrawer() {
      this.drawer.visible = false
    },
  },
}
</script>

<style lang="less" scoped>
::v-deep(.el-drawer) {
  .el-drawer__header {
    margin-bottom: 0px;
  }
  .el-drawer__body {
    padding: 20px;
  }
}
.addResources {
  ::v-deep(.el-form) {
    .el-input,
    .el-select {
      width: 100%;
      .el-input__inner {
        background-color: #f4f6f9;
        height: 48px;
      }
    }
    .el-textarea {
      .el-textarea__inner {
        background-color: #f4f6f9;
      }
    }
  }
  .bottomBtns {
    margin-top: 20px;
    width: 100%;
    display: flex;
    justify-content: flex-end;
    .el-button {
      width: 70px;
      height: 36px;
    }
    .el-button:last-child {
      color: #3d7fff;
      background-color: rgba(#3d7fff, 0.1);
    }
  }
}
.uploadImg {
  margin-top: 5px;
  > img {
    width: 150px;
    height: 150px;
  }
}
</style>
