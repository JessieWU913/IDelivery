package cs.qqq.Config;

import cs.qqq.Controller.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;


@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    //配置 认证、授权的信息
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // 授权
        httpSecurity.authorizeRequests()
                //放行 login页面、所需的 css图片
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();

        //  指定 登录页面
        httpSecurity.formLogin().loginPage("/userLogin").permitAll()
                .usernameParameter("username").passwordParameter("password")
                .defaultSuccessUrl("/")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        httpServletResponse.sendRedirect("/");
                    }
                })
        ;
        //failureForwardUrl("/userLogin/error")
        // 注销页面
        httpSecurity.logout()
                .logoutUrl("/userLogout")
                .logoutSuccessUrl("/userLogin");

    }



    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {

        AuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                new LoginController().toLogin();
            }
        };


        return  authenticationSuccessHandler;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String userSQL ="select user_name,password from sys_user where user_name = ?";
        String authoritySQL ="select u.username,a.role_name from sys_user u,sys_role a where u.role_id=a.role_id and u.username =?";
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(userSQL)
                .authoritiesByUsernameQuery(authoritySQL);
        System.out.println("=====================================");
    }

}
