package com.risksmart.common.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.risksmart.common.core.constant.HttpStatus;
import com.risksmart.common.core.exception.DemoModeException;
import com.risksmart.common.core.exception.InnerAuthException;
import com.risksmart.common.core.exception.ServiceException;
import com.risksmart.common.core.exception.auth.NotPermissionException;
import com.risksmart.common.core.exception.auth.NotRoleException;
import com.risksmart.common.core.text.Convert;
import com.risksmart.common.core.utils.StringUtils;
import com.risksmart.common.core.utils.html.EscapeUtil;
import com.risksmart.common.core.web.AjaxResult;

/**
 * 全局异常处理器
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String DEFAULT_ERROR_MESSAGE = "系统繁忙，请稍后重试";
    private static final String DEFAULT_VALIDATION_MESSAGE = "参数验证失败";

    private static String resolveRequestUri(HttpServletRequest request)
    {
        return request == null ? "" : request.getRequestURI();
    }

    /**
     * 权限码异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public AjaxResult handleNotPermissionException(NotPermissionException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求地址'{}',权限码校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色权限异常
     */
    @ExceptionHandler(NotRoleException.class)
    public AjaxResult handleNotRoleException(NotRoleException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求地址'{}',角色权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        log.error(e.getMessage(), e);
        Integer code = e.getCode();
        return StringUtils.isNotNull(code) ? AjaxResult.error(code, e.getMessage()) : AjaxResult.error(e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public AjaxResult handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public AjaxResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        String value = Convert.toStr(e.getValue());
        if (StringUtils.isNotEmpty(value))
        {
            value = EscapeUtil.clean(value);
        }
        Class<?> requiredType = e.getRequiredType();
        String requiredTypeName = requiredType == null ? "未知" : requiredType.getName();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), requiredTypeName, value));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public AjaxResult handleRuntimeException(RuntimeException e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        // 生产环境不应该暴露原始错误信息，返回通用错误提示
        String message = StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : DEFAULT_ERROR_MESSAGE;
        return AjaxResult.error(message);
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request)
    {
        String requestURI = resolveRequestUri(request);
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        // 生产环境不应该暴露原始错误信息，返回通用错误提示
        return AjaxResult.error(DEFAULT_ERROR_MESSAGE);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().isEmpty() ? DEFAULT_VALIDATION_MESSAGE : e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public AjaxResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = DEFAULT_VALIDATION_MESSAGE;
        if (e.getBindingResult().getFieldError() != null)
        {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        }
        return AjaxResult.error(message);
    }

    /**
     * 内部认证异常
     */
    @ExceptionHandler(InnerAuthException.class)
    public AjaxResult handleInnerAuthException(InnerAuthException e)
    {
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
