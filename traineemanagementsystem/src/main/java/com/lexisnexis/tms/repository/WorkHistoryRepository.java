package com.lexisnexis.tms.repository;

import com.lexisnexis.tms.entity.User;
import com.lexisnexis.tms.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkHistoryRepository extends JpaRepository<WorkHistory, String>
{
}
