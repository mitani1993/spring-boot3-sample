package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
public class GameController {
	@Autowired // ①
	HttpSession session; // ①

	@GetMapping("/")
	public String index() {
		session.invalidate(); // ②
		// 答えを作って Session に格納
		Random rnd = new Random(); // ③
		int answer = rnd.nextInt(100) + 1; // ③
		session.setAttribute("answer", answer); // ④
		System.out.println("answer=" + answer); // コンソールに正解を出力する(^^)
		return "game";
	}

	@PostMapping("/challenge") // ⑤
	public ModelAndView challenge(@RequestParam("number") int number, ModelAndView mv) { // ⑥
		// セッションから答えを取得
		int answer = (Integer) session.getAttribute("answer"); // ⑦
		// ユーザーの回答履歴を取得
		@SuppressWarnings("unchecked")
		List<History> histories = (List<History>) session.getAttribute("histories");// ⑧
		if (histories == null) {
			histories = new ArrayList<>();
			session.setAttribute("histories", histories);
		}
		// 判定→回答履歴追加
		if (answer < number) {
			histories.add(new History(histories.size() + 1, number, "もっと小さいです"));// ⑨
		} else if (answer == number) {
			histories.add(new History(histories.size() + 1, number, "正解です！")); // ⑨
		} else {
			histories.add(new History(histories.size() + 1, number, "もっと大きいです"));// ⑨
		}
		mv.setViewName("game"); // ⑩
		mv.addObject("histories", histories); // ⑩
		return mv;
	}
}
