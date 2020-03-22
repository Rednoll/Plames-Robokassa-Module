package com.inwaiders.plames.modules.robokassa.web.paygate.ajax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;

@RestController(value = "RobokassaGatewayWebAjax")
@RequestMapping("web/controller/ajax/robokassa/paygate")
public class GatewayWebAjax {

	@Autowired
	private ObjectMapper mapper = null;
	
	@GetMapping("")
	public ArrayNode mainPage(@RequestParam(required = false) String name, @RequestParam(required = false) Long id, @RequestParam(required = false, name="merchant_login") String merchantLogin, @RequestParam(name="test_mode", required = false) Boolean testMode, @RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "12", required = false) int pageSize) {
		
		if(page < 0) page = 0;
		if(pageSize < 1) pageSize = 12;
		
		List<RobokassaGateway> allPaygates = null;
		
		if(id == null && testMode == null && (name == null || name.isEmpty()) && (merchantLogin == null || merchantLogin.isEmpty())) {
			
			allPaygates = RobokassaGateway.getOrderedByName();
		}
		else {
			
			allPaygates = RobokassaGateway.search(name, id, merchantLogin, testMode);
		}
		
		ArrayNode jsonpaygates = mapper.createArrayNode();
		
			for(int i = page*pageSize; i < page*pageSize + pageSize; i++) {
				
				if(i >= allPaygates.size()) break;
				
				jsonpaygates.add(allPaygates.get(i).toJson(mapper));
			}
			
		return jsonpaygates;
	}
	
	@GetMapping(value = "/{id}/description", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@PathVariable(name="id") long paygateId) {

		RobokassaGateway paygate = RobokassaGateway.getById(paygateId);
		
		if(paygate != null) {
			
			try {
				
				return new ResponseEntity<String>(paygate.getDescription(PlamesLocale.getSystemLocale()), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>(PlamesLocale.getSystemLocale().getMessage("data.loading_error"), HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/{id}/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json, @PathVariable(name="id") long paygateId) {
		
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		boolean active = json.get("active").asBoolean();
	
		RobokassaGateway paygate = RobokassaGateway.getById(paygateId);
		
		if(paygate != null) {
			
			paygate.setActive(active);
			paygate.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
}
