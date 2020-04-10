package de.br.aff.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter
{
    private final AuthEntryPoint authEntryPoint;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.inMemoryAuthentication()
            .withUser("admin").password(encoder().encode("admin123")).roles("ADMIN")
            .and()
            .withUser("user").password(encoder().encode("user123")).roles("USER");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
            .csrf().disable() //disabled csrf - not to be used in production
            .exceptionHandling()
            .authenticationEntryPoint(authEntryPoint)
            .and()
            .authorizeRequests()
            .antMatchers("/v1/**").authenticated()
            .antMatchers("/internal/**").hasRole("ADMIN")
            .and()
            .formLogin()
            .and()
            .logout();

        //        http.headers().frameOptions().disable()
        //            .and()
        //            .authorizeRequests()
        //            .antMatchers("/h2-console/**").permitAll();

    }


    @Bean
    public PasswordEncoder encoder()
    {
        return new BCryptPasswordEncoder();
    }
}