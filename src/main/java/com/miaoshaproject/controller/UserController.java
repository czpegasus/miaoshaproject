package com.miaoshaproject.controller;

import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController{
    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户获取OTP验证码的接口
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
//    @CrossOrigin
    public CommonReturnType getOtp(@RequestParam("telephone")String telphone){
        // 按照一定规则生成 otp 验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);

        // 将 otp 验证码同对应的用户关联 (暂时使用 httpsession 的方式绑定 otp 与手机号)
        httpServletRequest.getSession().setAttribute(telphone, otpCode);

        // 将 otp 验证码通过短信通道发送给用户 (省略，使用控制台输出代替)
        System.out.println(String.format("telphone = %s & otpCode = %s", telphone, otpCode));

        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam("id")Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);

        //若获取的用户不存在
        if (userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);

        }

        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }
    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

}
