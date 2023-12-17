package com.holland.conf;

import com.holland.infrastructure.kit.exception.BizException;
import com.holland.infrastructure.kit.exception.NotImplementedException;
import com.holland.infrastructure.kit.exception.SimpleException;
import com.holland.infrastructure.kit.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    /**
     * 自定义服务异常的。简单的返回数据，不需要打印报错栈
     */
    @ExceptionHandler({SimpleException.class, NotImplementedException.class})
    public R<String> simpleException(Exception exception, HttpServletResponse response) {
        setRespErrStatus(response);
        return R.fail(exception.getMessage());
    }

    /**
     * 自定义服务异常的。打印异常站信息的业务异常类
     */
    @ExceptionHandler({BizException.class})
    public R<String> bizException(BizException e, HttpServletResponse response) {
        log.error("业务异常异常！", e);
        setRespErrStatus(response);
        return R.fail("业务异常");
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<String> handleAccessDeniedException(AccessDeniedException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("没有权限，请联系管理员授权 请求地址'{}',权限校验失败'{}'", request.getRequestURI(), e.getMessage());
        setRespErrStatus(response);
        return R.fail("没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<String> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("不支持[{}]请求方式 请求地址'{}'", e.getMethod(), request.getRequestURI(), e);
        setRespErrStatus(response);
        return R.fail("不支持[{}]请求方式", e.getMethod());
    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<String> handleMissingPathVariableException(MissingPathVariableException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("请求路径中缺少必需的路径变量[{}] 请求地址'{}'", e.getVariableName(), request.getRequestURI(), e);
        setRespErrStatus(response);
        return R.fail("请求路径中缺少必需的路径变量[{}]", e.getVariableName());
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("请求参数类型不匹配，参数[{}]要求类型为：'{}'，但输入值为：'{}' 请求地址'{}'", e.getName(), e.getRequiredType().getName(), e.getValue(), request.getRequestURI(), e);
        setRespErrStatus(response);
        return R.fail("请求参数类型不匹配，参数[{}]要求类型为：'{}'，但输入值为：'{}'", e.getName(), e.getRequiredType().getName(), e.getValue());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public R<String> handleBindException(BindException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("自定义验证异常 请求地址'{}'", request.getRequestURI(), e);
        setRespErrStatus(response);
        final String message = e.getAllErrors().get(0).getDefaultMessage();
        return R.fail("自定义验证异常: {}", message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("自定义验证异常 请求地址'{}'", request.getRequestURI(), e);
        setRespErrStatus(response);
        final String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail("自定义验证异常: {}", message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public R<String> ConstraintViolationExceptionHandler(ConstraintViolationException e, HttpServletResponse response, HttpServletRequest request) {
        log.error("自定义验证异常 请求地址'{}' err: {}", request.getRequestURI(), e.getMessage());
        setRespErrStatus(response);
        return R.fail("请求参数验证异常: {}", e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception e, HttpServletResponse response, HttpServletRequest request) {
        log.error("系统异常 请求地址'{}'", request.getRequestURI(), e);
        setRespErrStatus(response);
        return R.fail("系统异常");
    }

    /**
     * 如果开启分布式事务，就设置response.status = 500，seata的tm（事务管理器）
     * 就是感知到 TmTransactionException异常，发起事务回滚
     */
    private void setRespErrStatus(HttpServletResponse response) {
        //如果开启分布式事务,设置错误状态码,让事务回滚
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
