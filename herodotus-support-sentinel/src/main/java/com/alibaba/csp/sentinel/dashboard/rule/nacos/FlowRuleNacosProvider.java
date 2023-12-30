/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2019-2021 Zhenggengwei<码匠君>, herodotus@aliyun.com
 *
 * This file is part of Herodotus Cloud.
 *
 * Herodotus Cloud is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Herodotus Cloud is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with with Herodotus Cloud;
 * if no see <https://gitee.com/herodotus/herodotus-cloud>
 *
 * - Author: Zhenggengwei<码匠君>
 * - Contact: herodotus@aliyun.com
 * - License: GNU Lesser General Public License (LGPL)
 * - Blog and source code availability: https://gitee.com/herodotus/herodotus-cloud
 */

package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import cn.herodotus.engine.assistant.core.domain.Result;
import cn.herodotus.professional.api.nacos.annotation.ConditionalOnNacosConfigured;
import cn.herodotus.professional.api.nacos.domain.request.ConfigRequest;
import cn.herodotus.professional.api.nacos.service.NacosConfigsService;
import com.alibaba.csp.sentinel.dashboard.config.SentinelProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: 规则读取 </p>
 *
 * @author : gengwei.zheng
 * @date : 2021/11/18 14:18
 */
@Component
@Primary
@ConditionalOnNacosConfigured
public class FlowRuleNacosProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    private final NacosConfigsService nacosConfigService;
    private final SentinelProperties sentinelProperties;
    private final Converter<String, List<FlowRuleEntity>> converter;

    public FlowRuleNacosProvider(NacosConfigsService nacosConfigService, SentinelProperties sentinelProperties, Converter<String, List<FlowRuleEntity>> converter) {
        this.nacosConfigService = nacosConfigService;
        this.sentinelProperties = sentinelProperties;
        this.converter = converter;
    }

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {

        ConfigRequest request = new ConfigRequest();
        request.setNamespaceId(sentinelProperties.getNamespace());
        request.setDataId(appName + sentinelProperties.getDataIdSuffix());
        request.setGroup(sentinelProperties.getGroup());

        Result<String> response =  nacosConfigService.find(request);

        if (ObjectUtils.isNotEmpty(response) && response.getCode() == 0) {
            String rules = response.getData();
            return converter.convert(rules);
        } else {
            return new ArrayList<>();
        }
    }
}
