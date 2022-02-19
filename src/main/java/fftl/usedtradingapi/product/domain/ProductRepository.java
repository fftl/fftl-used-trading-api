package fftl.usedtradingapi.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByState(String state);
    List<Product> findByCity(String city);
    List<Product> findByTown(String town);
}
