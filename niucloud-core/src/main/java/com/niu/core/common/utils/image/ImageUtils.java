package com.niu.core.common.utils.image;

import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.utils.CommonUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 */
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
}
