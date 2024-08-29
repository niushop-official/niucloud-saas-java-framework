package com.niu.core.common.utils.system;

import cn.hutool.core.util.StrUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class LocalMavenTools {

    /**
     * @param filePath
     * @return
     */
    public static boolean existPomXmlFile(String filePath) {
        File pomXmlFile = new File(filePath);
        if (pomXmlFile.isFile() && pomXmlFile.exists()) {
            return true;
        }
        return false;
    }

    /**
     * @param filePath
     * @return
     */
    public static String readPomXmlEntryFromJar(String filePath) {
        File pomJarFile = new File(filePath);
        if (pomJarFile.isFile() && pomJarFile.getName().toLowerCase().endsWith(".jar")) {
            try {
                JarFile jarFile = new JarFile(pomJarFile);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith("pom.xml")) {
                        return entry.getName();
                    }
                    // System.out.println(entry.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * @param jarPath
     * @param pomEntry
     * @return
     */
    public static Map<String, String> readPomXmlCoordinate(String jarPath, String pomEntry) {
        Map<String, String> coordinateMap = new HashMap<>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
            JarEntry entry = jarFile.getJarEntry(pomEntry);
            InputStream inputStream = jarFile.getInputStream(entry);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model model = mavenReader.read(inputStream);
            String groupId = model.getGroupId();
            if (StrUtil.isEmpty(groupId)) {
                Parent parent = model.getParent();
                if (parent != null) {
                    groupId = parent.getGroupId();
                }
            }
            String artifactId = model.getArtifactId();
            String version = model.getVersion();
            coordinateMap.put("groupId", groupId);
            coordinateMap.put("artifactId", artifactId);
            coordinateMap.put("version", version);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("读取Jar中的pom.xml错误.");
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return coordinateMap;
    }

    /**
     * @param jarPath
     * @param coordinateMap
     */
    public static void addLocalRepository(String jarPath, Map<String, String> coordinateMap) {
        String addGroupId = coordinateMap.get("groupId");
        String addArtifactId = coordinateMap.get("artifactId");
        String addVersion = coordinateMap.get("version");
        StringBuilder mvnCommand = new StringBuilder();
        mvnCommand.append(OSEnvironmentUtils.getMvnCommand()).append(" ");
        mvnCommand.append("install:install-file -Dpackaging=jar").append(" ");
        mvnCommand.append("-Dfile=").append(jarPath).append(" ");
        mvnCommand.append("-DgroupId=").append(addGroupId).append(" ");
        mvnCommand.append("-DartifactId=").append(addArtifactId).append(" ");
        mvnCommand.append("-Dversion=").append(addVersion);
        OSCommandExecute.exec(mvnCommand.toString());
    }

    /**
     * 添加模块
     *
     * @param addonSrcHome
     * @param addonName
     */
    public static void addPomModuleNode(String addonSrcHome, String addonName) {
        String pomFilePath = addonSrcHome + "/pom.xml";
        FileWriter fileWriter = null;
        try {
            Reader reader = new FileReader(pomFilePath);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model model = mavenReader.read(reader);
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                reader = null;
            }
            // 修改模块名
            List<String> modules = model.getModules();
            if (modules == null) {
                modules = new ArrayList<>();
            }
            for (String module : modules) {
                if (module.equals(addonName)) {
                    return;
                }
            }
            modules.add(addonName);
            model.setModules(modules);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            fileWriter = new FileWriter(pomFilePath);
            writer.write(fileWriter, model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更改maven依赖错误.");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param addonSrcHome
     * @param coordinateMap
     */
    public static void addPomDependencyNode(String addonSrcHome, Map<String, String> coordinateMap) {
        String addGroupId = coordinateMap.get("groupId");
        String addArtifactId = coordinateMap.get("artifactId");
        String addVersion = coordinateMap.get("version");
        String pomFilePath = addonSrcHome + "/pom.xml";
        FileWriter fileWriter = null;
        try {
            Reader reader = new FileReader(pomFilePath);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model model = mavenReader.read(reader);
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<Dependency> dependencyList = model.getDependencies();
            if (dependencyList != null && dependencyList.size() > 0) {
                boolean flag = false;
                int index = 0;
                for (int i = 0; i < dependencyList.size(); i++) {
                    Dependency dependency = dependencyList.get(i);
                    String groupId = dependency.getGroupId();
                    String artifactId = dependency.getArtifactId();
                    if (groupId.equals(addGroupId) && addArtifactId.equals(artifactId)) {
                        flag = true;
                        index = i;
                    }
                }
                if (flag) {
                    dependencyList.remove(index);
                }
            }
            Dependency newDependency = new Dependency();
            newDependency.setGroupId(addGroupId);
            newDependency.setArtifactId(addArtifactId);
            newDependency.setVersion(addVersion);
            newDependency.setScope("provided");
            dependencyList.add(newDependency);
            model.setDependencies(dependencyList);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            fileWriter = new FileWriter(pomFilePath);
            writer.write(fileWriter, model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更改maven依赖错误.");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 移除模块
     *
     * @param addonSrcHome
     * @param addonName
     */
    public static void removePomModuleNode(String addonSrcHome, String addonName) {
        String pomFilePath = addonSrcHome + "/pom.xml";
        FileWriter fileWriter = null;
        try {
            Reader reader = new FileReader(pomFilePath);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model model = mavenReader.read(reader);
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                reader = null;
            }
            // 修改模块名
            List<String> modules = model.getModules();
            modules.remove(addonName);
            model.setModules(modules);
            // 写到文件
            MavenXpp3Writer writer = new MavenXpp3Writer();
            fileWriter = new FileWriter(pomFilePath);
            writer.write(fileWriter, model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更改maven依赖错误.");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param addonSrcHome
     * @param coordinateMap
     */
    public static void removePomDependencyNode(String addonSrcHome, Map<String, String> coordinateMap) {
        String addGroupId = coordinateMap.get("groupId");
        String addArtifactId = coordinateMap.get("artifactId");
        String addVersion = coordinateMap.get("version");
        String pomFilePath = addonSrcHome + "/pom.xml";
        FileWriter fileWriter = null;
        try {
            Reader reader = new FileReader(pomFilePath);
            MavenXpp3Reader mavenReader = new MavenXpp3Reader();
            Model model = mavenReader.read(reader);
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<Dependency> dependencyList = model.getDependencies();
            if (dependencyList != null && dependencyList.size() > 0) {
                boolean flag = false;
                int index = 0;
                for (int i = 0; i < dependencyList.size(); i++) {
                    Dependency dependency = dependencyList.get(i);
                    String groupId = dependency.getGroupId();
                    String artifactId = dependency.getArtifactId();
                    if (false) {
                        if (groupId.equals(addGroupId) && addArtifactId.equals(artifactId)) {
                            flag = true;
                            index = i;
                        }
                    } else {
                        if (addArtifactId.equals(artifactId)) {
                            flag = true;
                            index = i;
                        }
                    }
                }
                if (flag) {
                    dependencyList.remove(index);
                } else {
                    // 不需要移除
                    return;
                }
            }
            model.setDependencies(dependencyList);
            MavenXpp3Writer writer = new MavenXpp3Writer();
            fileWriter = new FileWriter(pomFilePath);
            writer.write(fileWriter, model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更改maven依赖错误.");
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
