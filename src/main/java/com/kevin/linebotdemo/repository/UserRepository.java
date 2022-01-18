package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author liyanting
 */
@Repository
public interface UserRepository extends JpaRepository<WebUser, Long> {
}
