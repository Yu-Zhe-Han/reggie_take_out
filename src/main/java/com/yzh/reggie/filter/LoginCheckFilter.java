package com.yzh.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yzh.reggie.common.BaseContext;
import com.yzh.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检测用户是否登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
     public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    //过滤的方法
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

        //获取本次请求的URI
        String requestURI=request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        //定义不需要请求的路径
        String[] urls=new String[]{"/employee/login","./employee/logout","/backend/**","/front/**","/common/**","/user/sendMsg","/user/login"
        };
        //判断是否需要处理
        boolean check=check(urls,requestURI);
        //如果不需要处理直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1判断是否为登录状态
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId=(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //4-2判断是否为登录状态
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId=(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //没有登录返回登录界面,通过输出流方式向客服端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    /**
     * 路径匹配，查看是否放行
     * @param requesyURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls,String requesyURI){
        for(String url:urls){
           boolean match= PATH_MATCHER.match(url,requesyURI);
           if(match){
               return  true;
           }
        }
        return false;

    }
}
