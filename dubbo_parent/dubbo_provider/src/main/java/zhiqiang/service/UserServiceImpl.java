package zhiqiang.service;

import POJO.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zhiqiang.dao.UserDao;


@com.alibaba.dubbo.config.annotation.Service(loadbalance = "roundrobin")
public class UserServiceImpl implements UserService{

    @Autowired
    UserDao userDao;

    @Override
    @Transactional
    public User findById(int id) {
        try {
            return userDao.findById(id);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
