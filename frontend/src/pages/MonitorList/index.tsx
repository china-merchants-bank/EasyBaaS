import React, { useRef, useState } from 'react';
import { message, Spin, Alert } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { ModalForm, ProFormText } from '@ant-design/pro-form';
import { MONITOR_STATUS, RES_SUC } from '@/utils/constant';
import { getMonitorList, oprMonitor, updateGrafanaConfig } from '@/services/monitorApis';
import { FundProjectionScreenOutlined } from '@ant-design/icons';
import styles from './index.less';

// 修改grafana配置
const handleUpdate = async (value: MONITOR.UpdateGrafanaReq) => {
  try {
    await updateGrafanaConfig(value);
    message.success('监控数据源IP地址修改成功');
    return true;
  } catch (e) {
    message.error('监控数据源IP地址修改失败');
    return false;
  }
};

const MonitorList: React.FC = () => {
  const [isLoading, setLoading] = useState<boolean>(false);
  const [updateModalVisible, handleModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();

  const content = (
    <>
      <p>
        通过监控服务可查看链上信息以及主机信息，包括：各节点块高，节点进程，链的配置信息，主机CPU，负载，内存，CITA微服务存活历史，
        IO变化历史，RabbitMq服务存活状态等
      </p>
      <div className={styles.links}>
        <FundProjectionScreenOutlined />
        <a
          type="link"
          href={`http://${window.location.hostname}:3000`}
          target="_blank"
          style={{ marginLeft: 8 }}
        >
          监控查看
        </a>
      </div>
    </>
  );

  // 监控组件启停
  const handleOpr = async (
    component: MONITOR.MonitorItem,
    oprType: MONITOR.MonitorOprType,
    oprTypeLabel: string,
  ) => {
    message.info(`${component.name}正在${oprTypeLabel}中...`);
    setLoading(true);
    try {
      const params: MONITOR.OprMonitorReq = {
        name: component.name,
        address: component.address,
        type: oprType,
      };
      await oprMonitor(params);
      message.success(`${component.name}${oprTypeLabel}成功`);
      setLoading(false);
      if (actionRef.current) {
        actionRef.current.reload();
      }
    } catch (e) {
      setLoading(false);
      message.error(`${component.name}${oprTypeLabel}失败`);
    }
  };

  const columns: ProColumns<MONITOR.MonitorItem>[] = [
    {
      title: '镜像名称',
      dataIndex: 'name',
    },
    {
      title: '镜像地址',
      dataIndex: 'address',
    },
    {
      title: '运行状态',
      dataIndex: 'status',
      valueEnum: {
        [MONITOR_STATUS.RUNNING]: { text: '运行中', status: 'Success' },
        [MONITOR_STATUS.STOPPED]: { text: '已停止', status: 'Error' },
        [MONITOR_STATUS.DELETED]: { text: '已删除', status: 'Default' },
      },
    },
    {
      title: '操作',
      valueType: 'option',
      width: '15%',
      render: (_, record) => [
        record.status === MONITOR_STATUS.RUNNING && (
          <a key="stop" onClick={() => handleOpr(record, 'stop', '停止')}>
            停止
          </a>
        ),
        record.status === MONITOR_STATUS.STOPPED && (
          <a key="start" onClick={() => handleOpr(record, 'start', '启动')}>
            启动
          </a>
        ),
        record.sign && (
          <a
            key="update"
            onClick={() => {
              handleModalVisible(true);
            }}
          >
            配置修改
          </a>
        ),
      ],
    },
  ];

  return (
    <PageContainer content={content}>
      <Spin spinning={isLoading} size="large">
        <ProTable<MONITOR.MonitorItem>
          columns={columns}
          search={false}
          rowKey="key"
          actionRef={actionRef}
          request={async () => {
            const { code, data } = await getMonitorList();
            return {
              data: data || [],
              success: code === RES_SUC,
              total: data.length || 0,
            };
          }}
          postData={(data: MONITOR.MonitorItem[]) =>
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
      <ModalForm
        title="修改监控数据获取源IP地址"
        width="400px"
        visible={updateModalVisible}
        onVisibleChange={handleModalVisible}
        validateTrigger="onBlur"
        modalProps={{
          destroyOnClose: true,
        }}
        onFinish={handleUpdate}
      >
        <ProFormText
          name="prometheusUrl"
          rules={[
            {
              required: true,
              message: '请输入监控数据获取源IP地址',
            },
            {
              pattern: /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/,
              message: '请输入正确的IP地址',
            },
          ]}
        />
        <Alert
          type="warning"
          message="本功能是修改监控的数据源地址，具有一定的危险性。若监控页面无法获取数据，请联系支持人员后再进行操作。"
        />
      </ModalForm>
    </PageContainer>
  );
};

export default MonitorList;
