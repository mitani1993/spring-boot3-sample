package com.example.todolist.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoQuery;

public interface TodoDao {
	// JPQLによる検索
	Page<Todo> findByJPQL(TodoQuery todoQuery, Pageable pageable);
	
//	// Criteria APIによる検索
//	List<Todo> findByCriteria(TodoQuery todoQuery);
}
