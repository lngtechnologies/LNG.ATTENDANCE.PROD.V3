package com.lng.attendancecompanyservice.repositories.custOnboarding;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.lng.attendancecompanyservice.entity.custOnboarding.MailBounce;

@Repository
public interface MailBounceRepository extends PagingAndSortingRepository<MailBounce, Integer> {

}
