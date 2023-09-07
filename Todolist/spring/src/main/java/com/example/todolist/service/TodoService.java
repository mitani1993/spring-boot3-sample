package com.example.todolist.service;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.todolist.common.Utils;
import com.example.todolist.form.TodoData;
import com.example.todolist.form.TodoQuery;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TodoService {
	private final MessageSource messageSource;
	
    // --------------------------------------------------------------------------------
    // Todo のチェック
    // --------------------------------------------------------------------------------
    public boolean isValid(TodoData todoData, BindingResult result, boolean isCreate, Locale locale) {
        boolean ans = true;

        // 件名が全角スペースだけで構成されていたらエラー
        if (!Utils.isBlank(todoData.getTitle())) {
            if (Utils.isAllDoubleSpace(todoData.getTitle())) {
                FieldError fieldError = new FieldError(
                    result.getObjectName(),
                    "title",
                    messageSource.getMessage("DoubleSpace.todoData.title", null, locale));
                result.addError(fieldError);
                ans = false;
            }
        }

        // 期限が""ならチェックしない
        String deadline = todoData.getDeadline();
        if (!deadline.equals("")) {
            // yyyy-mm-dd形式チェック
            if (!Utils.isValidDateFormat(deadline)) {
                FieldError fieldError = new FieldError(
                    result.getObjectName(),
                    "deadline",
                    messageSource.getMessage("InvalidFormat.todoData.deadline", null, locale));
                result.addError(fieldError);
                ans = false;

            } else {
                // 過去日付チェックは新規登録の場合のみ
                if (isCreate) {
                    // 過去日付ならエラー
                    if (!Utils.isTodayOrFurtureDate(deadline)) {
                        FieldError fieldError = new FieldError(
                            result.getObjectName(),
                            "deadline",
                            messageSource.getMessage("Past.todoData.deadline", null, locale));
                        result.addError(fieldError);
                        ans = false;
                    }
                }
            }
        }

        return ans;
    }

    // --------------------------------------------------------------------------------
    // 検索条件のチェック
    // --------------------------------------------------------------------------------
    public boolean isValid(TodoQuery todoQuery, BindingResult result, Locale locale) {
        boolean ans = true;

        // 期限:開始の形式をチェック
        String date = todoQuery.getDeadlineFrom();
        if (!date.equals("") && !Utils.isValidDateFormat(date)) {
            FieldError fieldError = new FieldError(
                result.getObjectName(),
                "deadlineFrom",
                messageSource.getMessage("InvalidFormat.todoQuery.deadlineFrom", null, locale));
            result.addError(fieldError);
            ans = false;
        }

        // 期限:終了の形式をチェック
        date = todoQuery.getDeadlineTo();
        if (!date.equals("") && !Utils.isValidDateFormat(date)) {
            FieldError fieldError = new FieldError(
                result.getObjectName(),
                "deadlineTo",
                messageSource.getMessage("InvalidFormat.todoQuery.deadlineTo", null, locale));
            result.addError(fieldError);
            ans = false;
        }
        return ans;
    }
}