package com.cos.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cos.project.domain.Article;
import com.cos.project.dto.AddArticleRequest;
import com.cos.project.dto.UpdateArticleRequest;
import com.cos.project.repository.BlogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor //final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service
public class BlogService_before {

	private final BlogRepository blogRepository;
	
	//블로그 글 추가 메시지
	public Article save(AddArticleRequest request, String userName) {
		return blogRepository.save(request.toEntity(userName));
	}
	
	//모든 글 조회
	public List<Article> findAll(){
		return blogRepository.findAll();
	}
	
	
	//글 하나 조회
	public Article findById(Long id) throws Exception{
		return  blogRepository.findById(id).orElseThrow(() -> new IllegalAccessException("not found : "+ id)) ;
	}
	
	
	
	//글 하나 삭제 
	
	public void delete(Long id) {
		blogRepository.deleteById(id);
	}
	
	
	//글 하나 수정
	@Transactional
	public Article update(Long id, UpdateArticleRequest request) {
		Article article =blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found " + id) );
		article.update(request.getTitle(), request.getContent());
		// @Transactional이 적용된 메서드 내에서 JPA 엔티티의 필드를 변경하면, 해당 변경 사항은 트랜잭션이 커밋될 때 자동으로 감지되어 dirty checking을 통해 데이터베이스에 반영됩니다. 
		//따라서 blogRepository.save(article) 호출은 명시적으로 하지 않아도 됩니다.

//		- article은 blogRepository.findById(id)로 조회된 영속 상태의 엔티티입니다.
//		- article.update(...)를 통해 필드를 변경하면, JPA는 이를 감지합니다.
//		- 트랜잭션이 커밋될 때 변경된 필드를 자동으로 DB에 반영합니다.

		
		
		//blogRepository.save(article);		@Transaction으로 생략 가능
		return article;
	
	}
	
}
