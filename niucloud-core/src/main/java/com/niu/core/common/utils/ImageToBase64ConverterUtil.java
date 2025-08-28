package com.niu.core.common.utils;

import com.niu.core.common.component.context.WebAppEnvs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ImageToBase64ConverterUtil {

    private static final String RETURN_BASE64_STRING = "data:image/%s;base64,%s";

    public static String convertToBase64(String imagePath) throws IOException {
        if (StringUtils.isEmpty(imagePath)){
            return "";
        }
        String iconUrl = WebAppEnvs.get().webRootDownResource + imagePath;
        if (new File(iconUrl).exists()){
            byte[] fileContent = Files.readAllBytes(Paths.get(iconUrl));
            // 获取文件扩展名（png/jpg等）
            String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();
            String base64String = Base64.getEncoder().encodeToString(fileContent);
            return String.format(RETURN_BASE64_STRING, extension, base64String);
        }
        return "";
    }
}
