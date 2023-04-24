declare namespace API {
  type BasicRes<T> = {
    code: string;
    data: T;
    msg: string;
    success?: boolean;
  };

  type FileLimitRule = {
    name?: {
      value: string;
      errMsg: string;
    };
    size?: {
      value: number;
      errMsg: string;
    };
    type?: {
      value: string[];
      errMsg: string;
    };
  };
}
