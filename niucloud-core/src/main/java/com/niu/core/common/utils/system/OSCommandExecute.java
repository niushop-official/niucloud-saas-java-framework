package com.niu.core.common.utils.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * CMD命令执行
 */
public class OSCommandExecute {

    public static class CommandResult {

        private boolean success = false;
        private List<String> messageList = new ArrayList<>();

        public CommandResult() {
        }

        public CommandResult(boolean success) {
            this.success = success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }

        public void appendContent(String message) {
            messageList.add(message);
        }

        public String getLine(int line) {
            if (line > 0 && line <= messageList.size()) {
                return messageList.get(line + 1);
            }
            return "";
        }

        public String getMessage() {
            StringBuffer stringBuffer = new StringBuffer();
            this.messageList.stream().forEach(m -> stringBuffer.append(stringBuffer));
            return stringBuffer.toString();
        }

    }

    /**
     * @param commandList
     * @return
     */
    public static CommandResult execWithCommandArray(List<String> commandList) {
        CommandResult commandResult = new CommandResult();
        Process process = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(commandList);
            process = builder.start();
            inputStreamReader = new InputStreamReader(process.getInputStream(), "GBK");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                commandResult.appendContent(line + System.getProperty("line.separator"));
            }
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                commandResult.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return commandResult;
    }

    /**
     * @param command
     * @return
     */
    public static CommandResult execWithCommandLine(String command) {
        CommandResult commandResult = new CommandResult();
        Process process = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(command);
            inputStreamReader = new InputStreamReader(process.getInputStream(), "GBK");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                commandResult.appendContent(line + System.getProperty("line.separator"));
            }
            process.waitFor();
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                commandResult.setSuccess(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return commandResult;
    }

    /**
     * @param command
     * @return
     */
    public static void exec(String command) {
        Process process = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(command);
            inputStreamReader = new InputStreamReader(process.getInputStream(), "GBK");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            int exitValue = process.exitValue();
            System.out.println("exitValue=" + exitValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * @param command
     * @return
     */
    public static void execWithBuilder(String command) {
        Process process = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();
            inputStreamReader = new InputStreamReader(process.getInputStream(), "GBK");
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static void main(String[] args) {
        String command = "D:/Apps/Maven/apache-maven-3.5.0/bin/mvn.cmd -version";
        CommandResult commandResult = execWithCommandLine(command);
        System.out.println(commandResult.isSuccess());
        System.out.println(commandResult.getMessage());
        List<String> commandArray = new ArrayList<>();
        commandArray.add("D:/Apps/Maven/apache-maven-3.5.0/bin/mvn.cmd");
        commandArray.add("-version");
        CommandResult commandResult2 = execWithCommandArray(commandArray);
        System.out.println(commandResult2.isSuccess());
        System.out.println(commandResult2.getMessage());
    }

}
