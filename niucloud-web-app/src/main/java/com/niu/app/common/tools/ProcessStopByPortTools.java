package com.niu.app.common.tools;

import com.niu.app.common.utils.OSCommandExecute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessStopByPortTools {

    public static void main(String[] args) {
        killProcessByPorts(8080, 8081);
    }

    /**
     * @param closePorts
     */
    private static void killProcessByPorts(long... closePorts) {
        if (closePorts != null) {
            Arrays.stream(closePorts).forEach(port -> killProcessByPort(port));
        }
    }

    /**
     * @param closePort
     */
    private static void killProcessByPort(long closePort) {
        System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
        OSCommandExecute.CommandResult commandResult = OSCommandExecute.execWithCommandLine("netstat -ano");
        List<String> lineList = new ArrayList<>();
        String bindHost = "0.0.0.0:" + closePort;
        for (String line : commandResult.getMessageLine()) {
            if (line.contains("TCP") && line.contains(bindHost)) {
                lineList.add(line);
            }
        }
        if (lineList.size() <= 0) {
            System.out.println("服务端口:" + bindHost + ",没有启动.");
        } else {
            for (String line : lineList) {
                line = line.trim();
                line = line.replaceAll("\\s+", "\\$");
                String[] subStrs = line.split("\\$");
                String pid = subStrs[subStrs.length - 1];
                System.out.println("服务端口:" + bindHost + ",被进程: " + pid + " 占用,已执行停止.");
                OSCommandExecute.exec("taskkill -f -pid " + pid);
            }
        }
        System.out.println("√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√√");
    }

}
