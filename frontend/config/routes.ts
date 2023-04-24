export default [
  {
    path: '/login',
    layout: false,
    routes: [{ name: '登录', path: '/login', component: './Login' }],
  },
  {
    icon: 'smile',
    name: '欢迎使用',
    path: '/welcome',
    component: './Welcome',
    // hideInMenu: true,
    menuHeaderRender: false,
    access: 'isLogin',
  },
  {
    icon: 'deploymentUnit',
    name: '节点管理',
    path: '/node',
    routes: [
      { path: '/node', redirect: '/node/deploy' },
      { name: '节点部署', path: 'deploy', component: './NodeDeploy' },
      { name: '已有节点纳管', path: 'adopt', component: './NodeAdopt' },
      { name: '节点维护', path: 'list', component: './NodeList' },
      { name: '节点日志', path: 'log', component: './NodeLog', hideInMenu: true },
    ],
    access: 'isLogin',
  },
  {
    icon: 'barChart',
    name: '监控服务',
    path: '/monitor',
    routes: [
      { path: '/monitor', redirect: '/monitor/list' },
      { name: '监控维护', path: 'list', component: './MonitorList' },
      { name: '告警配置', path: 'config', component: './AlertConfig' },
    ],
    access: 'isLogin',
  },
  { path: '/', redirect: '/login' },
  { component: './404' },
];
