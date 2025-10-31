/**
 * 掲示板アプリケーション
 */

// DOMの読み込み完了を待つ
document.addEventListener('DOMContentLoaded', function() {
	initializeApp();
});

/**
 * アプリケーションの初期化
 */
function initializeApp() {
	initializeSearchDialog();// 検索ダイアログの初期化
	initializeEmojiDialog();// 絵文字ダイアログの初期化
	initializeExistingFeatures();
	initializeSearchKeywordDisplay();//検索キーワード
	initializeFormValidation();//
}

/**
 * 検索ダイアログの初期化
 */
function initializeSearchDialog() {
	const searchButton = document.getElementById('searchButton');
	const searchDialog = document.getElementById('searchDialog');
	const closeSearchDialog = document.getElementById('closeSearchDialog');
	const executeSearch = document.getElementById('executeSearch');
	const searchKeywordInput = document.getElementById('searchKeywordInput');
	const clearSearchButton = document.getElementById('clearSearchButton');

	// 検索ボタンクリック - ダイアログを開く (A03-001)
	if (searchButton) {
		searchButton.addEventListener('click', function() {
			openSearchDialog();
		});
	}

	// 閉じるボタンクリック - ダイアログを閉じる (A03-001)
	if (closeSearchDialog) {
		closeSearchDialog.addEventListener('click', function() {
			closeDialog(searchDialog);
		});
	}

	// 検索ボタンクリック - 検索実行 (A03-002)
	if (executeSearch) {
		executeSearch.addEventListener('click', function() {
			executeSearchAction();
		});
	}

	// Enterキーで検索実行
	if (searchKeywordInput) {
		searchKeywordInput.addEventListener('keypress', function(e) {
			if (e.key === 'Enter') {
				executeSearchAction();
			}
		});
	}

	// 検索解除ボタンクリック
	if (clearSearchButton) {
		clearSearchButton.addEventListener('click', function(e) {
			e.preventDefault();
			clearSearch();
		});
	}

	// オーバーレイクリックで閉じる
	if (searchDialog) {
		searchDialog.addEventListener('click', function(e) {
			if (e.target === searchDialog) {
				closeDialog(searchDialog);
			}
		});
	}
}

/**
 * 検索ダイアログを開く
 */
function openSearchDialog() {
	const searchDialog = document.getElementById('searchDialog');
	const searchKeywordInput = document.getElementById('searchKeywordInput');

	if (searchDialog) {
		searchDialog.classList.add('active');
		// 入力欄にフォーカス
		if (searchKeywordInput) {
			searchKeywordInput.value = currentSearchKeyword;
			setTimeout(() => searchKeywordInput.focus(), 100);
		}
	}
}

/**
 * 検索実行 
 */
function executeSearchAction() {
	const keyword = document.getElementById('searchKeywordInput')?.value.trim();
	if (!keyword) return alert('検索キーワードを入力してください');

	const params = new URLSearchParams(location.search);
	const bbsId = params.get('bbsId') || document.getElementById('bbsIdHidden')?.value || '';
	const base = location.pathname.includes('/bbs/history') ? '/bbs/history' : '/';

	location.href = `${base}?bbsId=${bbsId}&keyword=${encodeURIComponent(keyword)}&page=0`;
}
/**
 * 検索解除 
 */
function clearSearch() {
	const params = new URLSearchParams(location.search);
	const bbsId = params.get('bbsId') || document.getElementById('bbsIdHidden')?.value || '';
	const base = location.pathname.includes('/bbs/history') ? '/bbs/history' : '/';

	location.href = `${base}?bbsId=${bbsId}&page=0`;
}

/**
 * 検索キーワード表示
 */
function initializeSearchKeywordDisplay() {
	const urlParams = new URLSearchParams(location.search);
	const keyword = urlParams.get('keyword');

	const searchInfo = document.getElementById('searchInfo');
	const keywordDisplay = document.getElementById('searchKeywordDisplay');

	if (keyword && searchInfo && keywordDisplay) {
		searchInfo.style.display = 'block';
		keywordDisplay.textContent = keyword;
	}
}


/**
 * 絵文字ダイアログの初期化
 */
