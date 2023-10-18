import React, { useCallback, useState } from 'react';
import { LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { Avatar, Menu, Spin, message } from 'antd';
import { history, useModel } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import type { MenuInfo } from 'rc-menu/lib/interface';
import { changePassword, logout } from '@/services/userApis';
import Util from '@/utils/util';
import { ModalForm, ProFormText } from '@ant-design/pro-form';

const loginOut = async () => {
  try {
    await logout();
    if (window.location.pathname !== '/login') {
      Util.clearStorage();
      history.replace('/login');
    }
  } catch (e) {
    message.error('退出登录失败');
  }
};

const AvatarDropdown: React.FC = () => {
  const { initialState, setInitialState } = useModel('@@initialState');
  const [modalVisible, setModalVisible] = useState<boolean>(false);

  const handleChangePassword = async (values: USER.PasswordReq) => {
    try {
      await changePassword({
        ...values,
        oldPassword: Util.encryptBySHA265(values.oldPassword),
        newPassword: Util.encryptBySHA265(values.newPassword),
      });
      message.info('密码修改成功，请重新登录');
      setModalVisible(false);
      loginOut();
    } catch (e) {
      message.error('修改密码失败');
    }
  };

  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout' && initialState) {
        loginOut();
        setInitialState({ ...initialState, currentUser: undefined });
      }
      if (key === 'settings') {
        setModalVisible(true);
      }
    },
    [initialState, setInitialState],
  );

  const loading = (
    <span className={`${styles.action} ${styles.account}`}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;

  if (!currentUser || !currentUser.name) {
    return loading;
  }

  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
      <Menu.Item key="settings">
        <SettingOutlined />
        修改密码
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="logout">
        <LogoutOutlined />
        退出登录
      </Menu.Item>
    </Menu>
  );
  return (
    <>
      <HeaderDropdown overlay={menuHeaderDropdown}>
        <span className={`${styles.action} ${styles.account}`}>
          <Avatar size="small" className={styles.avatar} icon={<UserOutlined />} />
          <span className={`${styles.name} anticon`}>{currentUser.name}</span>
        </span>
      </HeaderDropdown>
      <ModalForm
        width={'30%'}
        modalProps={{
          destroyOnClose: true,
          closable: false,
          keyboard: false,
          maskClosable: false,
          onCancel: () => {
            setModalVisible(false);
          },
        }}
        title="修改密码"
        visible={modalVisible}
        onFinish={handleChangePassword}
      >
        <ProFormText
          name="userName"
          label="用户名"
          rules={[
            {
              required: true,
            },
          ]}
        />
        <ProFormText.Password
          name="oldPassword"
          label="旧密码"
          rules={[
            {
              required: true,
            },
          ]}
        />
        <ProFormText.Password
          name="newPassword"
          label="新密码"
          rules={[
            {
              required: true,
            },
          ]}
        />
      </ModalForm>
    </>
  );
};

export default AvatarDropdown;
