package com.spring.emailtemplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.emailtemplate.emailservice.FaceEmbeddingService;
import com.spring.emailtemplate.entity.FaceEmbedding;

@RestController
@RequestMapping("/api/faceembad")
public class FaceEmbeddingController {

	@Autowired
	private FaceEmbeddingService faceEmbeddingService;

	@PostMapping("/register-face")
	public ResponseEntity<FaceEmbedding> registerFace(@RequestBody FaceEmbedding faceData) {
		FaceEmbedding faceEmbedding = faceEmbeddingService.saveEmbedding(faceData);
		return ResponseEntity.ok(faceEmbedding);
	}

	@PostMapping("/verify-face")
	public ResponseEntity<String> verifyFace(@RequestBody FaceEmbedding liveFace) {
	    if (liveFace == null || liveFace.getUserId() == null || liveFace.getEmbedding() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid face data provided!");
	    }

	    String userId = liveFace.getUserId();
	    double[] liveEmbedding = liveFace.getEmbedding();
	    double threshold = 0.8;
	    boolean isVerified = faceEmbeddingService.verifyEmbedding(userId, liveEmbedding, threshold);

	    if (isVerified) {
	        return ResponseEntity.ok("Access Granted!");
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied!");
	    }
	}
}