function initializeEmojiDialog() {
	const emojiButton = document.getElementById('emojiButton');
	const emojiDialog = document.getElementById('emojiDialog');
	const closeEmojiDialog = document.getElementById('closeEmojiDialog');

	// 絵文字ボタンクリック - ダイアログを開く
	if (emojiButton) {
		emojiButton.addEventListener('click', function() {
			openEmojiDialog();
		});
	}

	// 閉じるボタンクリック - ダイアログを閉じる
	if (closeEmojiDialog) {
		closeEmojiDialog.addEventListener('click', function() {
			closeDialog(emojiDialog);
		});
	}

	// オーバーレイクリックで閉じる
	if (emojiDialog) {
		emojiDialog.addEventListener('click', function(e) {
			if (e.target === emojiDialog) {
				closeDialog(emojiDialog);
			}
		});
	}

	// 絵文字グリッドを生成
	generateEmojiGrid();
}

/**
 * 絵文字グリッドを生成
 */
function generateEmojiGrid() {
	const emojiGrid = document.getElementById('emojiGrid');
	if (!emojiGrid) return;

	//設計書に記載されている絵文字のHTMLコード
	const emojiCodes = [
		'&#x1F600;', '&#x1F603;', '&#x1F604;', '&#x1F601;',
		'&#x1F606;', '&#x1F979;', '&#x1F605;', '&#x1F602;',
		'&#x1F923;', '&#x1F972;', '&#x1F60A;', '&#x1F607;',
		'&#x1F642;', '&#x1F643;', '&#x1F609;', '&#x1F60C;',
		'&#x1F60D;', '&#x1F970;', '&#x1F618;', '&#x1F617;',
		'&#x1F619;', '&#x1F61A;', '&#x1F60B;', '&#x1F61B;',
		'&#x1F61D;', '&#x1F61C;', '&#x1F92A;', '&#x1F928;',
		'&#x1F9D0;', '&#x1F913;', '&#x1F60E;', '&#x1F978;',
		'&#x1F929;', '&#x1F973;', '&#x1F60F;', '&#x1F612;',
		'&#x1F61E;', '&#x1F614;', '&#x1F61F;', '&#x1F615;',
		'&#x1F641;', '&#x1F623;', '&#x1F616;', '&#x1F62B;',
		'&#x1F629;', '&#x1F97A;', '&#x1F622;', '&#x1F62D;',
		'&#x1F624;', '&#x1F620;', '&#x1F621;', '&#x1F92C;',
		'&#x1F92F;', '&#x1F633;', '&#x1F975;', '&#x1F976;',
		'&#x1F636;', '&#x1F631;', '&#x1F628;', '&#x1F630;',
		'&#x1F625;', '&#x1F613;', '&#x1F917;', '&#x1F914;',
		'&#x1FAE3;', '&#x1F92D;', '&#x1FAE2;', '&#x1FAE1;',
		'&#x1F92B;', '&#x1FAE0;', '&#x1F925;', '&#x1F636;',
		'&#x1FAE5;', '&#x1F610;', '&#x1FAE4;', '&#x1F611;',
		'&#x1FAE8;', '&#x1F62C;', '&#x1F644;', '&#x1F62F;',
		'&#x1F626;', '&#x1F627;', '&#x1F62E;', '&#x1F632;',
		'&#x1F971;', '&#x1F634;', '&#x1F924;', '&#x1F62A;',
		'&#x1F62E;', '&#x1F635;', '&#x1F910;', '&#x1F974;',
		'&#x1F922;', '&#x1F92E;', '&#x1F927;', '&#x1F637;',
		'&#x1F912;', '&#x1F915;', '&#x1F911;', '&#x1F920;',
		'&#x1F608;', '&#x1F47F;', '&#x1F479;', '&#x1F47A;',
		'&#x1F921;', '&#x1F4A9;', '&#x1F47B;', '&#x1F480;',
		'&#x1F47D;', '&#x1F47E;', '&#x1F916;', '&#x1F383;',
		'&#x1F63A;', '&#x1F638;', '&#x1F639;', '&#x1F63B;',
		'&#x1F63C;', '&#x1F63D;', '&#x1F640;', '&#x1F63F;',
		'&#x1F63E;'
	];

	// 各絵文字のボタンを生成
	emojiCodes.forEach(code => {
		const button = document.createElement('button');
		button.className = 'emoji-button';
		button.innerHTML = code;
		button.type = 'button';

		// クリックイベント
		button.addEventListener('click', function() {
			insertEmojiToMessage(this.textContent);
		});

		emojiGrid.appendChild(button);
	});
}

/**
 * 絵文字ダイアログを開く
 */
function openEmojiDialog() {
	const emojiDialog = document.getElementById('emojiDialog');
	if (emojiDialog) {
		emojiDialog.classList.add('active');
	}
}

/**
 * 絵文字をメッセージに挿入
 */
