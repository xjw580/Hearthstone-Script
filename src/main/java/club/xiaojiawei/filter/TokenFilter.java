package club.xiaojiawei.filter;

import club.xiaojiawei.controller.WebDashboardController;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static club.xiaojiawei.enums.ConfigurationEnum.ENABLE_VERIFY;

/**
 * @author 肖嘉威
 * @date 2023/10/1 15:26
 * @msg
 */
@WebFilter(urlPatterns = {"/dashboard/*", "/info"})
public class TokenFilter implements Filter {

    @Resource
    private Properties scriptConfiguration;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (Objects.equals(scriptConfiguration.getProperty(ENABLE_VERIFY.getKey()), "false")){
            filterChain.doFilter(servletRequest, servletResponse);
        }else {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            Cookie[] cookies = request.getCookies();
            if (cookies == null){
                response.setStatus(403);
                return;
            }
            Cookie cookie = null;
            for (Cookie c : cookies) {
                if (Objects.equals(c.getName(), "token")){
                    cookie = c;
                    break;
                }
            }if (cookie == null){
                response.setStatus(403);
                return;
            }
            if (WebDashboardController.TOKEN_SET.contains(cookie.getValue())){
                filterChain.doFilter(servletRequest, servletResponse);
            }else {
                response.setStatus(403);
            }
        }
    }
}
