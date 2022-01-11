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

import cn.herodotus.integration.nacos.annotation.ConditionalOnNacosEnabled;
import cn.herodotus.integration.nacos.properties.NacosProperties;
import cn.herodotus.integration.nacos.service.NacosConfigService;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.apache.commons.lang3.StringUtils;
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
@ConditionalOnNacosEnabled
public class FlowRuleNacosProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    @Autowired
    private NacosConfigService nacosConfigService;
    @Autowired
    private NacosProperties nacosProperties;
    @Autowired
    private Converter<String, List<FlowRuleEntity>> converter;

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        String rules = nacosConfigService.getConfig(nacosProperties.getSentinel().getNamespace(), appName + nacosProperties.getSentinel().getDataIdSuffix(), nacosProperties.getSentinel().getGroup());

        if (StringUtils.isBlank(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
