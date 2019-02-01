package com.rocketmq.demo.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对Web服务应用进行拦截 主要用于添加跨域设置
 *
 */
public class CrossDomainInterceptor implements HandlerInterceptor {

    /**
     * 完成页面的render后调用
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object,
                                Exception exception) throws Exception {
    }

    /**
     * 在调用Controller具体方法后拦截
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object,
                           ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在调用Controller具体方法前拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Object object)
            throws Exception {
        String uri = httpRequest.getRequestURI();
        //处理跨域请求
        httpResponse.addHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.addHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("uri=============拦截器通过=======添加跨域相关设置===========:     " + uri);
        return true;
    }
}
