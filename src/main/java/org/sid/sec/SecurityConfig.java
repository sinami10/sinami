package org.sid.sec;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		
	PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
			
		/*
		 * auth.inMemoryAuthentication().withUser("amin").password(encoder.encode("amin"
		 * )).roles("ADMIN","USER");
		 * auth.inMemoryAuthentication().withUser("nada").password(encoder.encode("nada"
		 * )).roles("USER");
		 */
	
	auth.jdbcAuthentication()
    .dataSource(dataSource)
    .usersByUsernameQuery("select username as principal , password as credentials , active from users where username=?")
    .authoritiesByUsernameQuery("select username as principal , role as role from users_roles where username=?")
    .rolePrefix("ROLE_")
    .passwordEncoder(NoOpPasswordEncoder.getInstance());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login");
		http.authorizeRequests().antMatchers("/consulterCompte","/operations").hasRole("USER");
		http.authorizeRequests().antMatchers("/saveOperation").hasRole("ADMIN");
		http.exceptionHandling().accessDeniedPage("/404");
	}

	
}
