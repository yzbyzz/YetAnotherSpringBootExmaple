package zz;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import zz.domain.User;
import zz.service.UserService;

import java.util.LinkedList;
import java.util.List;

/**
 * zz.TestRedis
 *
 * @author zz
 * @date 2018/5/7
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestRedis {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @Test
    public void testSerializer() {
        // 1.
        // 这里的 opsForValue().get() 的参数必须转成 String 类型。
        // 除非在 RedisConfig 中 将 keySerializer 设置成 GenericJackson2JsonRedisSerializer 等能将其他类型转换成 String 的。

        // 2.
        // 如果切换了 RedisConfig 中的 ValueSerializer，要先用 redis-cli 将其中的旧数据删除。
        // 不同 Serializer 格式之间的转换可能存在问题。

        final int ID = 123;
        User oldUser;
        oldUser = (User) redisTemplate.opsForValue().get(String.valueOf(ID));
        log.debug("oldUser=" + oldUser);

        User user = new User();
        user.setId(ID);
        user.setName("name");
        log.debug("user=" + user);

        redisTemplate.opsForValue().set(String.valueOf(user.getId()), user);

        User newUser;
        newUser = (User) redisTemplate.opsForValue().get(String.valueOf(ID));
        log.debug("newUser=" + newUser);

        Assert.assertEquals(user.getId(), newUser.getId());
        Assert.assertEquals(user.getName(), newUser.getName());


        List<User> userList = new LinkedList<>();
        userList.add(user);
        user.setId(233);
        user.setName("new");
        userList.add(user);

        redisTemplate.opsForValue().set("userList", userList);
        List<User> newUserList;
        newUserList = (List<User>) redisTemplate.opsForValue().get("userList");

        Assert.assertEquals(userList, newUserList);
    }

    @Test
    public void testSerizlizer2() {
        // 保存用于恢复，以免影响其他部分
        RedisSerializer oldKeySerializer = redisTemplate.getKeySerializer();
        RedisSerializer oldValueSerializer = redisTemplate.getValueSerializer();

        RedisSerializer redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setValueSerializer(redisSerializer);

        final String KEY = "key";
        String VALUE = "value";

        redisTemplate.opsForValue().set(KEY, VALUE);
        Assert.assertEquals(VALUE, redisTemplate.opsForValue().get(KEY));
        Assert.assertEquals(VALUE, stringRedisTemplate.opsForValue().get(KEY));


        VALUE = "Val2";
        stringRedisTemplate.opsForValue().set(KEY, VALUE);
        Assert.assertEquals(VALUE, stringRedisTemplate.opsForValue().get(KEY));
        Assert.assertEquals(VALUE, redisTemplate.opsForValue().get(KEY));


        // 恢复原本设置
        redisTemplate.setKeySerializer(oldKeySerializer);
        redisTemplate.setValueSerializer(oldValueSerializer);
    }


    @Test
    public void testCache() {
        final int USER_ID = 1;

        User user = userService.get(USER_ID);
        log.debug("user=" + user);
        Assert.assertEquals(userService.DEFAULT_NAME, user.getName());

        // 这次会直接返回 cache
        user = userService.get(USER_ID);
        log.debug("user=" + user);

        // 获得修改过的 cache
        final String ANOTHER_NAME = "another user";
        user.setName(ANOTHER_NAME);
        userService.update(user);
        user = userService.get(USER_ID);
        log.debug("user=" + user);
        Assert.assertEquals(ANOTHER_NAME, user.getName());


        // 另一种修改的方式
        final String NEW_NAME = "updated";
        userService.updateName(USER_ID, NEW_NAME);
        user = userService.get(USER_ID);
        log.debug("user=" + user);
        Assert.assertEquals(NEW_NAME, user.getName());


        // 删除后，cache 中的数据会被删除，name 会变成初始值
        userService.delete(USER_ID);
        user = userService.get(USER_ID);
        log.debug("user=" + user);
        Assert.assertEquals(userService.DEFAULT_NAME, user.getName());

        // 即使 cache 中没有该数据，也会指向 delete 中的逻辑
        userService.delete(USER_ID);
        userService.delete(USER_ID);

    }
}
