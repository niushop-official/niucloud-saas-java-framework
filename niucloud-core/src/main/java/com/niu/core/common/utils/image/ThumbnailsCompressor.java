package com.niu.core.common.utils.image;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ThumbnailsCompressor {

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outPath          图片输出文件
     * @param compressionRatio 压缩比例
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, String outPath, double compressionRatio) throws IOException {
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(1).toFile(outPath);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outPath          图片输出文件
     * @param compressionRatio 压缩比例
     * @param quality          压缩质量
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, String outPath, double compressionRatio, double quality) throws IOException {
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(quality).toFile(outPath);
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
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(1).toFile(outFile);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outFile          图片输出文件
     * @param compressionRatio 压缩比例
     * @param quality          压缩质量
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, File outFile, double compressionRatio, double quality) throws IOException {
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(quality).toFile(outFile);
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
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(1).toOutputStream(outputStream);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream      图片输入流
     * @param outputStream     图片输出流
     * @param compressionRatio 压缩比例
     * @param quality          压缩质量
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, OutputStream outputStream, double compressionRatio, double quality) throws IOException {
        Thumbnails.of(inputStream).scale(compressionRatio).outputQuality(quality).toOutputStream(outputStream);
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
        Thumbnails.of(inputStream).size(targetWidth, targetHeight).outputQuality(1).toOutputStream(outputStream);
    }

    /**
     * 根据压缩比例压缩
     *
     * @param inputStream  图片输入流
     * @param outputStream 图片输出流
     * @param targetWidth  目标宽度
     * @param targetHeight 目标高度
     * @param quality      压缩质量
     * @throws IOException
     */
    public static void compressImage(InputStream inputStream, OutputStream outputStream, int targetWidth, int targetHeight, double quality) throws IOException {
        Thumbnails.of(inputStream).size(targetWidth, targetHeight).outputQuality(quality).toOutputStream(outputStream);
    }


}
