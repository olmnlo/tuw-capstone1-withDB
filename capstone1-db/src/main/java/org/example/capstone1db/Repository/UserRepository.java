package org.example.capstone1db.Repository;

import org.example.capstone1db.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
