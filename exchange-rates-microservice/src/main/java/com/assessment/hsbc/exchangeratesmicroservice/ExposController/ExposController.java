package com.assessment.hsbc.exchangeratesmicroservice.ExposController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assessment.hsbc.exchangeratesmicroservice.load.LoadEntity;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/expos")
public class ExposController {

	@Autowired
	private ExposService service;

	@GetMapping("/greetings")
	public Object helloWorld() {
		return "Exposing Data...,";
	}

	@GetMapping("/")
	public ResponseEntity<?> findAll() {
		ResponseEntity<?> responce = null;
		try {
			Iterable<LoadEntity> listAll = service.listAll();
			responce = new ResponseEntity<>(listAll, HttpStatus.OK);
		} catch (Exception e) {
			responce = new ResponseEntity<>("Error while fetching data..,", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responce;
	}

	@GetMapping("/{date}")
	public ResponseEntity<?> findByDate(@PathVariable String date) {
		return service.getByDate(date);
	}

	@GetMapping("/betweendates")
	public ResponseEntity<?> findBetweenDate(@RequestParam String frmDate, @RequestParam String toDate) {
		return service.findBetweenDate(frmDate, toDate);
	}

}
