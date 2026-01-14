package com.value.data.common.service;
import com.risksmart.common.core.utils.uuid.SnowFlake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratorService {

    private final SnowFlake snowFlake;
    public IdGeneratorService(@Value("${snowFlake.datacenterId:1}") Long dataCenterId,
                              @Value("${snowFlake.machineId:1}") Long machineId) {
       snowFlake = new SnowFlake(dataCenterId.longValue(), machineId.longValue());
    }

    public long generateUniqueId() {
        return snowFlake.nextId();
    }
}
