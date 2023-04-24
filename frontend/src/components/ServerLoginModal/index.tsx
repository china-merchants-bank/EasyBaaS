import React from 'react';
import { ModalForm, ProFormText } from '@ant-design/pro-form';

type CreateFormProps = {
  modalVisible: boolean;
  onFinish: (values: NODE.SeverUser) => Promise<boolean>;
  onCancel: () => void;
};

const ServerLoginModal: React.FC<CreateFormProps> = (props) => {
  const { modalVisible, onFinish, onCancel } = props;

  return (
    <ModalForm
      width={'30%'}
      modalProps={{
        destroyOnClose: true,
        closable: false,
        keyboard: false,
        maskClosable: false,
        onCancel: () => {
          onCancel();
        },
      }}
      title="请输入服务器的用户名密码"
      visible={modalVisible}
      onFinish={onFinish}
    >
      <ProFormText
        name="username"
        label="用户名"
        rules={[
          {
            required: true,
          },
        ]}
      />
      <ProFormText.Password
        name="password"
        label="密码"
        rules={[
          {
            required: true,
          },
        ]}
      />
    </ModalForm>
  );
};

export default ServerLoginModal;
