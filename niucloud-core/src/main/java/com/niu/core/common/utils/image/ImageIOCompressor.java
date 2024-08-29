package com.niu.core.common.utils.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 原始的Java API操作
 */
public class ImageIOCompressor {

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outPath          图片输出文件
     * @param compressionRatio 压缩比例
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, String outPath, double compressionRatio) throws IOException {
        compressImage(inputStream, new FileOutputStream(outPath), compressionRatio);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outFile          图片输出文件
     * @param compressionRatio 压缩比例
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, File outFile, double compressionRatio) throws IOException {
        compressImage(inputStream, new FileOutputStream(outFile), compressionRatio);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outputStream     图片输出流
     * @param compressionRatio 压缩比例
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, OutputStream outputStream, double compressionRatio) throws IOException {
        if (compressionRatio > 1) {
            throw new RuntimeException("压缩比例不能大于1.");
        }
        // 读取图片
        BufferedImage originalImage = ImageIO.read(inputStream);
        int srcWidth = originalImage.getWidth();
        int srcHeight = originalImage.getHeight();
        int targetWidth = (int) (srcWidth * compressionRatio);
        int targetHeight = (int) (srcHeight * compressionRatio);
        // 缩放图片
        Image compressedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        // 创建输出图片缓冲
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        // 绘制缩放后的图片到输出图片缓冲
        outputImage.createGraphics().drawImage(compressedImage, 0, 0, null);
        // 写入输出文件
        ImageIO.write(outputImage, "jpg", outputStream);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream  图片输入流
     * @param outputStream 图片输出流
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, OutputStream outputStream, int targetWidth, int targetHeight) throws IOException {
        // 读取图片
        BufferedImage originalImage = ImageIO.read(inputStream);
        // 缩放图片
        Image compressedImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        // 创建输出图片缓冲
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        // 绘制缩放后的图片到输出图片缓冲
        outputImage.createGraphics().drawImage(compressedImage, 0, 0, null);
        // 写入输出文件
        ImageIO.write(outputImage, "jpg", outputStream);
    }


}
