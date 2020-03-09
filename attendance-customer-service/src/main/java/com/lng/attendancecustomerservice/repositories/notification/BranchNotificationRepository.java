package com.lng.attendancecustomerservice.repositories.notification;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecustomerservice.entity.notification.BranchNotification;
@Repository
public interface BranchNotificationRepository extends PagingAndSortingRepository<BranchNotification, Integer> {

}
