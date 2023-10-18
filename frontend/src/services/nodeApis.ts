import { request } from 'umi';

// 节点部署
export async function deployNode(params: FormData) {
  return request<API.BasicRes<void>>('/citaNode/deployNode', {
    method: 'POST',
    body: params,
  });
}

// 获取节点列表
export async function getNodeList() {
  return request<API.BasicRes<NODE.NodeItem[]>>('/citaNode/nodeLists');
}

// 节点启动
export async function startNode(nodeName: string) {
  return request<API.BasicRes<string>>('/citaNode/startNode', {
    method: 'GET',
    params: { nodeName },
  });
}

// 节点停止
export async function stopNode(nodeName: string) {
  return request<API.BasicRes<string>>('/citaNode/stopNode', {
    method: 'GET',
    params: { nodeName },
  });
}

// 节点日志查询
export async function getNodeLogs(param: NODE.QueryNodeLogReq) {
  return request<API.BasicRes<NODE.NodeLogItem[]>>('/citaNode/queryLog', {
    method: 'GET',
    params: param,
  });
}

// 节点升级
export async function upgradeNode(nodeName: string) {
  return request<API.BasicRes<void>>('/citaNode/updateNode', {
    method: 'POST',
    data: { nodeName },
  });
}

// 节点重启
export async function restartNode(nodeName: string) {
  return request<API.BasicRes<void>>('/citaNode/reStartNode', {
    method: 'GET',
    params: { nodeName },
  });
}

// 节点纳管
export async function adoptNode(params: NODE.AdoptNodeReq) {
  return request<API.BasicRes<void>>('/citaNode/deploymentExternalExistsNode', {
    method: 'POST',
    data: params,
  });
}

// 获取节点trace状态
export async function getNodeTraceStatus(nodeName: string) {
  return request<API.BasicRes<boolean>>('/nodeLog/statusTrace', {
    method: 'GET',
    params: { nodeName },
  });
}

// 开启节点trace日志
export async function openNodeTrace(nodeName: string) {
  return request<API.BasicRes<void>>('/nodeLog/openTrace', {
    method: 'GET',
    params: { nodeName },
  });
}

// 关闭节点trace日志
export async function closeNodeTrace(nodeName: string) {
  return request<API.BasicRes<boolean>>('/nodeLog/closeTrace', {
    method: 'GET',
    params: { nodeName },
  });
}

// 获取节点日志所占空间大小
export async function getNodeLogSize(params: NODE.GetLogSizeReq) {
  return request<API.BasicRes<string>>('/nodeLog/logFileSize', {
    method: 'POST',
    data: params,
  });
}

// 清理节点日志
export async function clearNodeLog(params: NODE.ClearLogReq) {
  return request<API.BasicRes<string>>('/nodeLog/clearLogFiles', {
    method: 'POST',
    data: params,
  });
}
