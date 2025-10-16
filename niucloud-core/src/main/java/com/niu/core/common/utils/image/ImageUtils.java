package com.niu.core.common.utils.image;

import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.StringUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Log4j2
public class ImageUtils {

    /**
     * 图片转base64
     * @throws IOException
     */
    public static String imageToBase64(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return "";

            byte[] imageBytes = Files.readAllBytes(Paths.get(path));
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/"+ getImageExt(path) +";base64," + base64Image;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取图片后缀
     * @param fileName
     * @return
     */
    public static String getImageExt(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');

        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1);
        } else {
            return "";
        }
    }

    /**
     * 读取图片
     * @param imgPath
     * @return
     * @throws Exception
     */
    public static BufferedImage readImage(String imgPath) throws Exception {
        if (CommonUtils.isUrl(imgPath)) {
            URL url = new URL(imgPath);
            return ImageIO.read(url);
        } else {
            File defaultFile = new File(WebAppEnvs.get().webRootDownResource, imgPath);
            return ImageIO.read(defaultFile);
        }
    }

    /**
     * 读取图片
     * @param imgPath
     * @param defaultImg
     * @return
     */
    public static BufferedImage readImage(String imgPath, String defaultImg) {
        try {
            return readImage(imgPath);
        } catch (Exception e) {
            try {
                return readImage(defaultImg);
            } catch (Exception d) {
                return null;
            }
        }
    }

    /**
     * 获取图片尺寸
     * @param imagePath 图片路径或URL
     * @return 包含宽度和高度的Map
     */
    public static ImageSize getImageSize(String imagePath) {
        ImageSize result = new ImageSize();
        Map<String, Integer> size = new HashMap<>();

        if (StringUtils.isEmpty(imagePath)) {
            return result;
        }

        try {
            BufferedImage bufferedImage = null;

            if (CommonUtils.isUrl(imagePath)) {
                // 处理网络图片
                URL url = new URL(imagePath);
                bufferedImage = ImageIO.read(url);
            } else {
                // 处理本地图片
                File file = new File(WebAppEnvs.get().webRootDownResource, imagePath);
                if (file.exists() && file.isFile()) {
                    bufferedImage = ImageIO.read(file);
                }
            }

            if (bufferedImage != null) {
                result.setWidth(bufferedImage.getWidth());
                result.setHeight(bufferedImage.getHeight());
            }
        } catch (IOException e) {
            log.error("获取图片尺寸失败:{}", e.getMessage(), e);
        }

        return result;
    }


    /**
     * 获取第一张图片的尺寸（处理逗号分隔的图片字符串）
     * @param images 逗号分隔的图片字符串
     * @return 包含宽度和高度的Map
     */
    public static ImageSize getFirstImageSize(String images) {
        ImageSize imageSize = new ImageSize();
        if (StringUtils.isEmpty(images)) {
            return imageSize;
        }

        String[] imageArray = images.split(",");
        if (imageArray.length == 0) {
            return imageSize;
        }

        String firstImage = imageArray[0].trim();
        return getImageSize(firstImage);
    }

    @Data
    public static class ImageSize {
        private Integer width = 0;
        private Integer height = 0;
    }
}
