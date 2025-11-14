package com.cos.project.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cos.project.domain.Article;
import com.cos.project.dto.AddArticleRequest;
import com.cos.project.dto.ArticleResponse;
import com.cos.project.dto.UpdateArticleRequest;
import com.cos.project.repository.BlogRepository;
import com.cos.project.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest_before {

	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	private WebApplicationContext context;
	
	@Autowired
	BlogService blogService;
	@Autowired
	BlogRepository blogRepository;
	
	@DisplayName("addArticle : 블로그 글 추가 성공")
	@Test
	public void addArticle() throws Exception {
		//given
		final String url = "/api/articles";
		final String title = "title";
		final String content = "content";
		final AddArticleRequest userRequest = new AddArticleRequest(title, content);
			
		//객체 JSON 으로 직렬화
		final String requestBody = objectMapper.writeValueAsString(userRequest);
		
		//when
		//설정한 내용을 바탕으로 요청 전송
		ResultActions resultActions = mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody));
		
		//then
		
		resultActions.andExpect(status().isCreated());
		
		List<Article> articles = blogRepository.findAll();
		
		assertThat(articles.size()).isEqualTo(1);
		assertThat(articles.get(0).getTitle()).isEqualTo(title);
		assertThat(articles.get(0).getContent()).isEqualTo(content);
	
	}

	@DisplayName("findAllArticles : 블로그 글 목록 조회에 성공한다")
	@Test
	public void findAllArticles() throws Exception{
		//given
		final String url = "/api/articles";
		final String title = "title";
		final String content = "content";
		
		blogRepository.save(Article.builder().title(title).content(content).build());
		
		//when
		final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
		
		//given
		result.andExpect(status().is(200))
		.andExpect(jsonPath("$[0].content").value(content))
		.andExpect(jsonPath("$[0].title").value(title));
		
	
		
	}
	
	
	
	@DisplayName("findAllArticles : 블로그 글 하나 조회에 성공한다")
	@Test
	public void findOneArticles() throws Exception{
		//given
		final String url = "/api/articles";
		final String title = "title";
		final String content = "content";
		
		blogRepository.save(Article.builder().title(title).content(content).build());
		
		//when
		final ResultActions result = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));
		
		//given
		result.andExpect(status().is(200))
		.andExpect(jsonPath("$[0].content").value(content))
		.andExpect(jsonPath("$[0].title").value(title));
		
	
		
	}
	
	
	@DisplayName("블로그 글 하나 조회")
	@Test
	public void findArticle() throws Exception {
		
		//given
//		Long id = 4;
		String title = "test";
		String content = "testcontent";
		String url = "/api/articles/{id}";
		
		Article savedArticle = blogRepository.save(new Article(title, content));
		
		//when
		ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));
		
		//then
		result.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(content))
		.andExpect(jsonPath("$.title").value(title));
		
		
	}
	
	@DisplayName("deleteArticle: 블로그 글 삭제에 성공한다")
	@Test
	public void deleteArticle() throws Exception {
		//given
		final String url = "/api/articles/{id}";
		final String title = "title";
		final String content = "content";
		
		
		Article savedArticle = blogRepository.save(Article.builder().title(title).content(content).build());
		
		//when
	 ResultActions result = mockMvc.perform(delete(url, savedArticle.getId())).andExpect(status().isOk());
			
	//Then
	 List<Article> articles = blogRepository.findAll();
	 
	 assertThat(articles).isEmpty();
	
	
	}
	
	@DisplayName("updateArticle: 블로그 글 수정에 성공한다")
	@Test
	public void updateArticle() throws Exception {
		
		//given
		final String url = "/api/articles/{id}";
		final String title = "title";
		final String content = "content";
		
		final String updatedtitle = "updatedtitle";
		final String updatedcontent = "updatedcontent";
		
		Article savedArticle = blogRepository.save(Article.builder().title(title).content(content).build());
		
		UpdateArticleRequest request =new UpdateArticleRequest(updatedtitle, updatedcontent);
		
		//when
		
		ResultActions result = 
		mockMvc.perform(put(url, savedArticle.getId()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(request)));
		
		//then
		
		result.andExpect(status().isOk());
		
		Article updatedArticle = blogRepository.findById(savedArticle.getId()).get();
		
		assertThat(updatedArticle.getTitle()).isEqualTo(updatedtitle);
		assertThat(updatedArticle.getContent()).isEqualTo(updatedcontent);
		
		
		
	}
	
	
	
	@BeforeEach
	public void mockMvcSetUp() {
	this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	blogRepository.deleteAll();
	}
	
	
}
