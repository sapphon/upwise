package org.sapphon.upwise.configuration;

import org.sapphon.upwise.service.UserService;
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
                //log in or out
                formLogin().loginPage("/login").passwordParameter("upwisePassword").usernameParameter("upwiseLoginUsername").permitAll()
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login?loggedout").permitAll()
                //sauce for h2 console to work with spring security enabled
                .and().headers().frameOptions().sameOrigin()
                //h2 console and analytics require admin
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/h2")).hasAuthority("ADMIN")
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/allanalytics")).hasAuthority("ADMIN")
                //public UI endpoints
                //.and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/loggedout")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/randomwisdom")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/viewwisdom")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/user/**")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdomleaderboard")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdomleadermatrix")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/recentwisdom")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/scripts/**")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/styles/**")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/favicon*")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/forgotpassword")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/choosenewpassword**")).permitAll()
                //public API endpoints
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdom/all")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdom/add")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/vote/all")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/wisdom/random")).permitAll()
                .and().authorizeRequests().requestMatchers(new AntPathRequestMatcher("/health")).permitAll()
                //anything not public requires authentication
                .and().authorizeRequests().anyRequest().fullyAuthenticated()
                .and().csrf().disable();
    }
}
