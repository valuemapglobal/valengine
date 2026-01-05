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
      >确定</el-button>
      <el-button @click="handleClose">取消</el-button>
    </div>
  </div>
</template>

<script>
import { submitModel } from '@/api/dataRisk/riskModel.js'
import { mapState } from 'vuex'
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
      industryList: [
        "通用",
        '农业',
        '林业',
        '畜牧业',
        '渔业',
        '农、林、牧、渔专业及辅助性活动',
        '煤炭开采和洗选业',
        '石油和天然气开采业',
        '黑色金属矿采选业',
        '有色金属矿采选业',
        '非金属矿采选业',
        '开采专业及辅助性活动',
        '其他采矿业',
        '农副食品加工业',
        '食品制造业',
        '酒、饮料和精制茶制造业',
        '烟草制品业',
        '纺织业',
        '纺织服装、服饰业',
        '皮革、毛皮、羽毛及其制品和制鞋业',
        '木材加工和木、竹、藤、棕、草制品业',
        '家具制造业',
        '造纸和纸制品业',
        '印刷和记录媒介复制业',
        '文教、工美、体育和娱乐用品制造业',
        '石油、煤炭及其他燃料加工业',
        '化学原料和化学制品制造业',
        '医药制造业',
        '化学纤维制造业',
        '橡胶和塑料制品业',
        '非金属矿物制品业',
        '黑色金属冶炼和压延加工业',
        '有色金属冶炼和压延加工业',
        '金属制品业',
        '通用设备制造业',
        '专用设备制造业',
        '汽车制造业',
        '铁路、船舶、航空航天和其他运输设备制造业',
        '电气机械和器材制造业',
        '计算机、通信和其他电子设备制造业',
        '仪器仪表制造业',
        '其他制造业',
        '废弃资源综合利用业',
        '金属制品、机械和设备修理业',
        '电力、热力生产和供应业',
        '燃气生产和供应业',
        '水的生产和供应业',
        '房屋建筑业',
        '土木工程建筑业',
        '建筑安装业',
        '建筑装饰、装修和其他建筑业',
        '批发业',
        '零售业',
        '铁路运输业',
        '道路运输业',
        '水上运输业',
        '航空运输业',
        '管道运输业',
        '多式联运和运输代理业',
        '装卸搬运和仓储业',
        '邮政业',
        '住宿业',
        '餐饮业',
        '电信、广播电视和卫星传输服务',
        '互联网和相关服务',
        '软件和信息技术服务业',
        '货币金融服务',
        '资本市场服务',
        '保险业',
        '其他金融业',
        '房地产业',
        '租赁业',
        '商务服务业',
        '研究和试验发展',
        '专业技术服务业',
        '科技推广和应用服务业',
        '水利管理业',
        '生态保护和环境治理业',
        '公共设施管理业',
        '土地管理业',
        '居民服务业',
        '机动车、电子产品和日用产品修理业',
        '其他服务业',
        '教育',
        '卫生',
        '社会工作',
        '新闻和出版业',
        '广播、电视、电影和录音制作业',
        '文化艺术业',
        '体育',
        '娱乐业',
        '中国共产党机关',
        '国家机构',
        '人民政协、民主党派',
        '社会保障',
        '群众团体、社会团体和其他成员组织',
        '基层群众自治组织',
        '国际组织'
      ]
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
            name: '应用行业',
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

  /deep/.el-form {
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