package fftl.usedtradingapi.product.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAddressState(String state);
    List<Product> findByAddressCity(String city);
    List<Product> findByAddressTown(String town);
}
