package com.niu.core.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.base.ThreadLocalHolder;
import com.niu.core.enums.common.ChannelEnum;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统请求工具类
 */
public class RequestUtils {

    /**
     * 获取默认站点id
     */
    public static Integer defaultSiteId(){
        return 0;
    };

    /**
     * request请求后台获取站点id
     * @return
     */
    public static Integer adminSiteId(){
        HttpServletRequest request = RequestUtils.handler();
        if (request != null) {
            return Integer.parseInt(request.getHeader("site-id"));
        }
        return 0;
    }

    /**
     * request请求前端获取站点id
     * @return
     */
    public static Integer apiSiteId(){
        HttpServletRequest request = RequestUtils.handler();
        if (request != null) {
            return Integer.parseInt(ObjectUtil.defaultIfNull(request.getHeader("site-id"), "0"));
        }
        return 0;

    }

    /**
     * request请求后台请求获取token
     * @return
     */
    public static String adminToken(){
        HttpServletRequest request = RequestUtils.handler();
        if (request != null) {
            return request.getHeader("token");
        }
        return "";
    }

    /**
     * request前端请求获取token
     * @return
     */
    public static String apiToken(){
        HttpServletRequest request = RequestUtils.handler();
        if (request != null) {
            return request.getHeader("token");
        }
        return "";
    }

    /**
     * request前端请求来源渠道
     * @return
     */
    public static String channel(){
        HttpServletRequest request = RequestUtils.handler();
        if (request != null) {
            return request.getHeader("channel");
        }
        return ChannelEnum.H5.getCode();
    }

    /**
     * request前端请求来源ip
     * @return
     */
    public static String ip() {
        HttpServletRequest request = RequestUtils.handler();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取本地线程siteId
     * @return
     */
    public static Integer siteId(){
        return ThreadLocalHolder.getInteger("site-id")!=null?ThreadLocalHolder.getInteger("site-id"):0;
    }
    /**
     * 设置本地线程siteId
     */
    public static void setSiteId(Integer siteId){
        ThreadLocalHolder.put("site-id", siteId);
    }
    /**
     * 获取本地线程uid
     * @return
     */
    public static Integer uid(){
        return ThreadLocalHolder.getInteger("uid");
    }

    /**
     * 设置本地线程uid
     */
    public static void setUid(Integer uid){ ThreadLocalHolder.put("uid", uid);}

    /**
     * 获取本地线程memberId
     * @return
     */
    public static Integer memberId(){return ThreadLocalHolder.getInteger("member-id");}

    /**
     * 设置本地线程memberId
     */
    public static void setMemberId(Integer memberId){ThreadLocalHolder.put("member-id", memberId);}

    /**
     * 获取本地线程请求类型
     * @return
     */
    public static String appType(){
        return ThreadLocalHolder.getString("app-type");
    }

    /**
     * 设置本地线程请求类型
     */
    public static void setAppType(String appType){ThreadLocalHolder.put("app-type", appType);}

    /**
     * 获取请求的ip地址
     * @return
     */
    public static String getRequestIpAddr(){return ThreadLocalHolder.getString("request-ipaddr");}
    /**
     * 设置获取请求的ip地址
     * @param request
     */
    public static void setRequestIpAddr(HttpServletRequest request){ThreadLocalHolder.put("request-ipaddr", IpUtils.getIpAddr(request));}

    /**
     * 设置当前请求的域名
     *
     * @param requestDomain
     */
    public static void setRequestDomain(String requestDomain){

        ThreadLocalHolder.put("request-domain", requestDomain);
    }
    /**
     * 获取当前请求的域名
     *
     * @return
     */
    public static String getRequestDomain(){

        return ThreadLocalHolder.getString("request-domain");
    }

    /**
     * 设置当前请求类型
     *
     * @param requestSecure
     */
    public static void setRequestSecure(String requestSecure){

        ThreadLocalHolder.put("request-secure", requestSecure);
    }
    /**
     * 当前请求类型 http 还是 https
     *
     * @return
     */
    public static String getRequestSecure(){

        return ThreadLocalHolder.getString("request-secure");
    }

    /**
     * 获取当前请求的路由地址
     *
     * @return
     */
    public static String getReqeustURI(){
        HttpServletRequest request = RequestUtils.handler();
        String rule="";
        if (request != null) {
            //未被参数化的路由
            String bestMatchingPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            //当前访问的路由
            rule = request.getRequestURI().trim().toLowerCase();
            //检测路由中是否存在大括号 是否为带参路由
            if (bestMatchingPattern.contains("{")) {
                rule = bestMatchingPattern.replace("{", "<").replace("}", ">");
            }
        }
        return rule;
    }

    /**
     * 获取当前请求的Method
     *
     * @return
     */
    public static String getRequestMethod(){
        HttpServletRequest request = RequestUtils.handler();
        String method="";
        if(request !=null){
            method = request.getMethod().trim().toLowerCase();
        }
        return method;
    }
    /**
     * 获取请求对象
     * @return HttpServletRequest
     */
    public static HttpServletRequest handler() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    /**
     * 获取请求域名
     *
     * @param isScheme
     * @return
     */
    public static String getDomain(Boolean isScheme) {
        return isScheme ? getRequestSecure() + "://" + getRequestDomain() : getRequestDomain();
    }
}
