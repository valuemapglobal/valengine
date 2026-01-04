package com.value.decision.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Slf4j(topic = "[SnowFlakeIdConfig]")
public class SnowFlakeCloud {

    private static String hostName;

    private static String hostAddress;

    private static SnowFlake snowFlake;

    static {
        snowFlake = new SnowFlake(getWorkId(), getDataCenterId());
        log.warn("【{}：{}】SnowFlake创建完成。WordId:{}，DataCenterId:{}", hostName, hostAddress, SnowFlakeCloud.getWorkId(), SnowFlakeCloud.getDataCenterId());
    }

    public static long nextId() {
        return snowFlake.nextId();
    }

    /**
     * workId使用IP生成
     * @return workId
     */
    private static Long getWorkId() {
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 1;
            for (int b : ints) {
                sums = sums + b;
            }
            return (long) (sums % 32);
        }
        catch (UnknownHostException e) {
            // 失败就随机
            return RandomUtils.nextLong(1, 32);
        }
    }


    /**
     * dataCenterId使用hostName生成
     * @return dataCenterId
     */
    private static Long getDataCenterId() {
        try {
            hostName = SystemUtils.getHostName();
            int[] ints = StringUtils.toCodePoints(hostName);
            int sums = 1;
            for (int i: ints) {
                sums = sums + i;
            }
            return (long) (sums % 32);
        }
        catch (Exception e) {
            // 失败就随机
            return RandomUtils.nextLong(1, 32);
        }
    }

    public static void main(String[] args) {
//        SnowFlakeIdConfig snowFlakeIdConfig = new SnowFlakeIdConfig();
//        SnowFlake snowFlake = snowFlakeIdConfig.snowFlake();
        for (int i = 0; i < (1 << 12); i++) {//1 << 12：：：1对应二进制等于'1'，二进制'1'向左移12位得到'1000000000000',对应十进制为 4096,所以下方会打印出4096个唯一码
            System.out.println(SnowFlakeCloud.nextId());
        }
        System.out.println(SnowFlakeCloud.getDataCenterId());
        System.out.println(SnowFlakeCloud.getWorkId());
    }
}
