package com.value.decision.model.decisionmanage.common;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.value.decision.model.decisionmanage.model.vo.*;
import com.value.decision.model.rdenew.vo.DataEntryVO;
import com.value.decision.model.rdenew.vo.IdentificationVO;
import com.value.decision.model.rdenew.vo.RdeRiskVariableArrayVO;
import com.value.decision.model.rdenew.vo.RdeRiskVariableConditionNewVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * drl组装工具类
 */

@Component
public class GenerateRuleUtil {



    /**
     * 生成规则 -- 预警new
     */
    public Map<String,Object> generateRule(GenerateRuleVO record){

        Map<String,Object> map = new HashMap<>();

        StringBuffer group = new StringBuffer();
        group.append("rule \"RDE-" + record.getCode() + "" + RandomUtil.randomString(4) + "\"\r\n");

        //条件记录
        StringBuffer cwhen = new StringBuffer();
        //条件集合
        List<RdeRiskVariableArrayVO> conditionArray = record.getConditionArray();
        RdeRiskVariableConditionNewVO rdeRiskVariableConditionNewVO = new RdeRiskVariableConditionNewVO();
        if (conditionArray == null || conditionArray.isEmpty()) {
            return null;
        }

        if (record.getArraySize() == 1){
            rdeRiskVariableConditionNewVO.setArraySize(conditionArray.get(conditionArray.size()-1).getCondition().get(0).getArraySize());
            rdeRiskVariableConditionNewVO.setOperator(conditionArray.get(conditionArray.size()-1).getCondition().get(0).getOperator());
            rdeRiskVariableConditionNewVO.setResult(conditionArray.get(conditionArray.size()-1).getCondition().get(0).getResult());
            conditionArray.remove(conditionArray.size() - 1);
        }

        //必须携带的json对象
        StringBuffer cwhenFrist = new StringBuffer();
        cwhenFrist.append("	salience " + (record.getSalience() != null ? record.getSalience() : 0) + "\n   when \n   $fact: JsonRuleEntityVO(jsonObject: jsonObject)\n");

        //如果为分类增加的组装
        StringBuffer cwhenFristClass = new StringBuffer();

        //如果为分析指标的组装
        StringBuffer cwhenFristIndex = new StringBuffer();
        cwhenFristIndex.append("	salience 1\nwhen\n   $fact: JsonRuleEntityVO(jsonObject: jsonObject)\n$obj: JSONObject() from jsonObject.getJSONObject(\"indexObj\")\n");

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
        cwhenThen.append("then\n");
        StringBuffer cthenEnd = new StringBuffer();
        cthenEnd.append("   list.add(\""+ record.getCode() +"\");\nend");

        //针对分类模块的then语法拼接
        StringBuffer cthenClass = new StringBuffer();

        //对象类型集合
        List<DataEntryVO> objList = new ArrayList<>();
        //对象或集合别名List
        List<String> anotherList = new ArrayList<>();
        for (int i = 0; i < conditionArray.size(); i++) {
            List<RdeRiskVariableConditionNewVO> condition = conditionArray.get(i).getCondition();
            for (int j = 0; j < condition.size(); j++) {
                //有集合
                if ("5".equals(condition.get(j).getSelectObj().getObjectFlag()) && !cwhenBodyArray.toString().contains(condition.get(j).getSelectObj().getAnotherName())){
                    cwhenBodyArray.append("  "+condition.get(j).getSelectObj().getAnotherName()+": JSONObject() from jsonObject.getJSONObject(\"companyModules\").getJSONArray(\""+ condition.get(j).getSelectObj().getObjectName() +"\")\n");
                    if (record.getArraySize() == 1){
                        cwhenBodyArray.append("  json: JSONObject() from jsonObject.getJSONObject(\"json\")\n");
                    }
                }
                //对象
                if ("0".equals(condition.get(j).getSelectObj().getObjectFlag()) && !cwhenFristClass.toString().contains(condition.get(j).getSelectObj().getAnotherName())){
                    cwhenFristClass.append("  "+condition.get(j).getSelectObj().getAnotherName()+": JSONObject() from jsonObject.getJSONObject(\""+ condition.get(j).getSelectObj().getObjectName() +"\")\n");
                }
                if ("6".equals(record.getBusinessCode())){
                    //预警增加数据标识id返回拼接
                    cwhenThen.append("   metaIdList.add("+ condition.get(j).getSelectObj().getAnotherName() +".getInteger(\"metaId\")" +");\n");
                }

                DataEntryVO dataEntryVO = new DataEntryVO();
                dataEntryVO.setManageNo(condition.get(j).getSelectObj().getManageNo());  //接口信息唯一标识
                dataEntryVO.setSourceNo(condition.get(j).getSelectObj().getSourceNo());  //接口供应商唯一标识
                dataEntryVO.setInterfaceNo(condition.get(j).getSelectObj().getInterfaceNo());  //接口编号
                dataEntryVO.setObjectFlag(condition.get(j).getSelectObj().getObjectFlag());  //对象或集合标识  0为对象 5为集合
                dataEntryVO.setObjectName(condition.get(j).getSelectObj().getObjectName());  //对象模块/名称
                objList.add(dataEntryVO);
                anotherList.add(condition.get(j).getSelectObj().getAnotherName());
            }
        }

        //根据数据模块进行去重
        if (!"3".equals(record.getDataType())){
            objList = objList.stream().filter(x -> StringUtils.isNotEmpty(x.getInterfaceNo())).collect(Collectors.toList());
            objList =objList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(DataEntryVO::getInterfaceNo))), ArrayList::new));
            map.put("objList", JSONObject.toJSONString(objList));
            System.out.println(map.get("objList"));
        }


        //对于对象属性为分析指标字段时，需要先将此对象对应的drl存入String中，后加在此规则后面
        String strIndex = "";


        //and条件集合
        for (int i = 0; i < conditionArray.size(); i++) {
            //or条件集合
            List<RdeRiskVariableConditionNewVO> condition = conditionArray.get(i).getCondition();
            if (condition != null) {
                //如果子集合大于1即为多个或者关系,特殊处理 定义新的拼接子主体
                if (condition.size()>1){
                    //定义子集合主体的拼接
                    StringBuffer cwhenBodySubset = new StringBuffer();

                    //定义子集合主体最后一条记录的拼接  有&& 和 ||即需要放在括号的外面
                    StringBuffer cwhenBodySubsetLast = new StringBuffer();
                    for (int j = 0; j < condition.size(); j++) {

                        RdeRiskVariableConditionNewVO cod = condition.get(j);
                        //获取对象属性
                        IdentificationVO selectObj = cod.getSelectObj();
                        //获取逻辑运算符及类型
                        Map<String, Object> result = cod.getResult();

                        //group -> type属性为0时是对象
                        if ("0".equals(selectObj.getObjectFlag())) {
                            List<String> logicalOperation = logicalOperation(cod.getOperator());
                            //属性值为字符串时
                            if ("1".equals(result.get("resultType").toString())){
                                logicalOperation = logicalOperationZFQ(cod.getOperator());
                            }

                            //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                            if (j == condition.size() - 1 && i != conditionArray.size() - 1){
                                //如果带有时间函数则需要新的组装
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubsetLast.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " &&\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") &&\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" &&\n");
                                    }else {
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") &&\n");
                                    }
                                }
                            }else if (j == condition.size() - 1 && i == conditionArray.size() - 1){
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubset.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + "\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +"\n");
                                    }else {
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")\n");
                                    }
