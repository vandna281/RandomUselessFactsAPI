package com.yapily.facts.api.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapily.facts.api.config.FactConstants;
import com.yapily.facts.api.entity.Translation;

@Service("restApiService")
public class RestApiService {

	final static Logger logger = LoggerFactory.getLogger(RestApiService.class);

	@Value("${yandex.api.key}")
	private String apiKey;

	@Value("${yandex.base.url}")
	private String baseUrl;

	public JSONObject fetchFromrandomFactlessApi() throws ParseException, JsonProcessingException {
		final String uri = "https://uselessfacts.jsph.pl/random.json?language=en";
		RestTemplate restTemplate = new RestTemplate();
		Object factObj = restTemplate.getForObject(uri, Object.class);
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(factObj);
		JSONParser parser = new JSONParser();
		JSONObject fact = (JSONObject) parser.parse(jsonString);
		return fact;
	}

	public String getTranslatedText(String text, String lang) {
		logger.info("Translating text {} in Language {}", text, lang);
		RestTemplate template = new RestTemplate();
		StringBuilder url = new StringBuilder(baseUrl);
		url.append(FactConstants.TRANSLATION_URL);
		url.append(FactConstants.KEY).append("=").append(apiKey).append("&");
		url.append(FactConstants.TEXT_TO_TRANSLATE).append("=").append(text).append("&");
		url.append(FactConstants.TARGET_LANGUAGE).append("=").append(lang);
		ResponseEntity<Translation> translatedEntity = template.getForEntity(url.toString(), Translation.class);

		return translatedEntity.getBody().getText().get(0);
	}

}
