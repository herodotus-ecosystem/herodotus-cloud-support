### 基础镜像，使用alpine操作系统，openjkd使用
FROM herodotus/oracle-jdk:8u333

# 作者
MAINTAINER herodotus <herodotus@aliyun.com>

#工作目录
WORKDIR /data/sentinel-dashboard

#在容器中创建挂载点，可以多个
VOLUME ["/data/sentinel-dashboard","/data/sentinel-dashboard/logs"]

#声明了容器应该打开的端口并没有实际上将它打开
EXPOSE 8858

ENV JAVA_OPTS=""
ENV SENTINEL_ADMIN_USERNAME="sentinel"
ENV SENTINEL_ADMIN_PASSWORD="sentinel"
ENV INFLUXDB_URL=""
ENV INFLUXDB_USERNAME=""
ENV INFLUXDB_PASSWORD=""
ENV INFLUXDB_DATABASE=""
ENV NACOS_SERVER_ADDRESS=""
ENV NACOS_CONFIG_DATA_ID_SUFFIX="-flow-rules"
ENV NACOS_CONFIG_NAMESPACE=""
ENV NACOS_CONFIG_GROUP="sentinel"
ENV NACOS_CONFIG_TYPE="json"

#拷贝本地文件到镜像中
COPY ./target/sentinel-dashboard.jar /data/sentinel-dashboard/sentinel-dashboard.jar

#指定容器启动时要执行的命令，但如果存在CMD指令，CMD中的参数会被附加到ENTRYPOINT指令的后面
ENTRYPOINT java -Dserver.port=8858 -Dcsp.sentinel.dashboard.server=localhost:8858 -Dauth.username=${SENTINEL_ADMIN_USERNAME} -Dauth.password=${SENTINEL_ADMIN_PASSWORD} -Dherodotus.nosql.influxdb.endpoint=${INFLUXDB_URL} -Dherodotus.nosql.influxdb.database=${INFLUXDB_DATABASE} -Dherodotus.nosql.influxdb.password=${INFLUXDB_PASSWORD} -Dherodotus.nosql.influxdb.username=${INFLUXDB_USERNAME} -Dspring.cloud.nacos.config.server-addr=${NACOS_SERVER_ADDRESS} -Dherodotus.nacos.server-address=${NACOS_SERVER_ADDRESS} -Dherodotus.nacos.sentinel.namespace=${NACOS_CONFIG_NAMESPACE} -Dherodotus.nacos.sentinel.data-id-suffix=${NACOS_CONFIG_DATA_ID_SUFFIX} -Dherodotus.nacos.sentinel.group=${NACOS_CONFIG_GROUP} -Dherodotus.nacos.sentinel.type=${NACOS_CONFIG_TYPE} ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar -Dfile.encoding=utf-8 sentinel-dashboard.jar
