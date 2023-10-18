#!/bin/sh
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
selectPort=
function getRunJarPort(){
  read -p "请输入Jar包运行的端口(端口范围8000-65535,直接确定则使用8080端口)：" jarPort

  #判断端口是否为空，空则使用8080
  if [ -n "$jarPort" ];then
    resultPort=$jarPort
    echo "当前用户选择的端口为：$resultPort"
    #判断用户输入是否为端口号
    echo "$resultPort"|[ -n "`sed -n '/^[1-9][0-9]\{0,4\}$/p'`" ] && result="1" || result="0"
    case $result in
    0)
      echo "端口号格式不正确，请重新输入。"
      getRunJarPort
    ;;
    1)
      if (( $resultPort >= 8000 && $resultPort <= 65535 ));then
        echo "端口号正确"
      else
        echo "请输入8000-65535范围内端口号"
        getRunJarPort
      fi;
    ;;
    *)
      echo "端口号格式不正确，请重新输入。"
      getRunJarPort
    ;;
    esac
  else
    resultPort="8080"
    echo "用户未输入端口号，则使用$resultPort端口"
  fi;
  selectPort=$resultPort
  sed -i 's/^server.port=[0-9]\{1,5\}/server.port='$selectPort'/g'  application.properties
}



#镜像导入
function loadDockerImages()
{
  echo "开始导入镜像。" 
  docker pull prom/alertmanager:v0.22.2
  docker pull google/cadvisor:v0.33.0
  docker pull citamon/agent-cita-exporter:20.2.2
  docker pull grafana/grafana:8.0.3
  docker pull prom/node-exporter:v1.1.2
  docker pull ncabatoff/process-exporter:0.7.5
  docker pull prom/prometheus:v2.28.0
  docker pull kbudde/rabbitmq-exporter:v1.0.0-RC9
  docker pull cita/cita-run:ubuntu-18.04-20190419;
  echo "镜像导入完成。"
}

#启动jar函数
function createUser()
{
  
  #创建用户
  useradd userClient
  echo "user@1234Client" | passwd --stdin userClient 
  echo "当前系统为运行jar创建用户 userClient 密码为 user@1234Client"
  groupadd docker
  usermod -G docker userClient
  setfacl -m u:userClient:rwx /var/run/docker.sock

  #line=`wc -l /etc/ssh/sshd_config | awk '{print $1}'`
  #sed -i $line'a AllowUsers userClient' /etc/ssh/sshd_config

  #cp -R images /home/userClient/

}

#telnet检查方法
function checkTelnetCommand(){
  rpmResult=`rpm -qa |grep telnet`
  echo "$rpmResult"
  if [  -n "$rpmResult" ];then
    rpmContains=$(echo $rpmResult | grep "telnet")
    if [[ "" != "$rpmContains"  ]];then
      echo "已安装telnet"
      return;
    fi;
  fi;

  dpkgResult=`dpkg -l | grep telnet`
  echo "$dpkgResult"
  if [  -n "$dpkgResult" ];then
    dpkgContains=$(echo $dpkgResult | grep "telnet")
    if [[ "" != "$dpkgContains"  ]];then
      echo "已安装telnet"
      return;
    fi;
  fi;

  yumResult=`yum list installed | grep telnet`
  echo "$yumResult"
  if [  -n "$yumResult" ];then
    yumContains=$(echo $yumResult | grep "telnet")
    if [[ "" != "$yumContains"  ]];then
      echo "已安装telnet"
      return;
    fi;
  fi;

  #执行到此处说明没有安装telnet，给与提示，结束进程
}


