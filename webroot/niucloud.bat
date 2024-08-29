chcp 65001
@echo off

IF NOT "%1"=="" GOTO start

rem Jar目录
set BootAppName=jar/web-app-boot-exec.jar
set WebAppName=web-app-exec.jar

rem JVM参数
set JVM_OPTS="-Dname=%BootAppName% -Duser.timezone=Asia/Shanghai -Xms1024m -Xmx2048m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC"

ECHO.
	ECHO.  [1] 启动 %BootAppName%
	ECHO.  [2] 关闭 %BootAppName%
	ECHO.  [3] 重启 %BootAppName%
	ECHO.  [4] 启动状态 %BootAppName%
	ECHO.  [5] 退 出
ECHO.

ECHO.请输入选择操作的项目序号:
set /p ID=
	IF "%id%"=="1" GOTO:start
	IF "%id%"=="2" GOTO:stop
	IF "%id%"=="3" GOTO:restart
	IF "%id%"=="4" GOTO:state
	IF "%id%"=="5" EXIT
PAUSE

:: 启动
:start
java %JVM_OPTS% -jar %BootAppName%
timeout /T 5
goto:eof

:: 停止
:stop
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %BootAppName%`) do (
    set boot_pid=%%a
)
taskkill /f /pid %boot_pid%
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %WebAppName%`) do (
    set web_pid=%%a
)
taskkill /f /pid %web_pid%
goto:eof

:: 重启
:restart
    call :stop
    call :start
goto:eof

:: 状态
:state
echo ----------------------------------------------------------------
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %BootAppName%`) do (
    echo %%a  %%b
)
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %WebAppName%`) do (
    echo %%a  %%b
)
goto:eof
