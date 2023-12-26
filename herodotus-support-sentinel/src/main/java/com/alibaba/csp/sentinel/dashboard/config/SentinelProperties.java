package com.alibaba.csp.sentinel.dashboard.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Description: TODO </p>
 *
 * @author : gengwei.zheng
 * @date : 2023/12/25 18:14
 */
@ConfigurationProperties(prefix = "herodotus.nacos.sentinel")
public class SentinelProperties {

    /**
     * Nacos中存储Sentinel配置的命名空间ID字段
     */
    private String namespace;
    /**
     * acos中存储Sentinel配置的 data id 后缀。默认值：-flow-rules
     */
    private String dataIdSuffix = "-flow-rules";
    /**
     * Nacos中存储Sentinel配置的Group。默认值：sentinel
     */
    private String group = "sentinel";
    /**
     * Nacos中存储Sentinel配置的类型。默认值：json
     */
    private ConfigType type = ConfigType.JSON;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDataIdSuffix() {
        return dataIdSuffix;
    }

    public void setDataIdSuffix(String dataIdSuffix) {
        this.dataIdSuffix = dataIdSuffix;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ConfigType getType() {
        return type;
    }

    public void setType(ConfigType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("namespaceId", namespace)
                .add("dataIdSuffix", dataIdSuffix)
                .add("group", group)
                .add("type", type)
                .toString();
    }
}
