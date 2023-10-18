import React, { useState } from 'react';
import { ModalForm, ProFormText } from '@ant-design/pro-form';
import { Modal, Button, Typography, Image } from 'antd';
import smtpImage from '@/assets/smtpConfig.jpg';

type ModalProps = {
  modalVisible: boolean;
  onFinish: (values: MONITOR.ConfigEmailReq) => Promise<boolean>;
  onCancel: () => void;
};

const EmailConfigModal: React.FC<ModalProps> = (props) => {
  const { modalVisible, onFinish, onCancel } = props;
  const [remarkVisible, handleRemarkVisible] = useState<boolean>(false);
  const { Paragraph } = Typography;

  return (
    <>
      <ModalForm
        title="配置告警服务发件邮箱"
        width={'40%'}
        modalProps={{
          destroyOnClose: true,
          onCancel: () => {
            onCancel();
          },
        }}
        visible={modalVisible}
        onFinish={onFinish}
      >
        <ProFormText
          name="alarmUsername"
          label="邮箱地址"
          rules={[
            {
              required: true,
            },
          ]}
        />
        <ProFormText
          name="alarmPassword"
          label="授权码"
          rules={[
            {
              required: true,
            },
          ]}
        />
        <ProFormText
          name="smtpAddress"
          label="smtp地址"
          rules={[
            {
              required: true,
            },
          ]}
        />
        <Button
          type="link"
          onClick={() => {
            handleRemarkVisible(true);
          }}
        >
          填写说明
        </Button>
      </ModalForm>
      <Modal
        title="填写说明"
        width={'70%'}
        visible={remarkVisible}
        onCancel={() => {
          handleRemarkVisible(false);
        }}
        footer={null}
      >
        <Typography>
          <Paragraph>1、参考下图，开启smtp服务，生成授权码并记录</Paragraph>
          <Image src={smtpImage} width={'80%'} />
          <Paragraph>2、将获取的授权码与邮箱对应的smtp地址填入表单中</Paragraph>
          <Paragraph>
            <ul>
              <li>qq邮箱smtp地址：smtp.qq.com:465</li>
              <li>163邮箱smtp地址：smtp.163.com:465</li>
              <li>126邮箱smtp地址：smtp.126.com:465</li>
            </ul>
          </Paragraph>
        </Typography>
      </Modal>
    </>
  );
};

export default EmailConfigModal;