#启动jar
function startJar(){
  chown -R userClient:userClient /home/userClient

 #检查8080端口是否被占用
  #usePort=`netstat -nptl | grep 8080 | awk '{print $7}' | cut -d '' -f1-1`
  usePort=`netstat -nptl | grep $selectPort | awk '{print $7}' | cut -d '' -f1-1`
  if [ -n "$usePort" ]; then
    echo "当前系统 $selectPort 端口被占用，无法继续后续流程。" && exit 1
  fi
  
  cp EasyBaaS-1.0.0.jar /home/userClient/EasyBaaS-1.0.0.jar
  
  cp -R shell /home/userClient/shell
  cp -R files /home/userClient/files
  chown -R userClient:userClient /home/userClient/shell
  chown -R userClient:userClient /home/userClient/files

  chmod -R 777 /home/userClient/shell
  chmod -R 777 /home/userClient/files

  cp application.properties /home/userClient/application.properties
    #修改jar属主属组
  chown userClient:userClient /home/userClient/EasyBaaS-1.0.0.jar
  chown userClient:userClient /home/userClient/application.properties
#指定配置文件，使用外联配置启动jar
  su - userClient<<EOF
mkdir log
nohup java -jar -Dspring.config.location=application.properties EasyBaaS-1.0.0.jar  >log/log.txt 2>&1 &
echo "已后台启动客户端，请手动查看 /home/userClient/log 内日志。" 
exit;
EOF
  #开启端口
  result=`firewall-cmd --zone=public --add-port=$selectPort/tcp --permanent`

  echo "当前系统已开启 $selectPort 端口。"

  result3000=`firewall-cmd --zone=public --add-port=3000/tcp --permanent`

  #配置端口后，需要重启防火墙
  reloadResult=`firewall-cmd --reload`
  echo "当前系统已重启防火墙"
}

#判断是否安装了docker
function isInstallDocker()
{
  if [ -e "/etc/systemd/system/docker.service" ];then
    return 1
  else
    return 0
  fi
}

#判断docker版本
function validataDockerVersion()
{
  dockerInfo=`docker -v | awk '{print $3}' | cut -d ',' -f1-1`
  DOCKER_VERSION="20.10.1"
  if [ "$dockerInfo" = "$DOCKER_VERSION" ];then
    return 1
  else
    return 0
  fi
}

#Linux基础信息检查
function basicValida(){
  #linux系统版本检查
  #linuxVersion=`cat /etc/redhat-release |cut -d' ' -f1-2`
  #if [[ $linuxVersion != 'Red Hat' ]];then
  #    echo "当前系统不是Red Hat。" && exit 0
  #else
  #    echo "当前系统为：" $linuxVersion。
  #fi

  #linux系统内核检查
  unamer=`uname -r |cut -d'.' -f1-2`
  #unamerResult=$(expr "$unamer >= 3.10"|bc)
  unamerResult=`expr $unamer \>= 3.10 `
  if [[ $unamerResult == '0' ]];then
      echo "当前系统内核版本不正确。" && exit 1
  else
      echo "当前系统内核版本为：" $unamer。
  fi
}

