package com.example.todolist.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.todolist.dao.TodoDaoImpl;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;
import com.example.todolist.repository.TodoRepository;
import com.example.todolist.service.TodoService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TodoListController {
	private final TodoRepository todoRepository;
	private final TodoService todoService; 
	private final HttpSession session;

	@PersistenceContext
	private EntityManager entityManager;
	TodoDaoImpl todoDaoImpl;

	@PostConstruct 
	public void init() {
		todoDaoImpl = new TodoDaoImpl(entityManager);
	}

// ToDo 一覧表示(Todolist で追加)

	@GetMapping("/todo")
	public ModelAndView showTodoList(ModelAndView mv,
			@PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable) {
		mv.setViewName("todoList");
		Page<Todo> todoPage = todoRepository.findAll(pageable);
		mv.addObject("todoQuery", new TodoQuery());
		mv.addObject("todoPage", todoPage); 
		mv.addObject("todoList", todoPage.getContent()); 
		session.setAttribute("todoQuery", new TodoQuery()); 
		return mv;
	}

	@PostMapping("/todo/query")
	public ModelAndView queryTodo(@ModelAttribute TodoQuery todoQuery, BindingResult result,
			@PageableDefault(page = 0, size = 5) Pageable pageable, 

			ModelAndView mv) {
		mv.setViewName("todoList");
		Page<Todo> todoPage = null; 
		if (todoService.isValid(todoQuery, result)) {
			// エラーがなければ検索
			todoPage = todoDaoImpl.findByJPQL(todoQuery, pageable); 
			// 入力された検索条件を session に保存
			session.setAttribute("todoQuery", todoQuery);
			mv.addObject("todoPage", todoPage); 
			mv.addObject("todoList", todoPage.getContent()); 
		} else {
			// エラーがあった場合検索
			mv.addObject("todoPage", null); 
			mv.addObject("todoList", null); 
		}
		return mv;
	}

	@GetMapping("/todo/query")
	public ModelAndView queryTodo(@PageableDefault(page = 0, size = 5) Pageable pageable, ModelAndView mv) {
		mv.setViewName("todoList");
		// session に保存されている条件で検索
		TodoQuery todoQuery = (TodoQuery) session.getAttribute("todoQuery");
		Page<Todo> todoPage = todoDaoImpl.findByJPQL(todoQuery, pageable);
		mv.addObject("todoQuery", todoQuery); // 検索条件表示用
		mv.addObject("todoPage", todoPage); // page 情報
		mv.addObject("todoList", todoPage.getContent()); // 検索結果
		return mv;
	}

// 【処理 1 】 ToDo 一覧画面(todoList.html)で[新規追加]リンクがクリックされたとき
	@GetMapping("/todo/create")
	public ModelAndView createTodo(ModelAndView mv) {
		mv.setViewName("todoForm"); 
		mv.addObject("todoData", new TodoData()); 
		session.setAttribute("mode", "create"); 
		return mv;
	}

// 【処理 2 】 ToDo 入力画面(todoForm.html)で[登録]ボタンがクリックされたとき
	@PostMapping("/todo/create")
	public String createTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, Model model) {
		// エラーチェック
		boolean isValid = todoService.isValid(todoData, result);
		if (!result.hasErrors() && isValid) {
			// エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo);
			return "redirect:/todo";
		} else {
			// エラーあり
			return "todoForm";
		}
	}

	// 【処理 3 】 ToDo 入力画面で[キャンセル登録]ボタンがクリックされたとき
	@PostMapping("/todo/cancel")
	public String cancel() {
		return "redirect:/todo";
	}

	@GetMapping("/todo/{id}")
	public ModelAndView todoById(@PathVariable(name = "id") int id, ModelAndView mv) {
		mv.setViewName("todoForm");
		Todo todo = todoRepository.findById(id).get(); 
		mv.addObject("todoData", todo); 
		session.setAttribute("mode", "update"); 
		return mv;
	}

	@PostMapping("/todo/update")
	public String updateTodo(@ModelAttribute @Validated TodoData todoData, BindingResult result, Model model) {
		// エラーチェック
		boolean isValid = todoService.isValid(todoData, result);
		if (!result.hasErrors() && isValid) {
			// エラーなし
			Todo todo = todoData.toEntity();
			todoRepository.saveAndFlush(todo); 
			return "redirect:/todo";
		} else {
			// エラーあり
			return "todoForm";
		}
	}

	@PostMapping("/todo/delete")
	public String deleteTodo(@ModelAttribute TodoData todoData) {
		todoRepository.deleteById(todoData.getId());
		return "redirect:/todo";
	}
}
