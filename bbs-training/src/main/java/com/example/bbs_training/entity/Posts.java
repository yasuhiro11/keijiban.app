package com.example.bbs_training.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tbl_posts")
public class Posts {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_posts_gen")
	@SequenceGenerator(name = "seq_posts_gen", sequenceName = "tbl_posts_id_seq", allocationSize = 1)
	private Integer id;

	@Column(name = "bbs_id")
	private Integer bbsId;

	@NotBlank(message = "{M01-002}")
	@Size(max = 32, message = "{M01-003}")
	private String name;

	@NotBlank(message = "{M01-004}")
	@Size(max = 1000, message = "{M01-005}")
	private String message;

	@Column
	private Integer goodCount;

	@Column
	private Integer badCount;

	@Column(name = "create_date")
	private LocalDateTime createDate;

	@Column(name = "update_date")
	private LocalDateTime updateDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBbsId() {
		return bbsId;
	}

	public void setBbsId(Integer bbsId) {
		this.bbsId = bbsId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getGoodCount() {
		return goodCount;
	}

	public void setGoodCount(Integer goodCount) {
		this.goodCount = goodCount;
	}

	public Integer getBadCount() {
		return badCount;
	}

	public void setBadCount(Integer badCount) {
		this.badCount = badCount;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate) {
		this.updateDate = updateDate;
	}

}
