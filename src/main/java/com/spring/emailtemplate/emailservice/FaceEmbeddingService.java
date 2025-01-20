package com.spring.emailtemplate.emailservice;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spring.emailtemplate.entity.FaceEmbedding;
import com.spring.emailtemplate.repository.FaceEmbeddingRepository;

@Service
public class FaceEmbeddingService {

	@Autowired
	private FaceEmbeddingRepository faceEmbeddingRepository;

	public FaceEmbedding saveEmbedding(FaceEmbedding faceEmbedding) {
		return faceEmbeddingRepository.save(faceEmbedding);
	}

	// Find face embedding by user ID
	public Optional<FaceEmbedding> findByUserId(String userId) {
		return faceEmbeddingRepository.findByUserId(userId);
	}

	// Verify if the face matches the stored embedding
	public boolean verifyEmbedding(String userId, double[] liveEmbedding, double threshold) {
		Optional<FaceEmbedding> storedEmbeddingOpt = faceEmbeddingRepository.findByUserId(userId);

		if (storedEmbeddingOpt.isPresent()) {
			FaceEmbedding storedEmbedding = storedEmbeddingOpt.get();
			double similarity = cosineSimilarity(storedEmbedding.getEmbedding(), liveEmbedding);
			return similarity >= threshold;
		}
		return false;
	}

	// Calculate cosine similarity
	private double cosineSimilarity(double[] vectorA, double[] vectorB) {
		if (vectorA.length != vectorB.length) {
			throw new IllegalArgumentException("Vectors must be of the same length!");
		}

		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;

		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}
