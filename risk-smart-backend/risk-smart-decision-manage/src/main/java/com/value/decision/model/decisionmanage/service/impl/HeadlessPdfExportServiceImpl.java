package com.value.decision.model.decisionmanage.service.impl;

import com.value.decision.model.decisionmanage.service.HeadlessPdfExportService;
import com.value.decision.model.decisionmanage.service.ReportTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 无头浏览器PDF生成服务实现类 - 使用Playwright
 */
@Service
@Slf4j
public class HeadlessPdfExportServiceImpl implements HeadlessPdfExportService {

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Override
    public byte[] convertHtmlContentToPdf(String htmlContent) throws Exception {
        // 使用预渲染服务生成HTML
        return convertPreRenderedHtmlToPdf(htmlContent);
    }

    /**
     * 将预渲染的HTML内容转换为PDF（简化版本）
     */
    public byte[] convertPreRenderedHtmlToPdf(String htmlContent) throws Exception {
//        long startTime = System.currentTimeMillis();
//        String taskId = "PDF_" + System.currentTimeMillis();
//
//        log.info("[{}] 开始使用Playwright生成PDF", taskId);
//        log.info("[{}] HTML内容长度: {}", taskId, htmlContent.length());
//
//        Playwright playwright = null;
//        Browser browser = null;
//        Page page = null;
//
//        try {
//            // 1. 初始化Playwright
//            log.info("[{}] 初始化Playwright", taskId);
//            long playwrightStart = System.currentTimeMillis();
//            playwright = Playwright.create();
//            long playwrightEnd = System.currentTimeMillis();
//            log.info("[{}] Playwright初始化完成，耗时: {} ms", taskId, playwrightEnd - playwrightStart);
//
//            // 2. 启动浏览器
//            log.info("[{}] 启动浏览器", taskId);
//            long browserStart = System.currentTimeMillis();
//            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
//                .setHeadless(true)
//                .setArgs(Arrays.asList(
//                    "--disable-gpu",
//                    "--no-sandbox",
//                    "--disable-dev-shm-usage",
//                    "--disable-extensions",
//                    "--disable-plugins",
//                    "--disable-web-security",
//                    "--allow-running-insecure-content",
//                    "--disable-blink-features=AutomationControlled",
//                    "--no-first-run",
//                    "--no-default-browser-check"
//                ));
//
//            browser = playwright.chromium().launch(options);
//            long browserEnd = System.currentTimeMillis();
//            log.info("[{}] 浏览器启动完成，耗时: {} ms", taskId, browserEnd - browserStart);
//
//            // 3. 创建新页面
//            log.info("[{}] 创建新页面", taskId);
//            long pageStart = System.currentTimeMillis();
//            page = browser.newPage();
//            // 设置默认超时时间
//            page.setDefaultTimeout(60000);
//            long pageEnd = System.currentTimeMillis();
//            log.info("[{}] 页面创建完成，耗时: {} ms", taskId, pageEnd - pageStart);
//
//            // 4. 准备HTML内容并设置页面
//            log.info("[{}] 准备HTML内容并设置页面", taskId);
//            long contentStart = System.currentTimeMillis();
//
//            // 预渲染的HTML已经包含所有样式和数据，直接使用
//            String optimizedHtml = prepareHtmlForPdf(htmlContent);
//
//            // 调试日志：记录HTML处理信息
//            log.info("[{}] 原始HTML长度: {}, 处理后HTML长度: {}", taskId, htmlContent.length(), optimizedHtml.length());
//
//            page.setContent(optimizedHtml, new Page.SetContentOptions()
//                .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
//                .setTimeout(10000));
//            long contentEnd = System.currentTimeMillis();
//            log.info("[{}] 页面内容设置完成，耗时: {} ms", taskId, contentEnd - contentStart);
//
//            // 5. 简化的等待策略 - 预渲染HTML无需等待动态内容
//            log.info("[{}] 等待页面加载完成", taskId);
//            long waitStart = System.currentTimeMillis();
//            try {
//                // 只需要等待DOM内容加载完成，无需等待网络和动态内容
//                page.waitForLoadState(LoadState.DOMCONTENTLOADED);
//
//                // 额外等待1秒确保样式应用
//                Thread.sleep(1000);
//
//                log.info("[{}] 预渲染HTML加载完成", taskId);
//
//            } catch (TimeoutError e) {
//                log.warn("[{}] 页面加载等待超时，继续执行PDF生成", taskId);
//            } catch (InterruptedException e) {
//                log.warn("[{}] 等待被中断", taskId);
//                Thread.currentThread().interrupt();
//            }
//            long waitEnd = System.currentTimeMillis();
//            log.info("[{}] 页面加载完成，耗时: {} ms", taskId, waitEnd - waitStart);
//
//            // 7. 生成PDF
//            log.info("[{}] 开始生成PDF", taskId);
//            long pdfStart = System.currentTimeMillis();
//
//            // 创建PDF选项 - 优化PDF生成以支持样式
//            Page.PdfOptions pdfOptions = new Page.PdfOptions()
//                .setFormat("A4")
//                .setPrintBackground(true)
//                .setLandscape(false)
//                .setScale(1.0);
//
//            byte[] pdfBytes = page.pdf(pdfOptions);
//            long pdfEnd = System.currentTimeMillis();
//            log.info("[{}] PDF生成完成，耗时: {} ms", taskId, pdfEnd - pdfStart);
//
//            long totalTime = System.currentTimeMillis() - startTime;
//            log.info("[{}] Playwright PDF生成完成，大小: {} KB，总耗时: {} ms", taskId, pdfBytes.length / 1024, totalTime);
//
//            return pdfBytes;
//
//        } catch (Exception e) {
//            long totalTime = System.currentTimeMillis() - startTime;
//            log.error("[{}] Playwright PDF生成失败，总耗时: {} ms", taskId, totalTime, e);
//            throw new Exception("PDF生成失败：" + e.getMessage(), e);
//        } finally {
//            // 7. 清理资源
//            if (page != null) {
//                try {
//                    page.close();
//                    log.info("[{}] 页面已关闭", taskId);
//                } catch (Exception e) {
//                    log.warn("[{}] 关闭页面失败", taskId, e);
//                }
//            }
//            if (browser != null) {
//                try {
//                    browser.close();
//                    log.info("[{}] 浏览器已关闭", taskId);
//                } catch (Exception e) {
//                    log.warn("[{}] 关闭浏览器失败", taskId, e);
//                }
//            }
//            if (playwright != null) {
//                try {
//                    playwright.close();
//                    log.info("[{}] Playwright已关闭", taskId);
//                } catch (Exception e) {
//                    log.warn("[{}] 关闭Playwright失败", taskId, e);
//                }
//            }
        return null;
        //}
    }

//    /**
//     * 准备HTML内容用于PDF生成 - 最小化修改原始HTML
//     */
//    private String prepareHtmlForPdf(String htmlContent) {
//        // 创建最小化的PDF打印样式，不修改原始HTML结构
//        String pdfStyles = "<style>\n" +
//            "  @page {\n" +
//            "    size: A4;\n" +
//            "    margin: 0.4in;\n" +
//            "  }\n" +
//            "  \n" +
//            "  /* 确保颜色和背景在PDF中正确显示 */\n" +
//            "  * {\n" +
//            "    -webkit-print-color-adjust: exact !important;\n" +
//            "    color-adjust: exact !important;\n" +
//            "  }\n" +
//            "  \n" +
//            "  /* 确保背景色和边框在PDF中显示 */\n" +
//            "  .bg-white, \n" +
//            "  [class*=\"bg-\"] {\n" +
//            "    print-color-adjust: exact !important;\n" +
//            "  }\n" +
//            "  \n" +
//            "  /* 确保文本颜色在PDF中正确显示 */\n" +
//            "  .text-green-600, .text-green-500, .text-green-400,\n" +
//            "  .text-yellow-600, .text-yellow-500, .text-yellow-400,\n" +
//            "  .text-red-600, .text-red-500, .text-red-400,\n" +
//            "  .text-blue-600, .text-blue-500, .text-blue-400 {\n" +
//            "    color-adjust: exact !important;\n" +
//            "  }\n" +
//            "  \n" +
//            "  /* 确保边框在PDF中显示 */\n" +
//            "  .border, \n" +
//            "  .border-*,\n" +
//            "  [class*=\"border-\"] {\n" +
//            "    print-color-adjust: exact !important;\n" +
//            "  }\n" +
//            "  \n" +
//            "  /* 通用打印优化 */\n" +
//            "  @media print {\n" +
//            "    body {\n" +
//            "      -webkit-print-color-adjust: exact;\n" +
//            "      color-adjust: exact;\n" +
//            "    }\n" +
//            "    \n" +
//            "    /* 避免分页时内容被截断 */\n" +
//            "    .bg-white, \n" +
//            "    .rounded-xl,\n" +
//            "    .shadow-sm {\n" +
//            "      page-break-inside: avoid;\n" +
//            "    }\n" +
//            "  }\n" +
//            "</style>";
//
//        // 检查是否已经是完整的HTML文档
//        if (htmlContent.contains("<!DOCTYPE html") || htmlContent.contains("<html")) {
//            // 如果已经是完整HTML文档，在head中添加样式
//            if (htmlContent.contains("<head>")) {
//                return htmlContent.replace("</head>", pdfStyles + "</head>");
//            } else if (htmlContent.contains("<html>")) {
//                return htmlContent.replace("<html>", "<html><head>" + pdfStyles + "</head>");
//            } else {
//                return htmlContent.replace("<!DOCTYPE html>", "<!DOCTYPE html><html><head>" + pdfStyles + "</head>");
//            }
//        } else {
//            // 如果只是HTML片段，包装成完整文档但保持原有结构
//            // 添加内联样式作为后备方案
//            String inlineStyleBackup = "<style>\n" +
//                "  .bg-white { background-color: #ffffff !important; }\n" +
//                "  .text-green-600 { color: #16a34a !important; }\n" +
//                "  .text-red-600 { color: #dc2626 !important; }\n" +
//                "  .text-blue-600 { color: #2563eb !important; }\n" +
//                "  .rounded-xl { border-radius: 0.75rem !important; }\n" +
//                "  .shadow-sm { box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05) !important; }\n" +
//                "  .p-5 { padding: 1.25rem !important; }\n" +
//                "  .mb-6 { margin-bottom: 1.5rem !important; }\n" +
//                "  .container { max-width: 1200px; margin: 0 auto; }\n" +
//                "</style>\n";
//
//            return "<!DOCTYPE html>\n" +
//                   "<html>\n" +
//                   "<head>\n" +
//                   "  <meta charset=\"UTF-8\">\n" +
//                   "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
//                   pdfStyles + inlineStyleBackup +
//                   "</head>\n" +
//                   "<body>\n" +
//                   htmlContent +
//                   "</body>\n" +
//                   "</html>";
//        }
//    }
}