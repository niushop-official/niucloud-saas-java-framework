package com.niu.cms.service.admin.article.param;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 会员标签参数
*/
@Data
public class ArticleParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "title参数缺失")
    @NotBlank(message = "文章标题必须填写")
    private String title;

    @NotNull(message = "category_id参数缺失")
    @Min(value = 0, message = "文章栏目必须填写")
    private Integer categoryId;

    private String intro = "";

    private String summary = "";

    private String image = "";

    private String author = "";

    @NotNull(message = "content参数缺失")
    @NotBlank(message = "文章内容必须填写")
    private String content;

    private Long visitVirtual;

    private Integer isShow = 1;

    private Long sort;
}

