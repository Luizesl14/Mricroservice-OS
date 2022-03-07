package com.systemorderproducer.aplicatiton.core.service;




import com.google.gson.Gson;
import com.systemorderproducer.aplicatiton.dto.OrderProducerDto;
import com.systemorderproducer.domain.model.OrderProducer;
import com.systemorderproducer.domain.objectValue.IOrderProducerService;
import com.systemorderproducer.domain.shared.GenericEntity_;
import com.systemorderproducer.domain.shared.GenericObjectMapper;
import com.systemorderproducer.insfrastructure.http.OrderProducerException;
import com.systemorderproducer.insfrastructure.repositories.IOrderProducerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderProducerService implements IOrderProducerService {

    @Autowired
    private  GenericObjectMapper mapper;

    @Autowired
    private IOrderProducerRepository IOrderProducerRepository;


    public Page<OrderProducerDto> bringAll(Integer page, Integer pageSize){
        return this.mapper.mapEntityPageIntoDtoPage(
                this.IOrderProducerRepository.findAll(
                        PageRequest.of(page,pageSize, Sort.by("id"))), OrderProducerDto.class);
    }

    public OrderProducerDto bringByid(Integer id){
        OrderProducer orderProducer = this.IOrderProducerRepository.findById(id)
                .orElseThrow(()-> new OrderProducerException("ORDER PRODUCER - NÃO ENCONTRADA" , HttpStatus.NOT_FOUND));
        return  this.mapper.mapTo(orderProducer, OrderProducerDto.class);
    }


    public OrderProducerDto saveOrder(OrderProducerDto orderProducerDto){
        this.IOrderProducerRepository.save(this.creatObject(orderProducerDto));
        return orderProducerDto;
    }

    @JmsListener(destination = "topc.mailbox")
    public boolean saveObject(String order){
        Object obj = new Gson().fromJson(order, Object.class);
        OrderProducer orderProducer = this.creatObject(obj);
        this.IOrderProducerRepository.save(orderProducer);
        return true;
    }

    public OrderProducerDto updateObject(OrderProducerDto orderProducerDto){
       OrderProducer newOrderProducer = this.mapper.mapTo(orderProducerDto, OrderProducer.class);
       OrderProducer serarchOrderProducer =
               this.mapper.mapTo(this.bringByid(orderProducerDto.getId()), OrderProducer.class);

        BeanUtils.copyProperties(newOrderProducer, serarchOrderProducer,
                GenericEntity_.ID,GenericEntity_.IDENTIFY, GenericEntity_.CREATED_AT,GenericEntity_.DELIVERY_DATE);
        return this.mapper.mapTo(
                this.IOrderProducerRepository.save(newOrderProducer), OrderProducerDto.class);

    }

    public void deleteObject(Integer id){
            this.IOrderProducerRepository.deleteById(id);
            throw new OrderProducerException("Deletado com sucesso!", HttpStatus.ACCEPTED);
    }

    public OrderProducer creatObject(Object obj) {
        OrderProducer orderProducer = this.mapper.mapTo(obj, OrderProducer.class);
        orderProducer.setCreatedAt(LocalDateTime.now());
        orderProducer.setIdentify(UUID.randomUUID().toString());
        orderProducer.setLimtDeliveryDate(15);
        orderProducer.setDeliveryDate(orderProducer.getCreatedAt().plusDays(orderProducer.getLimtDeliveryDate()));
        return orderProducer;
    }


}
