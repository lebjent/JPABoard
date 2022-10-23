package com.board.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.constant.Role;
import com.board.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
	
	//회원가입시 중복체크하기를 위한 쿼리 메소드
	User findByUserId(String userId);
	
	//@Query어노테이션으로 JPQL을 사용해서 회원리스트 조회 (검색조건X)
	@Query("select u from User u where u.role =:role order by u.regTime")
	List<User> findUser(@Param("role") Role role, Pageable pageable);
	
	//총 회원수 카운트(검색조건X)
	@Query("select count(u) from User u where u.role = :role")
	Long countUser(@Param("role")Role role);
	
	//@Query어노테이션으로 JPQL을 사용해서 회원리스트 조회 (회원이름)
	@Query("select u from User u where u.role =:role and u.name like %:keyword% order by u.regTime")
	List<User> findUserName(@Param("role") Role role, @Param("keyword")String keyword, Pageable pageable);
	
	//총 회원수 카운트(회원이름)
	@Query("select count(u) from User u where u.role = :role and u.name like %:keyword%")
	Long countUserName(@Param("role")Role role , @Param("keyword")String keyword);
	
	//@Query어노테이션으로 JPQL을 사용해서 회원리스트 조회 (회원아이디)
	@Query("select u from User u where u.role =:role and u.userId like %:keyword% order by u.regTime")
	List<User> findUserId(@Param("role") Role role, @Param("keyword")String keyword, Pageable pageable);
	
	//총 회원수 카운트(회원아이디)
	@Query("select count(u) from User u where u.role = :role and u.userId like %:keyword%")
	Long countUserId(@Param("role")Role role , @Param("keyword")String keyword);
	
	//@Query어노테이션으로 JPQL을 사용해서 회원리스트 조회 (전화번호)
	@Query("select u from User u where u.role =:role and u.phone like %:keyword% order by u.regTime")
	List<User> findUserPhone(@Param("role") Role role, @Param("keyword")String keyword, Pageable pageable);
	
	//총 회원수 카운트(전화번호)
	@Query("select count(u) from User u where u.role = :role and u.phone like %:keyword%")
	Long countUserPhone(@Param("role")Role role , @Param("keyword")String keyword);
	
	//@Query어노테이션으로 JPQL을 사용해서 회원리스트 조회 (이름+아이디)
	@Query("select u from User u where u.role =:role and u.name like %:keyword% or u.userId like %:keyword% order by u.regTime")
	List<User> findUserNameId(@Param("role") Role role, @Param("keyword")String keyword, Pageable pageable);
	
	//총 회원수 카운트(이름+아이디)
	@Query("select count(u) from User u where u.role = :role and u.name like %:keyword% or u.userId like %:keyword%")
	Long countUserNameId(@Param("role")Role role , @Param("keyword")String keyword);
	
}
