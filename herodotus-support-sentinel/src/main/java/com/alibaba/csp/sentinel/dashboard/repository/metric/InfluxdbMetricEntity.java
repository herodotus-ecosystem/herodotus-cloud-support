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

import com.google.common.base.MoreObjects;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

/**
 * <p>Description: Sentinel 信息存储对象 </p>
 *
 * @author : gengwei.zheng
 * @date : 2021/11/17 17:41
 */
@Measurement(name = "sentinelInfo")
public class InfluxdbMetricEntity {

    @Column(name = "time")
    private Instant time;

    @Column(name = "gmtCreate")
    private Long gmtCreate;

    @Column(name = "gmtModified")
    private Long gmtModified;
    /**
     * 监控信息的时间戳
     */
    @Column(name = "app", tag = true)
    private String app;

    @Column(name = "resource", tag = true)
    private String resource;

    @Column(name = "timestamp")
    private Long timestamp;

    /**
     * 通过qps
     */
    @Column(name = "passQps")
    private Long passQps;

    /**
     * 成功qps
     */
    @Column(name = "successQps")
    private Long successQps;

    /**
     * 限流qps
     */
    @Column(name = "blockQps")
    private Long blockQps;

    /**
     * 异常qps
     */
    @Column(name = "exceptionQps")
    private Long exceptionQps;

    /**
     * 所有successQps的rt的和
     */
    @Column(name = "rt")
    private double rt;

    /**
     * 本次聚合的总条数
     */
    @Column(name = "count")
    private int count;
    @Column(name = "resourceCode")
    private int resourceCode;

    public static InfluxdbMetricEntity copyOf(InfluxdbMetricEntity oldEntity) {
        InfluxdbMetricEntity entity = new InfluxdbMetricEntity();
        entity.setApp(oldEntity.getApp());
        entity.setGmtCreate(oldEntity.getGmtCreate());
        entity.setGmtModified(oldEntity.getGmtModified());
        entity.setTimestamp(oldEntity.getTimestamp());
        entity.setResource(oldEntity.getResource());
        entity.setPassQps(oldEntity.getPassQps());
        entity.setBlockQps(oldEntity.getBlockQps());
        entity.setSuccessQps(oldEntity.getSuccessQps());
        entity.setExceptionQps(oldEntity.getExceptionQps());
        entity.setRt(oldEntity.getRt());
        entity.setCount(oldEntity.getCount());
        entity.setResource(oldEntity.getResource());
        return entity;
    }

    public synchronized void addPassQps(Long passQps) {
        this.passQps += passQps;
    }

    public synchronized void addBlockQps(Long blockQps) {
        this.blockQps += blockQps;
    }

    public synchronized void addExceptionQps(Long exceptionQps) {
        this.exceptionQps += exceptionQps;
    }

    public synchronized void addCount(int count) {
        this.count += count;
    }

    public synchronized void addRtAndSuccessQps(double avgRt, Long successQps) {
        this.rt += avgRt * successQps;
        this.successQps += successQps;
    }

    /**
     * {@link #rt} = {@code avgRt * successQps}
     *
     * @param avgRt      average rt of {@code successQps}
     * @param successQps
     */
    public synchronized void setRtAndSuccessQps(double avgRt, Long successQps) {
        this.rt = avgRt * successQps;
        this.successQps = successQps;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Long gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
        this.resourceCode = resource.hashCode();
    }

    public Long getPassQps() {
        return passQps;
    }

    public void setPassQps(Long passQps) {
        this.passQps = passQps;
    }

    public Long getBlockQps() {
        return blockQps;
    }

    public void setBlockQps(Long blockQps) {
        this.blockQps = blockQps;
    }

    public Long getExceptionQps() {
        return exceptionQps;
    }

    public void setExceptionQps(Long exceptionQps) {
        this.exceptionQps = exceptionQps;
    }

    public double getRt() {
        return rt;
    }

    public void setRt(double rt) {
        this.rt = rt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getResourceCode() {
        return resourceCode;
    }

    public Long getSuccessQps() {
        return successQps;
    }

    public void setSuccessQps(Long successQps) {
        this.successQps = successQps;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setResourceCode(int resourceCode) {
        this.resourceCode = resourceCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("time", time)
                .add("gmtCreate", gmtCreate)
                .add("gmtModified", gmtModified)
                .add("app", app)
                .add("resource", resource)
                .add("timestamp", timestamp)
                .add("passQps", passQps)
                .add("successQps", successQps)
                .add("blockQps", blockQps)
                .add("exceptionQps", exceptionQps)
                .add("rt", rt)
                .add("count", count)
                .add("resourceCode", resourceCode)
                .toString();
    }
}
