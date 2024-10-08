package com.pishgaman.phonebook.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

  @Query("select u from User u where u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  @Query("select u from User u where u.username = :username")
  Optional<User> findByUsername(@Param("username") String username);

  @Query("select u from User u where u.username = :username and u.id <> :id")
  User findUserByUsernameAndIdNot(@Param("username") String username, @Param("id") Integer id);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN false ELSE true END FROM User u")
  boolean isTableEmpty();


}
