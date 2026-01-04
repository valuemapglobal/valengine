package com.value.decision.rulepool;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.risksmart.common.core.web.AjaxResult;
import com.value.decision.model.decisionmanage.model.*;
import com.value.decision.model.decisionmanage.model.dto.BusinessQueryDTO;
import com.value.decision.model.decisionmanage.model.dto.ProductQueryDTO;
import com.value.decision.model.decisionmanage.model.dto.RulePoolExportDTO;
import com.value.decision.model.decisionmanage.model.dto.RulePoolQueryDTO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudRuleGroupVO;
import com.value.decision.model.decisionmanage.model.dto.model.ModelAntiFraudVO;
import com.value.decision.model.decisionmanage.model.vo.ProductVO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolStatsVO;
import com.value.decision.model.decisionmanage.model.vo.RulePoolVO;
import com.value.decision.model.decisionmanage.model.vo.ScoreCardRecordVO;
import com.value.decision.model.decisionmanage.service.*;
import com.value.decision.model.decisionmanage.task.RulePoolSnapshotTask;
import com.value.decision.model.rdenew.domain.RdeModelDecisionCodeLevel;
import com.value.decision.model.rdenew.function.CommonRuleFunctionDataNew;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudRuleGroupService;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudRuleRecordService;
import com.value.decision.model.rdenew.service.RdeModelAntiFraudService;
import com.value.decision.model.rdenew.service.RdeModelDecisionCodeLevelService;
import com.value.decision.model.rdenew.vo.RdeModelAntiFraudRuleRecordVO;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleGroupSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudRuleRecordSnapshot;
import com.value.decision.snapshot.domain.RdeModelAntiFraudSnapshot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/rule-pool")
@AllArgsConstructor
public class RulePoolController {
    private final ProductService productService;
    private final BusinessService businessService;
    private final RdeModelAntiFraudService rdeModelAntiFraudService;
    private final CommonRuleFunctionDataNew commonRuleFunctionDataNew;
    private final RdeModelAntiFraudRuleGroupService rdeModelAntiFraudRuleGroupService;
    private final RdeModelAntiFraudRuleRecordService rdeModelAntiFraudRuleRecordService;
    private final IScoreCardRecordService scoreCardRecordService;
    private final IScorePrimaryIndexService scorePrimaryIndexService;
    private final IScoreIndexRuleService scoreIndexRuleService;
    private final RdeModelDecisionCodeLevelService rdeModelDecisionCodeLevelService;
    private final RulePoolService service;
    private final RulePoolSnapshotTask snapshotTask;

    @Deprecated
    @PostMapping("/product/_search")
    public AjaxResult productSearch(@RequestBody ProductQueryDTO product){
        PageHelper.startPage(product.getPageNum(),product.getPageSize());

        final List<Product> list = productService.lambdaQuery()
                .eq(Product::getDeptFlag,1)
                .eq(Product::getDataStatus,0)
                .orderByDesc(Product::getCreateTime)
                .list();

        final PageInfo<Product> pageInfo = new PageInfo<>(list);
        if (list!=null && list.size()>0){
            final List<ProductVO> productVOList = list.stream().map(ProductVO::of).collect(Collectors.toList());
            final PageInfo<ProductVO> page = new PageInfo<>();
            BeanUtil.copyProperties(pageInfo,page);
            page.setList(productVOList);
            return AjaxResult.success(page);
        }

        return AjaxResult.success(pageInfo);
    }

    @Deprecated
    @PostMapping("/business/_search")
    public AjaxResult businessSearch(@RequestBody(required = false) BusinessQueryDTO business) {
        if (business != null && (business.getPageNum()!=null && business.getPageSize()!=null)){
            PageHelper.startPage(business.getPageNum(),business.getPageSize());
        }
        final List<Business> list = businessService.lambdaQuery()
                .eq(Business::getDeptFlag,1)
                .eq(Business::getDataStatus,0)
                .orderByDesc(Business::getCreateTime)
                .list();
        final PageInfo<Business> pageInfo = new PageInfo<>(list);
        return AjaxResult.success(pageInfo);
    }

