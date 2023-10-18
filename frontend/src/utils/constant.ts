/* *
 * response code
 * */
// 请求成功code
export const RES_SUC = 'SUC0000';
/* 接口统一校验返回码 */
// 登录过期
export const RES_SESSION_OUT = 'EASYBASS348';
// token校验失败
export const RES_TOKEN_ERROR = 'EASYBASS323';
// 服务器用户名密码错误
export const RES_SERVER_PASSWORD_ERROR = 'EASYBASS349';
// 服务器无有权限用户名密码
export const RES_SERVER_NEED_PASSWORD = 'EASYBASS350';

/*
 * 节点相关
 * */
export const NODE_STATUS = {
  RUNNING: 'run',
  STOPPED: 'stop',
  WARNING: 'warn',
};
export const NODE_LOG_TYPE = ['forever', 'auth', 'chain', 'jsonrpc', 'executor', 'network', 'bft'];

/*
 * 监控相关
 * */
export const MONITOR_STATUS = {
  RUNNING: 'started',
  STOPPED: 'stopped',
  DELETED: 'deleted',
};

// 默认登录密码
export const DEFAULT_PASSWORD = 'adminpw';
