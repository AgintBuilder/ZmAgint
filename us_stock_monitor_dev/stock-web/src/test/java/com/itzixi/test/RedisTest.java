package com.itzixi.test;

import com.itzixi.Application;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.Set;
import java.util.concurrent.TimeUnit;
/**
 * @ClassName RedisTest
 * @Author duqy
 * @Version 1.0
 * @Description Redis 测试类
 **/
@SpringBootTest(classes = Application.class)
public class RedisTest {

    @BeforeAll
    public static void setup() {
        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 向 Redis 的 AATC Set 中添加链接
     */
    @Test
    public void testAddToAATCSet() {
        String redisKey = "stock:news:exist:AATC";
        String link = "https://www.baidu.com";

        // 添加到 Set
        stringRedisTemplate.opsForSet().add(redisKey, link);

        // 设置过期时间为 7 天
        stringRedisTemplate.expire(redisKey, 7, TimeUnit.DAYS);

        System.out.println("成功添加链接到 AATC: " + link);

        // 验证是否添加成功
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(redisKey, link);
        System.out.println("链接是否存在于 Set 中：" + isMember);

        // 查看 Set 中的所有成员
        Set<String> members = stringRedisTemplate.opsForSet().members(redisKey);
        System.out.println("AATC Set 中的所有链接:");
        for (String member : members) {
            System.out.println("  - " + member);
        }
    }

    /**
     * 查看 AATC 的 Redis Set 内容
     */
    @Test
    public void testViewAATCSet() {
        String redisKey = "stock:news:exist:AATC";

        Set<String> members = stringRedisTemplate.opsForSet().members(redisKey);
        System.out.println("AATC Set 中的所有链接:");
        for (String member : members) {
            System.out.println("  - " + member);
        }

        Long size = stringRedisTemplate.opsForSet().size(redisKey);
        System.out.println("Set 大小：" + size);

        Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.DAYS);
        System.out.println("剩余过期时间（天）：" + expire);
    }

    /**
     * 删除 AATC Set 中的指定链接
     */
    @Test
    public void testRemoveFromAATCSet() {
        String redisKey = "stock:news:exist:AATC";
        String link = "https://www.baidu.com";

        Long removedCount = stringRedisTemplate.opsForSet().remove(redisKey, link);
        System.out.println("删除了 " + removedCount + " 个链接");

        // 验证是否删除成功
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(redisKey, link);
        System.out.println("链接是否还存在于 Set 中：" + isMember);
    }



    @Test
    public void testIfExist() {
        String redisKey = "stock:news:exist:AATC";
        String link = "https://www.baidu.com";

        Boolean isMember = stringRedisTemplate.opsForSet().isMember(redisKey, link);
        if (Boolean.TRUE.equals(isMember)) {
            // Redis 中存在，直接返回 true
            System.out.println("链接是否还存在于 Set 中：" + isMember);
        }
    }


}
