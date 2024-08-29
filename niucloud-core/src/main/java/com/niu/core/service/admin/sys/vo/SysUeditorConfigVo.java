package com.niu.core.service.admin.sys.vo;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.niu.core.common.utils.RequestUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 百度编译器配置类
 */
@Data
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class SysUeditorConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态 */
    private String state;

    /** 图片 */
    private String imageActionName;

    /** 提交的图片表单名称，默认值：upfile */
    private String imageFieldName;

    /** 上传大小限制，单位B，默认值：2048000 */
    private Integer imageMaxSize;

    /** 上传图片格式显示，默认值：[".png", ".jpg", ".jpeg", ".gif", ".bmp"] */
    private List<String> imageAllowFiles;

    /** 是否压缩图片,默认是true */
    private boolean imageCompressEnable;

    /** 图片压缩最长边限制，默认值：1600 */
    private Integer imageCompressBorder;

    /** 插入的图片浮动方式，默认值：none */
    private String imageInsertAlign;

    /** 图片访问路径前缀，默认值：空 */
    private String imageUrlPrefix;

    /** 执行上传涂鸦的action名称，默认值：uploadscrawl */
    private String scrawlActionName;

    /** 提交的图片表单名称，默认值：upfile */
    private String scrawlFieldName;

    /** 上传大小限制，单位B，默认值：2048000 */
    private Integer scrawlMaxSize;

    /** 图片访问路径前缀，默认值：空 */
    private String scrawlUrlPrefix;

    /** 插入的图片浮动方式，默认值：none */
    private String scrawlInsertAlign;

    /** 执行上传截图的action名称，默认值：uploadimage */
    private String snapscreenActionName;

    /** 图片访问路径前缀 */
    private String snapscreenUrlPrefix;

    /** 插入的图片浮动方式，默认值：none */
    private String snapscreenInsertAlign;

    /** 例外的图片抓取域名 */
    private List<String > catcherLocalDomain;

    /** 执行抓取远程图片的action名称，默认值：catchimage */
    private String catcherActionName;

    /** 提交的图片列表表单名称，默认值：source */
    private String catcherFieldName;

    /** 图片访问路径前缀，默认值：空 */
    private String catcherUrlPrefix;

    /** 上传保存路径,可以自定义保存路径和文件名格式，默认值：{filename}{rand:6} */
    private Integer catcherMaxSize;

    /** 抓取图片格式显示，默认值：[".png", ".jpg", ".jpeg", ".gif", ".bmp"] */
    private List<String> catcherAllowFiles;

    /** 执行上传视频的action名称，默认值：uploadvideo */
    private String videoActionName;

    /** 提交的视频表单名称，默认值：upfile */
    private String videoFieldName;

    /** 视频访问路径前缀 */
    private String videoUrlPrefix;

    /** 上传大小限制，单位B，默认值：102400000 */
    private Integer videoMaxSize;

    /** 上传视频格式显示 */
    private List<String> videoAllowFiles;

    /** 执行上传文件的action名称，默认值：uploadfile */
    private String fileActionName;

    /** 提交的文件表单名称，默认值：upfile */
    private String fileFieldName;

    /** 文件访问路径前缀 */
    private String fileUrlPrefix;

    /** 上传保存路径,可以自定义保存路径和文件名格式，默认值：{filename}{rand:6} */
    private Integer fileMaxSize;

    /** 上传文件格式显示 */
    private List<String> fileAllowFiles;

    /** 执行图片管理的action名称，默认值：listimage */
    private String imageManagerActionName;

    /** 每次列出文件数量 */
    private Integer imageManagerListSize;

    /** 图片访问路径前缀 */
    private String imageManagerUrlPrefix;

    /** 插入的图片浮动方式，默认值：none */
    private String imageManagerInsertAlign;

    /** 列出的文件类型 */
    private List<String> imageManagerAllowFiles;

    /** 执行文件管理的action名称，默认值：listfile */
    private String fileManagerActionName;

    /** 指定要列出文件的目录 */
    private String fileManagerUrlPrefix;

    /** 每次列出文件数量 */
    private Integer fileManagerListSize;

    /** 列出的文件类型 */
    private List<String> fileManagerAllowFiles;

    /** 公式配置 */
    private JSONObject formulaConfig;

    /**
     * 构造函数
     */
    public SysUeditorConfigVo(){
        this.state="SUCCESS";
        this.imageActionName="image";
        this.imageFieldName="file";
        this.imageMaxSize=10485760;
        List<String> imageAllowFiles=new ArrayList<>();
        imageAllowFiles.add(".jpg");
        imageAllowFiles.add(".png");
        imageAllowFiles.add(".jpeg");
        this.imageAllowFiles=imageAllowFiles;
        this.imageCompressEnable=true;
        this.imageCompressBorder=5000;
        this.imageInsertAlign="none";
        this.imageUrlPrefix= RequestUtils.getRequestDomain();
        this.scrawlActionName="crawl";
        this.scrawlFieldName="file";
        this.scrawlMaxSize=10485760;
        this.scrawlUrlPrefix="";
        this.scrawlInsertAlign="none";
        this.snapscreenActionName="snap";
        this.snapscreenUrlPrefix="";
        this.snapscreenInsertAlign="none";
        List<String> catcherLocalDomain=new ArrayList<>();
        catcherLocalDomain.add("127.0.0.1");
        catcherLocalDomain.add("localhost");
        this.catcherLocalDomain=catcherLocalDomain;
        this.catcherActionName="catch";
        this.catcherFieldName="source";
        this.catcherUrlPrefix="";
        this.catcherMaxSize=10485760;
        List<String> catcherAllowFiles=new ArrayList<>();
        catcherAllowFiles.add(".jpg");
        catcherAllowFiles.add(".png");
        catcherAllowFiles.add(".jpeg");
        this.catcherAllowFiles=catcherAllowFiles;
        this.videoActionName="video";
        this.videoFieldName="file";
        this.videoUrlPrefix= RequestUtils.getRequestDomain();
        this.videoMaxSize=104857600;
        List<String> videoAllowFiles=new ArrayList<String>();
        videoAllowFiles.add(".mp4");
        this.videoAllowFiles=videoAllowFiles;
        this.fileActionName="file";
        this.fileFieldName="file";
        this.fileUrlPrefix="";
        this.fileMaxSize=104857600;
        List<String> fileAllowFiles=new ArrayList<>();
        fileAllowFiles.add(".zip");
        fileAllowFiles.add(".pdf");
        fileAllowFiles.add(".doc");
        this.fileAllowFiles=fileAllowFiles;
        this.imageManagerActionName="listImage";
        this.imageManagerListSize=20;
        this.imageManagerUrlPrefix="";
        this.imageManagerInsertAlign="none";
        List<String> imageManagerAllowFiles=new ArrayList<>();
        imageManagerAllowFiles.add(".jpg");
        imageManagerAllowFiles.add(".png");
        imageManagerAllowFiles.add(".jpeg");
        this.imageManagerAllowFiles=imageManagerAllowFiles;
        this.fileManagerActionName="listFile";
        this.fileManagerUrlPrefix="";
        this.fileManagerListSize=20;
        List<String> fileManagerAllowFiles=new ArrayList<>();
        fileManagerAllowFiles.add(".zip");
        fileManagerAllowFiles.add(".pdf");
        fileManagerAllowFiles.add(".doc");
        this.fileManagerAllowFiles=fileManagerAllowFiles;
        JSONObject jsonObject=new JSONObject();
        jsonObject.set("imageUrlTemplate", RequestUtils.getRequestDomain()+"{}");
        this.formulaConfig=jsonObject;
    }

}
