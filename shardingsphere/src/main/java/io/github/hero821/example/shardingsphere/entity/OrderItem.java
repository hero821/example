package io.github.hero821.example.shardingsphere.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_order_item")
@Data
public class OrderItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "status")
    private String status;
}
