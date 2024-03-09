package com.management.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<List<User>> findUsersByFirstnameContainingIgnoreCase(String firstname);

    Optional<List<User>> findUsersByLastnameContainingIgnoreCase(String firstname);

    Optional<List<User>> findUsersByIdIn(List<Integer> userIds);

    Optional<List<User>> findUsersByRole(Role role);
}
