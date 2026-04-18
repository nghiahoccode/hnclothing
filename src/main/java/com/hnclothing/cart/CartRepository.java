package com.hnclothing.cart;

import com.hnclothing.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUser(User user);


    Optional<Cart> findBySessionToken(String sessionToken);
}