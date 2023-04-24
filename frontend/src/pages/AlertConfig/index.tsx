import React, { useRef, useState } from 'react';
import { message, Spin, Button, Popconfirm, Descriptions } from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import type { ProColumns, ActionType } from '@ant-design/pro-table';
import ProTable from '@ant-design/pro-table';
import { ModalForm, ProFormText } from '@ant-design/pro-form';
import { RES_SUC } from '@/utils/constant';
import {
  getAlertList,
  addAlert,
  updateAlert,
  deleteAlert,
  configAlertEmail,
} from '@/services/monitorApis';
import { PlusOutlined } from '@ant-design/icons';
import ConfigEmailModal from './components/EmailConfigModal';

const remark = (
  <Descriptions title="告警触发条件说明：" column={4}>
    <Descriptions.Item>1、CITA节点整体运行状态异常告警</Descriptions.Item>
    <Descriptions.Item>2、CITA节点各个服务进程运行状态异常告警</Descriptions.Item>
    <Descriptions.Item>3、CITA区块高度不一致时告警（多个节点时生效）</Descriptions.Item>
    <Descriptions.Item>4、CITA节点区块高度超过20s未增长告警</Descriptions.Item>
    <Descriptions.Item>5、监控组件不可用告警</Descriptions.Item>
    <Descriptions.Item>6、cpu使用率超过70%告警</Descriptions.Item>
    <Descriptions.Item>7、内存使用率超过70%告警</Descriptions.Item>
    <Descriptions.Item>8.硬盘使用率超过70%告警</Descriptions.Item>
    <Descriptions.Item>
      相同内容告警每8小时重复发送一次，告警状态恢复同样会发送恢复邮件提醒
    </Descriptions.Item>
  </Descriptions>
);

const AlertConfig: React.FC = () => {
  const [isLoading, setLoading] = useState<boolean>(false);
  const [modalVisible, handleModalVisible] = useState<boolean>(false);
  const [configModalVisible, handleConfigModalVisible] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();

  const extraAction = (
    <Button
      type="primary"
      onClick={() => {
        handleConfigModalVisible(true);
      }}
    >
      告警服务发件邮箱配置
    </Button>
  );

  const columns: ProColumns<MONITOR.AlertItem>[] = [
    {
      title: '序号',
      valueType: 'index',
      width: '10%',
    },
    {
      title: '告警人姓名',
      dataIndex: 'alertName',
      editable: false,
      width: '30%',
    },
    {
      title: '告警人邮箱',
      dataIndex: 'alertEmail',
      width: '40%',
      fieldProps: {
        allowClear: false,
      },
      formItemProps: {
        rules: [
          {
            required: true,
            message: '告警人邮箱不能为空',
          },
          { max: 128, message: '邮箱长度不超过128字符' },
          {
            pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            message: '邮箱格式错误',
          },
        ],
      },
    },
    {
      title: '操作',
      valueType: 'option',
      width: '20%',
      render: (node, record, _, action) => [
        <a
          key="update"
          onClick={() => {
            // @ts-ignore
            action?.startEditable?.(record.key);
          }}
        >
          修改
        </a>,
        <Popconfirm
          key="delete"
          title="确认删除？"
          onConfirm={async () => {
            setLoading(true);
            try {
              await deleteAlert(record.alertEmail);
              message.success('删除成功');
              actionRef.current?.reload();
            } catch (e) {
              message.error('删除失败');
            }
            setLoading(false);
          }}
        >
          <Button type="link" danger>
            删除
          </Button>
        </Popconfirm>,
      ],
    },
  ];
  return (
    <PageContainer content={remark} extra={extraAction}>
      <Spin spinning={isLoading} size="large">
        <ProTable<MONITOR.AlertItem>
          headerTitle="告警人列表"
          columns={columns}
          search={false}
          rowKey="key"
          actionRef={actionRef}
          request={async () => {
            const { code, data } = await getAlertList();
            return {
              data: data || [],
              success: code === RES_SUC,
              total: data.length || 0,
            };
          }}
          postData={(data: MONITOR.AlertItem[]) =>
            data.map((item, index) => {
              return { ...item, key: `alert${index}` };
            })
          }
          toolbar={{
            actions: [
              <Button
                type="primary"
                key="add"
                onClick={() => {
                  handleModalVisible(true);
                }}
              >
                <PlusOutlined /> 新增
              </Button>,
            ],
          }}
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
          editable={{
            type: 'single',
            actionRender: (row, config, dom) => [dom.save, dom.cancel],
            onSave: async (_, data) => {
              try {
                const params: MONITOR.AlertItem = {
                  alertName: data.alertName,
                  alertEmail: data.alertEmail,
                };
                await updateAlert(params);
                message.success('修改成功');
                actionRef.current?.reload();
              } catch (e) {
                message.error('修改失败');
              }
            },
          }}
          form={{
            ignoreRules: false,
          }}
        />
      </Spin>
      <ModalForm
        title="新增告警人"
        width={'30%'}
        modalProps={{
          destroyOnClose: true,
        }}
        visible={modalVisible}
        onVisibleChange={handleModalVisible}
        onFinish={async (value) => {
          try {
            await addAlert(value as MONITOR.AlertItem);
            handleModalVisible(false);
            message.success('新增成功');
            actionRef.current?.reload();
          } catch (e) {
            message.error('新增失败');
          }
        }}
      >
        <ProFormText
          name="alertName"
          label="姓名"
          rules={[
            { required: true, message: '请输入姓名' },
            { max: 64, message: '姓名长度不超过64字符' },
          ]}
        />
        <ProFormText
          name="alertEmail"
          label="邮箱"
          rules={[
            { required: true, message: '请输入邮箱' },
            { max: 128, message: '邮箱长度不超过128字符' },
            {
              pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
              message: '邮箱格式错误',
            },
          ]}
        />
      </ModalForm>
      <ConfigEmailModal
        modalVisible={configModalVisible}
        onFinish={async (values) => {
          try {
            await configAlertEmail(values);
            message.success('配置告警服务发件邮箱成功');
            handleConfigModalVisible(false);
            return Promise.resolve(true);
          } catch (e) {
            message.error('配置告警服务发件邮箱失败');
            return Promise.resolve(false);
          }
        }}
        onCancel={() => {
          handleConfigModalVisible(false);
        }}
      />
    </PageContainer>
  );
};

export default AlertConfig;
