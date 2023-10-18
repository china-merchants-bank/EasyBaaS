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

import com.cmbchina.baas.easyBaas.model.MonitorInfo;

import java.util.List;

public interface MonitorInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MonitorInfo record);

    int insertSelective(MonitorInfo record);

    MonitorInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MonitorInfo record);

    int updateByPrimaryKey(MonitorInfo record);

    List<MonitorInfo> selectByName(String name);

    int deleteByName(String name);

    List<MonitorInfo> selectByDeployStatus(String deployStatus);

    List<MonitorInfo> selectByNameLike(String name);
}