/*
 * 公共方法
 * */
import CryptoJs from 'crypto-js';
import type { RcFile } from 'antd/lib/upload';

const storage = window.sessionStorage;

const Util = {
  // sha256加密
  encryptBySHA265(password: string) {
    return CryptoJs.SHA256(password).toString();
  },

  // AES加密
  encryptByAES(password: string) {
    const key = this.getStorageItem('algorithmCode');
    const secretKey = CryptoJs.enc.Utf8.parse(key);
    const content = CryptoJs.enc.Utf8.parse(password);
    const a = CryptoJs.AES.encrypt(content, secretKey, {
      mode: CryptoJs.mode.ECB,
      padding: CryptoJs.pad.Pkcs7,
    });
    return a.toString();
  },

  /* storage操作 */
  setStorageItem(key: string = '', value: string = '') {
    storage.setItem(key, value);
  },
  getStorageItem(key: string = '') {
    return storage.getItem(key) || '';
  },
  removeStorageItem(key: string = '') {
    storage.removeItem(key);
  },
  clearStorage() {
    storage.clear();
  },

  /* 文件下载 */
  downloadBlobFile(blob: Blob, fileName: string = 'downloadFile') {
    const link = document.createElement('a');
    const url = window.URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', fileName);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  // 构造文件上传的formData数据
  generateFormData(params: any = {}): FormData {
    const formData = new FormData();
    if (params) {
      Object.keys(params).forEach((key) => {
        formData.append(key, params[key]);
      });
    }
    return formData;
  },

  // 校验文件格式、大小、名称等
  checkFile(file: RcFile, fileLimit: API.FileLimitRule): string {
    if (!file) {
      return '';
    }
    const fileSize = file.size;
    const fileName = file.name;
    // @ts-ignore
    const fileType = file.name.match(/\.[a-zA-Z\.]+$/)[0];
    if (fileLimit.name && fileName !== fileLimit.name.value) {
      return fileLimit.name.errMsg || '';
    }
    if (fileLimit.type && !fileLimit.type.value.includes(fileType)) {
      return fileLimit.type.errMsg;
    }
    if (fileLimit.size && fileSize >= fileLimit.size.value) {
      return fileLimit.size.errMsg;
    }
    return '';
  },
};

export default Util;
