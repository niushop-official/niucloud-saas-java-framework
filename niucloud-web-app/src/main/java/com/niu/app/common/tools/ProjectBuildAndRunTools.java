package com.niu.app.common.tools;

import com.google.common.io.Files;
import com.niu.app.common.utils.OSCommandExecute;
import com.niu.app.common.utils.OSEnvironmentUtils;

import java.io.File;
import java.io.IOException;

public class ProjectBuildAndRunTools {

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
        System.out.println("Java环境变量：" + OSEnvironmentUtils.getJavaEnv());
        System.out.println("当前工作路径：" + userDir);
        System.out.println("Maven环境变量：" + OSEnvironmentUtils.getMavenEnv());
        System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
        if (true) {
            String mvnCommand = OSEnvironmentUtils.getMvnCommand();
            String pomXML = userDir + "\\pom.xml";
            StringBuilder mvnExecCommand = new StringBuilder();
            mvnExecCommand.append(mvnCommand).append(" ");
            mvnExecCommand.append("-f").append(" ").append(pomXML).append(" ");
            mvnExecCommand.append("clean package");
            OSCommandExecute.exec(mvnExecCommand.toString());
        }
        if (true) {
            System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
            String bootJarSrc = userDir + "\\niucloud-boot\\target\\web-app-boot-exec.jar";
            String bootJarTarget = userDir + "\\webroot\\jar\\web-app-boot-exec.jar";
            try {
                Files.copy(new File(bootJarSrc), new File(bootJarTarget));
                System.out.println("已复制文件：" + bootJarTarget);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
            String appJarSrc = userDir + "\\niucloud-web-app\\target\\web-app-exec.jar";
            String appJarTarget = userDir + "\\webroot\\jar\\web-app-exec.jar";
            try {
                Files.copy(new File(appJarSrc), new File(appJarTarget));
                System.out.println("已复制文件：" + appJarTarget);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
        }
        if (false) {
            String javaCommand = OSEnvironmentUtils.getJavaEnv() + "/bin/java.exe";
            if (true) {
                OSCommandExecute.winKillProcessByJavaAppName(OSEnvironmentUtils.getJavaEnv(), "web-app-exec.jar");
                OSCommandExecute.winKillProcessByJavaAppName(OSEnvironmentUtils.getJavaEnv(), "web-app-boot-exec.jar");
                String bootJarExec = userDir + "\\webroot\\jar\\web-app-boot-exec.jar";
                StringBuilder javaRunBootCommand = new StringBuilder();
                javaRunBootCommand.append(javaCommand).append(" ");
                javaRunBootCommand.append("-jar").append(" ");
                javaRunBootCommand.append(bootJarExec);
                System.out.println("启动应用：" + javaRunBootCommand.toString());
                OSCommandExecute.exec(javaRunBootCommand.toString());
            } else {
                OSCommandExecute.winKillProcessByJavaAppName(OSEnvironmentUtils.getJavaEnv(), "web-app-exec.jar");
                String webappJarExec = userDir + "\\webroot\\jar\\web-app-exec.jar";
                StringBuilder javaRunAppCommand = new StringBuilder();
                javaRunAppCommand.append(javaCommand).append(" ");
                javaRunAppCommand.append("-jar").append(" ");
                javaRunAppCommand.append(webappJarExec);
                System.out.println("启动应用：" + javaRunAppCommand.toString());
                OSCommandExecute.exec(javaRunAppCommand.toString());
            }
        }

    }

}
