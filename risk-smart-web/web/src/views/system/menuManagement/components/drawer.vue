<template>
  <div>
    <el-drawer
      :title="flag === 0 ? '添加菜单' : '修改菜单'"
      :before-close="handleClose"
      :visible.sync="dialog"
      :wrapperClosable="false"
      direction="rtl"
      custom-class="demo-drawer"
      size="80%"
      ref="drawer"
    >
      <div class="demo-drawer__content" style="padding: 20px">
        <el-form :model="form" ref="form" :rules="rules">
          <div class="permutation">
            <el-form-item
              class="perSon"
              label="上级菜单"
              :label-width="formLabelWidth"
              prop="parentId"
            >
              <el-cascader
                style="width: 100%"
                v-model="form.parentId"
                :show-all-levels="false"
                :options="menuOptions"
                :props="{ checkStrictly: true }"
                clearable
              ></el-cascader>
            </el-form-item>
            <el-form-item
              class="perSon"
              label="菜单目录"
              :label-width="formLabelWidth"
            >
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
          <div class="permutation">
            <!-- <el-form-item
              class="perSon"
              label="菜单图标"
              :label-width="formLabelWidth"
            >
              <el-popover
                placement="bottom-start"
                width="460"
                trigger="click"
                @show="$refs['iconSelect'].reset()"
              >
                <IconSelect ref="iconSelect" @selected="selected" />
                <el-input
                  slot="reference"
                  v-model="form.icon"
                  placeholder="点击选择图标"
                  readonly
                >
                  <svg-icon
                    v-if="form.icon"
                    slot="prefix"
                    :icon-file-name="form.icon"
                    class="el-input__icon"
                    style="height: 32px; width: 16px"
                  />
                  <i
                    v-else
                    slot="prefix"
                    class="el-icon-search el-input__icon"
                  />
                </el-input>
              </el-popover> 
            </el-form-item>-->
            <el-form-item
              class="perSon"
              label="菜单名称"
              :label-width="formLabelWidth"
              prop="menuName"
            >
              <el-input
                v-model="form.menuName"
                autocomplete="off"
                placeholder="请输入"
              ></el-input>
            </el-form-item>
            <el-form-item
              class="perSon"
              label="显示顺序"
              :label-width="formLabelWidth"
              prop="orderNum"
            >
              <el-input-number
                v-model="form.orderNum"
                style="width: 100%"
                controls-position="right"
                :min="0"
                :max="10"
              ></el-input-number>
            </el-form-item>
          </div>
          <div class="permutation">
            <el-form-item :label-width="formLabelWidth" class="perSon">
              <span slot="label">
                <el-tooltip
                  content="选择停用则路由将不会出现在侧边栏，也不能被访问"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                是否外链
              </span>
              <el-radio-group v-model="form.isFrame">
                <el-radio label="0">是</el-radio>
                <el-radio label="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
            <div class="perSon" style="display: flex; align-item: center">
              <el-form-item :label-width="formLabelWidth">
                <span slot="label">
                  <el-tooltip
                    content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问"
                    placement="top"
                  >
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                  显示状态
                </span>
                <el-radio-group v-model="form.visible">
                  <el-radio label="0">显示</el-radio>
                  <el-radio label="1">隐藏</el-radio>
                </el-radio-group>
              </el-form-item>
            </div>
          </div>
          <div class="permutation">
            <el-form-item
              class="perSon"
              :label-width="formLabelWidth"
              prop="path"
            >
              <span slot="label">
                <el-tooltip
                  content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                路由地址
              </span>
              <el-input
                v-model="form.path"
                autocomplete="off"
                placeholder="请输入"
              >
              </el-input>
            </el-form-item>
            <el-form-item
              class="perSon"
              :label-width="formLabelWidth"
              prop="perms"
            >
              <span slot="label"> 权限标识 </span>
              <el-input
                v-model="form.perms"
                autocomplete="off"
                placeholder="请输入"
              >
              </el-input>
            </el-form-item>
          </div>
          <div class="permutation">
            <el-form-item class="perSon" :label-width="formLabelWidth">
              <span slot="label">
                <el-tooltip
                  content="选择停用则路由将不会出现在侧边栏，也不能被访问"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                菜单状态
              </span>
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-form>
        <div class="demo-drawer__footer">
          <el-button type="primary" @click="applyImmediate">{{
            this.flag === 0 ? '立即添加' : '立即修改'
          }}</el-button>
          <el-button @click="cancelForm">取消</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import IconSelect from '@/components/IconSelect'
