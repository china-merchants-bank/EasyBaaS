import { request } from 'umi';

// 获取验证码
export async function getCaptcha(type: USER.VrCodeReq) {
  return request('/admin/getVerCode', {
    method: 'GET',
    params: { type },
    responseType: 'blob',
    skipErrorHandler: true,
  });
}

// 用户登录
export async function login(params: USER.loginReq) {
  return request<API.BasicRes<USER.loginRes>>('/admin/login', {
    method: 'POST',
    data: params,
  });
}

// 用户登出
export async function logout() {
  return request<API.BasicRes<string>>('/admin/logout');
}


// 用户修改密码
export async function changePassword(params: USER.PasswordReq) {
  return request<API.BasicRes<null>>('/admin/changePassword', {
    method: 'PUT',
    data: params,
  });
}
