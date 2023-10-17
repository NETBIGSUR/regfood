package com.trus.reggie.filter;


import com.alibaba.fastjson2.JSON;
import com.trus.reggie.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;


        log.info("拦截到请求：{}",request.getRequestURI());

//        1、获取本次请求的URI
        String requestURI = request.getRequestURI();


        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
        };

//        2、判断本次请求是否需要处理

        boolean check = check(urls,requestURI);

//        3、如果不需要处理，则直接放行

        if(check){
            filterChain.doFilter(request,response);
            return;
        }

//        4、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee")!=null){
//            System.err.println("************************");
            filterChain.doFilter(request,response);
//            System.err.println("放行");
            return;
        }

//        5、如果未登录则返回未登录结果

        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));



            return;



    }

    public static boolean check(String[] urls,String requestURI){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURI);
            if(match) return true;
        }
        return false;
    }
}
