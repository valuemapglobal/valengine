package com.value.data.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelUtils {
    public ExcelUtils(){

    }
    /**
     * 下载
     *
     * @param response web响应
     * @param datas    导出数据
     * @param clazz    实体类
     * @param fileName 导出文件名称
     * @return
     */
    public static void download(HttpServletResponse response, List datas, Class clazz, String fileName) {
        // parseHead（获取实体类定义的属性名称，也就是excel表头）
        download(response, datas, clazz, parseHead(clazz), fileName);
    }

    /**
     * 解析实体类
     *
     * @param clazz    实体类
     * @return
     */
    private static List<List<String>> parseHead(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<List<String>> heads = new ArrayList<>();

        for (Field field : fields) {
            List<String> head = new ArrayList<>();
            // 在开发我们应该少不了swagger, 这个注解是swagger提供的，当然我们也可以自定义一个注解。（作用是为了属性名映射中文名称输出到excel表头）
            ApiModelProperty apiAnnotation = field.getAnnotation(ApiModelProperty.class);
            head.add(apiAnnotation.name());
            heads.add(head);
        }
        return heads;
    }


    /**
     * 下载
     *
     * @param response web响应
     * @param datas    导出数据
     * @param head     表头
     * @param fileName 导出文件名称
     * @return
     */
    public static void download(HttpServletResponse response, List datas, Class clazz, List<List<String>> head, String fileName) {
        // 设置web响应输出的文件名称
        setResponseHeader(response, fileName);
        try {
            // 核心中的核心
            export(response.getOutputStream(), head, datas, clazz);
        } catch (IOException e) {
            log.info("无法获取响应流", e);
        }
    }

    /**
     * 设置web响应输出的文件名称
     * @param response web响应
     * @param fileName 导出文件名称
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        response.reset();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.info("不支持的编码", e);
        }
        response.setCharacterEncoding("UTF-8");
    }

    /**
     *
     * @param os     字节输出流
     * @param head   表头
     * @param datas  导出数据
     * @param clazz  实体类
     * @throws IOException
     */
    private static void export(OutputStream os, List<List<String>> head, List datas, Class clazz) throws IOException {
        if (datas == null) {
            datas = new ArrayList();
        }
        ExcelWriter excelWriter = null;
        BufferedOutputStream bos = new BufferedOutputStream(os);
        try {
            excelWriter = EasyExcel.write(bos, clazz).build();
            WriteSheet testSheet = EasyExcel.writerSheet("sheet1")
                    .head(head)
                    .build();
            excelWriter.write(datas, testSheet);
        } catch (Exception e) {
            log.info("easyexcel初始化错误", e);
        } finally {
            bos.flush();
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

}
