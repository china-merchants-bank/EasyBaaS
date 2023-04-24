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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsConfig {

    private boolean panic_on_internal;
    private boolean fragments_grow;
    private boolean panic_on_protocol;
    private boolean enable;
    private Integer in_buffer_capacity;
    private boolean panic_on_queue;
    private Integer fragment_size;
    private boolean panic_on_timeout;
    private boolean method_strict;
    private Integer thread_number;
    private boolean panic_on_capacity;
    private boolean masking_strict;
    private boolean key_strict;
    private Integer max_connections;
    private String listen_ip;
    private String listen_port;
    private Integer queue_size;
    private Integer fragments_capacity;
    private boolean tcp_nodelay;
    private boolean shutdown_on_interrupt;
    private boolean out_buffer_grow;
    private boolean panic_on_io;
    private boolean panic_on_new_connection;
    private Integer out_buffer_capacity;
    private boolean encrypt_server;
    private boolean in_buffer_grow;
    private boolean panic_on_shutdown;
    private boolean panic_on_encoding;

}
