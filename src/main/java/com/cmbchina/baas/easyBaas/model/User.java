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
 
package com.cmbchina.baas.easyBaas.model;

import lombok.Data;

@Data
public class User {
    private Integer id;

    private String userName;

    private String password;

    private String userType;

    private String nodeAddress;

    private String createTime;

    private String updateTime;

    public User(Integer id, String userName, String password, String userType, String nodeAddress, String createTime, String updateTime) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.nodeAddress = nodeAddress;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public User(Integer id, String userName, String password, String userType, String createTime, String updateTime) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public User(String userName, String password, String userType, String nodeAddress, String createTime, String updateTime) {
        this.userName = userName;
        this.password = password;
        this.userType = userType;
        this.nodeAddress = nodeAddress;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public User(String userName, String password, String userType) {
        this.userName = userName;
        this.password = password;
        this.userType = userType;
    }

    public User(String userName, String password, String nodeAddress, String updateTime) {
        this.userName = userName;
        this.password = password;
        this.nodeAddress = nodeAddress;
        this.updateTime = updateTime;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }
}