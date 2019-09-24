package app.config;

import app.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
           auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers("/", "/login", "/logout", "/start").permitAll();
        http.authorizeRequests().antMatchers("/online",
                "/members",
                "/member/*",
                "/member/*",
                "/jury",
                "/jury/*",
                "/jury/*/*",
                "/categories",
                "/category/*",
                "/category/*/*",
                "/criterions",
                "/criterion/*",
                "/criterion/*/*",
                "/performances",
                "/performance/*",
                "/performance/*/*",
                "/online",
                "/online/send",
                "/statement",
                "/configuration"

        ).hasAuthority("admin");
        http.authorizeRequests().antMatchers("/main", "/evaluation").hasAuthority("user");
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
        http.authorizeRequests().and().formLogin()//
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/login")//
                .defaultSuccessUrl("/start")//
                .failureUrl("/login?error=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
        http.authorizeRequests().and().rememberMe().tokenRepository(this.persistentTokenRepository()).tokenValiditySeconds(60);

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl db=new JdbcTokenRepositoryImpl();
        db.setDataSource(this.dataSource);
        return db;
    }
}

