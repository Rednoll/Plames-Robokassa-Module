package com.inwaiders.plames.modules.robokassa.dao;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.robokassa.domain.RobokassaGateway;

@Repository
public interface RobokassaGatewayRepository extends JpaRepository<RobokassaGateway, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT paygate FROM RobokassaGateway paygate WHERE paygate.id = :id AND paygate.deleted != true")
	public RobokassaGateway getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT paygate FROM RobokassaGateway paygate WHERE paygate.name = :name AND paygate.deleted != true")
	public RobokassaGateway getByName(@Param(value = "name") String name);

	@Query("SELECT paygate FROM RobokassaGateway paygate WHERE paygate.deleted != true ORDER BY paygate.name")
	public List<RobokassaGateway> getOrderedByName();
	
	@Override
	@Query("SELECT paygate FROM RobokassaGateway paygate WHERE paygate.deleted != true")
	public List<RobokassaGateway> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM RobokassaGateway paygate WHERE paygate.deleted != true")
	public long count();
}