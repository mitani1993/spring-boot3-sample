package com.example.todolist.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.todolist.common.Utils;
import com.example.todolist.entity.Todo;
import com.example.todolist.form.TodoQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class TodoDaoImpl implements TodoDao {
	private final EntityManager entityManager;

	@Override
	public Page<Todo> findByJPQL(TodoQuery todoQuery, Pageable pageable) {
		StringBuilder sb = new StringBuilder("select t from Todo t where 1 = 1");
		List<Object> params = new ArrayList<>();
		int pos = 0;
		// 実行する JPQL の組み立て
		// 件名
		if (todoQuery.getTitle().length() > 0) {
			sb.append(" and t.title like ?" + (++pos));
			params.add("%" + todoQuery.getTitle() + "%"); 
		}
		// 重要度
		if (todoQuery.getImportance() != -1) {
			sb.append(" and t.importance = ?" + (++pos)); 
			params.add(todoQuery.getImportance());
		}
		// 緊急度
		if (todoQuery.getUrgency() != -1) {
			sb.append(" and t.urgency = ?" + (++pos)); 
			params.add(todoQuery.getUrgency()); 
		}
		// 期限：開始～
		if (!todoQuery.getDeadlineFrom().equals("")) {
			sb.append(" and t.deadline >= ?" + (++pos)); 
			params.add(Utils.str2date(todoQuery.getDeadlineFrom())); 
		}
		// ～期限：終了で検索
		if (!todoQuery.getDeadlineTo().equals("")) {
			sb.append(" and t.deadline <= ?" + (++pos)); 
			params.add(Utils.str2date(todoQuery.getDeadlineTo())); 
		}
		// 完了
		if (todoQuery.getDone() != null && todoQuery.getDone().equals("Y")) {
			sb.append(" and t.done = ?" + (++pos)); 
			params.add(todoQuery.getDone()); 
		}
		// order
		sb.append(" order by id");
		Query query = entityManager.createQuery(sb.toString()); 
		for (int i = 0; i < params.size(); ++i) { 
			query = query.setParameter(i + 1, params.get(i));
		}
		int totalRows = query.getResultList().size();
		query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());
		@SuppressWarnings("unchecked")
		Page<Todo> page = new PageImpl<Todo>(query.getResultList(), pageable, totalRows);
		return page;

	}

//	// Criteria API による検索
//	@Override
//	public List<Todo> findByCriteria(TodoQuery todoQuery) {
//		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//		CriteriaQuery<Todo> query = builder.createQuery(Todo.class);
//		Root<Todo> root = query.from(Todo.class);
//		List<Predicate> predicates = new ArrayList<>();
//		// 件名
//		String title = "";
//		if (todoQuery.getTitle().length() > 0) {
//			title = "%" + todoQuery.getTitle() + "%";
//		} else {
//			title = "%";
//		}
//		predicates.add(builder.like(root.get(Todo_.TITLE), title));
//		// 重要度
//		if (todoQuery.getImportance() != -1) {
//			predicates.add(builder.and(builder.equal(root.get(Todo_.IMPORTANCE), todoQuery.getImportance())));
//		}
//		// 緊急度
//		if (todoQuery.getUrgency() != -1) {
//			predicates.add(builder.and(builder.equal(root.get(Todo_.URGENCY), todoQuery.getUrgency())));
//		}
//		// 期限：開始～
//		if (!todoQuery.getDeadlineFrom().equals("")) {
//			predicates.add(builder.and(builder.greaterThanOrEqualTo(root.get(Todo_.DEADLINE),
//					Utils.str2date(todoQuery.getDeadlineFrom()))));
//		}
//		// ～期限：終了で検索
//		if (!todoQuery.getDeadlineTo().equals("")) {
//			predicates.add(builder.and(
//					builder.lessThanOrEqualTo(root.get(Todo_.DEADLINE), Utils.str2date(todoQuery.getDeadlineTo()))));
//		}
//		// 完了
//		if (todoQuery.getDone() != null && todoQuery.getDone().equals("Y")) {
//			predicates.add(builder.and(builder.equal(root.get(Todo_.DONE), todoQuery.getDone())));
//		}
//		// SELECT 作成
//		Predicate[] predArray = new Predicate[predicates.size()];
//		predicates.toArray(predArray);
//		query = query.select(root).where(predArray).orderBy(builder.asc(root.get(Todo_.id)));
//		// 検索
//		List<Todo> list = entityManager.createQuery(query).getResultList();
//		return list;
//	}

}
