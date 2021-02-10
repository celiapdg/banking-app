package com.ironhack.bankapp.repository.users;

import com.ironhack.bankapp.model.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
