package com.lng.attendancecompanyservice.repositories.notification;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.notification.Notification;


@Repository
public interface NotificationRepository extends PagingAndSortingRepository<Notification, Integer> {

	
}
