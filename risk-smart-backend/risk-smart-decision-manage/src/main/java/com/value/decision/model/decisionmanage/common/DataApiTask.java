package com.value.decision.model.decisionmanage.common;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.value.decision.process.model.ProcessPolicyTask;
import com.value.decision.common.dto.EncryptDTO;
import com.value.decision.common.utils.EncryptBodyUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

// 数据接口任务
@Component
class DataApiTask implements Callable<Map<String, Object>> {

    private String appkey;

    private String secret;

    private String interfaceRequestUrl;

    private String apiToken;

    private Map<String, Object> map;

    private ProcessPolicyTask processPolicyTask;

    public DataApiTask() {
    }

    public DataApiTask(Map<String, Object> map, ProcessPolicyTask processPolicyTask, String appkey, String secret, String interfaceRequestUrl, String apiToken) {
        this.processPolicyTask = processPolicyTask;
        this.map = map;
        this.appkey = appkey;
        this.secret = secret;
        this.interfaceRequestUrl = interfaceRequestUrl;
        this.apiToken = apiToken;
    }

    @Override
    public Map<String, Object> call() throws Exception {
        return callDataApi(map,processPolicyTask);
    }

    private Map<String, Object> callDataApi(Map<String, Object> map, ProcessPolicyTask processPolicyTask) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ckey", processPolicyTask.getCkey());
        hashMap.put("companyName", processPolicyTask.getEnterpriseName());
        hashMap.put("number", processPolicyTask.getIdNumber());
        hashMap.put("mobile", processPolicyTask.getMobilePhone());
        hashMap.put("name", processPolicyTask.getPersonalName());
        hashMap.put("orderNo", processPolicyTask.getOrderNo());
        hashMap.put("systemFlag", processPolicyTask.getSystemFlag());
        hashMap.put("userOrderNo", processPolicyTask.getUserOrderNo());
        map.put("apiToken", apiToken);
        map.put("paramData", hashMap);
        String result = null;
        EncryptDTO encryptBody = null;
        try {
            long l = System.currentTimeMillis();
            encryptBody = EncryptBodyUtil.createEncryptBody(appkey, secret, map.get("manageNo").toString()
                    , map.get("sourceNo").toString(), map.get("interfaceNo").toString(), processPolicyTask.getOrderNo(), hashMap);

            result = HttpUtil.post(interfaceRequestUrl, JSON.toJSONString(encryptBody));
            System.out.println("接口耗时："+(System.currentTimeMillis() - l) +" "+"接口名称："+map.get("interfaceNo").toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("数据中台接口调用失败。请求参数："+ JSON.toJSONString(encryptBody)+",返还参数："+result);
        }
        if (!JSONUtil.isJson(result) || !"200".equals(JSONObject.parseObject(result).getString("code"))) {
            throw new RuntimeException("数据中台接口调用失败，返回数据格式有误。请求参数："+ JSON.toJSONString(encryptBody)+",返还参数："+result);
        }
        map.put("reposeBody",result);
        return map;
    }



}