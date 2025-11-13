package com.cos.project.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.cos.project.domain.Article;
import com.cos.project.dto.ArticleListViewResponse;
import com.cos.project.service.BlogService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

	private final BlogService blogService;

	@GetMapping("/articles")
	public String getArticles(Model model) {
		List<ArticleListViewResponse> articles = blogService.findAll().stream().map(ArticleListViewResponse::new)
				.toList();

		model.addAttribute("articles", articles); // 블로그 글 리스트 저장

		return "articleList"; // articleLIst.html라는 뷰 조회
	}

	@GetMapping("/articles/{id}")
	public String getArticles(Model model, @PathVariable(name = "id") Long id) throws Exception {
		Article article = blogService.findById(id);
		model.addAttribute("article", new ArticleListViewResponse(article)); // 블로그 글 리스트 저장
		return "article"; // articleLIst.html라는 뷰 조회
	}



	@GetMapping("/new-article")
	//id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑 (id는 없을 수도 있음)
	public String newArticle(Model model, @RequestParam(name = "id", required = false)Long id) throws Exception {
		if(id == null) {	//id가 없으면 생성 (새 글을 생성할 때)
			model.addAttribute("article", new ArticleListViewResponse());
		}else {	// id 가 없으면 수정 (기존 글을 수정할 때)
			Article	 article = blogService.findById(id);
			model.addAttribute("article", new ArticleListViewResponse(article));
		}
    	return "newArticle";		
		}

	
	
	}