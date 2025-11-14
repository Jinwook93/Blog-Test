package com.cos.project.dto;

import java.time.LocalDateTime;

import com.cos.project.domain.Article;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleListViewResponse {
	
	
		private Long id;
		private String title;
		private  String content;
		private LocalDateTime createdAt;
		private String author;
		
		public ArticleListViewResponse(Article article) {
			this.id = article.getId();
			this.title = article.getTitle();
			this.content = article.getContent();
			this.createdAt = article.getCreatedAt();
			this.author = article.getAuthor();
		}
		
		
		
}
