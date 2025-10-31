package com.example.bbs_training.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bbs_training.config.CsrfTokenValidator;
import com.example.bbs_training.entity.Bbs;
import com.example.bbs_training.entity.Posts;
import com.example.bbs_training.repository.BbsRepository;
import com.example.bbs_training.repository.PostsRepository;
import com.example.bbs_training.repository.SettingsRepository;

/**
 * 掲示板コントローラー
 * <p>
 * 掲示板システムの画面表示および投稿処理を制御するコントローラークラス。
 * 以下の機能を提供する：
 * </p>
 * <ul>
 *   <li>一覧・投稿画面の表示</li>
 *   <li>新規投稿の登録</li>
 *   <li>高評価・低評価の更新</li>
 *   <li>投稿履歴の表示</li>
 *   <li>キーワード検索</li>
 * </ul>
 * 
 * @author hanzawa
 * @version 1.0
 * @since 2025-10-31
 */

@Controller
public class BbsController {
	/**
	 * ログインスタンス
	 */
	private static final Logger logger = LoggerFactory.getLogger(BbsController.class);
	/**
	 * 掲示板リポジトリ
	 */
	@Autowired
	private BbsRepository bbsRepository;
	/**
	 * 投稿リポジトリ
	 */
	@Autowired
	private PostsRepository postsRepository;
	/**
	 * システム設定リポジトリ
	 */
	@Autowired
	private SettingsRepository settingsRepository;
	/**
	 * CSRFトークン検証クラス
	 */
	@Autowired
	private CsrfTokenValidator csrfTokenValidator;

