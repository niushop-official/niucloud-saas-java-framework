package com.niu.core.service.core.weapp;

import java.io.File;
import java.util.Map;

public interface ICoreWeappService {

    byte[] qrcodeBytes(Integer siteId, String page, Map<String, Object> data, Integer width);

    File qrcodeFile(Integer siteId, String filePath, String page, Map<String, Object> data, Integer width);
}
