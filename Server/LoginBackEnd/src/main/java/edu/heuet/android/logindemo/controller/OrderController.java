package edu.heuet.android.logindemo.controller;

import edu.heuet.android.logindemo.error.BusinessException;
import edu.heuet.android.logindemo.error.EmBusinessError;
import edu.heuet.android.logindemo.response.CommonReturnType;
import edu.heuet.android.logindemo.service.OrderService;
import edu.heuet.android.logindemo.service.model.OrderModel;
import edu.heuet.android.logindemo.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
/* @RequestMapping是URL映射 */
@RequestMapping("/order")
/*
  @CrossOrigin解决跨域请求问题
  No 'Access-Control-Allow-Origin' header is present on the requested resource
  加上这个注解后，就会让response对象返回'Access-Control-Allow-Origin' header 为 通配符*
  但是单纯的加注解，只是让跨域互通了，还不能实现互信
  需要再加两个参数，才能实现前后端互信
  */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
/*
继承BaseController，
父类的方法子类不重新，就会引用父类的方法，
若子类重写父类的方法，则引用子类的方法
这里不需要进行重写handlerException
*/
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    // 为了拿到Session
    @Autowired
    HttpServletRequest httpServletRequest;

    // 需要接受itemId,amount;还是统一用String接收，传给Service的时候再转换为Integer
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createOrder(
            @RequestParam(name = "itemId") String itemId,
            @RequestParam(name = "amount") String amount
    ) throws BusinessException{

        // 这里应该强转成Boolean，不能是boolean，否则有可能是空指针错误
        Boolean isLogin = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");

        if (isLogin == null || !isLogin.booleanValue()){
            throw  new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        // 获取用户登陆信息，之后才能getId
        UserModel userModel = (UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(), Integer.parseInt(itemId), Integer.parseInt(amount));

        return CommonReturnType.create(null);
    }


}