	/**
	 * 一覧・投稿画面を表示
	 * <p>
	 * 掲示板の投稿一覧を表示する。システム設定で指定された境界年数以内の投稿を
	 * 新しい順にページング表示する。キーワード検索が指定された場合は、
	 * 投稿者名またはメッセージに該当するキーワードを含む投稿のみを表示する。
	 * </p>
	 * <p>
	 * CSRF対策としてトークンを生成し、セッションおよびモデルに格納する。
	 * </p>
	 * 
	 * @param bbsId 掲示板ID。指定がない場合はデフォルトで1を使用
	 * @param page ページ番号。指定がない場合はデフォルトで0（先頭ページ）を使用
	 * @param keyword 検索キーワード。任意パラメータ
	 * @param model ビューに渡すデータを格納するモデル
	 * @param session HTTPセッション
	 * @return 一覧・投稿画面のビュー名（index）
	 */
	// 一覧・投稿画面を表示
	@GetMapping("/")
	public String index(
			@RequestParam(defaultValue = "1") int bbsId, //bbsId: 掲示板ID。指定が無ければデフォで1を使用
			@RequestParam(defaultValue = "0") int page, //page: ページ番号。指定が無ければデフォで0（先頭ページ）を使用
			@RequestParam(required = false) String keyword,
			Model model, HttpSession session) {

		// LE001: 処理開始ログ
		logger.info("LI001: {}処理を開始しました。", "一覧表示");

		//トークン発行＆セッション保存
		String csrfToken = UUID.randomUUID().toString();//UUIDを生成
		session.setAttribute("csrfToken", csrfToken);//セッションに保存
		model.addAttribute("csrfToken", csrfToken);//HTMLに渡す
		// ページ番号が負の値の場合は0に補正
		if (page < 0) {
			page = 0;
		}
		// 掲示板情報を取得
		Bbs bbs = bbsRepository.findById(bbsId).orElse(null);
		model.addAttribute("bbs", bbs);
		// システム設定から一覧表示件数を取得（デフォルト10件）
		int limit = settingsRepository.findBySettingKeyAndActiveFlagTrue("list_view_limit")
				.map(s -> Integer.parseInt(s.getSettingValue()))
				.orElse(10);
		// ページング設定（降順ソート）
		Pageable pageable = PageRequest.of(page, limit, Sort.by("createDate").descending());
		// 境界日時を計算（現在日時から3年前）
		LocalDateTime borderDate = LocalDateTime.now().minusYears(3);

		Page<Posts> postsPage;

		// 検索キーワードがあるかどうかで分岐
		if (keyword != null && !keyword.isEmpty()) {
			// キーワード検索あり
			postsPage = postsRepository.findRecentWithKeyword(bbsId, borderDate, keyword, pageable);
		} else {
			// キーワード検索なし
			postsPage = postsRepository.findByBbsIdAndCreateDateAfter(bbsId, borderDate, pageable);
		}
		// モデルに属性を追加
		model.addAttribute("postsList", postsPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", postsPage.getTotalPages());
		model.addAttribute("bbsId", bbsId);
		model.addAttribute("keyword", keyword); // 検索ワードを画面に渡す
		// 過去の投稿（境界年数より前）が存在するかチェック
		boolean hasOlderPosts = postsRepository.existsByBbsIdAndCreateDateBefore(bbsId, borderDate);
		model.addAttribute("hasOlderPosts", hasOlderPosts);
		// 新規投稿用のオブジェクトをモデルに追加（リダイレクト時のエラー表示用）
		if (!model.containsAttribute("post")) {
			model.addAttribute("post", new Posts());
		}
		// LE002: 処理終了ログ
		logger.info("LI002: {}処理を終了しました。", "一覧表示");
		return "index";
	}

	/**
	 * 新規投稿を保存
	 * <p>
	 * ユーザーが入力した投稿者名とメッセージを検証し、データベースに登録する。
	 * バリデーションエラーが発生した場合は、エラーメッセージを表示して元の画面に戻る。
	 * CSRF対策としてトークンの検証を実施する。
	 * </p>
	 * <p>
	 * 入力チェック内容：
	 * </p>
	 * <ul>
	 *   <li>投稿者名：必須、32文字以内</li>
	 *   <li>メッセージ：必須、1000文字以内</li>
	 *   <li>CSRFトークン：セッションと一致すること</li>
	 * </ul>
	 * 
	 * @param posts 投稿データ。バリデーション対象
	 * @param result バリデーション結果
	 * @param page 現在のページ番号。リダイレクト先で使用
	 * @param redirectAttributes リダイレクト時に渡す属性
	 * @param request HTTPリクエスト
	 * @param session HTTPセッション
	 * @return リダイレクト先のURL
	 */
	@PostMapping("/post")
	public String post(
			@Valid @ModelAttribute("post") Posts posts, BindingResult result,
			@RequestParam(defaultValue = "0") Integer page,
			RedirectAttributes redirectAttributes, HttpServletRequest request,
			HttpSession session) {
		// LE001: 処理開始ログ
		logger.info("LI001: {}処理を開始しました。", "投稿");

		if (result.hasErrors()) {
			// バリデーションエラー時のログ
			if (result.hasFieldErrors("name")) {
				if (posts.getName() == null || posts.getName().isEmpty()) {
					logger.error("LE002: {}未入力エラー", "名前");
				} else {
					logger.error("LE003: {}文字列長超過エラー、入力値：{}", "名前", posts.getName());
				}
			}
			if (result.hasFieldErrors("message")) {
				if (posts.getMessage() == null || posts.getMessage().isEmpty()) {
					logger.error("LE002: {}未入力エラー", "投稿内容");
				} else {
					logger.error("LE003: {}文字列長超過エラー、入力値：{}", "投稿内容", posts.getMessage());
				}
			}

			// 入力内容とエラーをフラッシュスコープに保存
			redirectAttributes.addFlashAttribute(
					"org.springframework.validation.BindingResult.post", result);
			redirectAttributes.addFlashAttribute("post", posts);
			return "redirect:/?page=" + page;
		}
		try {
			// CSRFトークン検証（不正リクエストチェック）
			if (!csrfTokenValidator.isValid(request, session)) {
				redirectAttributes.addFlashAttribute("messageCode", "M01-006");
				return "redirect:/?page=" + page;
			}
			// 投稿データの設定
			posts.setBbsId(1);// 掲示板ID固定値
			posts.setGoodCount(0);// 高評価数初期化
			posts.setBadCount(0);// 低評価数初期化
			posts.setCreateDate(LocalDateTime.now());// 登録日時設定
			postsRepository.save(posts);// データベースに保存

		} catch (Exception e) {
			// LE001: 予期せぬエラーが発生
			logger.error("LE001: 予期せぬエラーが発生しました。例外メッセージ：{}", e.getMessage());
			// 処理中のシステムエラー（M00-001）
			redirectAttributes.addFlashAttribute("messageCode", "M00-001");
			return "redirect:/?page=" + page;
		}
		// LI002: 処理終了ログ
		logger.info("LI002: {}処理を終了しました。", "投稿");

		return "redirect:/?page=" + page;
	}

	/**
	 * 高評価
	 * <p>
	 * 指定された投稿IDの高評価件数をインクリメントする。
	 * 更新日時も現在日時に更新する。
	 * </p>
	 * 
	 * @param postId 投稿ID
	 * @return リダイレクト先のURL（一覧画面）
	 */
	@PostMapping("/good")
	public String good(@RequestParam Integer postId) {
		Posts post = postsRepository.findById(postId).orElse(null);
		if (post != null) {
			post.setGoodCount(post.getGoodCount() + 1);
			post.setUpdateDate(LocalDateTime.now());
			postsRepository.save(post);
		}
		return "redirect:/";
	}

	/**
	 * 低評価
	 * <p>
	 * 指定された投稿IDの低評価件数をインクリメントする。
	 * 更新日時も現在日時に更新する。
	 * </p>
	 * 
	 * @param postId 投稿ID
	 * @return リダイレクト先のURL（一覧画面）
	 */
	@PostMapping("/bad")
	public String bad(@RequestParam Integer postId) {
		Posts post = postsRepository.findById(postId).orElse(null);
		if (post != null) {
			post.setBadCount(post.getBadCount() + 1);
			post.setUpdateDate(LocalDateTime.now());
			postsRepository.save(post);
		}
		return "redirect:/";
	}

	/**
	 * 投稿履歴画面を表示
	 * <p>
	 * 「もっと見る」リンク先として、システム設定で指定された境界年数より
	 * 過去の投稿一覧を表示する。キーワード検索が指定された場合は、
	 * 投稿者名またはメッセージに該当するキーワードを含む投稿のみを表示する。
	 * </p>
	 * 
	 * @param bbsId 掲示板ID
	 * @param page ページ番号。指定がない場合はデフォルトで0（先頭ページ）を使用
	 * @param keyword 検索キーワード。任意パラメータ
	 * @param model ビューに渡すデータを格納するモデル
	 * @return 投稿履歴画面のビュー名（bbs/history）
	 */
	@GetMapping("/bbs/history")
	public String showHistory(@RequestParam int bbsId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String keyword,
			Model model) {
		// 境界日時を計算（現在日時から3年前）
		LocalDateTime borderDate = LocalDateTime.now().minusYears(3);
		// 一覧表示件数（固定値10件）
		int limit = 10;
		// ページング設定（降順ソート）
		Pageable pageable = PageRequest.of(page, limit, Sort.by("createDate").descending());
		// 過去の投稿を取得（キーワード検索対応）
		Page<Posts> oldPosts = postsRepository.findHistoryWithKeyword(bbsId, borderDate, keyword, pageable);
		// モデルに属性を追加
		model.addAttribute("keyword", keyword);
		model.addAttribute("postsList", oldPosts.getContent());
		model.addAttribute("bbsId", bbsId);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", oldPosts.getTotalPages());
		// 掲示板情報を取得（history.htmlのタイトル用）
		Bbs bbs = bbsRepository.findById(bbsId).orElse(null);
		model.addAttribute("bbs", bbs);

		return "bbs/history";
	}

}