#安装Java
function installJava(){

  basicValida  

  #cpu检查
  cpuInfo=`cat /proc/cpuinfo | grep "processor" | wc -l`
  
  if [[ $cpuInfo -lt '2' ]];then
  #if [[ $cpuInfo -le '1' ]];then
      echo "当前系统CPU不满足条件。" && exit 1
  else
      echo "当前系统CPU为：" $cpuInfo核。
  fi
  
  cpuMem=`free | grep "Mem:" |awk '{print $2}'`
  
  if [[ $cpuMem -le '3670016' ]];then
  #if [[ $cpuMem -le '1024' ]];then
      echo "当前系统内存不满足条件。" && exit 1
  else
      echo "当前系统内存为：" $cpuMem。
  fi
  
  #安装jdk剩余空间检查
  freeStorage=`df -h /usr | grep "G" | awk '{print $4}' | cut -d 'G' -f1-1 | cut -d '.' -f1-1`
  freeStorageResult=`expr $freeStorage \< 3`
  if [[ $freeStorageResult == '1' ]];then
      echo "当前系统 /usr 下可用空间不满足条件。" && exit 1
  else
      echo "当前系统 /usr 下可用空间为：" $freeStorage G。
  fi
  
  #系统挂载磁盘的剩余空间
  mountPath=$1
  if [[ $mountPath == '' ]];then
      mountPath=`pwd`
  fi
  echo "当前系统存储路径为：$mountPath"
  freeStorage=`df -h $mountPath | grep "G" | awk '{print $4}' | cut -d 'G' -f1-1 | cut -d '.' -f1-1`
  #echo "当前系统 $mountPath 下可用空间为 $freeStorage。"
  freeStorageResult=`expr $freeStorage \< 1`
  #echo "$freeStorageResult"
  if [[ $freeStorageResult == '1' ]];then
  #if [[ $freeStorage -le '3' ]];then
      echo "当前系统 $mountPath 下可用空间不满足条件。" && exit 1
  else
      echo "当前系统 $mountPath 下可用空间为：" $freeStorage G。
  fi
  
   
  #检查是否安装过jdk以及jdk版本
  jdkInfo=`java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | grep version`
  if [ -n "$jdkInfo" ]; then
    echo "当前系统已经安装JDK，开始判断JDK版本。"
    jdkVersion=`java -version 2>&1 | sed '1!d' | sed -e 's/"//g' | awk '{print $3}' | cut -d'.' -f1-2` 
    jdkVersionResult=`expr $jdkVersion \>= 1.8`
    if [[ $jdkVersionResult == '1' ]];then
      echo "当前系统安装JDK版本符合要求。"
    else
      echo "当前系统安装JDK版本过低，请安装1.8及以上版本JDK。"
    fi
  else
    echo "当前系统未安装JDK。"
	echo "开始安装JDK......"
  
    #开始安装jdk
    #1.解压压缩包
    tar -zxf jdk-8u161-linux-i586.tar.gz
    #2.移动解压后文件
    mv jdk1.8.0_161 /usr/jdk1.8.0_161
    #3.配置环境变量
    sed -i "$ a\ " /etc/profile
    sed -i "$ a\JAVA_HOME=/usr/jdk1.8.0_161" /etc/profile
    sed -i "$ a\PATH=\$JAVA_HOME/bin:\$PATH" /etc/profile
    sed -i "$ a\CLASSPATH=.:\$JAVA_HOME/lib/tools.jar:\$JAVA_HOME/lib/dt.jar" /etc/profile
    sed -i "$ a\export JAVA_HOME PATH CLASSPATH" /etc/profile
    #4.生效
    source /etc/profile
    echo "JDK安装完毕。"
    fi
}


