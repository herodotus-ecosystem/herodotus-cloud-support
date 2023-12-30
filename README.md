# herodotus dashboard

## 介绍

基于最新版 `Spring Cloud Alibaba Sentinel Dashboard` 扩展 改造，支持微服务流量监控数据持久化存储到 `Influxdb` 时序数据库，支持通过 `Sentinel Dashboard` 界面存储流量控制配置至至 `Nacos` 中。

时序数据存储基于 Influxdb v1.X 版本实现，Nacos 支持 v1.X 和 v2.X 版本。默认使用 `Sentinel Dashboard` 原有内存方式存储，可通过配置参数动态开启或关闭 `Influxdb` 和 `Nacos` 存储机制。

## 下载方法

```bash
docker pull herodotus/sentinel-dashboard:tagname
```

## 启动方法

```bash
docker run --name sentinel  -d -p 8858:8858 -d  herodotus/sentinel-dashboard:tagname
```

## 环境变量参数

### Sentinel 相关参数

| 变量                    | 默认值   | 是否必需 | 说明                            |
| ----------------------- | -------- | -------- | ------------------------------- |
| JAVA_OPTS               |          | false    | JVM相关运行参数                 |
| SENTINEL_ADMIN_USERNAME | sentinel | false    | Sentinel Dashboard 管理员用户名 |
| SENTINEL_ADMIN_PASSWORD | sentinel | false    | Sentinel Dashboard 管理员密码   |

### InfluxDB 相关参数

| 变量              | 默认值 | 是否必需 | 说明                                       |
| ----------------- | ------ | -------- | ------------------------------------------ |
| INFLUXDB_URL      |        | false    | InfluxDB 连接地址，格式：http(s)://ip:port |
| INFLUXDB_USERNAME |        | false    | InfluxDB 用户名                            |
| INFLUXDB_PASSWORD |        | false    | InfluxDB 密码                              |
| INFLUXDB_DATABASE |        | false    | IInfluxDB 数据库                           |

**注意：** 需要同时设置 `INFLUXDB_URL`、`INFLUXDB_USERNAME` 、`INFLUXDB_PASSWORD` 、`INFLUXDB_DATABASE` 这四个参数，才会开启 InfluxDB 存储方式，否则还是使用默认的内存存储方式。

### Nacos 相关参数

| 变量                        | 默认值      | 是否必需 | 说明                                                                          |
| --------------------------- | ----------- | -------- | ----------------------------------------------------------------------------- |
| NACOS_SERVER_ADDRESS        |             | false    | Nacos Server 地址，格式：http://ip:port                                       |
| NACOS_CONFIG_DATA_ID_SUFFIX | -flow-rules | false    | Nacos 存储配置Data ID后缀名，用于区分配置的用途。例如：xxx-service-flow-rules |
| NACOS_CONFIG_NAMESPACE      |             | false    | Nacos 命名空间设置，对应 Nacos 的命名空间的ID，而不是命名空间的名称           |
| NACOS_CONFIG_GROUP          | sentinel    | false    | Nacos 配置 Group                                                              |
| NACOS_CONFIG_TYPE           | json        | false    | Nacos 配置类型，具体参见：com.alibaba.nacos.api.config.ConfigType             |
| NACOS_ADMIN_USERNAME        | nacos       | false    | Nacos 用户名（开启认证后才需要配置）                                          |
| NACOS_ADMIN_PASSWORD        | nacos       | false    | Nacos 密码（开启认证后才需要配置）                                            |
| NACOS_AUTH_ENABLED          | false       | false    | Nacos 是否开启认证                                                            |
| NACOS_TOKEN_TTL             | 18000       | false    | Nacos Token 有效时间（开启认证后才需要配置）                                  |

**注意：** 设置 `NACOS_SERVER_ADDRESS `参数，才会开启 Nacos 存储方式，否则还是使用默认的内存存储方式。

## Docker-compose 示例

```yaml
version: "3"
services:
  sentinel:
    image: herodotus/sentinel-dashboard:latest
    container_name: sentinel-dashboard
    environment:
      SENTINEL_ADMIN_USERNAME: herodotus
      SENTINEL_ADMIN_PASSWORD: herodotus
      INFLUXDB_URL: http://127.0.0.1:8086
      INFLUXDB_USERNAME: herodotus
      INFLUXDB_PASSWORD: herodotus
      INFLUXDB_DATABASE: sentinel
      NACOS_SERVER_ADDRESS: http://127.0.0.1:8848
      NACOS_CONFIG_DATA_ID_SUFFIX: -flow-rules
    ports:
      - "8858:8858"
```




1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
