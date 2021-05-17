package io.github.quickmsg.function;

import com.smqtt.common.security.annotation.EnableCustomConfig;
import com.smqtt.common.security.annotation.EnableRyFeignClients;
import com.smqtt.common.swagger.annotation.EnableCustomSwagger2;
import com.smqtt.common.core.utils.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * 系统模块
 *
 * @Author smqtt
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
@RefreshScope
//实现跨域注解
//origin="*"代表所有域名都可访问
//maxAge飞行前响应的缓存持续时间的最大年龄，简单来说就是Cookie的有效期 单位为秒
//若maxAge是负数,则代表为临时Cookie,不会被持久化,Cookie信息保存在浏览器内存中,浏览器关闭Cookie就消失
@CrossOrigin(origins = "*",maxAge = 3600)
public class SmqttFunctionApplication
{
    public static void main(String[] args)
    {
        ConfigurableApplicationContext context = SpringApplication.run(SmqttFunctionApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
        System.out.println("(♥◠‿◠)ﾉﾞ  功能模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
