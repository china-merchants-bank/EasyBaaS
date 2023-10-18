/*
 * request配置
 * */
import type { RequestOptionsInit } from 'umi-request';
import type { RequestConfig } from 'umi';
import { notification } from 'antd';
import { RES_SESSION_OUT, RES_SUC, RES_TOKEN_ERROR } from '@/utils/constant';
import { message } from 'antd';
import { history } from 'umi';
import Util from '@/utils/util';

const codeMessage: Record<number | string, string> = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '请求路径不存在。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
  [RES_SESSION_OUT]: '用户未登录或登录已过期，请重新登录!',
  [RES_TOKEN_ERROR]: '用户token校验失败，请重新登录！',
};

/** 异常处理程序 */
const errorHandler = (error: any) => {
  const { response } = error;
  if (response && response.status) {
    const errorText = codeMessage[response.status] || response.statusText;
    const { status, url } = response;

    notification.error({
      message: `请求错误 ${status}: ${url}`,
      description: errorText,
    });
  } else if (error.name === 'BizError') {
    message.error(error.info.errorMessage);
  } else if (!response) {
    notification.error({
      description: '您的网络发生异常，无法连接服务器',
      message: '网络异常',
    });
  }
  throw error;
};

// request拦截器--请求头添加token
const authHeaderInterceptor = (url: string, options: RequestOptionsInit) => {
  if (url.includes('/admin/getVerCode') || url.includes('/admin/login')) {
    return {
      url,
      options: { ...options, headers: { 'Content-Type': 'application/json' } },
    };
  }
  const authHeader = { Authorization: Util.getStorageItem('token') };
  if (url.includes('/citaNode/deployNode')) {
    return {
      url,
      options: { ...options, headers: { ...authHeader } },
    };
  }
  return {
    url,
    options: {
      ...options,
      headers: { ...authHeader, 'Content-Type': 'application/json' },
    },
  };
};

// request拦截器--响应处理登录超时或token错误
const responseInterceptor = async (response: Response): Promise<any> => {
  if (response.headers.get('content-type') === 'application/octet-stream') {
    return response.clone().blob();
  }
  const res = await response.clone().json();
  if ((res && res.code === RES_SESSION_OUT) || res.code === RES_TOKEN_ERROR) {
    Util.clearStorage();
    message.error(codeMessage[res.code]);
    history.replace('/login');
    return { ...response };
  }
  return response;
};

/** 配置request请求时的默认参数 */
const request: RequestConfig = {
  errorConfig: {
    adaptor: (resData: API.BasicRes<any>) => {
      return {
        success: resData.code === RES_SUC,
        data: resData.data,
        errorMessage: resData.msg,
      };
    },
  },
  errorHandler, // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
  prefix: '/easyBaas',
  // @ts-ignore
  requestInterceptors: [authHeaderInterceptor],
  responseInterceptors: [responseInterceptor],
};

export default request;
