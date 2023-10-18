# EasyBaaS

## 项目介绍
EasyBaaS是一个基于Linux环境的区块链节点管理工具，提供一键部署节点、实时监控及管理运维等功能，从搭建节点、启停节点、节点日志、监控告警等方面提升运维效率，屏蔽了区块链底层运维的复杂性，降低了区块链的使用门槛。

## 架构

* 应用功能架构

![客户端](https://github.com/china-merchants-bank/EasyBaaS/blob/master/images/EasyBaaS.png)

## 安装指南

### 1.1 最低环境配置
- Linux 内核版本 3.10+，2C4G，空间 100G(建议值)
- Java 环境版本 jdk1.8
- NodeJs 环境版本 10.0.0

### 1.2 环境准备

下载[jdk](https://www.oracle.com/hk/java/technologies/javase/javase8-archive-downloads.html )和[docker](https://download.docker.com/linux/static/stable/x86_64/ )安装包，root权限执行项目shell目录下的install-client.sh脚本文件安装docker和java环境。

```shell
# 运行脚本，选择选项1安装docker和java环境
source install-client.sh
```

### 1.3 镜像制作
例如：基于开源CITA 0.24.0版本制作镜像

1. 下载CITA 0.24.0版本和CITA客户端

   ```shell
   # CITA 0.24.0 版本下载
   wget https://github.com/cryptape/cita/releases/download/v0.24.0/cita_secp256k1_sha3.tar.gz
   # 解压
   tar zxvf cita_secp256k1_sha3.tar.gz
   # CITA 客户端下载
   wget https://github.com/cryptape/cita-cli/releases/download/0.19.6/cita-cli-x86_64-musl-tls-0.19.6.tar.gz
   # 解压
   tar zxvf cita-cli-x86_64-musl-tls-0.19.6.tar.gz
   ```

2. 拷贝文件

   - 将项目shell目录下的ClearCITANodeLogs.sh和BackupCITANodeLogs.sh脚本文件拷贝到解压后的cita_secp256k1_sha3目录下。
   - 将解压后的cita-cli拷贝到cita_secp256k1_sha3目录下。
   
3. 编写Dockerfile

   ```shell
   # 进入目录
   cd cita_secp256k1_sha3
   # 编写Dockerfile
   vim Dockerfile
   ```

   ```
   # 示例如下
   FROM cita/cita-run:ubuntu-18.04-20190419
   USER root
   RUN mkdir -p /home/cmb/cita/cmb-cita-runtime
   COPY ./bin /home/cmb/cita/cmb-cita-runtime/bin
   COPY ./scripts /home/cmb/cita/cmb-cita-runtime/scripts
   COPY ./resource /home/cmb/cita/cmb-cita-runtime/resource
   COPY ./cita-cli /home/cmb/cita/cmb-cita-runtime
   COPY ./ClearCITANodeLogs.sh /home/cmb/cita/cmb-cita-runtime/ClearCITANodeLogs.sh
   COPY ./BackupCITANodeLogs.sh /home/cmb/cita/cmb-cita-runtime/BackupCITANodeLogs.sh
   
   RUN mkdir -p /home/cmb/cita/cmb-cita-runtime/rabbitmq
   
   RUN cd /home/cmb/cita/cmb-cita-runtime/bin \
           && rabbitmq-plugins enable rabbitmq_management \
   	&& rabbitmqctl stop \
   	&& rm -rf /var/lib/rabbitmq/mnesia \
   	&& update-rc.d -f rabbitmq-server remove \
   	&& mv /usr/sbin/rabbitmqctl /usr/sbin/rabbitmqctl_dont_use \
   	&& mv /usr/sbin/rabbitmq-plugins /usr/sbin/rabbitmq-plugins_dont_use \
   	&& mv /usr/sbin/rabbitmq-server /usr/sbin/rabbitmq-server_dont_use \
   	&& sed -i 's/=\/usr\/sbin\//=\/usr\/lib\/rabbitmq\/bin\//' /etc/init.d/rabbitmq-server \
   	&& sed -i 's/--chuid rabbitmq/--chuid root:root/' /etc/init.d/rabbitmq-server \
   	&& sed -i 's/=\/var\/log\/rabbitmq/=\/home\/cmb\/cita\/cmb-cita-runtime\/rabbitmq\/log/' /etc/init.d/rabbitmq-server \
   	&& sed -i 's/=\/var\/run\/rabbitmq\/pid/=\/home\/cmb\/cita\/cmb-cita-runtime\/rabbitmq\/pid/' /etc/init.d/rabbitmq-server \
   	&& sed -i 's/sudo rabbitmq/rabbitmq/g' cita \
   	&& sed -i 's/sudo \/etc\/init.d\/rabbitmq-server/\/etc\/init.d\/rabbitmq-server/g' cita \
   	&& chmod -R a+rwx /home/cmb \
   	&& chmod -R a+rw /etc/rabbitmq \
   	&& chmod -R a+rw /etc/passwd
   
   ENV RABBITMQ_MNESIA_BASE=/home/cmb/cita/cmb-cita-runtime/rabbitmq/mnesia \
   	RABBITMQ_LOG_BASE=/home/cmb/cita/cmb-cita-runtime/rabbitmq/log \
   	RABBITMQ_CONFIG_FILE=/home/cmb/config/rabbitmq \
   	RABBITMQ_SERVER_ERL_ARGS="-setcookie rabbit" \
   	RABBITMQ_CTL_ERL_ARGS="-setcookie rabbit" \
   	PATH=/usr/lib/rabbitmq/bin:$PATH \
   	HOME=/home/cmb/cita/cmb-cita-runtime/rabbitmq
   
   RUN chmod 777 -R /home/cmb/cita/cmb-cita-runtime
   HEALTHCHECK --interval=5s --timeout=5s \
   CMD curl -sS 'http://localhost:1337' || exit 1
   WORKDIR /home/cmb/cita/cmb-cita-runtime
   
   ```

4. 生成镜像

   ```shell
   # 构建镜像
   docker build -t cita/cita_secp256k1_sha3:0.24.0 
   # 记录镜像id,使用一下命令，记录第三列的id
   docker images |grep cita_secp256k1_sha3 | grep 0.24.0
   ```

5. 修改配置文件

   - 生成16位的加密秘钥替换项目application.properties配置文件的aes.key字段。
   - 修改项目application.properties配置文件的image.citaNode和upgrade.citaNode字段。
   
   ```properties
   #image.citaNode是部署节点使用的配置，格式为：镜像名_镜像版本;镜像id;镜像实际名称;镜像版本
   image.citaNode=cita_secp256k1_sha3_0.24.0;2bd35243e00f;cita/cita_secp256k1_sha3:0.24.0
   #image.citaNode是升级节点使用的配置，格式为：镜像名_镜像版本;镜像id;镜像实际名称;镜像版本
   upgrade.citaNode=cita_secp256k1_sha3_0.24.0;2bd35243e00f;cita/cita_secp256k1_sha3:0.24.0
   ```

### 1.4 项目构建

项目代码构建，具体步骤如下：

```
//进入frontend目录
cd frontend
//安装包和相关的依赖包
npm install
//打包，将dist下的所有文件复制到src/main/resources/static下面
npm run build
//回到主目录下，检查代码的样式是否符合规范，并对checkstyle-result.xml中显示的不符合规范内容进行修改
mvn checkstyle:checkstyle
//将项目打成jar包
maven install
```

程序打包完成后，需记录所生成jar文件的名称，并在install-client.sh脚本中将原有名称EasyBaaS-1.0.0.jar替换为最新的名称。

### 1.5 项目运行
- 完成以上步骤后，创建项目运行目录，将EasyBaaS jar包、install-client.sh脚本文件、application.properties配置文件、shell和files文件夹等拷贝到该目录下，最终文件目录结构如下：
```
EasyBaaS
    ├── jdk-8u161-linux-x86.tar.gz  (jdk环境安装使用)
    ├── docker-20.10.1.tgz  (docker环境安装使用)
    ├── install-client.sh  (运行脚本环境安装使用)
    ├── EasyBaaS-1.0.0.jar  (客户端文件)
    ├── application.properties  (客户端外连配置文件)
    ├── cita-secp256k1_sha3.tar.gz (CITA区块链软件压缩包)
    ├── shell (shell命令文件夹，下面包含多个脚本)
    ├── files (监控配置文件文件夹，下面包含多个文件夹以及文件)
    ├── cita-secp256k1_sha3 (CITA区块链软件解压后文件夹)
        ├── bin (bin目录，下面包含多个文件)
        ├── resource (resource目录，下面包含多个文件)
        ├── scripts (scripts目录，下面包含多个文件)
        ├── cita-cli (CITA客户端工具)
        ├── ClearCITANodeLogs.sh (清理日志脚本)
        ├── BackupCITANodeLogs.sh (备份日志脚本)
        └── Dockerfile (镜像生成Dockerfile)
    ├── cita-cli-x86_64-musl-tls-0.19.6.tar.gz (CITA客户端工具压缩包)
    └── cita-cli (CITA客户端工具)

```
- 根据文档《EasyBaaS使用文档.md》中“重点注意事项”小节对服务器进行对应的检查，如无问题则可以使用root权限运行脚本启动项目部署节点
```
# 运行脚本，执行选项4
source install-client.sh
```
执行上述命令后会自动进行镜像加载、程序启动等步骤，至此项目部署完成。总体流程可参考[EasyBaaS用户部署流程](https://github.com/china-merchants-bank/EasyBaaS/blob/master/images/EasyBaaS%E7%94%A8%E6%88%B7%E9%83%A8%E7%BD%B2%E6%B5%81%E7%A8%8B.jpg)。

## 使用手册
请参考《EasyBaaS使用文档.md》

## 参与贡献
EasyBaaS在持续建设阶段，欢迎感兴趣的同学一起参与贡献。

如果你遇到问题或需要新功能，欢迎创建issue。

如果你可以解决某个issue, 欢迎发送PR。

如项目对您有帮助，欢迎star支持。

## 联系我们
邮箱地址：blockchaingroup@cmbchina.com