function insertEmojiToMessage(emoji) {
	const messageInput = document.getElementById('messageInput');
	const emojiDialog = document.getElementById('emojiDialog');

	if (messageInput) {
		// カーソル位置に絵文字を挿入
		const start = messageInput.selectionStart;
		const end = messageInput.selectionEnd;
		const text = messageInput.value;

		messageInput.value = text.substring(0, start) + emoji + text.substring(end);

		// カーソル位置を絵文字の後ろに移動
		const newCursorPos = start + emoji.length;
		messageInput.setSelectionRange(newCursorPos, newCursorPos);
		messageInput.focus();
	}

	// ダイアログを閉じる
	closeDialog(emojiDialog);

	console.log('絵文字を挿入:', emoji);
}

/**
 * ダイアログを閉じる
 */
function closeDialog(dialog) {
	if (dialog) {
		dialog.classList.remove('active');
	}
}

/**
 * 既存の機能を初期化
 */
function initializeExistingFeatures() {
	// いいねボタンのイベントリスナー
	const likeButtons = document.querySelectorAll('.btn-good');
	likeButtons.forEach(button => {
		button.addEventListener('click', handleLike);
	});

	// よくないねボタンのイベントリスナー
	const dislikeButtons = document.querySelectorAll('.btn-bad');
	dislikeButtons.forEach(button => {
		button.addEventListener('click', handleDislike);
	});

	// ソートラジオボタンのイベントリスナー
	const sortOptions = document.querySelectorAll('input[name="sort"]');
	sortOptions.forEach(option => {
		option.addEventListener('change', handleSortChange);
	});
}
//
///**
// * いいね処理
// */
function handleLike(event) {
	// フォーム送信を許可
	console.log('いいね！が押されました');
}

/**
 * よくないね処理
 */
function handleDislike(event) {
	// フォーム送信を許可
	console.log('よくないねが押されました');
}

/**
 * 昇順・降順（Sort）
 */
function handleSortChange(event) {
	const sortValue = event.target.value;
	const postsList = document.querySelector('.posts-list');
	const posts = Array.from(postsList.querySelectorAll('.post-item'));

	// 投稿日時でソート
	posts.sort((a, b) => {
		const dateA = new Date(a.querySelector('.post-date').textContent.replace(/\//g, '-'));
		const dateB = new Date(b.querySelector('.post-date').textContent.replace(/\//g, '-'));
		return sortValue === 'asc' ? dateA - dateB : dateB - dateA;
	});

	// 再配置
	posts.forEach(post => postsList.appendChild(post));
}

/**
 * 現在の日時を取得
 */
function getCurrentDateTime() {
	const now = new Date();
	const year = now.getFullYear();
	const month = String(now.getMonth() + 1).padStart(2, '0');
	const day = String(now.getDate()).padStart(2, '0');
	const hours = String(now.getHours()).padStart(2, '0');
	const minutes = String(now.getMinutes()).padStart(2, '0');

	return `${year}/${month}/${day} ${hours}:${minutes}`;
}

/**
 * 投稿を動的に追加
 */
function addPost(name, message, datetime) {
	// 新しい投稿要素を作成する例
	const postHTML = `
        <div class="post-item">
            <div class="post-header">
                <span class="post-number">No.<span>新規</span></span>
                <span class="post-name">${name}</span>
                <span class="post-date">${datetime}</span>
            </div>
            <div class="post-message">${message}</div>
            <div class="post-actions">
                <button class="btn-good">👍 <span>0</span>件</button>
                <button class="btn-bad">👎 <span>0</span>件</button>
            </div>
        </div>
    `;

	// ここで実際にDOMに追加する処理を実装
	console.log('新しい投稿を追加:', postHTML);
}

/**
 * クライアント側の入力チェック
 */
function initializeFormValidation() {
	const form = document.getElementById('postForm');
	if (!form) return;

	form.addEventListener('submit', function(e) {
		const nameInput = document.getElementById('nameInput');
		const messageInput = document.getElementById('messageInput');

		let errors = [];

		// 名前チェック
		if (!nameInput.value.trim()) {
			errors.push("名前を入力してください");
		} else if (nameInput.value.length > 32) {
			errors.push("名前は32文字以内で入力してください");
		}

		// メッセージチェック
		if (!messageInput.value.trim()) {
			errors.push("メッセージを入力してください");
		} else if (messageInput.value.length > 1000) {
			errors.push("メッセージは1000文字以内で入力してください");
		}

		// エラーがあれば送信中止
		if (errors.length > 0) {
			e.preventDefault();//そのページに留まる
			alert(errors.join("\n"));
		}
	});
}
