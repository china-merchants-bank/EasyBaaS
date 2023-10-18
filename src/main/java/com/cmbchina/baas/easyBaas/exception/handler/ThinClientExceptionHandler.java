/**
 *    Copyright (c) 2023 招商银行股份有限公司
 *    EasyBaaS is licensed under Mulan PSL v2.
 *    You can use this software according to the terms and conditions of the Mulan PSL v2.
 *    You may obtain a copy of Mulan PSL v2 at:
 *                http://license.coscl.org.cn/MulanPSL2
 *    THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *    EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *    MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *    See the Mulan PSL v2 for more details.
 */
 
package com.cmbchina.baas.easyBaas.exception.handler;

import cn.hutool.json.JSONException;
import com.cmbchina.baas.easyBaas.constant.ConstantsContainer;
import com.cmbchina.baas.easyBaas.exception.ErrorCodes;
import com.cmbchina.baas.easyBaas.exception.exceptions.BlockHeightException;
import com.cmbchina.baas.easyBaas.exception.exceptions.CreateApplicationException;
import com.cmbchina.baas.easyBaas.exception.exceptions.DecryptException;
import com.cmbchina.baas.easyBaas.exception.exceptions.FileContentException;
import com.cmbchina.baas.easyBaas.exception.exceptions.FileFormatException;
import com.cmbchina.baas.easyBaas.exception.exceptions.GetBlockHeightException;
import com.cmbchina.baas.easyBaas.exception.exceptions.IsNotHaveTelnetException;
import com.cmbchina.baas.easyBaas.exception.exceptions.LoginShellException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NetworkTelnetException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeAddressException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNameException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeNotExistException;
import com.cmbchina.baas.easyBaas.exception.exceptions.NodeVersionException;
import com.cmbchina.baas.easyBaas.exception.exceptions.PrivateKeyOrAddressException;
import com.cmbchina.baas.easyBaas.exception.exceptions.UnZipException;
import com.cmbchina.baas.easyBaas.response.Response;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ThinClientExceptionHandler {

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Response handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.PARAM_NOT_COMPLETE.getCode())).
                msg(ErrorCodes.PARAM_NOT_COMPLETE.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Response handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.PARAM_TYPE_ERROR.getCode())).
                msg(ErrorCodes.PARAM_TYPE_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public Response handleConstraintViolationException(ConstraintViolationException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.PARAM_IS_INVALID.getCode())).
                msg(ErrorCodes.PARAM_IS_INVALID.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        StringBuilder stringBuilder = new StringBuilder();
        allErrors.forEach(objectError -> {
            stringBuilder.append(objectError.getDefaultMessage()).append(";");
        });
        return Response.builder().code(String.valueOf(ErrorCodes.PARAM_IS_INVALID.getCode())).
                msg(ErrorCodes.PARAM_IS_INVALID.getMessage() + ConstantsContainer.IP_PORT_SEP + stringBuilder.toString()).data(null).build();
    }

    @ExceptionHandler(value = MultipartException.class)
    public Response handleMultipartException(MultipartException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.FILEDATA_DATA_ERROR.getCode())).
                msg(ErrorCodes.FILEDATA_DATA_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = JSONException.class)
    public Response handleJSONException(JSONException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.VALIDATE_TOKEN_FAILED_ERROR.getCode())).
                msg(ErrorCodes.VALIDATE_TOKEN_FAILED_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = LoginShellException.class)
    public Response loginShellException(LoginShellException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).
                msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NoSuchAlgorithmException.class)
    public Response noSuchAlgorithmException(NoSuchAlgorithmException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.SFTP_LOGIN_FAILED.getCode())).
                msg(ErrorCodes.SFTP_LOGIN_FAILED.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NodeNameException.class)
    public Response nodeNameExistsException(NodeNameException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.NODE_NAME_ERROR.getCode())).
                msg(ErrorCodes.NODE_NAME_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = PrivateKeyOrAddressException.class)
    public Response privateKeyOrAddressException(PrivateKeyOrAddressException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.PRIVATEKEY_ADDRESS_ERROR.getCode())).
                msg(ErrorCodes.PRIVATEKEY_ADDRESS_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = FileFormatException.class)
    public Response fileFormatException(FileFormatException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.FILEDATA_TYPE_ERROR.getCode())).
                msg(ErrorCodes.FILEDATA_TYPE_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = FileContentException.class)
    public Response fileContentException(FileContentException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.FILEDATA_CONTENT_ERROR.getCode())).
                msg(ErrorCodes.FILEDATA_CONTENT_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = UnZipException.class)
    public Response unZipException(UnZipException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.FILEDATA_UNPACKAGING_FAIL.getCode())).
                msg(ErrorCodes.FILEDATA_UNPACKAGING_FAIL.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = IOException.class)
    public Response iOException(IOException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.FILEDATA_OPERATOR_FAIL.getCode())).
                msg(ErrorCodes.FILEDATA_OPERATOR_FAIL.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NodeAddressException.class)
    public Response nodeAddressException(NodeAddressException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.NODE_ADDRESS_ERROR.getCode())).
                msg(ErrorCodes.NODE_ADDRESS_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NetworkTelnetException.class)
    public Response networkTelnetException(NetworkTelnetException exception) {
        return Response.builder().code(String.valueOf(ErrorCodes.NETWORK_TELNET_ERROR.getCode())).
                msg(ErrorCodes.NETWORK_TELNET_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NodeNotExistException.class)
    public Response nodeNotExistException(NodeNotExistException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.NODE_NOT_EXIST.getCode())).
                msg(ErrorCodes.NODE_NOT_EXIST.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = NodeVersionException.class)
    public Response nodeVersionException(NodeVersionException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.NODE_VERSION_ERROR.getCode())).
                msg(ErrorCodes.NODE_VERSION_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = CreateApplicationException.class)
    public Response createApplicationException(CreateApplicationException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.CREATE_APPLICATION_ERROR.getCode())).
                msg(ErrorCodes.CREATE_APPLICATION_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = BlockHeightException.class)
    public Response blockHeightException(BlockHeightException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.BLOCK_HEIGHT_ERROR.getCode())).
                msg(ErrorCodes.BLOCK_HEIGHT_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = IsNotHaveTelnetException.class)
    public Response isNotHaveTelnetException(IsNotHaveTelnetException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.IS_NOT_HAVE_TELNET.getCode())).
                msg(ErrorCodes.IS_NOT_HAVE_TELNET.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = GetBlockHeightException.class)
    public Response getBlockHeightException(GetBlockHeightException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.GET_BLOCK_HEIGHT_ERROR.getCode())).
                msg(ErrorCodes.GET_BLOCK_HEIGHT_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = DecryptException.class)
    public Response decryptException(DecryptException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.PASSWORD_DECRYPT_ERROR.getCode())).
                msg(ErrorCodes.PASSWORD_DECRYPT_ERROR.getMessage()).data(null).build();
    }

    @ExceptionHandler(value = JSchException.class)
    public Response jschException(JSchException exception) {
        log.error(exception.getMessage());
        return Response.builder().code(String.valueOf(ErrorCodes.SERVER_OPERATOR_ERROR.getCode())).
                msg(ErrorCodes.SERVER_OPERATOR_ERROR.getMessage()).data(null).build();
    }

}