//                                cwhenBody.append("   jsonObject.getJSONObject("+logicalOperation.get(1)+ "\"" + selectObj.getObjectName() +"\")"+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") \n");
                                }
                            }else {
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubset.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " ||\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")) ||\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                    }else {
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")) ||\n");
                                    }
                                    //1对象模块名 2根据VariableRecord中type类型判断  3属性 4逻辑运算符转换 5匹配结果值
//                                cwhenBody.append("   jsonObject.getJSONObject("+logicalOperation.get(1)+ "\"" + selectObj.getObjectName() +"\")"+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                }
                            }
                        }else if ("5".equals(selectObj.getObjectFlag())){  //group -> type属性为5时是集合
                            List<String> logicalOperation = logicalOperation(cod.getOperator());
                            //属性值为字符串时
                            if ("1".equals(result.get("resultType").toString())){
                                logicalOperation = logicalOperationZFQ(cod.getOperator());
                            }

                            //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                            if (j == condition.size() - 1 && i != conditionArray.size() - 1){
                                //如果带有时间函数则需要新的组装
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubsetLast.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " &&\n");
                                }else {
                                    //属性值为字符串时  //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") &&\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" &&\n");
                                    }else {
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ "("+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+")" +" &&\n");
                                    }
                                }
                            }else if (j == condition.size() - 1 && i == conditionArray.size() - 1){
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubset.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + "\n");
                                }else {
                                    //属性值为字符串时 //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +"\n");
                                    }else {
                                        cwhenBodySubsetLast.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ "("+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+")" +"\n");
                                    }
