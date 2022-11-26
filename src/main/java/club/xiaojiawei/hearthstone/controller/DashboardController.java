package club.xiaojiawei.hearthstone.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 肖嘉威
 * @date 2022/11/24 15:37
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @RequestMapping("/")
    public String index(){
        return "index";
    }

}
