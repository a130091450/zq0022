package zhiqiang.dao;

import POJO.User;
import zhiqiang.service.UserService;

public interface UserDao {

    User findById(int id);

}
