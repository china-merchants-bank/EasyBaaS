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
 
package com.cmbchina.baas.easyBaas.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface TableMapper {
    @Update("CREATE TABLE IF NOT EXISTS smtp_info "
            +
            "(id INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, email VARCHAR (64) UNIQUE NOT NULL, auth_code VARCHAR (64) "
            +
            "UNIQUE NOT NULL, smtp_host VARCHAR (64), create_time DATETIME NOT NULL, update_time DATETIME NOT NULL);")
    void createSMTPInfoTable();

    @Update("CREATE TABLE IF NOT EXISTS alerter "
            +
            "(id INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, name VARCHAR (32) UNIQUE NOT NULL, email VARCHAR (64) "
            +
            "UNIQUE NOT NULL, create_time DATETIME NOT NULL, update_time DATETIME NOT NULL);")
    void createAlerterTable();

    @Update("CREATE TABLE IF NOT EXISTS monitor_info "
            +
            "(id INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, name VARCHAR (40) NOT NULL, host VARCHAR (40) NOT NULL, deploy_status VARCHAR (1) NOT NULL, "
            +
            "create_time DATETIME NOT NULL, update_time DATETIME NOT NULL);")
    void createMonitorInfoTable();

    @Update("CREATE TABLE IF NOT EXISTS node_info "
            +
            "(id INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, node_name VARCHAR (32) UNIQUE ON CONFLICT FAIL, address VARCHAR (64), "
            +
            "host VARCHAR (64) NOT NULL, deploy_status VARCHAR (1) NOT NULL, version VARCHAR (32) NOT NULL,"
            +
            "network_name VARCHAR (32),network_path VARCHAR (128),node_folder VARCHAR (128),create_time DATETIME NOT NULL, update_time DATETIME NOT NULL);")
    void createNodeInfoTable();

    @Update("CREATE TABLE IF NOT EXISTS user "
            +
            "(id INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, user_name VARCHAR (32) , password VARCHAR (64), "
            + "user_type VARCHAR (10), node_address VARCHAR(64), create_time DATETIME, update_time DATETIME);")
    void createUserTable();

    @Insert("INSERT INTO user "
            +
            "(id, user_name, password, user_type, create_time, update_time) VALUES (1, 'admin', "
            + "'254c1d292bb64eabc52697447ef56c6ac57e2386a4da07f8b26d3a7b496fefeb', 'client', 1624463538000, 1624463538000);")
    void insertUserTableWithClientUser();

    @Select("select count(1) from sqlite_master where type = 'table' and name = 'node_info';")
    String queryNodeInfoTable();

    @Update("alter table node_info add column network_path VARCHAR (128);")
    void alterTableNodeInfoAddColumnNetworkPath();

    @Update("alter table node_info add column node_folder VARCHAR (128);")
    void alterTableNodeInfoAddColumnNodeFolder();

    @Select("select  from node_info;")
    String queryNodeInfoCount();
}
