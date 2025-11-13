package com.cos.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cos.project.domain.Article;

@Repository
public interface BlogRepository extends JpaRepository<Article, Long> {

}
