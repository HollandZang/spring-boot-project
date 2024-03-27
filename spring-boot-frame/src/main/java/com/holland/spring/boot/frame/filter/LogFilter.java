package com.holland.spring.boot.frame.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSON;
import com.holland.infrastructure.kit.web.R;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;

@Component
@Order(1)
@Slf4j
public class LogFilter extends HttpFilter {

    /**
     * @apiNote 请求必须记录名单，只要在里面就必然会记录操作日志，否则只会记录失败请求日志
     */
    private List<String> getMustUris() {
        final List<String> list = new ArrayList<>();
        return list;
    }

    /**
     * @apiNote 建议必须过滤 token验证、轮询api，静态资源
     * @apiNote 建议过滤     常用、稳定、调用量大的api
     */
    private List<String> getIgnoreUris() {
        List<String> list = new ArrayList<>();
        return list;
    }

    private Predicate<HttpServletRequest>[] ignoreChains;
    private List<String> mustUris;
    private List<String> ignoreUris;

    // 过滤 OPTIONS 探针
    private final Predicate<HttpServletRequest> chainOfOptions = req -> HttpMethod.OPTIONS.matches(req.getMethod());
    // 过滤 websocket 连接
    private final Predicate<HttpServletRequest> chainOfWebsocket = req -> "websocket".equals(req.getHeader("upgrade")) || "Upgrade".equals(req.getHeader("connection"));
    // 过滤文件上传api
    private final Predicate<HttpServletRequest> chainOfMultipart = req -> {
        String contentType = req.getContentType();
        return contentType != null && contentType.contains("multipart");
    };
    // 过滤指定api
    private final Predicate<HttpServletRequest> chainOfIgnoreUri = req -> {
        final String uri = getUri(req);
        return ignoreUris.contains(uri);
    };

    @Override
    public void init() throws ServletException {
        super.init();
        if (log.isInfoEnabled()) {
            ignoreUris = getIgnoreUris();
            log.info("loading ignore uris: " + ignoreUris);
            ignoreChains = new Predicate[]{
                    chainOfOptions
                    , chainOfWebsocket
                    , chainOfMultipart
                    , chainOfIgnoreUri
            };
            mustUris = getMustUris();
            log.info("loading must uris: " + mustUris);
        }
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final String uri = getUri(req);

        if (!log.isInfoEnabled()) {
            filterChain.doFilter(req, servletResponse);
            return;
        }
        final boolean isMust = mustUris.contains(uri);
        // 不在请求必须记录名单，且被在过滤名单。就不记录日志
        if (!isMust) {
            for (Predicate<HttpServletRequest> ignoreChain : ignoreChains) {
                if (ignoreChain.test(req)) {
                    filterChain.doFilter(req, servletResponse);
                    return;
                }
            }
        }

        final ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(servletResponse);

        final String params = genParamsStr(req);
        final String bodyStr = IoUtil.read(req.getInputStream(), StandardCharsets.UTF_8);
        final HttpServletRequestWrapper reqWrapper = new MyHttpServletRequestWrapper(req, bodyStr);

        final long l = System.currentTimeMillis();
        final String traceId = RandomUtil.randomString(10);
        // 在logback中配置%X{traceId}
        MDC.put("traceId", traceId);
        if (isMust)
            log.info("uri[{}] params[{}] body[{}]", uri, params, bodyStr);

        filterChain.doFilter(reqWrapper, resp);

        final String content = new String(resp.getContentAsByteArray(), StandardCharsets.UTF_8);
        resp.copyBodyToResponse();
        final double time = (double) (System.currentTimeMillis() - l) / 1000;

        if (isMust)
            log.info("cost[{}s] status[{}] resp[{}]", time, resp.getStatus(), trunc(content, 256));
        else
            printErrReqAndResp(uri, resp, params, bodyStr, content, time);
    }

    private void printErrReqAndResp(String uri, ContentCachingResponseWrapper resp, String params, String bodyStr, String content, double time) {
        if (resp.getStatus() != 200) {
            // 打印 resp code 不为 200 日志
            log.info("uri[{}] params[{}] body[{}]", uri, params, bodyStr);
            log.info("cost[{}s] status[{}] resp[{}]", time, resp.getStatus(), trunc(content, 256));
        } else {
            R result = null;
            try {
                // 过滤 json 转换异常的
                result = JSON.parseObject(content, R.class);
            } catch (Exception ignore) {
                log.warn("返回值json转换异常请排查： uri[{}] params[{}] body[{}]", uri, params, bodyStr);
                log.warn("返回值json转换异常请排查： cost[{}s] status[{}] resp[{}]", time, resp.getStatus(), trunc(content, 256));
            }
            try {
                if (result != null && result.getCode() != 0 && result.getCode() != R.SUCCESS) {
                    // 打印 业务 code 不为 ResultCode.SUCCESS 日志
                    log.info("uri[{}] params[{}] body[{}]", uri, params, bodyStr);
                    log.info("cost[{}s] status[{}] resp[{}]", time, resp.getStatus(), trunc(content, 256));
                }
            } catch (Exception e) {
                log.error(String.format("err req: uri[%s] params[%s] body[%s] cost[%ss] status[%s] content[%s]"
                        , uri, params, bodyStr, time, resp.getStatus(), trunc(content, 256)), e);
            }
        }
    }

    private String getUri(HttpServletRequest req) {
        // 把前端可能误拼的多个/改为一个/
        return req.getRequestURI().replaceAll("/+", "/");
    }

    private String genParamsStr(HttpServletRequest req) {
        Map<String, String> m = new HashMap<>();
        Enumeration<String> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String k = parameterNames.nextElement();
            String v = req.getParameter(k);
            m.put(k, v);
        }
        return JSON.toJSONString(m);
    }

    private String trunc(String field, int length) {
        if (field == null)
            return null;
        if (field.length() <= length)
            return field;
        return field.substring(0, length);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
