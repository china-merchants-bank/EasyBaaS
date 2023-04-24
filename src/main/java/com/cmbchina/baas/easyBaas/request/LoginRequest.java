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

package com.cmbchina.baas.easyBaas.request;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public class LoginRequest {
    @JsonProperty("userName")
    private String userName = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("verifyCode")
    private String verifyCode = null;

    public LoginRequest userName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * Get userName
     *
     * @return userName
     **/
    @ApiModelProperty(example = "admin", required = true, value = "")
    @NotNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LoginRequest password(String password) {
        this.password = password;
        return this;
    }


    /**
     * Get password
     *
     * @return password
     **/
    @ApiModelProperty(example = "adminpw", required = true, value = "")
    @NotNull
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public LoginRequest verifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
        return this;
    }

    /**
     * Get verCode
     *
     * @return verCode
     **/
    @ApiModelProperty(example = "2j01", required = true, value = "")
    @NotNull
    public String getVerifyCode() {
        return verifyCode;
    }

    public void setverifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginRequest loginRequest = (LoginRequest) o;
        return Objects.equals(this.userName, loginRequest.userName)
                && Objects.equals(this.password, loginRequest.password)
                && Objects.equals(this.verifyCode, loginRequest.verifyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password, verifyCode);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LoginRequest {\n");

        sb.append("    userName: ").append(toIndentedString(userName)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    verifyCode: ").append(toIndentedString(verifyCode)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