//                                cwhenBody.append("   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")\n");
                                }
                            }else {
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBodySubset.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " ||\n");
                                }else {
                                    //属性值为字符串时 //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if (("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=")) || cod.getOperator().contains("ArrayOr")){
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")) ||\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                    }else {
                                        cwhenBodySubset.append("(checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")) ||\n");
                                    }
                                    //1对象模块名 2别名 3根据VariableRecord中type类型判断  4属性 5逻辑运算符转换 6匹配结果值
//                                cwhenBody.append("   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                }
                            }

                        }

                        if ("3".equals(condition.get(j).getSelectObj().getDataType())){
                            //指标规则拼在此规则后
                            strIndex = strIndex + "\n\n\n" + condition.get(j).getSelectObj().getDrlCode();
                        }
                    }
                    assembleParagraph(cwhenBodySubsetLast,cwhenBodySubset,cwhenBody);

                }else {
                    for (int j = 0; j < condition.size(); j++) {

                        RdeRiskVariableConditionNewVO cod = condition.get(j);
                        //获取对象属性
                        IdentificationVO selectObj = cod.getSelectObj();
                        //获取逻辑运算符及类型
                        Map<String, Object> result = cod.getResult();

                        //group -> type属性为0时是对象
                        if ("0".equals(selectObj.getObjectFlag())) {
                            List<String> logicalOperation = logicalOperation(cod.getOperator());
                            //属性值为字符串时
                            if ("1".equals(result.get("resultType").toString())){
                                logicalOperation = logicalOperationZFQ(cod.getOperator());
                            }

                            //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                            if (j == condition.size() - 1 && i != conditionArray.size() - 1){
                                //如果带有时间函数则需要新的组装
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " &&\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") &&\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" &&\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") &&\n");
                                    }
                                }
                            }else if (j == condition.size() - 1 && i == conditionArray.size() - 1){
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + "\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +"\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")\n");
                                    }
//                                cwhenBody.append("   jsonObject.getJSONObject("+logicalOperation.get(1)+ "\"" + selectObj.getObjectName() +"\")"+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") \n");
                                }
                            }else {
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunctionObj(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " ||\n");
                                }else {
                                    //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") ||\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" ||\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+   logicalOperation.get(1)+selectObj.getObjectName()+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                    }
                                    //1对象模块名 2根据VariableRecord中type类型判断  3属性 4逻辑运算符转换 5匹配结果值
//                                cwhenBody.append("   jsonObject.getJSONObject("+logicalOperation.get(1)+ "\"" + selectObj.getObjectName() +"\")"+attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                }
                            }
                        }else if ("5".equals(selectObj.getObjectFlag())){  //group -> type属性为5时是集合
                            List<String> logicalOperation = logicalOperation(cod.getOperator());
                            //属性值为字符串时
                            if ("1".equals(result.get("resultType").toString())){
                                logicalOperation = logicalOperationZFQ(cod.getOperator());
                            }

                            //如果为condition最后一条记录,conditionArray又不为最后一条记录则后不需要拼接||拼接&& ;如果为condition最后一条记录,conditionArray也为最后一条记录,则不需要拼接||和&&
                            if (j == condition.size() - 1 && i != conditionArray.size() - 1){
                                //如果带有时间函数则需要新的组装
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " &&\n");
                                }else {
                                    //属性值为字符串时  //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") &&\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" &&\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ "("+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+")" +" &&\n");
                                    }
                                }
                            }else if (j == condition.size() - 1 && i == conditionArray.size() - 1){
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + "\n");
                                }else {
                                    //属性值为字符串时 //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if ("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=") || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +")\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +"\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ "("+resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+")" +"\n");
                                    }
