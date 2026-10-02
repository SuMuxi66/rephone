package com.rephone.controller;

import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常 → 统一返回结构。业务异常透传 code，其余一律 500 不泄露内部信息。
 * 客户端输入问题（缺参/类型不匹配/请求体不可读）单独映射 400xx，
 * 避免与真实服务故障混在同一个 500 里，也便于客户端区分重试策略。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public R<Void> handleBiz(BizException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("[input] 缺少必填参数 name={}", e.getParameterName());
        return R.fail(40000, "缺少必填参数：" + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("[input] 参数类型错误 name={} value={}", e.getName(), String.valueOf(e.getValue()));
        return R.fail(40001, "参数格式错误：" + e.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleUnreadableBody(HttpMessageNotReadableException e) {
        // 只记具体原因，不回显原始内容，防止请求体被日志放大
        log.warn("[input] 请求体不可读 cause={}", String.valueOf(e.getMostSpecificCause().getMessage()));
        return R.fail(40002, "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleOther(Exception e) {
        log.error("未处理异常", e);
        return R.fail(500, "服务繁忙，请稍后重试");
    }
}
