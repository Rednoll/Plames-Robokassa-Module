package com.inwaiders.plames.modules.robokassa.dao;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;

@Service
public class RobokassaGatewayRepositoryInjector {

	@Autowired
	private RobokassaGatewayRepository repository;
	
	@PostConstruct
	private void inject() {
		
		RobokassaGateway.setRepository(repository);
	}
}
