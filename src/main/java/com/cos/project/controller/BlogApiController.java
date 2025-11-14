package com.cos.project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.domain.Article;
import com.cos.project.dto.AddArticleRequest;
import com.cos.project.dto.ArticleResponse;
import com.cos.project.dto.UpdateArticleRequest;
import com.cos.project.service.BlogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController	//HTTP Response Body 에 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {

	private final BlogService blogService;
	
	//HTTP 메서드가 POST일 때 전달받은 URL 과 동일하면 메서드로 매핑
	
//	@PostMapping("/api/articles")
//	public ResponseEntity<Article> addArticle( @RequestBody AddArticleRequest request){
//		Article savedArticle = blogService.save(request);
//		return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
//	}
	   @PostMapping("/api/articles")
	    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
	        Article savedArticle = blogService.save(request, principal.getName());
	        return ResponseEntity.status(HttpStatus.CREATED)
	                .body(savedArticle);
	    }
	
	@GetMapping("/api/articles")
	public ResponseEntity<List<ArticleResponse>> findAllArticle(){
		List<ArticleResponse> articles = blogService.findAll()
//				.stream().map(ArticleResponse :: new)
				.stream().map(article -> new ArticleResponse(article))
				.toList();
		
		return ResponseEntity.status(HttpStatus.OK).body(articles);
	}
	
	
	@GetMapping("/api/articles/{id}")
	public ResponseEntity<ArticleResponse> findArticle(@PathVariable(name = "id") Long id) throws Exception{
		Article article = blogService.findById(id);         
				
		
		return ResponseEntity.status(HttpStatus.OK).body(new ArticleResponse(article));
	}
	
	
	@DeleteMapping("/api/articles/{id}")
	public ResponseEntity<Void> deleteArticle(@PathVariable(name="id") Long id){
			blogService.delete(id);
			
			return ResponseEntity.ok().build();
	}
	
	
	@PutMapping("/api/articles/{id}")
	public ResponseEntity<Article> updateArticle(@PathVariable(name = "id") Long id, @RequestBody UpdateArticleRequest request) {
			Article updatedArticle = blogService.update(id, request);
//			ObjectMapper ob = new ObjectMapper();
//			String  addedArticle= ob.writeValueAsString(request);
//			return ResponseEntity.ok().body(addedArticle);
			return ResponseEntity.ok().body(updatedArticle);
	}
	
	
}
