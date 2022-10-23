package com.board.config;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;

//현재 로그인한 사용자의 정보를 등록자와 수정자로 지정하기 위해서 AuditorAware 인터페이스를 구현한 클래스를 생성
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = "";
				if(authentication != null) {
					userId = authentication.getName();
				}
		
		return Optional.of(userId);
	}
	
	//로그인한 이용자의 역할이 ADMIN인지 체크하는 역할
	public static boolean hasAdminRole() {
		//시큐리티 컨텍스트 객체를 얻습니다.
		SecurityContext context = SecurityContextHolder.getContext();
		//인증객체 얻기
		Authentication authentication = context.getAuthentication();
        
		
		Collection<? extends GrantedAuthority> role = authentication.getAuthorities();
		
        return role.stream().filter(o -> o.getAuthority().equals("ROLE_ADMIN")).findAny().isPresent();
	}

}
