import { request } from 'umi';

// 获取监控组件列表
export async function getMonitorList() {
  return request<API.BasicRes<MONITOR.MonitorItem[]>>('/monitor/component/list');
}

// 监控组件启停
export async function oprMonitor(params: MONITOR.OprMonitorReq) {
  return request<API.BasicRes<void>>('/monitor/component/startOrStop', {
    method: 'PUT',
    data: params,
  });
}

// grafana配置修改
export async function updateGrafanaConfig(params: MONITOR.UpdateGrafanaReq) {
  return request<API.BasicRes<void>>('/monitor/component/updateGrafanaConfig', {
    method: 'POST',
    data: params,
  });
}

// 获取告警人列表
export async function getAlertList() {
  return request<API.BasicRes<MONITOR.AlertItem[]>>('/monitor/alertRule/queryAllAlertEmails');
}

// 新增告警人
export async function addAlert(params: MONITOR.AlertItem) {
  return request<API.BasicRes<void>>('/monitor/alertRule/addAlertEmails', {
    method: 'POST',
    data: params,
  });
}

// 修改告警人
export async function updateAlert(params: MONITOR.AlertItem) {
  return request<API.BasicRes<void>>('/monitor/alertRule/updateAlertEmails', {
    method: 'PUT',
    data: params,
  });
}

// 删除告警人
export async function deleteAlert(alert: string) {
  return request('/monitor/alertRule/deleteAlertEmails', {
    method: 'DELETE',
    params: { alertEmail: alert },
  });
}

// 配置告警发件邮箱
export async function configAlertEmail(params: MONITOR.ConfigEmailReq) {
  return request<API.BasicRes<void>>('/monitor/alertRule/addAlertSendEmails', {
    method: 'POST',
    data: params,
  });
}
