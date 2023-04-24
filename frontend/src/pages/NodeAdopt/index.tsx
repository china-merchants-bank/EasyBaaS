import React, { useRef, useState } from 'react';
import { Card, Alert, message, Spin } from 'antd';
import type { FormInstance } from 'antd';
import ProForm, { ProFormText } from '@ant-design/pro-form';
import { PageContainer } from '@ant-design/pro-layout';
import style from './index.less';
import { adoptNode } from '@/services/nodeApis';
import Util from '@/utils/util';
import { RES_SERVER_PASSWORD_ERROR, RES_SERVER_NEED_PASSWORD } from '@/utils/constant';
import ServerLoginModal from '@/components/ServerLoginModal';

const NodeAdopt: React.FC = () => {
  const formRef = useRef<FormInstance>();
  const [isLoading, setLoading] = useState<boolean>(false);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [serverUser, setServerUser] = useState<NODE.SeverUser>();

  const onFinish = async (values: Record<string, any>) => {
    setLoading(true);
    const params: NODE.AdoptNodeReq = {
      nodeName: values.node,
      nodeAddress: values.nodeAddress,
      filePath: values.filePath,
      ...serverUser,
    };
    try {
      await adoptNode(params);
      message.success('节点纳管成功，请视情况清理原始数据');
      setLoading(false);
      formRef.current?.resetFields();
    } catch (e) {
      if (e.data?.code === RES_SERVER_PASSWORD_ERROR || e.data?.code === RES_SERVER_NEED_PASSWORD) {
        setModalVisible(true);
      } else {
        message.error('节点纳管失败');
      }
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <Card bordered={false}>
        <Alert
          className={style.tips}
          message={'注意：纳管节点需要使用有操作节点文件权限的用户，且纳管过程中，节点将会进行重启'}
          type="warning"
          showIcon
        />
        <Spin spinning={isLoading} size="large">
          <ProForm
            className={style.form}
            submitter={{
              searchConfig: {
                submitText: '立即导入',
                resetText: '清空',
              },
            }}
            onFinish={onFinish}
            validateTrigger="onBlur"
            formRef={formRef}
          >
            <ProFormText
              name="nodeAddress"
              label="节点部署主机IP"
              rules={[
                {
                  required: true,
                  message: '请输入节点部署主机IP',
                },
                {
                  pattern: /^((2[0-4]\d|25[0-5]|[01]?\d\d?)\.){3}(2[0-4]\d|25[0-5]|[01]?\d\d?)$/,
                  message: '请输入正确的IP地址',
                },
              ]}
            />
            <ProFormText
              name="node"
              label="节点名称"
              proFieldProps={{ proFieldKey: 'nodeName' }}
              rules={[
                {
                  required: true,
                  message: '请输入节点名称',
                },
                {
                  pattern: /^[A-Za-z0-9]{1,16}$/,
                  message: '节点名称只能包含字母、数字，且不能超过16位',
                },
              ]}
            />
            <ProFormText
              name="filePath"
              label="节点文件路径"
              placeholder="以/开头"
              rules={[
                {
                  required: true,
                  message: '请输入节点文件路径',
                },
                {
                  pattern: /^\/+/,
                  message: '节点文件路径必须以/开头',
                },
              ]}
            />
          </ProForm>
        </Spin>
        <ServerLoginModal
          modalVisible={modalVisible}
          onFinish={(value) => {
            setServerUser({
              username: value.username,
              password: Util.encryptByAES(value.password),
            });
            setModalVisible(false);
            formRef.current?.submit();
            return Promise.resolve(true);
          }}
          onCancel={() => {
            setServerUser(undefined);
            setModalVisible(false);
          }}
        />
      </Card>
    </PageContainer>
  );
};

export default NodeAdopt;
