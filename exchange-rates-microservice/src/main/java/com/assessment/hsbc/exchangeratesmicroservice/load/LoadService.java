package com.assessment.hsbc.exchangeratesmicroservice.load;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assessment.hsbc.exchangeratesmicroservice.utils.DateValidator;
import com.assessment.hsbc.exchangeratesmicroservice.utils.DateValidatorUsingDateFormat;
import com.assessment.hsbc.exchangeratesmicroservice.utils.Result;

@Service
public class LoadService {

	final String baseUrl = "https://api.ratesapi.io/api/";
	private static final Logger log = LoggerFactory.getLogger(LoadService.class);

	private DateValidator validator = new DateValidatorUsingDateFormat("yyyy-MM-dd");

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private LoadRepository repo;

	public ResponseEntity<?> loadData(String inputDate, String base, List<String> symbols) {
		String url = baseUrl;
		try {

			Date date = validator.isValid(inputDate);
			if (date == null) {
				return new ResponseEntity<String>("Invalied Date..,", HttpStatus.INTERNAL_SERVER_ERROR);
			} else if (date != null) {
				LocalDate currentDate = LocalDate.parse(inputDate);
				if (currentDate.getDayOfMonth()!=1) {
					log.info("Date day:" + date.getDay());
					return new ResponseEntity<String>("Allowed rate as of the 1'st Day of Month only.",
							HttpStatus.INTERNAL_SERVER_ERROR);
				} else {
					url += inputDate;
				}
			}

			if (base != null)
				url += base;
			if (symbols != null) {
				url += String.join(",", symbols);
			}
			log.info("==== RESTful API Response using Spring RESTTemplate START =======");
			log.info("URL:"+url);
			Result response = restTemplate.getForObject(url, Result.class);
			log.info("==== RESTful API Response using Spring RESTTemplate END =======");

			if (response != null) {
				return this.saveData(response);
			} else {
				return new ResponseEntity<String>("No Data fetched from exchange rates api.", HttpStatus.OK);
			}

		} catch (Exception e) {
			// log.error("Error While loading data from extenal api", e.getStackTrace());
			return new ResponseEntity<String>("Invalied Payload.., Error: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<?> saveData(Result response) {
		ResponseEntity<?> results = null;
		try {
			LoadEntity le = new LoadEntity();
			Map<String, Double> rates = response.getRates();
			le.setBase(response.getBase());

			if (rates != null) {
				if (rates.containsKey("GBP"))
					le.setGBP(rates.get("GBP"));

				if (rates.containsKey("HKD"))
					le.setHKD(rates.get("HKD"));

				if (rates.containsKey("USD"))
					le.setUSD(rates.get("USD"));
			}
			le.setDate(validator.isValid(response.getDate()));

			repo.save(le);
			results = new ResponseEntity<String>("Data saved succefully..,: ", HttpStatus.OK);
		} catch (Exception e) {
			results = new ResponseEntity<String>("Error while saving data..,: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return results;
	}

	public ResponseEntity<?> loadData(String base, List<String> symbols) {
		ResponseEntity<?> resp=null;
		Date fromDate = validator.getLastYearDate();
		Date eachMonths = null;
		String currDate = null;
		for (int i = 0; i < 12; i++) {
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.add(Calendar.MONTH, i+1);
			c.add(Calendar.DAY_OF_MONTH,0);
			eachMonths = c.getTime();
			currDate = validator.stringDate(eachMonths);
			resp = this.loadData(currDate, base, symbols);
		}
		return resp;
	}

}
