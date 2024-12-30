package com.niu.app.common.tools;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectCompileAndBuildTools {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String projectHome = ProjectCompileAndBuildTools.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (projectHome.contains("niucloud-web-app")) {
            projectHome = projectHome.substring(0, projectHome.indexOf("niucloud-web-app"));
        } else {
            System.err.println("工程[niucloud-web-app]不存在.");
            return;
        }
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        boolean installAndBuild = false;
        boolean srcBuild = false;
        for (String arg : args) {
            if ("all".equals(arg)) {
                installAndBuild = true;
                srcBuild = true;
            }
            if ("compile".equals(arg)) {
                installAndBuild = true;
            }
            if ("build".equals(arg)) {
                srcBuild = true;
            }
        }
        // 打包编译
        if (installAndBuild) {
            installAndBuildAdmin(projectHome);
            installAndBuildH5(projectHome);
            installAndBuildWeb(projectHome);
        }
        // 工程源码复制
        if (srcBuild) {
            buildAdminToRuntime(projectHome);
            buildH5ToRuntime(projectHome);
            buildWebToRuntime(projectHome);
        }
    }


    /**
     * @param home
     */
    public static void installAndBuildAdmin(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing admin");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/admin", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/admin/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD admin");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int buildResult = execute(home + "/admin", "npm run build");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    public static void installAndBuildH5(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing uni-app");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/uni-app", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/wap/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD uni-app");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int buildResult = execute(home + "/uni-app", "npm run build:h5");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    public static void installAndBuildWeb(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] Installing web");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        int installResult = execute(home + "/web", "npm install");
        System.out.println();
        if (installResult == 0) {
            System.out.println("[INFO] Installing SUCCESS");
        } else {
            System.out.println("[INFO] Installing ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] BUILD web");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        // 检查目标目录是否存在
        File distFile = new File(home + "/webroot/public/web/");
        if (!distFile.exists()) {
            System.out.println("[INFO] create wap directory");
            distFile.mkdirs();
        }
//        try {
//            System.setOut(new PrintStream(System.out, true, "GBK"));
//        } catch (UnsupportedEncodingException e) {
//            // ignore
//        }
        int buildResult = execute(home + "/web", "npm run generate");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        if (buildResult == 0) {
            System.out.println("[INFO] BUILD SUCCESS");
        } else {
            System.out.println("[INFO] BUILD ERROR");
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     * @param command
     */
    public static int execute(String home, String command) {
        if (System.getProperty("os.name").contains("Windows")) {
            return executeWithWin(home, command);
        } else {
            return executeWithLinux(home, command);
        }
    }

    /**
     * @param home
     * @param command
     * @return
     */
    public static int executeWithWin(String home, String command) {
        Process operateProcess = null;
        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + command, null, new File(home));
            operateProcess = process;
            new Thread(() -> {
                BufferedReader standardReader = null;
                try {
                    standardReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = standardReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        standardReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            new Thread(() -> {
                BufferedReader errorReader = null;
                try {
                    errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        errorReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                operateProcess.destroyForcibly();
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * @param home
     * @param command
     * @return
     */
    public static int executeWithLinux(String home, String command) {
        Process operateProcess = null;
        try {
            Process process = Runtime.getRuntime().exec(command, null, new File(home));
            operateProcess = process;
            new Thread(() -> {
                BufferedReader standardReader = null;
                try {
                    standardReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = standardReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        standardReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            new Thread(() -> {
                BufferedReader errorReader = null;
                try {
                    errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // ignore
                } finally {
                    try {
                        errorReader.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }).start();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                operateProcess.destroyForcibly();
            } catch (Exception e) {
                // ignore
            }
        }
        return 0;
    }

    /**
     * @param home
     */
    private static void buildAdminToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying admin start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/admin");
            File destDir = new File(home + "/webroot/runtime/admin");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", ".vscode", "dist", ".idea"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDirFile = new File(home + "/webroot/runtime/admin/src/addon");
            if (addonDirFile.exists()) {
                FileUtils.cleanDirectory(addonDirFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying admin ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    private static void buildH5ToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying uni-app start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/uni-app");
            File destDir = new File(home + "webroot/runtime/uni-app");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", "unpackage", "dist"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDirFile = new File(home + "/webroot/runtime/uni-app/src/addon");
            if (addonDirFile.exists()) {
                for (File subAddonDirFile : addonDirFile.listFiles()) {
                    if ("end.vue".equals(subAddonDirFile.getName()) || "components".equals(subAddonDirFile.getName())) {
                        continue;
                    }
                    FileUtils.deleteDirectory(subAddonDirFile);
                }
            }
            // 处理pages.json
            File pages = new File(home + "/webroot/runtime/uni-app/src/pages.json");
            if (pages.exists()) {
                String content = FileUtils.readFileToString(pages, "UTF-8");
                Pattern pattern = Pattern.compile("// \\{\\{ PAGE_BEGAIN \\}\\}[\\s\\S]*?// \\{\\{ PAGE_END \\}\\}");
                Matcher matcher = pattern.matcher(content);
                content = matcher.replaceAll("// {{ PAGE_BEGAIN }}\n// {{ PAGE_END }}");
                FileUtils.writeStringToFile(pages, content, "UTF-8");
            }
            // 处理 diy component 文件
            File diyComponentsFile = new File(home + "/webroot/resource/static/tmpl/diy-group.tpl");
            File fiexdComponentsFile = new File(home + "/webroot/resource/static/tmpl/fixed-group.tpl");
            String diyGroupContent = FileUtils.readFileToString(diyComponentsFile, "UTF-8").replace("{{ component }}", "").replace("{{ componentImport }}", "");
            String fixedGroupContent = FileUtils.readFileToString(fiexdComponentsFile, "UTF-8").replace("{{ component }}", "").replace("{{ componentImport }}", "");
            FileUtils.writeStringToFile(new File(home + "/webroot/runtime/uni-app/src/addon/components/diy/group/index.vue"), diyGroupContent, "UTF-8");
            FileUtils.writeStringToFile(new File(home + "/webroot/runtime/uni-app/src/addon/components/fixed/group/index.vue"), fixedGroupContent, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying uni-app ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * @param home
     */
    private static void buildWebToRuntime(String home) {
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying web start ->");
        System.out.println("[INFO] ------------------------------------------------------------------------");
        try {
            File srcDir = new File(home + "/web");
            File destDir = new File(home + "webroot/runtime/web");
            if (destDir.exists()) {
                FileUtils.deleteDirectory(destDir);
            }

            String[] exclusionDir = {"node_modules", ".output", "nuxt", "dist"};
            FileUtils.copyDirectory(srcDir, destDir, createExclusionFilter(exclusionDir));

            // 移除插件
            File addonDirFile = new File(home + "/webroot/runtime/web/src/addon");
            if (addonDirFile.exists()) {
                FileUtils.cleanDirectory(addonDirFile);
            }
        } catch (Exception e) {
            System.err.println("error:" + e.getMessage());
        }
        System.out.println("[INFO] ------------------------------------------------------------------------");
        System.out.println("[INFO] copying web ended <-");
        System.out.println("[INFO] ------------------------------------------------------------------------");
    }

    /**
     * 排除目录
     * @param dirs
     * @return
     */
    private static IOFileFilter createExclusionFilter(String[] dirs) {
        // 定义要排除的目录
        List<String> excludedDirs = Arrays.asList(dirs);

        // 创建一个过滤器来排除特定子目录
        IOFileFilter excludeSubDirsFilter = FileFilterUtils.trueFileFilter(); // 初始化为 false，表示过滤所有文件或目录

        // 循环创建过滤器
        for (String dirName : excludedDirs) {
            IOFileFilter tempFilter = FileFilterUtils.nameFileFilter(dirName);
            excludeSubDirsFilter = FileFilterUtils.and(excludeSubDirsFilter, FileFilterUtils.notFileFilter(tempFilter));
        }

        return excludeSubDirsFilter;
    }
}
