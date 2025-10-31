package com.example.bbs_training.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bbs_training.entity.Settings;

/**
 * システム設定テーブル（tbl_settings）にアクセスするためのリポジトリ
 */

public interface SettingsRepository extends JpaRepository<Settings, String> {
	/**
	 * 指定されたキーで、active_flag が true の設定を1件取得する
	 *
	 * @param settingKey 設定キー（例: "list_view_limit"）
	 * @return 該当する設定があればその情報、なければ空の Optional
	 */

	Optional<Settings> findBySettingKeyAndActiveFlagTrue(String settingKey);

}
