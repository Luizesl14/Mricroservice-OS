package com.systemorderservice.domain.objectValue.valueExtends;

import com.systemorderservice.aplicatiton.dto.OrderServiceDto;
import org.springframework.http.ResponseEntity;


public interface IController<T> {
    ResponseEntity<T> findAll(Integer page, Integer pageSize);
    ResponseEntity<T> findById(Integer id);
    ResponseEntity<T> saveObject(T t);
    ResponseEntity<OrderServiceDto> updateObject(OrderServiceDto t);
    ResponseEntity<T> deleteObject(Integer id);
}
