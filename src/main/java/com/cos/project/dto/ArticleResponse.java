package com.cos.project.dto;

import com.cos.project.domain.Article;

import lombok.Getter;

@Getter
public class ArticleResponse {
	String title;
	String content;
	
	public ArticleResponse(Article article) {
		this.title = article.getTitle();
		this.content = article.getContent();
	}
	
}
