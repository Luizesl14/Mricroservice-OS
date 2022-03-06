package com.systemorderservice.insfrastructure.repository;

import com.systemorderservice.domain.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IStatusOrderServiceRepository extends JpaRepository<OrderStatus, Integer> {
}
