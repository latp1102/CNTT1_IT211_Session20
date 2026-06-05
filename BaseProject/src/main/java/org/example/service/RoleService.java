package org.example.service;

import org.example.model.entity.Role;
import org.example.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }

    public void initializeRoles(){

        if(roleRepository.count() == 0){
            List<Role> roles = new ArrayList<>();
            Role roleAdmin = new Role();
            roleAdmin.setRoleName("ADMIN");

            Role roleUser = new Role();
            roleUser.setRoleName("USER");
            roles.add(roleAdmin);
            roles.add(roleUser);
            roleRepository.saveAll(roles);
        }
    }

}
