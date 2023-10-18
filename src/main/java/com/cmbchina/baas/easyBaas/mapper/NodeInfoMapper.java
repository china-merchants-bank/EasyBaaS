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

import com.cmbchina.baas.easyBaas.model.NodeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NodeInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NodeInfo record);

    int insertSelective(NodeInfo record);

    NodeInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NodeInfo record);

    int updateByPrimaryKey(NodeInfo record);

    long countNodeInfo();

    List<NodeInfo> selectByHostAddress(String address);

    List<String> selectAllHostAddress();

    List<NodeInfo> selectHostByUnDeploymentTag(String deployStatus);

    List<NodeInfo> selectAllNodeInfo();

    NodeInfo selectByNodeName(String nodeName);

    List<NodeInfo> selectDeploymentNodeByHostAddress(@Param("deployStatus") String deployStatus, @Param("address") String address);

    List<NodeInfo> selectAllNodeInfoByStatus();

}