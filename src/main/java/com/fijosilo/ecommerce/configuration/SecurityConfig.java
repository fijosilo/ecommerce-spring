package com.fijosilo.ecommerce.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/logout").permitAll()
                    .antMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                    .anyRequest().authenticated()
                .and().exceptionHandling()
                    // resource is protected and client is not authenticated
                    .authenticationEntryPoint((request, response, exception) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                    // client is authenticated but does not have enough permissions to see the protected resource
                    .accessDeniedHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                .and().formLogin()
                    .loginProcessingUrl("/login")
                    .successHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_OK))
                    .failureHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                .and().logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    // return status code on logout
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
        ;
    }

}
