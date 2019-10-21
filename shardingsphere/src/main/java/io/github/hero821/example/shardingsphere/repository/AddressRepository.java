package io.github.hero821.example.shardingsphere.repository;

import io.github.hero821.example.shardingsphere.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
