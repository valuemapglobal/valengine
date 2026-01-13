package com.risksmart.common.core.constant;

/**
 * 模型流程常量
 */
public class ModelConstants {

    public static final String MODEL_PROCESS_ID = "流程ID为空";

    /** 流程任务初始化 */
    public static final Integer TASK_INITIALIZATION = 1;

    /** 流程任务生成中 */
    public static final Integer TASK_GENERATION = 2;

    /** 流程任务生成成功 */
    public static final Integer TASK_SUCCESS = 3;

    /** 流程任务生成失败 */
    public static final Integer TASK_FAIL = 4;

    /** 流程为开启 */
    public static final Integer USE = 1;

    private ModelConstants() {
    }
}
