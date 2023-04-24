import React, { useRef, useState } from 'react';
import { Alert, message, Spin, Popconfirm } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { getNodeList, startNode, stopNode, upgradeNode, restartNode } from '@/services/nodeApis';
import { NODE_STATUS, RES_SUC } from '@/utils/constant';
import { history } from 'umi';

// 节点启动
const handleStart = async (node: NODE.NodeItem) => {
  message.info(`${node.nodeName}正在启动中...`);
  try {
    await startNode(node.nodeName);
    return true;
  } catch (e) {
    message.error(`${node.nodeName}启动失败`);
    return false;
  }
};

// 节点停止
const handleStop = async (node: NODE.NodeItem) => {
  message.info(`${node.nodeName}正在停止中...`);
  try {
    await stopNode(node.nodeName);
    message.success(`${node.nodeName}停止成功`);
    return true;
  } catch (e) {
    message.error(`${node.nodeName}停止失败`);
    return false;
  }
};

// 查看节点日志
const toLog = (node: NODE.NodeItem) => {
  history.push({
    pathname: '/node/log',
    query: {
      name: node.nodeName,
    },
  });
};

// 节点重启
const handleRestart = async (node: NODE.NodeItem) => {
  message.info(`${node.nodeName}正在重启...`);
  try {
    await restartNode(node.nodeName);
    return true;
  } catch (e) {
    message.error(`${node.nodeName}重启失败`);
    return false;
  }
};

// 节点升级
const handleUpgrade = async (nodeName: string = '') => {
  message.info(`${nodeName}正在升级...`);
  try {
    await upgradeNode(nodeName);
    return true;
  } catch (e) {
    message.error(`节点${nodeName}升级失败`);
    return false;
  }
};

const NodeList: React.FC = () => {
  const [isLoading, setLoading] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [intervalCounter, setIntervalCounter] = useState<number>(0);

  // 刷新节点状态
  const refreshNodeStatus = async (node: NODE.NodeItem) => {
    setIntervalCounter(intervalCounter + 1);
    // 定时3s*10
    if (intervalCounter > 10) {
      setLoading(false);
      message.warning('抱歉，网络不给力，请稍后刷新页面查看节点最新状态！');
    }
    try {
      const { data } = await getNodeList();
      const currentNode = data.find((item) => item.nodeName === node.nodeName);
      if (currentNode?.nodeStatus === NODE_STATUS.RUNNING && actionRef.current) {
        setLoading(false);
        message.success(`操作成功`);
        actionRef.current.reload();
        setIntervalCounter(0);
        return;
      }
    } catch (e) {
      //
    }
    setTimeout(() => {
      refreshNodeStatus(node);
    }, 1000 * 3);
  };

  const columns: ProColumns<NODE.NodeItem>[] = [
    {
      title: '节点名称',
      dataIndex: 'nodeName',
    },
    {
      title: '节点部署主机IP',
      dataIndex: 'nodeAddress',
    },
    {
      title: '节点版本',
      dataIndex: 'nodeVersion',
    },
    {
      title: '运行状态',
      dataIndex: 'nodeStatus',
      valueEnum: {
        [NODE_STATUS.RUNNING]: { text: '运行中', status: 'Success' },
        [NODE_STATUS.WARNING]: { text: '运行中', status: 'Warning' },
        [NODE_STATUS.STOPPED]: { text: '已停止', status: 'Error' },
      },
    },
    {
      title: '操作',
      valueType: 'option',
      width: '15%',
      render: (_, record) => [
        <a
          key="log"
          onClick={() => {
            toLog(record);
          }}
        >
          日志
        </a>,
        record.nodeStatus === NODE_STATUS.STOPPED && (
          <a
            key="start"
            onClick={async () => {
              setLoading(true);
              const result = await handleStart(record);
              if (result) {
                setTimeout(() => {
                  refreshNodeStatus(record);
                }, 1000 * 3);
              } else {
                setLoading(false);
              }
            }}
          >
            启动
          </a>
        ),
        record.nodeStatus === NODE_STATUS.RUNNING && (
          <Popconfirm
            key="stop"
            title="确认停止节点？"
            onConfirm={async () => {
              setLoading(true);
              const result = await handleStop(record);
              setLoading(false);
              if (result && actionRef.current) {
                actionRef.current.reload();
              }
            }}
          >
            <a>停止</a>
          </Popconfirm>
        ),
        record.nodeStatus === NODE_STATUS.WARNING && (
          <Popconfirm
            key="restart"
            title="确认重启节点？"
            onConfirm={async () => {
              setLoading(true);
              const result = await handleRestart(record);
              if (result) {
                setTimeout(() => {
                  refreshNodeStatus(record);
                }, 1000 * 3);
              } else {
                setLoading(false);
              }
            }}
          >
            <a>重启</a>
          </Popconfirm>
        ),
        <Popconfirm
          key="upgrade"
          title={
            <>
              1、节点升级需重启
              <br />
              2、建议在升级之前自行备份节点数据
              <br />
              确认进行节点升级？
            </>
          }
          onConfirm={async () => {
            setLoading(true);
            const result = await handleUpgrade(record.nodeName);
            if (result) {
              setTimeout(() => {
                refreshNodeStatus(record);
              }, 1000 * 3);
            } else {
              setLoading(false);
            }
          }}
        >
          <a>升级</a>
        </Popconfirm>,
      ],
    },
  ];
  return (
    <PageContainer>
      <Alert
        message={'启停操作具有风险，请谨慎操作'}
        type={'warning'}
        showIcon
        style={{
          marginBottom: 24,
        }}
      />
      <Spin spinning={isLoading} size="large">
        <ProTable<NODE.NodeItem>
          columns={columns}
          search={false}
          rowKey="key"
          actionRef={actionRef}
          request={async () => {
            const { code, data } = await getNodeList();
            return {
              data: data || [],
              success: code === RES_SUC,
              total: data.length || 0,
            };
          }}
          postData={(data: NODE.NodeItem[]) =>
            data.map((item, index) => {
              return { ...item, key: index };
            })
          }
          options={{
            reload: true,
            density: false,
            setting: false,
          }}
          pagination={{
            hideOnSinglePage: true,
            showSizeChanger: false,
            pageSize: 10,
          }}
        />
      </Spin>
    </PageContainer>
  );
};

export default NodeList;
