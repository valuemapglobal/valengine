package com.value.decision.engine.vo;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class WarnVO {

    /**
     * 命中规则
     */
    private String code;

    /**
     * 命中规则详情idList
     */
    private List<Integer> metaIdList;

    public WarnVO(String code, List<Integer> metaIdList) {
        this.code = code;
        this.metaIdList = metaIdList;
    }

    @Override
    public String toString() {
        return "WarnVO{" +
                "code='" + code + '\'' +
                ", metaIdList=" + metaIdList +
                '}';
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("001", "002", "002", "003", "003");
        List<Integer> metaIdList = Arrays.asList(12, 23, 24, 98, 69);

        Map<String, List<Integer>> collect = IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.groupingBy(list::get,
                        Collectors.mapping(metaIdList::get, Collectors.toList())));

        List<WarnVO> warnVOList = collect.entrySet().stream()
                .map(entry -> new WarnVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        warnVOList.forEach(System.out::println);

        System.out.println(JSON.toJSONString(warnVOList));
    }
}
