package zhiqiang.controller;

import POJO.User;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhiqiang.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    UserService userService;

    @RequestMapping("/findbyid")
    public User findById(int id){
        return userService.findById(id);
    }
}
