declare namespace NODE {
  type NodeItem = {
    nodeName: string;
    nodeAddress: string;
    nodeVersion: string;
    nodeStatus: string;
    key?: number;
  };

  type SeverUser = {
    username: string;
    password: string;
  };

  type DeployNodeReq = {
    nodeConfigFile: any;
    privateKey: string;
    address: string;
    nodeAddress: string;
    nodeName: string;
  } & Partial<SeverUser>;

  type QueryNodeLogReq = {
    nodeName: string;
    type: string;
    num: number;
  };
  type NodeLogItem = string;

  type AdoptNodeReq = {
    nodeName: string;
    nodeAddress: string;
    filePath: string;
  } & Partial<SeverUser>;

  type GetLogSizeReq = {
    nodeName: string;
  } & Partial<SeverUser>;

  type ClearLogReq = {
    nodeName: string;
    line: string; // 日志保留行数
  } & Partial<SeverUser>;
}
