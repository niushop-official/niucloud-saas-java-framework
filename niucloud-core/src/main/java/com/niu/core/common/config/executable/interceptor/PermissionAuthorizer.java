package com.niu.core.common.config.executable.interceptor;

import com.niu.core.common.component.context.SpringContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PermissionAuthorizer {

    private static PermissionAuthorizer permissionAuthorizer = new PermissionAuthorizer();

    private boolean init = false;

    private Map<String, Authorizer> authorizerMap = new HashMap<>();

    private PermissionAuthorizer() {
        this.init = false;
    }

    static class Authorizer {

        private String method;

        private String sourcePath;

        private HandlerMethod handlerMethod;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public void setSourcePath(String sourcePath) {
            this.sourcePath = sourcePath;
        }

        public HandlerMethod getHandlerMethod() {
            return handlerMethod;
        }

        public void setHandlerMethod(HandlerMethod handlerMethod) {
            this.handlerMethod = handlerMethod;
        }
    }

    /**
     * @param request
     * @param annotationClass
     * @return
     */
    public boolean existAnnotation(HttpServletRequest request, Class<? extends Annotation> annotationClass) {
        if (!init) {
            refresh();
            init = true;
        }
        String bestMatchingPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        Authorizer authorizer = authorizerMap.get(bestMatchingPattern);
        if (authorizer != null) {
            try{
                Class controller=Class.forName(authorizer.getHandlerMethod().getBean().toString());
                Annotation beanAnnotation= controller.getAnnotation(annotationClass);
                if(beanAnnotation!=null){
                    return true;
                }
                Annotation menthodAnnotation = authorizer.getHandlerMethod().getMethodAnnotation(annotationClass);
                if (menthodAnnotation != null) {
                    return true;
                }
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }

    /**
     *
     */
    private void refresh() {
        RequestMappingHandlerMapping mapping = SpringContext.bean(RequestMappingHandlerMapping.class);
        //获取method
        Map<RequestMappingInfo, HandlerMethod> methodMap = mapping.getHandlerMethods();
        Set<RequestMappingInfo> requestMappingInfoSet = methodMap.keySet();
        for (RequestMappingInfo requestMappingInfo : requestMappingInfoSet) {
            // 请求方法
            Set<RequestMethod> supportMethods = requestMappingInfo.getMethodsCondition().getMethods();
            // 请求路径
            RequestCondition requestCondition = requestMappingInfo.getActivePatternsCondition();
            String supportUrl = requestCondition.toString();
            if (supportUrl.startsWith("[")) {
                supportUrl = supportUrl.substring(1, supportUrl.length());
            }
            if (supportUrl.endsWith("]")) {
                supportUrl = supportUrl.substring(0, supportUrl.length() - 1);
            }
            HandlerMethod handlerMethod = methodMap.get(requestMappingInfo);
            Authorizer authorizer = new Authorizer();
            authorizer.setSourcePath(supportUrl);
            authorizer.setMethod(supportMethods.toString());
            authorizer.setHandlerMethod(handlerMethod);
            authorizerMap.put(supportUrl, authorizer);
        }
    }

    public static PermissionAuthorizer getInstance() {
        return permissionAuthorizer;
    }

    public static void useCase() {
//        if (PermissionAuthorizer.getInstance().existAnnotation(request, SaIgnore.class)) {
//            return true;
//        }
    }

}
