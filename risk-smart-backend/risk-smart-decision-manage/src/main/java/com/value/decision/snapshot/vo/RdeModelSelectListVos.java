package com.value.decision.snapshot.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName RdeModelSelectListVos
 * @Description TODO
 * @Authot Administrator
 * @Date 2023/5/9 17:17
 **/
@Data
public class RdeModelSelectListVos{

    private Integer total;

    private Integer accessTotal;

    private List<RdeModelSelectListVo> rdeModelSelectListVoList;
}
