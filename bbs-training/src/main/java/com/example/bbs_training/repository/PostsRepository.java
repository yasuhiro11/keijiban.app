package com.example.bbs_training.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bbs_training.entity.Posts;

public interface PostsRepository extends JpaRepository<Posts, Integer> {

	// ページング対応（全件）
	Page<Posts> findByBbsId(Integer bbsId, Pageable pageable);

	// 並び替えだけ（全件）
	List<Posts> findByBbsIdOrderByCreateDateDesc(Integer bbsId);

	// 3年以上前の投稿が存在するかどうか
	boolean existsByBbsIdAndCreateDateBefore(Integer bbsId, LocalDateTime borderDate);

	// 3年以内の投稿一覧（ページング対応、通常画面用）
	Page<Posts> findByBbsIdAndCreateDateAfter(Integer bbsId, LocalDateTime borderDate, Pageable pageable);

	// 3年以内の投稿一覧（全件取得、必要なら）
	List<Posts> findByBbsIdAndCreateDateAfterOrderByCreateDateDesc(Integer bbsId, LocalDateTime borderDate);

	// 3年以上前の投稿一覧（ページング対応、history画面用）
	@Query("""
			SELECT p FROM Posts p
			WHERE p.bbsId = :bbsId
			AND p.createDate < :borderDate
			AND (:keyword IS NULL OR :keyword = '' OR p.message LIKE CONCAT('%', :keyword, '%') OR p.name LIKE CONCAT('%', :keyword, '%'))""")
	Page<Posts> findHistoryWithKeyword(@Param("bbsId") int bbsId,
			@Param("borderDate") LocalDateTime borderDate,
			@Param("keyword") String keyword, Pageable pageable);
	// 3年以内の投稿一覧（ページング対応、index画面用）
	@Query("""
		    SELECT p FROM Posts p
		    WHERE p.bbsId = :bbsId
		    AND p.createDate >= :borderDate
		    AND (:keyword IS NULL OR :keyword = '' OR p.message LIKE CONCAT('%', :keyword, '%') OR p.name LIKE CONCAT('%', :keyword, '%'))""")
		Page<Posts> findRecentWithKeyword(@Param("bbsId") int bbsId,
		                                  @Param("borderDate") LocalDateTime borderDate,
		                                  @Param("keyword") String keyword,Pageable pageable);

}