import { AddOrUpdateMenuList } from '@/api/system/menuManage'
export default {
  name: 'MenuDrawer',
  components: {
    IconSelect,
  },
  data() {
    return {
      // 菜单树选项
      menuOptions: [],
      dialog: false,
      flag: 0,
      parentMune: null,
      form: {
        parentId: [0],
        menuType: 'M',
        icon: null,
        menuName: null,
        orderNum: null,
        isFrame: '1',
        path: null,
        visible: '0',
        status: '0',
      },
      rowData: {},
      formLabelWidth: '100px',
      labelPosition: 'top',
      rules: {
        parentId: [
          {
            required: true,
            message: '请选择上级菜单',
            trigger: 'blur',
          },
        ],
        menuName: [
          {
            required: true,
            message: '请输入菜单名称',
            trigger: 'blur',
          },
        ],
        orderNum: [
          {
            required: true,
            message: '请选择菜单顺序',
            trigger: 'blur',
          },
        ],
        // path: [
        //   {
        //     required: true,
        //     message: '请输入路由地址',
        //     trigger: 'blur',
        //   },
        // ],
      },
    }
  },
  created() {},
  mounted() {},
  watch: {
    'form.parentId': {
      deep: true,
      handler(val) {
        if (this.flag === 0) {
          this.parentMune = val.length > 0 ? val[val.length - 1] : ''
        } else {
          if (val instanceof Array) {
            this.parentMune = val.length > 0 ? val[val.length - 1] : ''
          } else {
            this.parentMune = val
          }
        }
      },
      immediate: true,
    },
    dialog(val) {
      if (!val) {
        this.form = {
          parentId: [0],
          menuType: 'M',
          icon: null,
          menuName: null,
          orderNum: null,
          isFrame: '1',
          path: null,
          visible: '0',
          status: '0',
        }
        this.parentMune = null
      }
    },
  },
  methods: {
    // 选择图标
    selected(name) {
      this.form.icon = name
    },
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children,
      }
    },
    handleClose() {
      this.dialog = false
    },
    cancelForm() {
      this.dialog = false
    },
    // 立即申请
    applyImmediate() {
      // flag为0的时候是新增,为1的时候是修改
      this.$refs['form'].validate(async (valid) => {
        if (valid) {
          try {
            let data = JSON.parse(JSON.stringify(this.form))
            data = { ...data, parentId: this.parentMune }
            let dig = 'add'
            if (this.flag === 1) {
              dig = 'put'
              let das = Object.keys(data)
              for (let key in this.rowData) {
                if (!das.includes(key)) {
                  data[key] = this.rowData[key]
                }
              }
            }

            await AddOrUpdateMenuList(data, dig)
            this.$emit('refresh')
            this.$message.success(
              this.flag === 0 ? '添加菜单成功' : '修改菜单成功'
            )
          } catch (error) {
            this.$message.error(
              this.flag === 0 ? '添加菜单失败' : '修改菜单失败'
            )
            return console.log(error)
          }
          this.dialog = false
        } else {
          return false
        }
      })
    },
  },
}
</script>

<style lang="less" scoped>
.permutation {
  display: flex;
  justify-content: space-between;
  .perSon {
    flex: 1;
    &:first-child {
      margin-right: 20px;
    }
  }
}
/deep/ .el-radio__input.is-checked .el-radio__inner {
  border-color: #409eff;
  background: #409eff;
}
/deep/ .el-upload-dragger {
  background: rgba(54, 98, 236, 0.02);
}
/deep/ .ql-container.ql-snow {
  height: 300px;
}
/deep/ .ql-editor {
  padding: 0;
}
.announcementType {
  display: flex;
  div {
    &:first-child {
      margin-right: 20px;
    }
  }
}
.demo-drawer__footer {
  display: flex;
  justify-content: flex-end;
  .el-button {
    padding: 0 20px;
    height: 42px;
    font-size: 14px;
    border-radius: 6px;
  }
}
.el-upload__text {
  margin-top: 40px;
  em {
    font-size: 14px;
    font-family: PingFangSC-Regular, sans-serif;
    font-weight: normal;
    color: #1677ff;
  }
  p {
    font-size: 12px;
    font-family: PingFangSC-Regular, sans-serif;
    font-weight: normal;
    color: rgba(0, 0, 0, 0.3);
  }
}
</style>
