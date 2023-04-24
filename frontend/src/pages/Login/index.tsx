import { LockOutlined, UserOutlined, SafetyOutlined } from '@ant-design/icons';
import { message, Form, Row, Col } from 'antd';
import React, { useState, useEffect } from 'react';
import ProForm, { ProFormText } from '@ant-design/pro-form';
import { history, useModel } from 'umi';
import { login, getCaptcha } from '@/services/userApis';
import styles from './index.less';
import Util from '@/utils/util';

const Login: React.FC = () => {
  const VR_CODE_TYPE = 'loginVerifyCode';
  const [submitting, setSubmitting] = useState(false);
  const [vrCode, setVrCode] = useState('');
  const { initialState, setInitialState } = useModel('@@initialState');

  // 获取验证码
  const getVrCode = async () => {
    try {
      const data = await getCaptcha(VR_CODE_TYPE);
      const url = window.URL.createObjectURL(new Blob([data], { type: 'image/png' }));
      setVrCode(url);
    } catch (e) {
      message.error('获取验证码失败！');
      setVrCode('');
    }
  };

  useEffect(() => {
    getVrCode();
  }, []);

  const fetchUser = async () => {
    const user = initialState?.fetchUser?.();
    if (user) {
      await setInitialState({
        ...initialState,
        currentUser: user,
      });
    }
  };

  const handleSubmit = async (values: USER.loginReq) => {
    setSubmitting(true);
    // 登录
    try {
      const { data } = await login({
        ...values,
        password: Util.encryptBySHA265(values.password),
      });
      message.success('登录成功');
      Util.setStorageItem('name', values.userName);
      Util.setStorageItem('token', data.token);
      Util.setStorageItem('algorithmCode', data.algorithmCode);
      await fetchUser();
      history.push('/welcome');
      return;
    } catch (e) {
      await getVrCode();
    }
    setSubmitting(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            {/*<img alt="logo" className={styles.logo} src="/logo.png" />*/}
          </div>
          <div className={styles.desc}>{'帮助用户更好的管理、运维、监控区块链节点'}</div>
        </div>

        <div className={styles.main}>
          <ProForm
            submitter={{
              searchConfig: {
                submitText: '登录',
              },
              render: (_, dom) => dom.pop(),
              submitButtonProps: {
                loading: submitting,
                size: 'large',
                style: {
                  width: '100%',
                },
              },
            }}
            onFinish={async (values) => {
              handleSubmit(values as USER.loginReq);
            }}
          >
            {
              <>
                <ProFormText
                  name="userName"
                  fieldProps={{
                    size: 'large',
                    prefix: <UserOutlined className={styles.prefixIcon} />,
                  }}
                  placeholder={'用户名'}
                  rules={[
                    {
                      required: true,
                      message: '用户名是必填项！',
                    },
                  ]}
                />
                <ProFormText.Password
                  name="password"
                  fieldProps={{
                    size: 'large',
                    prefix: <LockOutlined className={styles.prefixIcon} />,
                  }}
                  placeholder={'密码'}
                  rules={[
                    {
                      required: true,
                      message: '密码是必填项！',
                    },
                  ]}
                />
                <Form.Item>
                  <Row justify={'space-between'}>
                    <Col span={17}>
                      <ProFormText
                        name="verifyCode"
                        fieldProps={{
                          size: 'large',
                          prefix: <SafetyOutlined className={styles.prefixIcon} />,
                        }}
                        placeholder={'请输入验证码'}
                        rules={[
                          {
                            required: true,
                            message: '验证码是必填项!',
                          },
                        ]}
                      />
                    </Col>
                    <Col span={6}>
                      <img src={vrCode} alt={'加载失败，点击刷新'} onClick={getVrCode} />
                    </Col>
                  </Row>
                </Form.Item>
              </>
            }
          </ProForm>
        </div>
      </div>
    </div>
  );
};

export default Login;
