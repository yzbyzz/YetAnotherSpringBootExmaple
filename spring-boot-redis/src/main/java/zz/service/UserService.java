package zz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zz.domain.User;

/**
 * UserService
 *
 * @author zz
 * @date 2018/5/7
 */
@Service
@Slf4j
public class UserService {
    public final String DEFAULT_NAME = "def";

    @Cacheable(cacheNames = "user", key = "'id_'+#userId")
    public User get(int userId) {
        // get from db
        log.debug("[++] get userId=" + userId);

        User user = new User();
        user.setId(userId);
        user.setName(DEFAULT_NAME);
        log.debug("[++] create default user=" + user);
        return user;
    }

    @CachePut(cacheNames = "user", key = "'id_'+#user.getId()")
    public User update(User user) {
        // save to db
        log.debug("[++] update user=" + user);
        return user;
    }

    @CacheEvict(cacheNames = "user", key = "'id_'+#userId")
    public void delete(int userId) {
        // delete from db
        log.debug("[++] delete userId=" + userId);
    }

    @CachePut(cacheNames = "user", key = "'id_'+#userId")
    public User updateName(int userId, String name) {
        // update to db
        log.debug("[++] updateName userId=" + userId + ", name=" + name);

        User user = get(userId);
        user.setName(name);
        return user;
    }

    public void innerCall(int userId) {
        log.debug("[++] innerCall");
        get(userId);
    }
}
