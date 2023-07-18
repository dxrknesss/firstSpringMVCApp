package com.firstspringmvcapp.security;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.firstspringmvcapp.services.PersonDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;
    private final BeanFactory beanFactory;
    //private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService, BeanFactory beanFactory) {
        this.personDetailsService = personDetailsService;
        this.beanFactory = beanFactory;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

        authenticationProvider.setUserDetailsService(personDetailsService);
        authenticationProvider.setPasswordEncoder(passEncoder());

        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable()).authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/admin").hasRole("ADMIN")
                        .requestMatchers("/auth/login", "/error", "/auth/registration", "/css/**", "/js/**", "/api/**").permitAll()
                        .anyRequest().hasAnyRole("USER", "ADMIN"))
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/process_login")
                        .permitAll()
                        .defaultSuccessUrl("/people", true)
                        .failureUrl("/auth/login?error"))
                .logout(form -> form
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login"));
                //.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationManagerFactoryBean factoryBean =
                new AuthenticationManagerFactoryBean();
        factoryBean.setBeanFactory(beanFactory);

        return factoryBean.getObject();
    }
}
