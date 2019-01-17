package com.guess.filter;

import com.guess.util.Config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFilter implements Filter {


    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {// 第一次验证
            String domain = Config.getDomain();
            System.out.println(request.getMethod().toUpperCase() + " domain:" + domain);
            String origin = request.getHeader("Origin");
            System.out.println("origin:" + origin);
            if (origin.contains(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);//http://192.168.2.136:8080
                // response.setHeader("Access-Control-Allow-Credentials", "true");

                response.setHeader("Access-Control-Allow-Methods", "*");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers",
                        "Origin,X-Requested-With,Content-Type,Accept");
                response.setHeader("Pragma", "no-cache");
                response.addHeader("Cache-Control", "must-revalidate");
                response.addHeader("Cache-Control", "no-cache");
                response.addHeader("Cache-Control", "no-store");
                response.setDateHeader("Expires", 0);

            }
            return;

        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub
    }

}

