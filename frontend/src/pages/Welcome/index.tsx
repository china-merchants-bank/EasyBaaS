import React from 'react';
import { Typography, Card } from 'antd';

const Welcome: React.FC = () => {
  const { Title, Paragraph } = Typography;
  return (
    <Card>
      <Typography>
        <Title level={3}>欢迎使用EasyBaaS</Title>
        <Paragraph>为了帮助用户更好的管理、运维、监控节点，我们提供以下功能：</Paragraph>
        <Paragraph>1、节点部署：进行区块链节点的部署</Paragraph>
        <Paragraph>
          2、节点维护：查看已部署节点的相关信息与运行状态，提供节点启停、日志查看的操作
        </Paragraph>
        <Paragraph>3、节点升级：依据配置文件以及镜像提供节点版本升级功能</Paragraph>
        <Paragraph>4、开启trace日志：打开节点trace级别日志，提供更多日志信息</Paragraph>
        <Paragraph>5、日志清理：清理节点产生的日志，释放空间</Paragraph>
        <Paragraph>6、已有节点纳管：将使用Bin方式部署的已经存在的节点纳入客户端进行管理</Paragraph>
        <Paragraph>7、监控维护：查看当前监控服务各组件的运行情况，提供组件启停功能</Paragraph>
        <Paragraph>8、监控查看：前往grafana页面查看目前服务器以及节点的各项指标数据</Paragraph>
        <Paragraph>9、告警配置：配置发件邮箱以及收件邮箱，接收节点或服务器异常警告的通知</Paragraph>
        <Paragraph>10、应用集成：提供中间件镜像以及使用文档的下载</Paragraph>
      </Typography>
    </Card>
  );
};

export default Welcome;
