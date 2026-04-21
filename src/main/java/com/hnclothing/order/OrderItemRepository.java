package com.hnclothing.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    @Query("SELECT new com.hnclothing.order.OrderService$ProductSaleDTO(p.name, s.sizeName, CAST(pv.quantity AS long), SUM(oi.quantity)) " +
            "FROM OrderItem oi JOIN oi.productVariant pv JOIN pv.product p JOIN pv.size s JOIN oi.order o " +
            "WHERE o.status = com.hnclothing.order.OrderStatus.DELIVERED " +
            "GROUP BY p.name, s.sizeName, pv.quantity " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<com.hnclothing.order.OrderService.ProductSaleDTO> getProductSales();

}