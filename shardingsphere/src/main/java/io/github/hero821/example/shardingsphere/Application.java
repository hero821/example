package io.github.hero821.example.shardingsphere;

import io.github.hero821.example.shardingsphere.entity.Address;
import io.github.hero821.example.shardingsphere.entity.Order;
import io.github.hero821.example.shardingsphere.repository.AddressRepository;
import io.github.hero821.example.shardingsphere.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        OrderRepository orderRepository = context.getBean(OrderRepository.class);
        orderRepository.deleteAll();
        for (int i = 0; i < 10; i++) {
            orderRepository.save(Order.builder().addressId((long) i).userId((long) (Math.random() * 10)).build());
        }
        orderRepository.findAll();
        //根据ID查询只查一个表，因为可以算出来需要查哪个表
        orderRepository.findById(0L);
        orderRepository.findAllById(Arrays.asList(0L, 1L));
        //
        ExampleMatcher matcher = ExampleMatcher.matching();
        orderRepository.findAll(Example.of(Order.builder().addressId(0L).build(), matcher), PageRequest.of(10000, 10, new Sort(Sort.Direction.DESC, "id")));

        AddressRepository addressRepository = context.getBean(AddressRepository.class);
        addressRepository.deleteAll();
        for (int i = 0; i < 2; i++) {
            addressRepository.save(Address.builder().id((long) i).name(String.valueOf(i)).build());
        }
    }

}
