import React, { useState, useEffect } from 'react';
import {
  Radio,
  Card,
  Spin,
  Row,
  Col,
  message,
  Divider,
  Button,
  Switch,
  Tooltip,
  Space,
  Dropdown,
  Menu,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import MonacoEditor from 'react-monaco-editor/lib/editor';
import { NODE_LOG_TYPE } from '@/utils/constant';
import { history } from 'umi';
import {
  getNodeLogs,
  getNodeTraceStatus,
  openNodeTrace,
  closeNodeTrace,
  getNodeLogSize,
  clearNodeLog,
} from '@/services/nodeApis';
import { ReloadOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import styles from './index.less';
import Util from '@/utils/util';
import { RES_SERVER_PASSWORD_ERROR } from '@/utils/constant';
import ServerLoginModal from '@/components/ServerLoginModal';

const monacoOptions = {
  fontSize: 12,
  readOnly: true,
  domReadOnly: true,
  wordWrap: 'on' as 'on',
  scrollBeyondLastLine: false,
  minimap: { enabled: false },
};

const OPR_TYPE = {
  SIZE: 'getSize',
  CLEAR: 'clearLog',
};

const NodeLog: React.FC = () => {
  const currentNode: string = history.location.query?.name as string;
  // 节点日志
  const [logType, setLogType] = useState<string>(NODE_LOG_TYPE[0]);
  const [logs, setLogs] = useState<string>('');
  const [isLoading, setLoading] = useState<boolean>(false);
  // trace日志
  const [traceStatus, setTraceStatus] = useState<boolean>();
  const [switchDisabled, setSwitchDisabled] = useState<boolean>(true);
  // 日志文件大小
  const [logSize, setLogSize] = useState<string>();
  const [isClearDisabled, setClearDisabled] = useState<boolean>(true);
  const [isClearLoading, setClearLoading] = useState<boolean>(false);
  const [clearLine, setClearLine] = useState<string>();
  // 服务器密码
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [oprType, setOprType] = useState<string>();

  const queryLog = async (selectedType: string = NODE_LOG_TYPE[0]) => {
    setLoading(true);
    const params: NODE.QueryNodeLogReq = {
      nodeName: currentNode,
      type: selectedType,
      num: 100,
    };
    try {
      const { data } = await getNodeLogs(params);
      setLogs(data.join('\n'));
    } catch (e) {
      setLogs(`获取cita-${logType}日志失败`);
    }
    setLoading(false);
  };

  const monacoDidMount = () => {
    queryLog();
  };

  const getNodeTrace = async () => {
    try {
      const { data } = await getNodeTraceStatus(currentNode);
      setTraceStatus(data);
      setSwitchDisabled(false);
    } catch (e) {
      message.error('获取节点是否开启trace日志失败');
      setSwitchDisabled(true);
    }
  };

  const logTypeChange = (e: any) => {
    setLogType(e.target.value);
    queryLog(e.target.value);
    getNodeTrace();
  };

  useEffect(() => {
    getNodeTrace();
  }, []);

  const switchTrace = async (value: boolean) => {
    setLoading(true);
    setTraceStatus(value);
    message.info(`正在${value ? '开启' : '关闭'}trace日志，请稍后...`);
    try {
      if (value) {
        await openNodeTrace(currentNode);
        message.success('开启trace日志成功');
      } else {
        await closeNodeTrace(currentNode);
        message.success('关闭trace日志成功');
      }
      queryLog(logType);
      setLoading(true);
    } catch (e) {
      message.error(`${value ? '开启' : '关闭'}trace日志失败`);
    }
  };

  const getLogSize = async (user: NODE.SeverUser | undefined = undefined) => {
    const params: NODE.GetLogSizeReq = {
      nodeName: currentNode,
      ...user,
    };
    try {
      const { data } = await getNodeLogSize(params);
      setLogSize(data);
      setClearDisabled(false);
    } catch (e) {
      if (e.data?.code === RES_SERVER_PASSWORD_ERROR) {
        setOprType(OPR_TYPE.SIZE);
        setModalVisible(true);
      } else {
        message.error('获取当前节点日志文件所占空间大小失败');
      }
      setClearDisabled(true);
    }
  };

  const clearLog = async (value: string = '100', user: NODE.SeverUser | undefined = undefined) => {
    setClearLoading(true);
    message.info('正在清理日志文件，请稍后...');
    const params: NODE.ClearLogReq = {
      nodeName: currentNode,
      line: value,
      ...user,
    };
    try {
      const { data } = await clearNodeLog(params);
      message.success(data);
      setClearLoading(false);
      getLogSize();
      queryLog(logType);
    } catch (e) {
      if (e.data?.code === RES_SERVER_PASSWORD_ERROR) {
        setOprType(OPR_TYPE.CLEAR);
        setModalVisible(true);
      } else {
        message.error('清理节点日志文件失败');
        setClearLoading(false);
      }
    }
  };

  useEffect(() => {
    getLogSize();
  }, []);

  const action = (
    <Space>
      日志文件所占空间：{logSize}
      <Dropdown
        trigger={['click']}
        overlay={
          <Menu
            onClick={({ key }) => {
              clearLog(key);
              setClearLine(key);
            }}
          >
            <Menu.Item key="0">不备份日志</Menu.Item>
            <Menu.Item key="1000">备份最近1000条日志</Menu.Item>
          </Menu>
        }
      >
        <Button type="link" disabled={isClearDisabled} loading={isClearLoading}>
          清理
        </Button>
      </Dropdown>
    </Space>
  );

  return (
    <PageContainer
      header={{
        onBack: () => {
          history.goBack();
        },
        breadcrumb: {},
      }}
      content={'查看所选进程的最新100条日志'}
      extraContent={action}
    >
      <Card>
        <Spin spinning={isLoading} size="large">
          <Row className={styles.radios} align="middle" justify="space-between">
            <Col>
              <Radio.Group
                options={NODE_LOG_TYPE.map((item) => {
                  return { label: `cita-${item}`, value: item };
                })}
                optionType="button"
                buttonStyle="solid"
                size="large"
                value={logType}
                onChange={logTypeChange}
              />
            </Col>
            <Col>
              <Row align="middle">
                <Col>
                  <Space align="center">
                    <span>trace日志</span>
                    <Tooltip
                      title={
                        <>
                          1、开启之后，将会在日志中打印更多信息
                          <br />
                          2、开启或关闭本功能，需要重启节点
                        </>
                      }
                    >
                      <QuestionCircleOutlined /> :
                    </Tooltip>
                    <Switch
                      checkedChildren={'开启'}
                      unCheckedChildren={'关闭'}
                      checked={traceStatus}
                      disabled={switchDisabled}
                      onChange={async (value) => {
                        await switchTrace(value);
                      }}
                    />
                  </Space>
                </Col>
                <Col>
                  <Divider type="vertical" />
                  <Button
                    type="text"
                    onClick={() => {
                      queryLog(logType);
                    }}
                  >
                    <ReloadOutlined />
                  </Button>
                </Col>
              </Row>
            </Col>
          </Row>
          <MonacoEditor
            width={'95%'}
            height={650}
            options={monacoOptions}
            value={logs}
            editorDidMount={monacoDidMount}
          />
        </Spin>
        <ServerLoginModal
          modalVisible={modalVisible}
          onFinish={async (value) => {
            const user = {
              username: value.username,
              password: Util.encryptByAES(value.password),
            };
            setModalVisible(false);
            switch (oprType) {
              case OPR_TYPE.SIZE:
                getLogSize(user);
                break;
              case OPR_TYPE.CLEAR:
                clearLog(clearLine, user);
                break;
              default:
                break;
            }
            return Promise.resolve(true);
          }}
          onCancel={() => {
            setModalVisible(false);
          }}
        />
      </Card>
    </PageContainer>
  );
};

export default NodeLog;
