package com.spring.emailtemplate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.emailtemplate.entity.FaceEmbedding;

@Repository
public interface FaceEmbeddingRepository extends JpaRepository<FaceEmbedding, Long>{

	Optional<FaceEmbedding> findByUserId(String userId);

}
