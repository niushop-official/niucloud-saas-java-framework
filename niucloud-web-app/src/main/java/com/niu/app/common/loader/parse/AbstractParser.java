package com.niu.app.common.loader.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractParser {

    protected String home;

    protected Map<String, String> addonMap = new HashMap<>();

    protected Map<String, String> jarMap = new HashMap<>();

    public AbstractParser(String home) throws Exception {
        this.home = home;
    }

    /**
     * @return []
     * todo key=======================模块唯一标识
     * todo value=======================模块类摸板
     */
    public Map<String, String> toAddonMap() {
        return this.addonMap;

    }

    /**
     * @return []
     * todo key=======================模块唯一标识
     * todo value=======================jar包的文件路径
     */
    public Map<String, String> toJarMap() {
        return this.jarMap;
    }

    /**
     * @param file
     * @return
     */
    protected String readToContent(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

}
