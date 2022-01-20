package com.kevin.linebotdemo.repository;

import com.kevin.linebotdemo.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author liyanting
 */
@Repository
public interface UserRepository extends JpaRepository<Users, String> {

}
