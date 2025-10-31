package com.example.bbs_training.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsrfTokenValidator {
	// ログの宣言を追加
	private static final Logger logger = LoggerFactory.getLogger(CsrfTokenValidator.class);

	public boolean isValid(HttpServletRequest request, HttpSession session) {
		String sessionToken = (String) session.getAttribute("csrfToken");
		String requestToken = request.getParameter("csrfToken");
		// LE005: トークンチェックエラー（不一致時にログ出力）
		if (sessionToken == null || requestToken == null || !sessionToken.equals(requestToken)) {
			logger.error("LE005: CSRFトークンチェックエラー。入力値：{}、チェックトークン：{}", requestToken, sessionToken);
			return false;
		}
		return true;
	}

}
