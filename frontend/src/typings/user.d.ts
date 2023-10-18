declare namespace USER {
  type CurrentUser = {
    name: string;
    token: string;
  };

  type VrCodeReq = 'loginVerifyCode' | 'resetVerifyCode';

  type loginReq = {
    userName: string;
    password: string;
    verifyCode: string;
  };

  type loginRes = {
    token: string;
    algorithmCode: string; // 服务器密码AES加密密钥
  };

  type PasswordReq = {
    userName: string;
    oldPassword: string;
    newPassword: string;
  };
}
