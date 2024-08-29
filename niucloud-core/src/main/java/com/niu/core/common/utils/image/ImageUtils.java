package com.niu.core.common.utils.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
    public static String imageToBase64(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) return "";

        byte[] imageBytes = Files.readAllBytes(Paths.get(path));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        return "data:image/"+ getImageExt(path) +";base64," + base64Image;
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
}