    @Deprecated
    @PostMapping("/rdenew/model/antiFraud/newList")
    public AjaxResult antiFraudNewList(@RequestBody ModelAntiFraudVO rdeModelAntiFraud){
        List<RdeModelAntiFraudSnapshot> RdeModelAntiFraudList = rdeModelAntiFraudService.rulePoolList(rdeModelAntiFraud);
        AjaxResult paging = commonRuleFunctionDataNew.paging(RdeModelAntiFraudList, rdeModelAntiFraud.getPageNum(), rdeModelAntiFraud.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @Deprecated
    @PostMapping("/rdenew/model/antiFraud/rule/group/newList")
    public AjaxResult groupNewList(@RequestBody ModelAntiFraudRuleGroupVO record){
        List<RdeModelAntiFraudRuleGroupSnapshot> rdeModelAntiFraudRuleGroupList = rdeModelAntiFraudRuleGroupService.rulePoolList(record);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleGroupList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @Deprecated
    @PostMapping("/rdenew/model/antiFraud/rule/record/newList")
    public AjaxResult recordNewList(@RequestBody RdeModelAntiFraudRuleRecordVO record){
        List<RdeModelAntiFraudRuleRecordSnapshot> rdeModelAntiFraudRuleRecordList = rdeModelAntiFraudRuleRecordService.rulePoolList(record);
        AjaxResult paging = commonRuleFunctionDataNew.paging(rdeModelAntiFraudRuleRecordList, record.getPageNum(), record.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @Deprecated
    @PostMapping("/rdenew/model/decisionCodeLevel/getById")
    public AjaxResult getById(@RequestBody RdeModelDecisionCodeLevel record) {
        return AjaxResult.success("成功",rdeModelDecisionCodeLevelService.getRulePoolDetail(record));
    }

    @Deprecated
    @RequestMapping("/score-card-record/list")
    public AjaxResult list(@RequestBody ScoreCardRecordVO scoreCardRecord){
        List<ScoreCardRecordSnapshot> scoreCardRecordList = scoreCardRecordService.rulePoolList(scoreCardRecord);
        AjaxResult paging = commonRuleFunctionDataNew.paging(scoreCardRecordList, scoreCardRecord.getPageNum(), scoreCardRecord.getPageSize());
        Object data = paging.get("data");
        return AjaxResult.success(data);
    }

    @Deprecated
    @RequestMapping("/score-primary-index/list")
    public AjaxResult newList(@RequestBody ScorePrimaryIndex scorePrimaryIndex) {
        return AjaxResult.success(scorePrimaryIndexService.rulePoolList(scorePrimaryIndex));
    }

    @Deprecated
    @RequestMapping("/score-index-rule/list")
    public AjaxResult list(@RequestBody ScoreIndexRule scoreIndexRule){
        return AjaxResult.success(scoreIndexRuleService.rulePoolList(scoreIndexRule));
    }

    @Deprecated
    @PostMapping("/pool-list")
    public AjaxResult getPostList(){
        List<RulePoolVO> list = service.getRulePoolList();
        return AjaxResult.success(list);
    }

    // ==================== 新接口 ====================

    /**
     * 分页查询规则池列表（支持筛选和搜索）
     */
    @PostMapping("/page")
    public AjaxResult queryPage(@RequestBody RulePoolQueryDTO queryDTO) {
        PageInfo<RulePoolVO> pageInfo = service.queryRulePoolPage(queryDTO);
        return AjaxResult.success(pageInfo);
    }

    /**
     * 获取规则池统计信息（4个卡片数据）
     */
    @GetMapping("/stats")
    public AjaxResult getStats() {
        RulePoolStatsVO stats = service.getStats();
        return AjaxResult.success(stats);
    }

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/hot-keywords")
    public AjaxResult getHotKeywords(@RequestParam(defaultValue = "10") int topN) {
        List<String> hotKeywords = service.getHotKeywords(topN);
        return AjaxResult.success(hotKeywords);
    }

    /**
     * 导出规则池数据
     */
    @PostMapping("/export")
    public void export(@RequestBody RulePoolQueryDTO queryDTO, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("规则池导出_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 获取导出数据
            List<RulePoolVO> dataList = service.getExportData(queryDTO);

            // 转换为导出DTO
            List<RulePoolExportDTO> exportList = dataList.stream().map(vo -> {
                RulePoolExportDTO dto = new RulePoolExportDTO();
                BeanUtil.copyProperties(vo, dto);
                // 格式化时间
                if (vo.getCreateTime() != null) {
                    dto.setCreateTime(vo.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                return dto;
            }).collect(Collectors.toList());

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), RulePoolExportDTO.class)
                    .sheet("规则池")
                    .doWrite(exportList);

        } catch (Exception e) {
            log.error("规则池导出失败", e);
        }
    }

    /**
     * 手动触发创建月度快照（供测试或补数据使用）
     *
     * @param snapshotMonth 快照月份（yyyy-MM格式），如：2025-11
     */
    @PostMapping("/snapshot/create")
    public AjaxResult createSnapshot(@RequestParam String snapshotMonth) {
        try {
            snapshotTask.manualCreateSnapshot(snapshotMonth);
            return AjaxResult.success("快照创建成功，月份：" + snapshotMonth);
        } catch (Exception e) {
            log.error("快照创建失败", e);
            return AjaxResult.error("快照创建失败：" + e.getMessage());
        }
    }
}
