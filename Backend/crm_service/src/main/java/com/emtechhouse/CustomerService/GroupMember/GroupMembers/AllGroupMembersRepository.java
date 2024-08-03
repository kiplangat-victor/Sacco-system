package com.emtechhouse.CustomerService.GroupMember.GroupMembers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllGroupMembersRepository extends JpaRepository<AllGroupMembers, Long> {
}
