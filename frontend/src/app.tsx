import type { Settings as LayoutSettings } from '@ant-design/pro-layout';
import { PageLoading } from '@ant-design/pro-layout';
import type { RequestConfig, RunTimeLayoutConfig } from 'umi';
import { history } from 'umi';
import RightContent from '@/components/RightContent';
import customRequest from '@/services/request';
import Util from '@/utils/util';

const loginPath = '/login';

/** 获取用户信息比较慢的时候会展示一个 loading */
export const initialStateConfig = {
  loading: <PageLoading />,
};

export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: USER.CurrentUser;
  fetchUser?: () => USER.CurrentUser | undefined;
}> {
  const fetchUser = () => {
    const name = Util.getStorageItem('name');
    const token = Util.getStorageItem('token');
    if (name && token) {
      return { name, token };
    }
    return undefined;
  };
  // 如果是登录页面，不执行
  if (history.location.pathname !== loginPath) {
    const currentUser = fetchUser();
    return {
      fetchUser,
      currentUser,
      settings: {},
    };
  }
  return {
    fetchUser,
    settings: {},
  };
}

// request错误处理适配@umijs/plugin-request接口规范
export const request: RequestConfig = customRequest;

// ProLayout配置
export const layout: RunTimeLayoutConfig = ({ initialState }) => {
  return {
    pageTitleRender: () => '', // title不添加页面路由名称
    rightContentRender: () => <RightContent />,
    disableContentMargin: false,
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser?.token && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },

    onMenuHeaderClick: () => false,
    ...initialState?.settings,
  };
};
