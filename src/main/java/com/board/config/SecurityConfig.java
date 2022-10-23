package com.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.board.service.UserService;

@Configuration
//WebSecurity ConfigurationAdapter를 상속받는 클래스에 이 어노테이션을 선언하면 SpringSecurity filterChain이 자동으로 포함 
@EnableWebSecurity 
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserService userService;
	
	//http요청에 대한 보안설정
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http.formLogin()
					   .loginPage("/user/login")//로그인을 해야할 페이지를 설정
					   .defaultSuccessUrl("/")//로그인 성공 시 이동할 URL을 설정
					   .usernameParameter("userId")//로그인시 사용할 파라미터 이름으로 userId를 설정
					   .failureUrl("/user/login/error")//로그인 실패시 이동할 URL을 설정
					   .and()
					   .logout()
					   .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))//로그아웃 url설정
					   .logoutSuccessUrl("/");//로그아웃 성공시 이동할 url
		
		http.authorizeHttpRequests()
				.mvcMatchers("/","/user/**","/board/boardList/**").permitAll()
				.mvcMatchers("/board/**").hasAnyRole("USER","ADMIN")
				.mvcMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated();
		
		http.exceptionHandling()
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
					   
	}
	
	//static 디렉터리의 하위 파일은 인증을 무시하도록 설정
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().antMatchers("/css/**","/js/**","/img/**");
	}
	
	//스프링에서 제공하는 BCryptPasswordEncoder의 해쉬함수를 사용하여 비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//userDetailService를 구현하고 있는 객체로 memberService를 지정해주며, 비밀번호 암호화를 위해 passwordEncoder를 지정
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userService)
			.passwordEncoder(passwordEncoder());
			
	}
	
}
