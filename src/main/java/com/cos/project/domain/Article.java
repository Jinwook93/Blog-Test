package com.cos.project.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EntityListeners(AuditingEntityListener.class)
//- AuditingEntityListener는 JPA의 엔티티 생명주기 이벤트(예: persist, update 등)를 감지합니다.
//- 이를 통해 @CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy 같은 어노테이션이 동작하게 됩니다.
@Entity
@Getter
@NoArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false)
	private Long id;
	
	@Column(name = "title", nullable  = false)
	private String title;
	
	@Column(name = "content", nullable  = false)
	private String content;


	@CreatedDate	//엔티티에 생성 시간 추가
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@LastModifiedDate	//엔티티에 수정 시간 추가
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	
	  @Column(name = "author", nullable = false)
	    private String author;

	    @Builder
	    public Article(String author, String title, String content) {
	        this.author = author;
	        this.title = title;
	        this.content = content;
	    }
	
	
	
	@Builder
	public Article(String title, String content) {
		this.title = title;
		this.content = content;
	}
	
	public void update (String title, String content) {
		this.title = title;
		this.content = content;
	}
}
