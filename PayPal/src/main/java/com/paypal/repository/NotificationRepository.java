package com.paypal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypal.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>{

}
