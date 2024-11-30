package com.niu.core.common.component.context.event;

import org.springframework.context.ApplicationEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定义事件的基类
 */
public class Event extends ApplicationEvent {

    private Object source;

    // 站点ID
    private Integer siteId;

    // 应用列表
    private Set<String> appSignSet = new HashSet();

    // 是否授权发送
    private boolean authority = false;

    // 事件名称
    private String name;

    // 事件内容
    private Object content;

    public Event() {
        super("niu");
    }

    public Event(Object source) {
        super(source);
        this.source = source;
    }

    /**
     * @param name
     * @return
     */
    public boolean contain(String name) {
        return appSignSet.contains(name);
    }

    public Integer getSiteId() {
        return siteId;
    }

    public Event setSiteId(Integer siteId) {
        this.siteId = siteId;
        return this;
    }

    public boolean isAuthority() {
        return authority;
    }

    public Event setAuthority(boolean authority) {
        this.authority = authority;
        return this;
    }

    public Event addAppSign(String appSign) {
        this.appSignSet.add(appSign);
        return this;
    }

    public Event addAppSign(List<String> appSignList) {
        this.appSignSet.addAll(appSignList);
        return this;
    }

    public Event addAppSigns(String... appSigns) {
        if (appSigns != null) {
            Arrays.stream(appSigns).forEach(appSign -> {
                appSignSet.add(appSign);
            });
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public Event setName(String name) {
        this.name = name;
        return this;
    }

    public Object getContent() {
        return content;
    }

    public Event setContent(Object content) {
        this.content = content;
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "siteId=" + siteId +
                ", name='" + name + '\'' +
                ", content=" + content +
                '}';
    }

}
