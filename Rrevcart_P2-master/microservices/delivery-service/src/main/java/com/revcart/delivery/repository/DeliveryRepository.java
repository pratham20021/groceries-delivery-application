package com.revcart.delivery.repository;

import com.revcart.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDeliveryAgentId(Long deliveryAgentId);
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
}
