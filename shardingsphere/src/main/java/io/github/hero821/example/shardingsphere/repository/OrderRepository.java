package io.github.hero821.example.shardingsphere.repository;

import io.github.hero821.example.shardingsphere.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
