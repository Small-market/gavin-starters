package com.gavin.framework.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gavin.framework.entity.WebLog;
import com.gavin.framework.utils.IPUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Aspect
@RequiredArgsConstructor
public class WebLogAspect {

  private final ObjectMapper json;

  @Pointcut("execution(public * com..*.controller..*.*(..))")
  public void webLog() {
  }


  @Around("webLog()")
  public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    //获取当前请求对象
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    //记录请求信息
    WebLog webLog = new WebLog();
    Object result = joinPoint.proceed();
    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    Method method = methodSignature.getMethod();
    if (method.isAnnotationPresent(Operation.class)) {
      Operation apiOperation = method.getAnnotation(Operation.class);
      webLog.setDescription(apiOperation.description());
    }
    long endTime = System.currentTimeMillis();
    String urlStr = request.getRequestURL().toString();
    webLog.setBasePath(StrUtil.removeSuffix(urlStr, URLUtil.url(urlStr).getPath()));
    webLog.setIp(IPUtil.getIpAddress(request));
    webLog.setMethod(request.getMethod());
    webLog.setParameter(getParameter(method, joinPoint.getArgs()));
    webLog.setResult(result);
    webLog.setSpendTime((int) (endTime - startTime));
    webLog.setStartTime(startTime);
    webLog.setUri(request.getRequestURI());
    webLog.setUrl(request.getRequestURL().toString());
    log.info("{}", json.writeValueAsString(webLog));
    return result;
  }

  /**
   * 根据方法和传入的参数获取请求参数
   */
  private Object getParameter(Method method, Object[] args) {
    List<Object> argList = new ArrayList<>();
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      //将RequestBody注解修饰的参数作为请求参数
      RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
      if (requestBody != null) {
        argList.add(args[i]);
      }
      //将RequestParam注解修饰的参数作为请求参数
      RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
      if (requestParam != null) {
        Map<String, Object> map = new HashMap<>();
        String key = parameters[i].getName();
        if (!StringUtils.isEmpty(requestParam.value())) {
          key = requestParam.value();
        }
        map.put(key, args[i]);
        argList.add(map);
      }
    }
    if (argList.size() == 0) {
      return null;
    } else if (argList.size() == 1) {
      return argList.get(0);
    } else {
      return argList;
    }
  }
}
