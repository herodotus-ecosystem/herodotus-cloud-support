# 修改内容标记
1. 【修改】DashboardApplication： 增加 @EnableHerodotusInfluxdb @EnableHerodotusNacos 两个注解
2. 【修改】webapp.resources.app.scripts.directives.sidebar.sidebar.html:
```angular2html
流控规则路由从 dashboard.flowV1 改成 dashboard.flow

<-- nacos 动态规则配置-->
<li ui-sref-active="active" ng-if="!entry.isGateway">
      <a ui-sref="dashboard.flow({app: entry.app})">
      <i class="glyphicon glyphicon-filter"></i>&nbsp;&nbsp;流控规则</a>
</li>
```

3. 【新增】com.alibaba.csp.sentinel.dashboard.repository.metric 包中增加两个类 InfluxdbMetricEntity 和 InInfluxdbMetricsRepository
4. 【新增】com.alibaba.csp.sentinel.dashboard.rule.nacos 包中新增三个类 FlowRuleNacosProvider FlowRuleNacosPublisher NacosSentinelConfiguration