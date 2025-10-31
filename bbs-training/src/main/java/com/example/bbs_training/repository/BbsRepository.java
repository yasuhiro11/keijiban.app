package com.example.bbs_training.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bbs_training.entity.Bbs;

public interface BbsRepository extends JpaRepository<Bbs, Integer> {

}
