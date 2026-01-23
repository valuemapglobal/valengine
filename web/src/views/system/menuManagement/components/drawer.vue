<template>
  <div>
    <el-drawer
      :title="drawerTitle"
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
              :label="$t('menuManagement.parentMenu')"
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
              :label="$t('menuManagement.menuDirectory')"
              :label-width="formLabelWidth"
            >
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">{{
                  $t('menuManagement.directory')
                }}</el-radio>
                <el-radio label="C">{{ $t('menuManagement.menu') }}</el-radio>
                <el-radio label="F">{{ $t('menuManagement.button') }}</el-radio>
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
              :label="$t('menuManagement.menuName')"
              :label-width="formLabelWidth"
              prop="menuName"
            >
              <el-input
                v-model="form.menuName"
                autocomplete="off"
                :placeholder="$t('menuManagement.inputPlaceholder')"
              ></el-input>
            </el-form-item>
            <el-form-item
              class="perSon"
              :label="$t('menuManagement.displayOrder')"
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
                  :content="$t('menuManagement.disableTooltip')"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                {{ $t('menuManagement.isExternalLink') }}
              </span>
              <el-radio-group v-model="form.isFrame">
                <el-radio label="0">{{ $t('menuManagement.yes') }}</el-radio>
                <el-radio label="1">{{ $t('menuManagement.no') }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <div class="perSon" style="display: flex; align-items: center">
              <el-form-item :label-width="formLabelWidth">
                <span slot="label">
                  <el-tooltip
                    :content="$t('menuManagement.hideTooltip')"
                    placement="top"
                  >
                    <i class="el-icon-question"></i>
                  </el-tooltip>
                  {{ $t('menuManagement.displayStatus') }}
                </span>
                <el-radio-group v-model="form.visible">
                  <el-radio label="0">{{ $t('menuManagement.show') }}</el-radio>
                  <el-radio label="1">{{ $t('menuManagement.hide') }}</el-radio>
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
                  :content="$t('menuManagement.routeTooltip')"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                {{ $t('menuManagement.routeAddress') }}
              </span>
              <el-input
                v-model="form.path"
                autocomplete="off"
                :placeholder="$t('menuManagement.inputPlaceholder')"
              >
              </el-input>
            </el-form-item>
            <el-form-item
              class="perSon"
              :label-width="formLabelWidth"
              prop="perms"
            >
              <span slot="label">{{ $t('menuManagement.permissionKey') }}</span>
              <el-input
                v-model="form.perms"
                autocomplete="off"
                :placeholder="$t('menuManagement.inputPlaceholder')"
              >
              </el-input>
            </el-form-item>
          </div>
          <div class="permutation">
            <el-form-item class="perSon" :label-width="formLabelWidth">
              <span slot="label">
                <el-tooltip
                  :content="$t('menuManagement.disableTooltip')"
                  placement="top"
                >
                  <i class="el-icon-question"></i>
                </el-tooltip>
                {{ $t('menuManagement.menuStatus') }}
              </span>
              <el-radio-group v-model="form.status">
                <el-radio label="0">{{ $t('common.normal') }}</el-radio>
                <el-radio label="1">{{ $t('common.disabled') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </div>
        </el-form>
        <div class="demo-drawer__footer">
          <el-button type="primary" @click="applyImmediate">{{
            flag === 0
              ? $t('menuManagement.immediatelyAdd')
              : $t('menuManagement.immediatelyModify')
          }}</el-button>
          <el-button @click="cancelForm">{{ $t('common.cancel') }}</el-button>
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
      labelPosition: 'top',
    }
  },
  computed: {
    drawerTitle() {
      return this.flag === 0
        ? this.$t('menuManagement.addMenu')
        : this.$t('menuManagement.editMenu')
    },
    formLabelWidth() {
      return this.isEnglish() ? '140px' : '100px'
    },
    rules() {
      return {
        parentId: [
          {
            required: true,
            message: this.$t('menuManagement.selectParentMenu'),
            trigger: 'blur',
          },
        ],
        menuName: [
          {
            required: true,
            message: this.$t('menuManagement.inputMenuName'),
            trigger: 'blur',
          },
        ],
        orderNum: [
          {
            required: true,
            message: this.$t('menuManagement.selectMenuOrder'),
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
      }
    },
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
              this.flag === 0
                ? this.$t('menuManagement.addMenuSuccess')
                : this.$t('menuManagement.editMenuSuccess')
            )
          } catch (error) {
            this.$message.error(
              this.flag === 0
                ? this.$t('menuManagement.addMenuFailed')
                : this.$t('menuManagement.editMenuFailed')
            )
            return
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
