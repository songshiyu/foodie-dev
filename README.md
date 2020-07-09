# foodie-dev
架构师进阶之地基--单体项目架构

一、初始软件版本
java：jdk-8u191
tomcat：apache-tomcat-9.0.24
MariaDB 10.4.x
  1.下载安装包
    galera-4-26.4.3-1.rhel7.el7.centos.x86_64.rpm
    jemalloc-3.6.0-1.el7.x86_64.rpm
    jemalloc-devel-3.6.0-1.el7.x86_64.rpm
    MariaDB-client-10.4.11-1.el7.centos.x86_64.rpm
    MariaDB-common-10.4.11-1.el7.centos.x86_64.rpm
    MariaDB-compat-10.4.11-1.el7.centos.x86_64.rpm
    MariaDB-server-10.4.11-1.el7.centos.x86_64.rpm
  2.按照顺序安装依赖包
    yum install rsync nmap lsof perl-DBI nc
    rpm -ivh jemalloc-3.6.0-1.el7.x86_64.rpm
    rpm -ivh jemalloc-devel-3.6.0-1.el7.x86_64.rpm
  3.卸载冲突的MariaDb-libs
      rpm -qa | grep mariadb-libs
    然后删除
      rpm -ev --nodeps mariadb-libs-5.5.60-1.el7_5.x86_64
  4.安装 boost-devel 依赖环境
    yum install boost-devel.x86_64
  5.导入MariaDB的key
    rpm --import http://yum.mariadb.org/RPM-GPG-KEY-MariaDB
  6.安装 galera 环境
    rpm -ivh  galera-4-26.4.3-1.rhel7.el7.centos.x86_64.rpm
  7.安装 libaio (此步骤在安装 10.4.8 的时候需要)
    wget http://mirror.centos.org/centos/6/os/x86_64/Packages/libaio-0.3.107-10.el6.x86_64.rpm
    rpm -ivh libaio-0.3.107-10.el6.x86_64.rpm
  8.安装MariaDB的4个核心包
    rpm -ivh 
      MariaDB-common-10.4.11-1.el7.centos.x86_64.rpm 
      MariaDB-compat-10.4.11-1.el7.centos.x86_64.rpm 
      MariaDB-client-10.4.11-1.el7.centos.x86_64.rpm 
      MariaDB-server-10.4.11-1.el7.centos.x86_64.rpm
  9.安装完之后进行配置MariaDb
    启动：systemctl start mariadb.service 
    开机启动服务：systemctl enable mariadb.service
    进入MariaDB配置：mysql_secure_installation,可以按照安装mysql时的选项进行选择，当出现Thanks for using MariaDB!表明配置成功
  10.若想在其他地方连接数据库，还需以下步骤，同(mysql)
      登录：mysql -u root -p 
      赋予root用户远程连接权限：
        grant all privileges on *.* to 'root'@'%' identified by 'root密码';
        flush privileges;


架构师进阶之-集群项目架构


架构师进阶之-分布式架构
