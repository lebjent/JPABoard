package com.board.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@EntityListeners(value = {AuditingEntityListener.class}) //Auditing을 이용하기위한 어노테이션
@MappedSuperclass
@Getter
@Setter
public abstract class BaseTimeEntity {
	
	@CreatedDate
	@Column(updatable = false) //업데이트가 되지 못하게
	private LocalDateTime regTime;
	
	@LastModifiedDate
	private LocalDateTime updateTime;
	
}
