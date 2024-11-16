#!/bin/bash

# 启动的jar
BootAppName=jar/web-app-boot-exec.jar
WebAppName=web-app-exec.jar

# JVM参数
JVM_OPTS="-Dname=$BootAppName -Duser.timezone=Asia/Shanghai -Xms256m -Xmx256m -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -XX:+HeapDumpOnOutOfMemoryError"

# JAVA_HOME
# JAVA_HOME="/home/tools/jdk1.8.0_421"

# 启动服务
start(){
    if [ -e "${JAVA_HOME}/bin/java" ]; then
        echo "JAVA_HOME: ${JAVA_HOME}"
        nohup $JAVA_HOME/bin/java $JVM_OPTS -jar $BootAppName > /dev/null 2>&1 &
        echo $! > $(pwd)/jar/Temp/process.pid
    else
        nohup java $JVM_OPTS -jar $BootAppName > /dev/null 2>&1 &
        echo $! > $(pwd)/jar/Temp/process.pid        
    fi
    echo "niucloud service is starting."
}

# 停止服务
stop(){
    if [ -e "$(pwd)/jar/Temp/process.pid" ]; then
        processArray=$(ps -ef | grep `cat $(pwd)/jar/Temp/process.pid` | grep -v "grep" | awk '{print $2}')
        for process in $processArray
        do
            kill -9 $process
        done
        rm -rf $(pwd)/jar/Temp/process.pid
        echo "niucloud service is stopped."
    else
        echo "niucloud service not starting."
    fi
}

# 重启服务
restart(){
    stop
    start
}

# 服务状态
status(){
    echo "----------------------------------------------------------------"
    if [ -e "$(pwd)/jar/Temp/process.pid" ]; then
        ps -ef | grep `cat $(pwd)/jar/Temp/process.pid` | grep -v "grep"
    else
        echo "niucloud service not starting."
    fi
}

# 根据输入参数执行
case "$1" in 
    "start")
        start
        ;;
     "stop")
        stop
        ;;
     "restart")
        restart
        ;;
     "status")
        status
        ;;
      *)
        start
        ;;
esac


