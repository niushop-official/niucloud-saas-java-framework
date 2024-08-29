#!/bin/sh
#  start启动
BootAppName=jar/web-app-boot-exec.jar
WebAppName=web-app-exec.jar

# JVM参数
JVM_OPTS="-Dname=$BootAppName -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC"

# 启动
nohup java $JVM_OPTS -jar $BootAppName > /dev/null 2>&1 &