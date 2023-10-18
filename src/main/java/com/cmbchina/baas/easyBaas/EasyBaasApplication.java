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
 
package com.cmbchina.baas.easyBaas;

import cn.hutool.core.net.NetUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableSwagger2
@Controller
public class EasyBaasApplication {

    public static void main(String[] args) {

        /**
         * 方便开发时运行后直接输出访问路径，实际使用时需注释掉
         */
        ConfigurableApplicationContext application = SpringApplication.run(EasyBaasApplication.class, args);

        //获取当前运行环境
        Environment env = application.getEnvironment();

        String port = env.getProperty("server.port");

        System.out.println("\n--------------------------------------------------\n"
                + "Application is running! \n"
                + "Login Page : http://" + NetUtil.getLocalhost().getHostAddress() + ":" + port + "/index.html\n"
                + "用户名：admin\n"
                + "密码：adminpw\n"
                + "--------------------------------------------------");
    }

    @RequestMapping("/")
    public String indexPage() {
        return "index";
    }

}
