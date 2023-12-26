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
import cn.herodotus.professional.api.nacos.domain.request.ConfigPublishRequest;
import cn.herodotus.professional.api.nacos.domain.request.ConfigRequest;
import cn.herodotus.professional.api.nacos.service.NacosConfigsService;
import com.alibaba.csp.sentinel.dashboard.config.SentinelProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>Description: 规则发布 </p>
 *
 * @author : gengwei.zheng
 * @date : 2021/11/18 14:20
 */
@Component
@Primary
@ConditionalOnNacosConfigured
public class FlowRuleNacosPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {

    private static final Logger log = LoggerFactory.getLogger(FlowRuleNacosPublisher.class);

    private final NacosConfigsService nacosConfigsService;
    private final SentinelProperties sentinelProperties;
    private final Converter<List<FlowRuleEntity>, String> converter;

    public FlowRuleNacosPublisher(NacosConfigsService nacosConfigsService, SentinelProperties sentinelProperties, Converter<List<FlowRuleEntity>, String> converter) {
        this.nacosConfigsService = nacosConfigsService;
        this.sentinelProperties = sentinelProperties;
        this.converter = converter;
    }

    @Override
    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");

        boolean status;
        String action;

        if (CollectionUtils.isEmpty(rules)) {

            ConfigRequest request = new ConfigRequest();
            request.setNamespaceId(sentinelProperties.getNamespace());
            request.setDataId(app + sentinelProperties.getDataIdSuffix());
            request.setGroup(sentinelProperties.getGroup());

            Result<Boolean> result = nacosConfigsService.delete(request);
            status = result.getData();
            action = "Delete";
        } else {
            ConfigPublishRequest request = new ConfigPublishRequest();
            request.setNamespaceId(sentinelProperties.getNamespace());
            request.setDataId(app + sentinelProperties.getDataIdSuffix());
            request.setGroup(sentinelProperties.getGroup());
            request.setContent(converter.convert(rules));
            request.setType(sentinelProperties.getType().getType());

            Result<Boolean> result = nacosConfigsService.publish(request);
            status = result.getData();
            action = "Publish";
        }

        if (status) {
            log.debug("[Herodotus] |- [{}] sentinel flow rule config to nacos success.", action);
        } else {
            log.error("[Herodotus] |- [{}] sentinel flow rule config to nacos failed.", action);
        }
    }
}
