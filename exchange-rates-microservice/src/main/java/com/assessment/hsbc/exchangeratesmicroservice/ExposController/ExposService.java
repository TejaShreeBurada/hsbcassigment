package com.assessment.hsbc.exchangeratesmicroservice.ExposController;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.assessment.hsbc.exchangeratesmicroservice.load.LoadEntity;
import com.assessment.hsbc.exchangeratesmicroservice.utils.DateValidator;
import com.assessment.hsbc.exchangeratesmicroservice.utils.DateValidatorUsingDateFormat;

@Service
public class ExposService {

	@Autowired
	ExposRepository repo;

	private DateValidator validator = new DateValidatorUsingDateFormat("yyyy-MM-dd");

	public Iterable<LoadEntity> listAll() {
		return repo.findAll();
	}

	public ResponseEntity<?> getByDate(String dateIn) {
		Iterable<LoadEntity> le = null;
		Date date = validator.isValid(dateIn);
		if (date == null) {
			return new ResponseEntity<String>("Invalied Date..,", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		le = repo.findByDate(date);
		return new ResponseEntity<Iterable<LoadEntity>>(le, HttpStatus.OK);
	}

	public ResponseEntity<?> findBetweenDate(String frmStrDate, String toStrDate) {
		Iterable<LoadEntity> le = null;
		Date fromDate = validator.isValid(frmStrDate);
		if (fromDate == null) {
			return new ResponseEntity<String>("Invalied From Date Formate..,", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Date toDate = validator.isValid(toStrDate);
		if (toDate == null) {
			return new ResponseEntity<String>("Invalied To Date Formate..,", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		le = repo.findByDate(fromDate, toDate);
		return new ResponseEntity<Iterable<LoadEntity>>(le, HttpStatus.OK);
	}
}
