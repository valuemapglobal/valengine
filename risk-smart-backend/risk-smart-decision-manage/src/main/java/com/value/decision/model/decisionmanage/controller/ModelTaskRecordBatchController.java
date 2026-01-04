package com.value.decision.model.decisionmanage.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.value.decision.common.constant.SecurityConstants;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.ModelTaskRecordBatch;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchDetailVO;
import com.value.decision.model.decisionmanage.model.dto.batch.BatchQueryDTO;
import com.value.decision.model.decisionmanage.model.dto.batch.SubmitBatchDTO;
import com.value.decision.model.decisionmanage.service.IModelTaskRecordBatchService;
import com.value.decision.common.utils.security.SecurityUtils;
import com.value.decision.common.security.LoginUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/model-task-record-batch")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@AllArgsConstructor
public class ModelTaskRecordBatchController {

    private IModelTaskRecordBatchService batchService;

    /**
     * 上传文件(仅上传和校验,不执行任务)
     *
     * @param file      上传的Excel文件
     * @param processId 流程策略ID
     * @return 文件信息(包含fileUrl)
     */
    @PostMapping("/upload-file")
    @ResponseBody
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam("processId") Integer processId) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
            }

            // 参数校验
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("文件不能为空");
            }
            if (processId == null) {
                return AjaxResult.error("流程策略ID不能为空");
            }

            // 调用Service处理
            Map<String, Object> result = batchService.uploadFile(
                    file,
                    processId,
                    loginUser.getSysUser().getDeptId().intValue()
            );

            return AjaxResult.success("文件上传成功", result);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            return AjaxResult.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 提交批次任务(创建批次并异步执行)
     *
     * @param submitBatchDTO 提交批次请求参数
     * @return 批次信息
     */
    @PostMapping("/submit-batch")
    @ResponseBody
    public AjaxResult submitBatch(@RequestBody SubmitBatchDTO submitBatchDTO) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
            }

            // 参数校验
            if (submitBatchDTO.getFileUrl() == null || submitBatchDTO.getFileUrl().trim().isEmpty()) {
                return AjaxResult.error("文件URL不能为空");
            }
            if (submitBatchDTO.getFileName() == null || submitBatchDTO.getFileName().trim().isEmpty()) {
                return AjaxResult.error("文件名不能为空");
            }
            if (submitBatchDTO.getProcessId() == null) {
                return AjaxResult.error("流程策略ID不能为空");
            }

            // 调用Service处理
            Map<String, Object> result = batchService.submitBatch(
                    submitBatchDTO.getFileUrl(),
                    submitBatchDTO.getFileName(),
                    submitBatchDTO.getProcessId(),
                    submitBatchDTO.getResponseForm(),
                    loginUser.getUserid().intValue(),
                    loginUser.getUsername(),
                    loginUser.getSysUser().getDeptId().intValue()
            );

            return AjaxResult.success("批次任务提交成功，正在后台处理", result);

        } catch (Exception e) {
            log.error("批次任务提交失败", e);
            return AjaxResult.error("批次任务提交失败: " + e.getMessage());
        }
    }


    /**
     * 查询批次列表
     *
     * @param queryDTO 查询条件
     * @return 批次列表
     */
    @PostMapping("/batchList")
    @ResponseBody
    public AjaxResult batchList(@RequestBody BatchQueryDTO queryDTO) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
            }

            // 设置部门ID(必须查询当前部门的数据)
            queryDTO.setDeptId(loginUser.getSysUser().getDeptId().intValue());

            // 分页查询
            PageHelper.startPage(
                    queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1,
                    queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 10
            );

            List<ModelTaskRecordBatch> batchList = batchService.getBatchList(queryDTO);
            PageInfo<ModelTaskRecordBatch> pageInfo = new PageInfo<>(batchList);

            return AjaxResult.success(pageInfo);

        } catch (Exception e) {
            log.error("查询批次列表失败", e);
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询批次详情
     *
     * @param batchId  批次ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 批次详情
     */
    @GetMapping("/batchDetail/{batchId}")
    @ResponseBody
    public AjaxResult batchDetail(@PathVariable Long batchId,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
            }

            // 查询批次详情
            BatchDetailVO detailVO = batchService.getBatchDetail(batchId, pageNum, pageSize);

            if (detailVO == null) {
                return AjaxResult.error("批次不存在");
            }

            // 权限校验:只能查看本部门的批次
            if (!detailVO.getBatch().getDeptId().equals(loginUser.getSysUser().getDeptId().intValue())) {
                return AjaxResult.error("无权查看此批次");
            }

            return AjaxResult.success(detailVO);

        } catch (Exception e) {
            log.error("查询批次详情失败: batchId={}", batchId, e);
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询批次基本信息(不含任务列表)
     *
     * @param batchId 批次ID
     * @return 批次信息
     */
    @GetMapping("/batchInfo/{batchId}")
    @ResponseBody
    public AjaxResult batchInfo(@PathVariable Long batchId) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null) {
                return AjaxResult.error(SecurityConstants.ERR_SECURITY_MSG);
            }

            // 查询批次信息
            ModelTaskRecordBatch batch = batchService.getById(batchId);

            if (batch == null) {
                return AjaxResult.error("批次不存在");
            }

            // 权限校验
            if (!batch.getDeptId().equals(loginUser.getSysUser().getDeptId().intValue())) {
                return AjaxResult.error("无权查看此批次");
            }

            return AjaxResult.success(batch);

        } catch (Exception e) {
            log.error("查询批次信息失败: batchId={}", batchId, e);
            return AjaxResult.error("查询失败: " + e.getMessage());
        }
    }
}
