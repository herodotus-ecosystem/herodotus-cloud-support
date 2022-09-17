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

package com.alibaba.csp.sentinel.dashboard.repository.metric;

import cn.herodotus.engine.nosql.influxdb.annotation.ConditionalOnInfluxdbEnabled;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>Description: 自定义Influxdb度量数据存储 </p>
 *
 * @author : gengwei.zheng
 * @date : 2021/11/18 11:39
 */
@Component
@Primary
@ConditionalOnInfluxdbEnabled
public class InInfluxdbMetricsRepository implements MetricsRepository<MetricEntity> {

    @Autowired
    public InfluxDB influxdb;

    @Override
    public synchronized void save(MetricEntity metric) {
        try {
            Point point = getPoint(metric);
            influxdb.write(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private Point getPoint(MetricEntity metric) {
        return Point
                .measurement("sentinelInfo")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("app", metric.getApp())
                .tag("resource", metric.getResource())
                .addField("gmtCreate", metric.getGmtCreate().getTime())
                .addField("gmtModified", metric.getGmtModified().getTime())
                .addField("timestamp", metric.getTimestamp().getTime())
                .addField("passQps", metric.getPassQps())
                .addField("successQps", metric.getSuccessQps())
                .addField("blockQps", metric.getBlockQps())
                .addField("exceptionQps", metric.getExceptionQps())
                .addField("rt", metric.getRt())
                .addField("count", metric.getCount())
                .addField("resourceCode", metric.getResourceCode())
                .build();
    }

    @Override
    public synchronized void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        BatchPoints batchPoints = BatchPoints.builder()
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
        metrics.forEach(metric -> {
            Point point = getPoint(metric);
            batchPoints.point(point);
        });
        influxdb.write(batchPoints);
    }

    @Override
    public synchronized List<MetricEntity> queryByAppAndResourceBetween(String app, String resource, long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        String command = "SELECT * FROM sentinelInfo WHERE app='" + app + "' AND resource = '" + resource + "' AND gmtCreate>" + startTime + " AND gmtCreate<" + endTime;
        Query query = new Query(command);
        QueryResult queryResult = influxdb.query(query);
        // thread-safe - can be reused
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<InfluxdbMetricEntity> influxResults = resultMapper.toPOJO(queryResult, InfluxdbMetricEntity.class);
        try {
            influxResults.forEach(entity -> {
                MetricEntity metric = new MetricEntity();
                BeanUtils.copyProperties(entity, metric);
                metric.setTimestamp(new Date(entity.getTimestamp()));
                metric.setGmtCreate(new Date(entity.getGmtCreate()));
                metric.setGmtModified(new Date(entity.getGmtModified()));
                results.add(metric);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public synchronized List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }
        //最近一分钟的指标(实时数据)
        final long minTimeMs = System.currentTimeMillis() - 1000 * 60;
        String command = "SELECT * FROM sentinelInfo WHERE app='" + app + "' AND gmtCreate>" + minTimeMs;
        Query query = new Query(command);
        QueryResult queryResult = influxdb.query(query);
        // thread-safe - can be reused
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<InfluxdbMetricEntity> influxResults = resultMapper.toPOJO(queryResult, InfluxdbMetricEntity.class);
        try {
            if (CollectionUtils.isEmpty(influxResults)) {
                return results;
            }
            Map<String, InfluxdbMetricEntity> resourceCount = new HashMap<>(32);
            for (InfluxdbMetricEntity metricEntity : influxResults) {
                String resource = metricEntity.getResource();
                if (resourceCount.containsKey(resource)) {
                    InfluxdbMetricEntity oldEntity = resourceCount.get(resource);
                    oldEntity.addPassQps(metricEntity.getPassQps());
                    oldEntity.addRtAndSuccessQps(metricEntity.getRt(), metricEntity.getSuccessQps());
                    oldEntity.addBlockQps(metricEntity.getBlockQps());
                    oldEntity.addExceptionQps(metricEntity.getExceptionQps());
                    oldEntity.addCount(1);
                } else {
                    resourceCount.put(resource, InfluxdbMetricEntity.copyOf(metricEntity));
                }
            }
            //排序
            results = resourceCount.entrySet()
                    .stream()
                    .sorted((o1, o2) -> {
                        InfluxdbMetricEntity e1 = o1.getValue();
                        InfluxdbMetricEntity e2 = o2.getValue();
                        int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                        if (t != 0) {
                            return t;
                        }
                        return e2.getPassQps().compareTo(e1.getPassQps());
                    })
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
