declare namespace MONITOR {
  type MonitorItem = {
    name: string;
    address: string;
    status: string;
    sign: boolean;
    key?: number;
  };

  type MonitorOprType = 'start' | 'stop';

  type OprMonitorReq = {
    name: string;
    address: string;
    type: MonitorOprType;
  };

  type UpdateGrafanaReq = {
    prometheusUrl: string;
  };

  type AlertItem = {
    alertName: string;
    alertEmail: string;
  };

  type ConfigEmailReq = {
    smtpAddress: string;
    alarmUsername: string;
    alarmPassword: string; // 授权码
  };
}
