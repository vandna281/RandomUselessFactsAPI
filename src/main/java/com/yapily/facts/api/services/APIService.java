package com.yapily.facts.api.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yapily.facts.api.config.FactConstants;

@Service
public class APIService {

	@Autowired
	public RestApiService restSvc;

	@SuppressWarnings("unchecked")
	public int getUniqueCount() throws JsonParseException, JsonMappingException, IOException {
		int count = 0;
		List<Facts> factlist = getAllFactList();
		count = (factlist != null) ? factlist.size() : 0;
		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getfactIds() throws JsonParseException, JsonMappingException, IOException {
		List<Facts> factlist = getAllFactList();
		List idlist = factlist.stream().map(Facts::getId).distinct().collect(Collectors.toList());
		return idlist;
	}

	@SuppressWarnings("unchecked")
	public Facts getFactsById(String factId, String trgtLang)
			throws JsonParseException, JsonMappingException, IOException {
		List<Facts> factlist = getAllFactList();
		Map<String, Facts> factMap = factlist.stream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		if (factMap.containsKey(factId)) {
			return getFactByLang(factMap.get(factId), trgtLang);
		} else {
			return null;
		}
	}

	private Facts getFactByLang(Facts fact, String trgtLang) throws IOException {
		String transFactStr = null;
		if (!fact.getLanguage().equalsIgnoreCase(trgtLang.toString())) {
			transFactStr = restSvc.getTranslatedText(fact.getText(), trgtLang);
			if (transFactStr == null) {
				return null;
			} else {
				fact.setText(transFactStr);
				fact.setLanguage(trgtLang);
			}
		}
		return fact;
	}

	@SuppressWarnings("rawtypes")
	private List getAllFactList() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Facts> factlist = objectMapper.readValue(new File("uselessfacts.json"), new TypeReference<List<Facts>>() {
		});
		return factlist;
	}

	@PostConstruct
	@SuppressWarnings("unchecked")
	private void init() throws JsonProcessingException {
		Map<String,JSONObject> factsMap = new HashMap<String, JSONObject>();
		JSONArray factsList = new JSONArray();
		for (int i = 0; i < FactConstants.FACTCOUNT; i++) {
			try {
				JSONObject result = restSvc.fetchFromrandomFactlessApi();
				if (!factsMap.containsKey(result.get("id"))) {
					factsMap.put(result.get("id").toString(), result);
					factsList.add(result);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (factsList.size() > 0) {
			System.out.println("Unique Items : " + factsList.size());
			saveFactsToFile(factsList);
		}
	}

	private void saveFactsToFile(JSONArray factsList) {
		try (FileWriter file = new FileWriter("uselessfacts.json")) {

			file.write(factsList.toJSONString());
			file.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
