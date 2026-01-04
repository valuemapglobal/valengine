package com.value.decision.model.decisionmanage.common;

import cn.hutool.core.util.RandomUtil;
import com.value.decision.model.decisionmanage.model.vo.GenerateRuleVO;
import com.value.decision.model.rdenew.domain.RdeModelRuleProperty;
import com.value.decision.model.rdenew.domain.RdeRiskVariableGroup;
import com.value.decision.model.rdenew.domain.RdeRiskVariableRecord;
import com.value.decision.model.rdenew.mapper.RdeRiskVariableGroupMapper;
import com.value.decision.model.rdenew.mapper.RdeRiskVariableRecordMapper;
import com.value.decision.model.rdenew.vo.*;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * @description：解析规则
 * @author： andera
 * @create： 2023/8/9 17:57
 */
public class ParsingRules {

    @Resource
    private RdeRiskVariableGroupMapper rdeRiskVariableGroupMapper;
    @Resource
    private RdeRiskVariableRecordMapper rdeRiskVariableRecordMapper;

    /**
     * 生成规则 -- new
     */
    public String generateRule(GenerateRuleVO record) {

        StringBuffer group = new StringBuffer();
        group.append("rule \"RDE-" + record.getCode() + "" + RandomUtil.randomString(4) + "\"\r\n");

        //条件记录
        StringBuffer cwhen = new StringBuffer();
        //条件集合
        List<RdeRiskVariableArrayVO> conditionArray = record.getConditionArray();
        if (conditionArray == null || conditionArray.isEmpty()) {
            return "";
        }

        //必须携带的json对象
        StringBuffer cwhenFrist = new StringBuffer();
        cwhenFrist.append("	when \n   $fact: JsonRuleEntity(jsonObject: jsonObject)\n");

        cwhen.append("   eval(");

        //定义主体的拼接
        StringBuffer cwhenBody = new StringBuffer();

        //定义主体的拼接 针对集合
        StringBuffer cwhenBodyArray = new StringBuffer();

        //定义主体的拼接回括号
        StringBuffer cwhenBodyBack = new StringBuffer();
        cwhenBodyBack.append(")\n");

        //then语法拼接
        StringBuffer cwhenThen = new StringBuffer();
        cwhenThen.append("then\n   list.add(\"" + record.getCode() + "\");\nend");

        //对象类型集合
        for (int i = 0; i < conditionArray.size(); i++) {
            List<RdeRiskVariableConditionNewVO> condition = conditionArray.get(i).getCondition();
            for (int j = 0; j < condition.size(); j++) {
                //有集合
                if ("5".equals(condition.get(j).getSelectObj().getObjectFlag())) {
                    cwhenBodyArray.append("  " + condition.get(j).getSelectObj().getAnotherName() + ": JSONObject() from jsonObject.getJSONObject(\"companyModules\").getJSONArray(\"" + condition.get(j).getSelectObj().getObjectName() + "\")\n");
                }
            }
        }


        //and条件集合
        for (int i = 0; i < conditionArray.size(); i++) {
            //or条件集合
            List<RdeRiskVariableConditionNewVO> condition = conditionArray.get(i).getCondition();
            if (condition != null) {
                for (int j = 0; j < condition.size(); j++) {

                    RdeRiskVariableConditionNewVO cod = condition.get(j);
                    //获取对象属性
                    IdentificationVO selectObj = cod.getSelectObj();
                    //获取逻辑运算符及类型
                    Map<String, Object> result = cod.getResult();
                    //找到风险分组 对象
//                    RdeRiskVariableGroup group1 = rdeRiskVariableGroupMapper.selectById(objids.get(1));
                    //属性
//                    RdeRiskVariableRecord record1 = rdeRiskVariableRecordMapper.selectById(objids.get(2));
                    //group -> type属性为0时是对象
                    if ("0".equals(selectObj.getObjectFlag())) {
                        List<String> logicalOperation = logicalOperation(cod.getOperator());

                        //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                        if (j == condition.size() - 1 && i != conditionArray.size() - 1) {
                            cwhenBody.append("   jsonObject.getJSONObject(" + logicalOperation.get(1) + "\"" + selectObj.getObjectName() + "\")" + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "') &&\n");
                        } else if (j == condition.size() - 1 && i == conditionArray.size() - 1) {
                            cwhenBody.append("   jsonObject.getJSONObject(" + logicalOperation.get(1) + "\"" + selectObj.getObjectName() + "\")" + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "') \n");
                        } else {
                            //1对象模块名 2根据VariableRecord中type类型判断  3属性 4逻辑运算符转换 5匹配结果值
                            cwhenBody.append("   jsonObject.getJSONObject(" + logicalOperation.get(1) + "\"" + selectObj.getObjectName() + "\")" + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "') ||\n");
                        }
                    } else if ("5".equals(selectObj.getObjectFlag())) {  //group -> type属性为5时是集合
                        List<String> logicalOperation = logicalOperation(cod.getOperator());

                        //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                        if (j == condition.size() - 1 && i != conditionArray.size() - 1) {
                            cwhen.append("   " + logicalOperation.get(1) + selectObj.getAnotherName() + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "') &&\n");
                        } else if (j == condition.size() - 1 && i == conditionArray.size() - 1) {
                            cwhen.append("   " + logicalOperation.get(1) + selectObj.getAnotherName() + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "')\n");
                        } else {
                            //1对象模块名 2别名 3根据VariableRecord中type类型判断  4属性 5逻辑运算符转换 6匹配结果值
                            cwhen.append("   " + logicalOperation.get(1) + selectObj.getAnotherName() + attributeType(result.get("resultType").toString()) + "(\"" + selectObj.getStats() + "\")" + logicalOperation.get(0) + "('" + condition.get(j).getResult().get("result") + "') ||\n");
                        }

                    }
                }
            }
        }

        String codeDrl = group + cwhenFrist.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString();
        return codeDrl;
    }

    /**
     * 根据属性类型判断
     */
    public String attributeType(String type) {
        String str = null;
        if ("1".equals(type)) {        //字符型
            str = ".getString";
        } else if ("2".equals(type)) {  //布尔型
            str = ".getBoolean";
        } else if ("3".equals(type)) {  //整数型
            str = ".getInteger";
        } else if ("5".equals(type)) {  //浮点型
            str = ".getDouble";
        }
        return str;
    }

    /**
     * 根据传入的逻辑运算符进行下适用JSON格式的转换
     * type 逻辑计算符
     * pro 逻辑计算符中间涉及到的不等于符号 !的显示
     */
    public List<String> logicalOperation(String type) {
        String pro = "";
        List<String> str = new ArrayList<>();
        if ("==".equals(type)) {
            type = ".equals";
        } else if ("contains".equals(type)) {
            type = ".contains";
        } else if ("not contains".equals(type)) {
            type = ".contains";
            pro = "!";
        } else if ("matches".equals(type)) {
            type = ".matches";
        } else if ("not matches".equals(type)) {
            type = ".matches";
            pro = "!";
        } else if ("memberOf".equals(type)) {
            type = ".memberOf";
        } else if ("not memberOf".equals(type)) {
            type = ".memberOf";
            pro = "!";
        }

        str.add(type);
        str.add(pro);

        return str;
    }

    public String thePackageRule3(RdeModelDecisionCodeLevelVO2 record) {
        StringBuffer group = new StringBuffer();
        group.append("rule \"RDE-" + record.getCode() + "" + RandomUtil.randomString(4) + "\"\n");
        // 属性
        List<RdeModelRuleProperty> propertyList = record.getPropertyArray();
        if (propertyList != null) {
            for (RdeModelRuleProperty property : propertyList) {
                if (property != null && property.getKeyCode() != null) {
                    group.append(property.getKeyCode() + " " + property.getKeyValue() + "\n");
                }
            }
        }

        //条件记录
        StringBuffer cwhen = new StringBuffer();
        cwhen.append("	when \n");
        List<RdeRiskVariableConditionVO> conditionArray = record.getConditionArray();
        if (conditionArray == null || conditionArray.isEmpty()) {
            return "";
        }

        //开始遍历对象 1.如果出现集合则增加额外代码更新数量
        Map<String, String> listwhen = new HashMap<>();
        //对象
        StringBuffer when = new StringBuffer();

        Set<String> slist = new HashSet<>();
        //同一个集合对象拼接
        Map<String, String> listcond = new HashMap<>();
        Map<String, String> listcondstr = new HashMap<>();
        Boolean listbool = false;
        for (int i = 0; i < conditionArray.size(); i++) {
            List<RdeRiskVariableConditionVO> condition = conditionArray.get(i).getCondition();
            if (condition != null) {
                for (int j = 0; j < condition.size(); j++) {
                    RdeRiskVariableConditionVO cod = condition.get(j);
                    List<String> objids = cod.getSelectObj();
                    int num = objids.size();
                    if (num > 1) {
                        //对象
                        RdeRiskVariableGroup group1 = rdeRiskVariableGroupMapper.selectById(objids.get(1));
                        //集合
                        if ("5".equals(group1.getType())) {
                            if (StringUtils.isNotBlank(group1.getParentName())) {
                                slist.add("	$" + group1.getParentName() + ":" + group1.getParentName() + "()\n");//记录初始化对象，在下面进行逻辑判断
                            } else {//如果没有父对象则直接判断
                                when.append("		" + group1.getRemark() + ":" + group1.getListName() + "() from " + group1.getParentName() + "." + group1.getKeycode() + "\n");
                            }
                            //对象
                        } else if ("0".equals(group1.getType())) {
                            if (StringUtils.isNotBlank(group1.getParentName())) {
                                slist.add("		" + group1.getParentName() + "." + group1.getKeycode() + "()\n");
                            } else {
                                slist.add("		" + group1.getKeycode() + "()\n");
                            }

                        } else {//其它
                            if (StringUtils.isNotBlank(group1.getListName())) {
                                String str = listcondstr.get(group1.getListName());
                                if (StringUtils.isNotBlank(str)) {
                                    str = str.replace("> 0", cod.getOperator() + " " + cod.getResult());
                                    listcondstr.put(group1.getListName(), str);
                                } else if (StringUtils.isNotBlank(group1.getParentName())) {
                                    when.append("		" + group1.getParentName() + "( " + group1.getKeycode() + " " + cod.getOperator() + " " + cod.getResult() + " )\n");
                                }
                            } else if (StringUtils.isNotBlank(group1.getParentName())) {
                                when.append("		" + group1.getParentName() + "( " + group1.getKeycode() + " " + cod.getOperator() + " " + cod.getResult() + " )\n");
                            }
                        }

                        if (num > 2) {
                            //属性
                            RdeRiskVariableRecord record1 = rdeRiskVariableRecordMapper.selectById(objids.get(2));
                            if (cod.getOperator().contains("year")) {
                                //如果是集合
                                if ("5".equals(group1.getType())) {//如果是集合则记录痕迹，如果有多个则匹配拼接
                                    String getliststr = listcond.get(group1.getListName());
                                    if (getliststr != null) {
                                        listcond.put(group1.getListName(), getliststr + ",\n		" + "checkDateYearNum(" + record1.getCode() + "--" + cod.getResult() + ")");
                                    } else {
                                        listcond.put(group1.getListName(), "checkDateYearNum(" + record1.getCode() + "--" + cod.getResult() + ")");
                                    }
                                    listbool = true;
                                    listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                } else if ("0".equals(group1.getType())) {//是对象
                                    when.append("		" + group1.getKeycode() + "( checkDateYearNum(" + record1.getCode() + "," + cod.getResult() + ") )\n");
                                }
                            } else if (cod.getOperator().contains("month")) {
                                //如果是集合
                                if ("5".equals(group1.getType())) {//如果是集合则记录痕迹，如果有多个则匹配拼接
                                    String getliststr = listcond.get(group1.getListName());
                                    if (getliststr != null) {
                                        listcond.put(group1.getListName(), getliststr + ",\n		" + "checkDateMonthNum(" + record1.getCode() + "--" + cod.getResult() + ")");
                                    } else {
                                        listcond.put(group1.getListName(), "checkDateMonthNum(" + record1.getCode() + "--" + cod.getResult() + ")");
                                    }
                                    listbool = true;
                                    listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                } else if ("0".equals(group1.getType())) {//是对象
                                    when.append("		" + group1.getKeycode() + "( checkDateMonthNum(" + record1.getCode() + "," + cod.getResult() + ")\n");
                                }

                            } else if (cod.getOperator().contains("day")) {
                                when.append("		" + group1.getKeycode() + "( checkDateDayNum(" + record1.getCode() + ", " + cod.getResult() + ")\n");
                            } else {//其它类型
                                if ("5".equals(group1.getType())) {//集合
                                    if ("1".equals(record1.getType())) {//数值型
                                        String getliststr = listcond.get(group1.getListName());
                                        if (getliststr != null) {
                                            conditionOrNum(condition, listcond, group1, j, getliststr, record1, cod, i);
                                        } else {
                                            conditionOrNullNum(condition, listcond, group1, j, getliststr, record1, cod);
                                            listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
                                        }
                                        listbool = true;
                                        listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                    } else {//字符串
                                        if (cod.getResult().contains("$")) {
                                            String newstr = cod.getResult().replace("${", "").replace("}", "");
                                            String getliststr = listcond.get(group1.getListName());
                                            if (getliststr != null) {
                                                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " $appInfo." + newstr);
                                            } else {
                                                listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " $appInfo." + newstr);
                                            }
                                            listbool = true;
                                            listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                            slist.add("	$appInfo:CompanyBaseinfo()\n");
                                        } else {
                                            String getliststr = listcond.get(group1.getListName());
                                            if (getliststr != null) {
                                                //condition是size>1即条件为或者 并且之前遍历的key值没有此list对象
                                                conditionOr(condition, listcond, group1, j, getliststr, record1, cod, i);
                                                //TODO 拼接修改处 流水决策模型
//                                                    listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
//
                                            } else {
                                                //condition是size>1即条件为或者 并且之前遍历的key值没有此list对象
                                                conditionOrNull(condition, listcond, group1, j, getliststr, record1, cod);

                                                //TODO 拼接修改处 流水决策模型
//                                                listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
//                                                }
                                            }
                                            listbool = true;
                                            if (condition.size() > 1 && j != 0 && j != condition.size() - 1) {
                                                listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ") ||\n");
                                            } else if (condition.size() > 1 && j == 0) {
                                                listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ") ||\n");
                                            } else if (condition.size() > 1 && j == condition.size() - 1) {
                                                listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                            } else {
                                                listcondstr.put(group1.getListName(), "		List(size > 0 ) from collect  (${obj}(${cond}) from $" + group1.getParentName() + "." + group1.getKeycode() + ")\n");
                                            }
                                        }
                                    }
                                } else {//其它
                                    if (cod.getResult().contains("$")) {
                                        String newstr = cod.getResult().replace("${", "").replace("}", "");
                                        when.append("		" + group1.getKeycode() + "(" + record1.getCode() + " " + cod.getOperator() + " $appInfo." + newstr + " )\n");
                                        slist.add("	$appInfo:CompanyBaseinfo()\n");
                                    } else {

                                        //当页面选择或者的时候则进行此拼接 -- 方案二
                                        if (condition.size() > 1 && j != 0 && j != condition.size() - 1) {
                                            when.append("		" + group1.getKeycode() + "(" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "') ||\\n");
                                        } else if (condition.size() > 1 && j == 0) {
                                            when.append("		" + group1.getKeycode() + "(" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "') ||\n");
                                        } else if (condition.size() > 1 && j == condition.size() - 1) {
                                            when.append("       " + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "')\n");
                                        } else {
                                            when.append("		" + group1.getKeycode() + "(" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "')\n");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (i + 1 == conditionArray.size() && listbool) {//最后一次的时候保存集合
                for (String key : listcond.keySet()) {
                    String cond = listcond.get(key);
                    String condReplace = cond.replace(",", " || ");
                    String replace = condReplace.replace("--", ",");
                    String replaceLast = replace.replace("|| @", ",");
                    if (listcondstr.get(key) != null) {
                        String condStr = listcondstr.get(key);
                        when.append(condStr.replace("${obj}", key).replace("${cond}", replaceLast));
                    }
                }
            }
        }
        //条件对象
        for (String string : slist) {
            cwhen.append("		" + string);
        }
        //更新对象
        List<RdeRiskVariableConditionVO> modifyArray = record.getModifyArray();
        Set<String> sets = new HashSet<String>();
        if (modifyArray != null) {
            modifyArray.stream().forEach(x -> {
                List<String> objids = x.getSelectObj();
                RdeRiskVariableGroup group1 = rdeRiskVariableGroupMapper.selectById(objids.get(1));
                String str = group1.getRemark() + ":" + group1.getKeycode() + "()";
                sets.add(str);
            });
        }
        for (String string : sets) {
            when.append("		" + string + "\n");
        }
        cwhen.append(when);
        group.append(cwhen);
        //更新方法处理
        group.append("  then\n");
        StringBuffer then = new StringBuffer();

        //分类判断是否增加for循环
        if ("1".equals(record.getRuleCode())) {
            then.append("\t\t\tfor(int i =0;i< $li.size();i++){\n" +
                    "  \t\t\tBankExcelCashflowRuleVO p = (BankExcelCashflowRuleVO)$li.get(i);\n" +
                    "  \t\t\tp.setPurposeCategory(\"###\");\n" +
                    "  \t\t\tp.setCategoryType(\"@@@\");\n" +
                    "  \t\t\tupdate(p);\n" +
                    "  \t\t}\n");
        }

        if (modifyArray != null) {
            for (int i = 0; i < modifyArray.size(); i++) {
                RdeRiskVariableConditionVO cod = modifyArray.get(i);
                List<String> objids = cod.getSelectObj();
                int num = objids.size();
                if (num > 2) {
                    //对象
                    RdeRiskVariableGroup group1 = rdeRiskVariableGroupMapper.selectById(objids.get(1));
                    then.append("		modify(" + group1.getRemark() + "){\n");
                    //属性
                    RdeRiskVariableRecord record1 = rdeRiskVariableRecordMapper.selectById(objids.get(2));
                    if (record1.getType().equals("1")) {
                        then.append("			set" + captureName(record1.getCode()) + "(" + cod.getResult() + "d);\n");
                    } else {
                        then.append("			set" + captureName(record1.getCode()) + "('" + cod.getResult() + "');\n");
                    }
                    then.append("		}\n");
                }
            }
        }
        then.append("		myGlobalList.add(\"" + record.getCode() + "\");\n");

        group.append(then);
        group.append("end");
        group.append("\n");
        group.append("\n");

        return group.toString();
    }

    public void conditionOrNum(List<RdeRiskVariableConditionVO> condition, Map<String, String> listcond, RdeRiskVariableGroup group1, Integer j, String getliststr, RdeRiskVariableRecord record1, RdeRiskVariableConditionVO cod, Integer i) {
        if (condition.size() > 1 && listcond.get(group1.getListName()) == null) {
            //第一个
            if (j == 0) {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            } else {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            }
            //condition是size>1即条件为或者 并且之前遍历的key值有此list对象
        } else if (condition.size() > 1 && listcond.get(group1.getListName()) != null) {
            if (j == 0 && listcond.get(group1.getListName()) != null && i > 0) {
                listcond.put(group1.getListName(), getliststr + ",@\n		" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            } else {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            }
        } else {
            listcond.put(group1.getListName(), getliststr + ",@\n		" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
        }
    }

    public void conditionOrNullNum(List<RdeRiskVariableConditionVO> condition, Map<String, String> listcond, RdeRiskVariableGroup group1, Integer j, String getliststr, RdeRiskVariableRecord record1, RdeRiskVariableConditionVO cod) {
        if (condition.size() > 1 && listcond.get(group1.getListName()) == null) {
            //第一个
            if (j == 0) {
                listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            } else {
                listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
            }
            //condition是size>1即条件为或者 并且之前遍历的key值有此list对象
        } else if (condition.size() > 1 && listcond.get(group1.getListName()) != null) {
            listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
        } else if (j == 0 && condition.size() == 1) {
            listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
        } else {
            listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " " + cod.getResult());
        }
    }

    /**
     * 将字符串的首字母转大写
     *
     * @param str 需要转换的字符串
     * @return
     */
    private String captureName(String str) {
        // 进行字母的ascii编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * condition是size>1即条件为或者 并且之前遍历的key值没有此list对象
     *
     * @param condition
     * @param listcond
     * @param group1
     * @param j
     * @param getliststr
     * @param record1
     * @param cod
     */
    public void conditionOr(List<RdeRiskVariableConditionVO> condition, Map<String, String> listcond, RdeRiskVariableGroup group1, Integer j, String getliststr, RdeRiskVariableRecord record1, RdeRiskVariableConditionVO cod, Integer i) {
        if (condition.size() > 1 && listcond.get(group1.getListName()) == null) {
            //第一个
            if (j == 0) {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            } else {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            }
            //condition是size>1即条件为或者 并且之前遍历的key值有此list对象
        } else if (condition.size() > 1 && listcond.get(group1.getListName()) != null) {
            if (j == 0 && listcond.get(group1.getListName()) != null && i > 0) {
                listcond.put(group1.getListName(), getliststr + ",@\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            } else {
                listcond.put(group1.getListName(), getliststr + ",\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            }
        } else {
            listcond.put(group1.getListName(), getliststr + ",@\n		" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
        }
    }

    /**
     * condition是size>1即条件为或者 并且之前遍历的key值没有此list对象  //字符 对象key为null
     *
     * @param condition
     * @param listcond
     * @param group1
     * @param j
     * @param getliststr
     * @param record1
     * @param cod
     */
    public void conditionOrNull(List<RdeRiskVariableConditionVO> condition, Map<String, String> listcond, RdeRiskVariableGroup group1, Integer j, String getliststr, RdeRiskVariableRecord record1, RdeRiskVariableConditionVO cod) {
        if (condition.size() > 1 && listcond.get(group1.getListName()) == null) {
            //第一个
            if (j == 0) {
                listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            } else {
                listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
            }
            //condition是size>1即条件为或者 并且之前遍历的key值有此list对象
        } else if (condition.size() > 1 && listcond.get(group1.getListName()) != null) {
            listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
        } else if (j == 0 && condition.size() == 1) {
            listcond.put(group1.getListName(), record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
        } else {
            listcond.put(group1.getListName(), "@" + record1.getCode() + " " + cod.getOperator() + " '" + cod.getResult() + "'");
        }
    }

}
