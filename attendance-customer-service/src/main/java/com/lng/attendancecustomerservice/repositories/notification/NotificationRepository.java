package com.lng.attendancecustomerservice.repositories.notification;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.notification.Notification;

@Repository
public interface NotificationRepository extends PagingAndSortingRepository<Notification, Integer> {

	
}
