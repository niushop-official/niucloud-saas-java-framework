package com.niu.core.common.utils;

import com.niu.core.common.exception.CommonException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.*;

@Log4j2
public class BarcodeUtils {

    public static String generateBarcode(String msg, String path) {
        // 校验路径是否存在，不存在则创建
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path = path + msg + ".png";
        File file = new File(path);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            generateBarCode128(msg, 10.0, 0.3, true, false, outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return path;
    }

    /**
     * 生成code128条形码
     *
     * @param message       要生成的文本
     * @param height        条形码的高度
     * @param width         条形码的宽度
     * @param withQuietZone 是否两边留白
     * @param hideText      隐藏可读文本
     * @param outputStream  输出流
     */
    public static void generateBarCode128(String message, Double height, Double width, boolean withQuietZone, boolean hideText, OutputStream outputStream) {
        Code128Bean bean = new Code128Bean();

        // 分辨率，越大条形码就越大
        int dpi = 150;

        // 设置两侧是否留白
        bean.doQuietZone(withQuietZone);

        // 设置条形码高度和宽度
        bean.setBarHeight(ObjectUtils.defaultIfNull(height, 9.0D));
        if (width != null) {
            bean.setModuleWidth(width);
        }
        // 设置文本位置（包括是否显示）
        if (hideText) {
            bean.setMsgPosition(HumanReadablePlacement.HRP_NONE);
        }
        // 设置图片类型
        String format = "image/png";
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(outputStream, format, dpi,
                BufferedImage.TYPE_BYTE_BINARY, false, 0);

        // 生产条形码
        bean.generateBarcode(canvas, message);
        try {
            canvas.finish();
        } catch (IOException e) {
            log.error("生成条形码失败:{}", e.getMessage(), e);
            throw new CommonException("生成条形码失败");
        }
    }
}