//                                cwhenBody.append("   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +")\n");
                                }
                            }else {
                                if (condition.get(j).getOperator().contains("checkDate")){
                                    cwhenBody.append(timeFunction(condition.get(j).getOperator(),selectObj,result,condition.get(j)) + " ||\n");
                                }else {
                                    //属性值为字符串时 //逻辑运算符为等于时或者为多或者时结果放在前面拼接 .equals .contans
                                    if (("1".equals(result.get("resultType").toString()) && cod.getOperator().contains("=")) || cod.getOperator().contains("ArrayOr")){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString())+ logicalOperation.get(0) +"("+ selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")" +") ||\n");
                                    }else if ("0".equals(result.get("resultType").toString()) || "6".equals(result.get("resultType").toString()) || "7".equals(result.get("resultType").toString())){
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +""+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +" ||\n");
                                    }else {
                                        cwhenBody.append("checkNullValue("+selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")) &&"+"   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                    }
                                    //1对象模块名 2别名 3根据VariableRecord中type类型判断  4属性 5逻辑运算符转换 6匹配结果值
//                                cwhenBody.append("   "+logicalOperation.get(1) + selectObj.getAnotherName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\")"+ logicalOperation.get(0) +"("+ resultType(result.get("resultType").toString(),condition.get(j).getResult().get("result").toString()) +") ||\n");
                                }
                            }

                        }

                        if ("3".equals(condition.get(j).getSelectObj().getDataType())){
                            //指标规则拼在此规则后
                            strIndex = strIndex + "\n\n\n" + condition.get(j).getSelectObj().getDrlCode();
                        }
                    }
                }

            }
        }

        String codeDrl = null;
        //是否属于 集合size判断  0为否 1为是
        if (record.getArraySize() == 1){
            //then语法拼接
            StringBuffer cthenArray = new StringBuffer();
            cthenArray.append("json.put(\""+record.getCode()+"List\",(json.getInteger(\""+record.getCode()+"List\") == null ? 0 : json.getInteger(\""+record.getCode()+"List\") ) + 1);\n" +
                    "  insert(json);\n" +
                    "  System.out.println(\"i === \"+json.getInteger(\""+record.getCode()+"List\"));\n" +
                    "  if (json.getInteger(\""+record.getCode()+"List\") "+rdeRiskVariableConditionNewVO.getOperator()+" "+rdeRiskVariableConditionNewVO.getResult().get("result")+" ){\n" +
                    "      list.add(\""+record.getCode()+"\");\n" +
                    "  }" +
                    "end");
            //如果为分类的drl组装
            if("3".equals(record.getDataType())){ //分析指标
                //针对分类模块的then语法拼接
                StringBuffer cthenType = new StringBuffer();
                cthenType.append("   $obj.put(\""+ record.getObjStats() +"\"," +resultType(record.getObjResultType(),record.getObjResult())+ ");\n   insert($obj);\n");
                codeDrl = group + cwhenFristIndex.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenType.toString() + cthenArray.toString();
            }else if ("6".equals(record.getRuleCode()) && !"3".equals(record.getDataType())){
                cthenClass.append("   " + anotherList.get(0) +".put(\"labelAll\",\"" +record.getCode()+ "\");\n");
                codeDrl = group + cwhenFrist.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenClass.toString() + cthenArray.toString();
            }else {
                codeDrl = group + cwhenFrist.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenArray.toString();
            }
        }else {
            //如果为分类的drl组装
            if("3".equals(record.getDataType())){ //分析指标
                //针对分类模块的then语法拼接
                StringBuffer cthenType = new StringBuffer();
                cthenType.append("   $obj.put(\""+ record.getObjStats() +"\"," +resultType(record.getObjResultType(),record.getObjResult())+ ");\n   insert($obj);\n");
                codeDrl = group + cwhenFristIndex.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenType.toString() + cthenEnd.toString();
            }else if ("6".equals(record.getRuleCode()) && !"3".equals(record.getDataType())){
                cthenClass.append("   " + anotherList.get(0) +".put(\"labelAll\",\"" +record.getCode()+ "\");\n");
                codeDrl = group + cwhenFrist.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenClass.toString() + cthenEnd.toString();
            }else {
                codeDrl = group + cwhenFrist.toString() + cwhenFristClass.toString() + cwhenBodyArray.toString() + cwhen.toString() + cwhenBody.toString() + cwhenBodyBack.toString() + cwhenThen.toString() + cthenEnd.toString();
            }
        }

        System.out.println("打印 == ");
        System.out.println(codeDrl);
        map.put("codeDrl",codeDrl + strIndex);
