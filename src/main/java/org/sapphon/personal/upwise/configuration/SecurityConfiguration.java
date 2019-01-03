package org.sapphon.personal.upwise.configuration;

import org.sapphon.personal.upwise.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Autowired
    public SecurityConfiguration(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(userService).passwordEncoder(userService.getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.
                //login
                formLogin().loginPage("/login").passwordParameter("upwisePassword").usernameParameter("upwiseLoginUsername").permitAll()
                //sauce for h2 console to work with spring security enabled
                .and().headers().frameOptions().sameOrigin()
                //h2 console and analytics require admin
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/h2")).hasAuthority("ADMIN")
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/allanalytics")).hasAuthority("ADMIN")
                //public endpoints
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/loggedout")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/randomwisdom")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/viewwisdom")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/user/**")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdomleaderboard")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdomsearch")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/loggedout")
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/scripts/**")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/styles/**")).permitAll()
                //anything not public requires authentication
                .and().authorizeRequests().anyRequest().fullyAuthenticated()
                .and().csrf().disable();
    }
}