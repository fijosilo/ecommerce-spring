package com.fijosilo.ecommerce.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/logout").permitAll()
                    .antMatchers(HttpMethod.POST, "/register", "/login").permitAll()
                    .antMatchers("/admin/product").hasRole("ADMIN")
                    .antMatchers(HttpMethod.POST, "/image").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/image/**").permitAll()
                    .anyRequest().authenticated()
                .and().exceptionHandling()
                    // resource is protected and client is not authenticated
                    .authenticationEntryPoint((request, response, exception) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                    // client is authenticated but does not have enough permissions to see the protected resource
                    .accessDeniedHandler((request, response, exception) -> response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                .and().formLogin()
                    .usernameParameter("email")
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
