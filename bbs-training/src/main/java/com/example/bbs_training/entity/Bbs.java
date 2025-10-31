package com.example.bbs_training.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_bbs")
public class Bbs {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bbs_gen")
	@SequenceGenerator(name = "seq_bbs_gen", sequenceName = "tbl_bbs_id_seq",
	allocationSize = 1)

	private Integer id;
	@Column
	private String title;
	@Column
	private String administrator;
	@Column
	private LocalDateTime createDate;
	@Column
	private LocalDateTime updateDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
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
