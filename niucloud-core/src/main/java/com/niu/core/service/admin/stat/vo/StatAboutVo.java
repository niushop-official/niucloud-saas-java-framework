package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatAboutVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String desc;
    private String image;
    private String name;

    StatAboutVo(String desc, String image, String name){
        this.desc=desc;
        this.image=image;
        this.name=name;
    }

}
