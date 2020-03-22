package com.inwaiders.plames.modules.robokassa.web.paygate.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;

@RestController(value = "RobokassaGatewayRestController")
@RequestMapping("api/robokassa/rest")
public class GatewayRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/paygates/{id}", produces = "application/json; charset=UTF-8")
	public ObjectNode get(@PathVariable long id) {
		
		RobokassaGateway paygate = RobokassaGateway.getById(id);

		return paygate.toJson(mapper);
	}
	
	@PostMapping(value = "/paygates")
	public ObjectNode create(@RequestBody RobokassaGateway paygate) {

		paygate.save();

		return get(paygate.getId());
	}
	
	@PutMapping(value = "/paygates/{id}") 
	public ResponseEntity save(@PathVariable long id, @RequestBody JsonNode node) {
		
		RobokassaGateway paygate = RobokassaGateway.getById(id);
	
		if(paygate == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
		
		if(node.has("name") && node.get("name").isTextual()) {
		
			paygate.setName(node.get("name").asText());
		}
		
		if(node.has("merchant_login") && node.get("merchant_login").isTextual()) {
			
			paygate.setMerchantLogin(node.get("merchant_login").asText());
		}
		
		if(node.has("pass1") && node.get("pass1").isTextual()) {
			
			paygate.setPass1(node.get("pass1").asText());
		}
		
		if(node.has("pass2") && node.get("pass2").isTextual()) {
			
			paygate.setPass2(node.get("pass2").asText());
		}
		
		if(node.has("test_mode") && node.get("test_mode").isBoolean()) {
			
			paygate.setTestMode(node.get("test_mode").asBoolean());
		}
		
		paygate.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/paygates/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		RobokassaGateway paygate = RobokassaGateway.getById(id);
		
		if(paygate != null) {
			
			paygate.delete();
		
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}