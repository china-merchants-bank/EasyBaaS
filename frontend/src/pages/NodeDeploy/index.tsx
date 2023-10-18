import React, { useRef, useState } from 'react';
import { Card, Alert, message, Spin } from 'antd';
import type { FormInstance } from 'antd';
import ProForm, { ProFormText, ProFormUploadButton } from '@ant-design/pro-form';
import { PageContainer } from '@ant-design/pro-layout';
import style from './index.less';
import { deployNode } from '@/services/nodeApis';
import Util from '@/utils/util';
import { RES_SERVER_PASSWORD_ERROR } from '@/utils/constant';
import ServerLoginModal from '@/components/ServerLoginModal';
import type { RcFile } from 'antd/lib/upload';

const NodeDeploy: React.FC = () => {
  const formRef = useRef<FormInstance>();
  const [isLoading, setLoading] = useState<boolean>(false);
  const [configFile, setConfigFile] = useState<RcFile | null>(null);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [serverUser, setServerUser] = useState<NODE.SeverUser>();

  const onFinish = async (values: Record<string, any>) => {
    if (!configFile) {
      message.error('未选择配置文件或所选文件不符合要求，请重新选择文件');
      return;
    }
    setLoading(true);
    message.info('节点正在部署中，预计耗时20~30秒，请耐心等待...');
    const params: NODE.DeployNodeReq = {
      nodeName: values.node,
      nodeAddress: values.nodeAddress,
      privateKey: values.privateKey,
      address: values.address,
      nodeConfigFile: configFile,
      ...serverUser,
    };
    try {
      await deployNode(Util.generateFormData(params));
      message.success('节点部署成功，请前往节点维护页面查看');
      setLoading(false);
      formRef.current?.resetFields();
    } catch (e) {
      if (e.data.code === RES_SERVER_PASSWORD_ERROR) {
        setModalVisible(true);
      }
      message.error('节点部署失败');
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <Card bordered={false}>
        <Alert
          className={style.tips}
          message={
            '私钥以及地址请谨慎保存并进行确认。'
          }
          type="warning"
          showIcon
        />
        <Spin spinning={isLoading} size="large">
          <ProForm
            className={style.form}
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
            <ProFormUploadButton
              name="nodeConfigFile"
              label="配置文件（请联系节点运营方获取或自行准备）"
              title="选择文件"
              extra="支持格式：.zip，大小限制：1MB"
              accept=".zip"
              fieldProps={{
                listType: 'text',
                maxCount: 1,
                beforeUpload: (file) => {
                  const rule: API.FileLimitRule = {
                    type: {
                      value: ['.zip'],
                      errMsg: '节点配置文件格式只支持".zip"，请重新选择文件',
                    },
                    size: {
                      value: 1024 * 1024,
                      errMsg: '节点配置文件不能超过1MB，请重新选择文件',
                    },
                  };
                  const checkFileResult = Util.checkFile(file, rule);
                  if (checkFileResult) {
                    message.warning(checkFileResult);
                    setConfigFile(null);
                    return false;
                  }
                  setConfigFile(file);
                  return false;
                },
              }}
              rules={[
                {
                  required: true,
                  message: '请上传节点配置文件',
                },
              ]}
            />
            <ProFormText
              name="privateKey"
              label="私钥"
              rules={[
                {
                  required: true,
                  message: '请输入私钥',
                },
                {
                  pattern: /^0x[0-9a-fA-F]{0,68}$/,
                  message: '私钥以0x开头，且不超过70位',
                },
              ]}
            />
            <ProFormText
              name="address"
              label="address"
              rules={[
                {
                  required: true,
                  message: '请输入address',
                },
                {
                  pattern: /^0x[0-9a-fA-F]{0,48}$/,
                  message: 'address以0x开头，且不超过50位',
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

export default NodeDeploy;
