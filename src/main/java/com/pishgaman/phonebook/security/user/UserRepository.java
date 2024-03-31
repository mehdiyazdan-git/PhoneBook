package com.pishgaman.phonebook.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END FROM User u")
  boolean isTableEmpty();


}