#Docker环境安装部署
function installDockerEnvironment(){
  #setup
  tar zxf  docker-20.10.1.tgz && mv docker/* /usr/bin/ && rm -rf docker
   
  #systemd config
  cat >/etc/systemd/system/docker.service <<-EOF
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target
  
[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock
ExecReload=/bin/kill -s HUP $MAINPID
# Having non-zero Limit*s causes performance problems due to accounting overhead
# in the kernel. We recommend using cgroups to do container-local accounting.
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
# Uncomment TasksMax if your systemd version supports it.
# Only systemd 226 and above support this version.
#TasksMax=infinity
TimeoutStartSec=0
# set delegate yes so that systemd does not reset the cgroups of docker containers
Delegate=yes
# kill only the docker process, not all processes in the cgroup
KillMode=process
# restart the docker process if it exits prematurely
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s
  
[Install]
WantedBy=multi-user.target
EOF
   
  #start
  chmod +x /etc/systemd/system/docker.service

  firewall-cmd --zone=public --add-port=2375/tcp --permanent 
 
  #由于red hat 使用firewalld，需要先关闭防火墙，启动docker后再启动防火墙
  systemctl daemon-reload
  systemctl stop firewalld.service
  systemctl start docker
  systemctl enable docker.service
  systemctl start firewalld.service  
 
  ifconfig docker0 down
  brctl delbr docker0

  systemctl restart docker.service
 
  #testing
  systemctl status docker
  docker -v && echo "当前系统所需配置已全部完成。"
}

#安装Docker全流程
function installDocker(){

  basicValida  
 
  #系统挂载磁盘的剩余空间
  mountPath=$1
  if [[ $mountPath == '' ]];then
      mountPath=`pwd`
  fi
  echo "当前系统存储路径为：$mountPath"
  freeStorage=`df -h $mountPath | grep "G" | awk '{print $4}' | cut -d 'G' -f1-1 | cut -d '.' -f1-1`
  #freeStorageResult=$(expr "$freeStorage <= 100 "|bc)
  freeStorageResult=`expr $freeStorage \>= 1 `
  if [[ $freeStorageResult == '0' ]];then
      echo "当前系统 $mountPath 下可用空间不满足条件。" && exit 1
  else
      echo "当前系统 $mountPath 下可用空间为：" $freeStorage G。
  fi
  
  #检查2375端口是否被占用
  usePort=`netstat -nptl | grep 2375 | awk '{print $7}' | cut -d '' -f1-1`
  if [ -n "$usePort" ]; then
    #证明2375端口存在，需要判断是不是docker
    echo "当前系统2375端口被占用，需要判断是否为docker使用。"
  
    usePortProgram=`netstat -nptl | grep 2375 | awk '{print $7}' | cut -d '/' -f2`
    
    if [ "$usePortProgram" == "dockerd"  ];then
      echo "当前系统内占用2375端口的是docker。"
    else
      echo "当前系统内占用2375端口的不是docker。"  && exit 1
    fi
    
    echo "当前系统已经安装docker，开始判断docker版本。" 
    validataDockerVersion
    dockerVersionResult=$?
    if [ "$dockerVersionResult" = "1" ];then
      echo "当前系统安装docker版本符合要求。"
      loadDockerImages
    else
      echo "当前系统安装docker版本不符合推荐要求，可能存在风险。"
      loadDockerImages
    fi
  
  else
    #证明2375端口没有使用  
    isInstallDocker
    dockerFlag=$?
    if [ "$dockerFlag" == "1" ];then
      echo "当前系统docker已经安装，开始判断版本。"
      #docker配置文件存在，证明安装了docker，但是没有启动2375端口,判断版本是否正确
      validataDockerVersion
      dockerVersionResult=$?
      
      if [ "$dockerVersionResult" = "1" ];then
        echo "当前系统安装docker版本符合要求，修改配置文件开启2375端口"
        fileLine=`cat -n /etc/systemd/system/docker.service |grep 'ExecStart'|awk '{print $1}'`
        sed -i $fileLine's/$/& -H tcp:\/\/0.0.0.0:2375 -H unix:\/\/var\/run\/docker.sock/g' /etc/systemd/system/docker.service
        systemctl daemon-reload
        echo "当前系统2375端口已经配置完成。"
        systemctl stop firewalld.service
        echo "firewalld已经停止。"
        systemctl restart docker
        echo "docker完成重新启动。"
        systemctl enable docker.service
        systemctl start firewalld.service 
        echo "firewalld已经启动。"
        echo "当前系统所需配置已全部完成。"
        loadDockerImages
      else
        echo "当前系统安装docker版本不符合要求。" && exit 1
      fi
  
    else
    #docker配置文件不存在，需要安装docker
      echo "当前系统没有安装docker，开始安装docker。"
      installDockerEnvironment
      rcLocalFileLine=`cat -n /etc/rc.d/rc.local |grep 'touch'|awk '{print $1}'`
      sed -i $rcLocalFileLine'a systemctl stop firewalld.service\nsystemctl restart docker\nsystemctl start firewalld.service' /etc/rc.d/rc.local
      echo "当前系统防火墙以及docker启动策略修改完毕。"
      chmod +x /etc/rc.d/rc.local
      echo "当前系统自启文件执行权限配置完毕。"
      loadDockerImages
    fi
  fi
}

#function openDockerPortByHost(){
#  sed -i "$ a\{" /etc/docker/daemon.json
#  sed -i "$ a\  \"hosts\": [\"tcp://0.0.0.0:2375\", \"unix:///var/run/docker.sock\"]" /etc/docker/daemon.json
#  sed -i "$ a\}" /etc/docker/daemon.json

  #防火墙设置
#  firewall-cmd --zone=public --add-port=2375/tcp --permanent
#  firewall-cmd --reload
#}

function openDockerPort(){
  line=`awk '{print NR}' /etc/docker/daemon.json | tail -n1`
  if [ ! -n "$line" ];then
    echo "FILE IS NULL"
    echo ' ' > /etc/docker/daemon.json
    openDockerPortByHost
    sed -i '1d' /etc/docker/daemon.json
  else
    echo "IS NOT NULL"
    openDockerPortByHost
  fi
}


#检查2375端口
function checkDockerPort
{
  usePort=`netstat -nptl | grep 2375 | awk '{print $7}' | cut -d '' -f1-1`
  if [ -n "$usePort" ]; then
    #证明2375端口存在，需要判断是不是docker
    echo "当前系统2375端口被占用，需要判断是否为docker使用。"

    usePortProgram=`netstat -nptl | grep 2375 | awk '{print $7}' | cut -d '/' -f2`

    if [ "$usePortProgram" == "dockerd"  ];then
      echo "当前系统内占用2375端口的是docker。"
    else
      echo "当前系统内占用2375端口的不是docker。"  && exit 1
    fi
  else
    echo "请开始docker2375端口后在继续进行" && exit 1
  fi
}



#选择操作主入口
function selectOption(){
  echo "* * * * * * * * * * * * * * * * * * * * * * * * * *"
  echo -e "* * * * * * *请参考 \033[47;32m使用文档\033[0m 进行使用 * * * * * * *"
  echo -e "* \033[47;32m请输入需要部署的选项：\033[0m                          *"
  echo -e "* \033[47;32m1.部署docker以及java运行环境\033[0m                    *"
  echo -e "* \033[47;32m2.部署docker环境\033[0m                                *"
  echo -e "* \033[47;32m3.部署java环境\033[0m                                  *"
  echo -e "* \033[47;32m4.运行客户端\033[0m                                    *"
  echo -e "* \033[47;32m5.创建用户\033[0m                                      *"
  echo -e "* \033[47;32m6.退出\033[0m                                          *"
  echo "* * * * * * * * * * * * * * * * * * * * * * * * * *"  
  read -p "请选择：" options 
  echo "---------------------------------------"
  case $options in 
  1)
    installDocker $1
    checkTelnetCommand
    echo "---------------------------------------"
    installJava $1
    echo "---------------------------------------"
	loadDockerImages
    createUser
    echo "---------------------------------------"
    selectOption  
  ;;
  2)
	installDocker $1
	checkTelnetCommand
	echo "---------------------------------------"
	loadDockerImages
	createUser
	selectOption  
  ;;
  3)
   checkTelnetCommand
   echo "---------------------------------------"
   installJava $1
   echo "---------------------------------------"
   createUser
   echo "---------------------------------------"
   selectOption  
  ;;
  4)
    checkTelnetCommand
    echo "---------------------------------------"
    checkDockerPort
    echo "---------------------------------------"
    getRunJarPort
    echo "---------------------------------------"
    createUser
    startJar
  ;;
  5)
    echo "---------------------------------------"
    createUser
  ;;
  6)
    exit 0
  ;;
  *)
    echo "输入有误,请重新选择"
    echo "-----------------------------"
    selectOption
  ;;
  esac
}

selectOption

