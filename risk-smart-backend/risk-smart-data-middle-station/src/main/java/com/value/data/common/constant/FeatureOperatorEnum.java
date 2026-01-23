package com.value.data.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;

/**
 * 特征变量操作符枚举
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@AllArgsConstructor
@Getter
public enum FeatureOperatorEnum {
    LT("小于","LT",(s,v) -> String.format("(+dataUtil.respObj('%s').%s) < (+%s)",s.get(0),s.get(1),v)),
    GT("大于","GT",(s,v) -> String.format("(+dataUtil.respObj('%s').%s) > (+%s)",s.get(0),s.get(1),v)),
    EQ("等于","EQ",(s,v) -> String.format("dataUtil.respObj('%s').%s == '%s'",s.get(0),s.get(1),v)),
    NE("不等于","NE",(s,v) -> String.format("dataUtil.respObj('%s').%s != '%s'",s.get(0),s.get(1),v)),

    INCLUDE("包含","INCLUDE",(s,v) -> String.format(
            "(dataUtil.respObj('%s').%s).indexOf('%s')!==-1",
            s.get(0),s.get(1),v)
    ),
    NINCLUDE("不包含","NINCLUDE",(s,v) -> String.format(
            "(dataUtil.respObj('%s').%s).indexOf('%s')===-1",
            s.get(0),s.get(1),v)
    ),

    GEY("距今大于等于多少年","GEY", (s,v) -> String.format(
            "dateTimeUtil.betweenYear(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())>=(+%s)",
            s.get(0),s.get(1),v)
    ),
    GEM("距今大于等于多少月","GEM", (s,v) -> String.format(
                "dateTimeUtil.betweenMonth(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())>=(+%s)",
                s.get(0), s.get(1), v)
    ),
    GED("距今大于等于多少天","GED", (s,v) -> String.format(
            "dateTimeUtil.betweenDay(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())>=(+%s)",
            s.get(0),s.get(1),v)
    ),
    LEY("距今小于等于多少年","LEY", (s,v) -> String.format(
            "dateTimeUtil.betweenYear(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())<=(+%s)",
            s.get(0),s.get(1),v)
    ),
    LEM("距今小于等于多少月","LEM", (s,v) -> String.format(
            "dateTimeUtil.betweenMonth(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())<=(+%s)",
            s.get(0),s.get(1),v)
    ),
    LED("距今小于等于多少天","LED", (s,v) -> String.format(
            "dateTimeUtil.betweenDay(dateTimeUtil.parse(dataUtil.respObj('%s').%s),dateTimeUtil.now())<=(+%s)",
            s.get(0),s.get(1),v)
    ),


    SUM("求和","SUM",(s,v) -> {
        return String.format(
                "(function (){\n" +
                "    var resp = dataUtil.respList('%s');\n" +
                "    if (!resp || resp.length<1){ return 0; }\n" +
                "    var count = 0;\n" +
                "    for (var i = 0; i < resp.length; i++){\n" +
                "        var value = resp[i].%s;\n" +
                "        if (value){ count += (+value); }\n" +
                "    }\n" +
                "    return count;\n" +
                "})()",s.get(0),s.get(1)
        );
    }),
    COUNT("统计","COUNT",(s,v) -> {
        return String.format(
                "(function (){\n" +
                "    var resp = dataUtil.respList('%s');\n" +
                "    if (!resp || resp.length<1){\n" +
                "        return 0;\n" +
                "    }\n" +
                "    var count = 0;\n" +
                "    for (var i = 0; i < resp.length; i++){\n" +
                "        if (resp[i].%s){\n" +
                "            count += 1;\n" +
                "        }\n" +
                "    }\n" +
                "    return count;\n" +
                "})()",s.get(0),s.get(1)
        );
    });

    private String name;
    private String symbol;
    private BiFunction<List,Object,String> Parser;

    public static FeatureOperatorEnum getBySymbol(String symbol){
        for (FeatureOperatorEnum value : FeatureOperatorEnum.values()) {
            if(value.getSymbol().equals(symbol)){
                return value;
            }
        }
        return null;
    }
}