//        String string = JSONObject.toJSONString(codeDrl);

        return map;
    }


    /**
     * 根据属性类型判断
     */
    public String attributeType(String type){
        String str = null;
        if ("1".equals(type)){        //字符型
            str = ".getString";
        }else if ("6".equals(type)){  //布尔型
            str = ".getBoolean";
        }else if ("0".equals(type)){  //整数型
            str = ".getInteger";
        }else if ("7".equals(type)){  //浮点型
            str = ".getDouble";
        }
        return str;
    }


    /**
     * 根据传入的逻辑运算符进行下适用JSON格式的转换
     * type 逻辑计算符
     * pro 逻辑计算符中间涉及到的不等于符号 !的显示
     */
    public List<String> logicalOperation(String type){
        String pro = "";
        List<String> str = new ArrayList<>();
        if ("==".equals(type)){
            type = "==";
        }else if ("contains".equals(type)){
            type = ".contains";
        }else if ("not contains".equals(type)){
            type = ".contains";
            pro = "!";
        }else if ("matches".equals(type)){
            type = ".matches";
        }else if("not matches".equals(type)){
            type = ".matches";
            pro = "!";
        }else if ("memberOf".equals(type)){
            type = ".memberOf";
        }else if("not memberOf".equals(type)){
            type = ".memberOf";
            pro = "!";
        }

        str.add(type);
        str.add(pro);

        return str;
    }

    /**
     * 根据传入的逻辑运算符进行下适用JSON格式的转换
     * type 逻辑计算符
     * pro 逻辑计算符中间涉及到的不等于符号 !的显示  字符串时 !=的处理
     */
    public List<String> logicalOperationZFQ(String type){
        String pro = "";
        List<String> str = new ArrayList<>();
        if ("==".equals(type)){
            type = ".equals";
        }else if ("contains".equals(type)){
            type = ".contains";
        }else if ("not contains".equals(type)){
            type = ".contains";
            pro = "!";
        }else if ("matches".equals(type)){
            type = ".matches";
        }else if("not matches".equals(type)){
            type = ".matches";
            pro = "!";
        }else if ("memberOf".equals(type)){
            type = ".memberOf";
        }else if("not memberOf".equals(type)){
            type = ".memberOf";
            pro = "!";
        }else if ("!=".equals(type)){
            type = ".equals";
            pro = "!";
        }//增加一个多条件集合判断的逻辑运算符逻辑处理
        else if("notArrayOr".equals(type)){
            type = ".contains";
            pro = "!";
        }//增加一个多条件集合判断的逻辑运算符逻辑处理
        else if("ArrayOr".equals(type)){
            type = ".contains";
        }

        str.add(type);
        str.add(pro);

        return str;
    }

    /**
     * 值类型
     */
    public Object resultType(String resultType,String result){
        if ("1".equals(resultType)){        //字符型
            return "\""+result+"\"";
        }else if ("6".equals(resultType)){  //布尔型
            return result;
        }else if ("0".equals(resultType)){  //整数型
            return result;
        }else if ("7".equals(resultType)){  //浮点型
            return result;
        }
        return "\""+result+"\"";
    }

    /**
     * 针对集合
     * @param timeType  时间函数
     * @param selectObj  对象属性
     * @param result    逻辑运算符及类型
     * @param resultStr  结果值
     * @return
     */
    public String timeFunction(String timeType, IdentificationVO selectObj, Map<String, Object> result, RdeRiskVariableConditionNewVO resultStr){
        String str = null;
        if ("checkDateYearNum".equals(timeType)){
            str = "   checkDateYearNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateMonthNum".equals(timeType)){
            str = "   checkDateMonthNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateDayNum".equals(timeType)){
            str = "   checkDateDayNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateYearNumBig".equals(timeType)){
            str = "   checkDateYearNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateMonthNumBig".equals(timeType)){
            str = "   checkDateMonthNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateDayNumBig".equals(timeType)){
            str = "   checkDateDayNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }
        return str;
    }

    /**
     * 针对对象
     * @param timeType  时间函数
     * @param selectObj  对象属性
     * @param result    逻辑运算符及类型
     * @param resultStr  结果值
     * @return
     */
    public String timeFunctionObj(String timeType,IdentificationVO selectObj,Map<String, Object> result,RdeRiskVariableConditionNewVO resultStr){
        String str = null;
        if ("checkDateYearNum".equals(timeType)){
            str = "   checkDateYearNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateMonthNum".equals(timeType)){
            str = "   checkDateMonthNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateDayNum".equals(timeType)){
            str = "   checkDateDayNum(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateYearNumBig".equals(timeType)){
            str = "   checkDateYearNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateMonthNumBig".equals(timeType)){
            str = "   checkDateMonthNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }else if ("checkDateDayNumBig".equals(timeType)){
            str = "   checkDateDayNumBig(" + selectObj.getObjectName() +attributeType(result.get("resultType").toString())+"(\""+ selectObj.getStats() +"\"),"+ resultStr.getResult().get("result") +")";
        }
        return str;
    }


    /**
     * 组装段落处理
     * @param cwhenBodySubsetLast  内数组最后一个但不是外数组最后一个
     * @param cwhenBodySubset      内数组其他
     * @param cwhenBody            最终拼接结果
     * @return
     */
    public void assembleParagraph(StringBuffer cwhenBodySubsetLast,StringBuffer cwhenBodySubset,StringBuffer cwhenBody){
        String original = cwhenBodySubsetLast.toString();
        int lastIndex = original.lastIndexOf("&&");
        String[] split = original.split("&&");

//        String substring1 = original.substring(0, lastIndex);
//        String substring = original.substring(lastIndex + 2);


        System.out.println("lastIndex:"+lastIndex);
        if (cwhenBodySubsetLast.toString().contains("&&") && split.length >2){  //内数组最后一个 接着&&
            String processed = "("+original.substring(0, lastIndex) + original.substring(lastIndex + 2)+")";
            cwhenBody.append("(").append(cwhenBodySubset).append(processed).append(") &&");
        }else if (cwhenBodySubsetLast.toString().contains("&&") && split.length <= 2){  //外数组最后一个 不接&&
            String processed = "("+original.substring(0, lastIndex) + "&&" + original.substring(lastIndex + 2)+")";
            cwhenBody.append("(").append(cwhenBodySubset).append(processed).append(") ");
        }else if (cwhenBodySubsetLast.toString().contains("||")){  //内数组最后一个 接着||
            String cwhenBodySubsetLastRep = cwhenBodySubsetLast.toString().replaceAll("||", "");
            cwhenBody.append("(").append(cwhenBodySubset).append(cwhenBodySubsetLastRep).append(") ||");
        }else {
            cwhenBody.append("(").append(cwhenBodySubset).append(cwhenBodySubsetLast).append(")");  //不存在还连接内数组，即为外数组最后一个，需要分割&&拿到后半部分进行处理
        }
    }

}
