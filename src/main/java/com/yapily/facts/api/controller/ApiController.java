package com.yapily.facts.api.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yapily.facts.api.config.FactConstants;
import com.yapily.facts.api.services.APIService;
import com.yapily.facts.api.services.Facts;

@RestController
public class ApiController {

	@Autowired
	public APIService apiSvc;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public ResponseEntity getStatus() throws IOException {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			int count = apiSvc.getUniqueCount();
			if (count > 0) {
				Map<String, Object> factsresp = new HashMap<String, Object>();
				factsresp.put("total", FactConstants.FACTCOUNT);
				factsresp.put("unique", count);
				response.put("status", "COMPLETED");
				response.put("facts", factsresp);
			}
		} catch (Exception e) {
			response.put("status", "ERROR OCCURED WHILE FETCHING FACTS");
		}
		return new ResponseEntity(response, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/facts", method = RequestMethod.GET)
	public ResponseEntity getFactIds() throws JsonParseException, JsonMappingException, IOException {
		List factids = apiSvc.getfactIds();
		return new ResponseEntity(factids, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/facts/{factId}", method = RequestMethod.GET)
	public ResponseEntity getFactIds(@PathVariable String factId, @RequestParam(name = "lang", defaultValue = "en", required = false) String trgtLang)
			throws JsonParseException, JsonMappingException, IOException {
		Facts fact = apiSvc.getFactsById(factId, trgtLang);
		if (fact == null) {
			return new ResponseEntity("Failed to get the Fact by given ID and language", HttpStatus.OK);
		}
		return new ResponseEntity(fact, HttpStatus.OK);
	}
}
