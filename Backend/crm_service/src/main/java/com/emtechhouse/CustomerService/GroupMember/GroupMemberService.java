package com.emtechhouse.CustomerService.GroupMember;

import com.emtechhouse.CustomerService.GroupMember.GroupMeberSignatory.GroupMemberSignatory;
import com.emtechhouse.CustomerService.GroupMember.GroupMeberSignatory.GroupMemberSignatoryRepository;
import com.emtechhouse.CustomerService.GroupMember.GroupMembers.AllGroupMembers;
import com.emtechhouse.Utils.DataNotFoundException;
import com.emtechhouse.Utils.EntityResponse;
import com.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Log
public class GroupMemberService {
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    private final GroupMemberSignatoryRepository groupMemberSignatoryRepository;

    public GroupMemberService(GroupMemberSignatoryRepository groupMemberSignatoryRepository) {
        this.groupMemberSignatoryRepository = groupMemberSignatoryRepository;
    }

    public GroupMember addGroupMember(GroupMember groupMember) {
        try {
            return groupMemberRepository.save(groupMember);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<GroupMember> findAllGroupMembers() {
        try {
            return groupMemberRepository.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public GroupMember findById(Long id) {
        try {
            return groupMemberRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Data " + id + "was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public EntityResponse<?> updateGroupMember(@RequestBody GroupMember groupMember) {
        try {
            EntityResponse response = new EntityResponse();
            String currenUser = UserRequestContext.getCurrentUser();
            String currentEntity = EntityRequestContext.getCurrentEntityId();
            if (currenUser.isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                if (currentEntity.isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                } else {
                    Optional<GroupMember> groupMember1 = groupMemberRepository.findById(groupMember.getId());
                    if (groupMember1.isPresent()) {
                        groupMember.setUniqueId(groupMember.getVerificationDocNumber());
                        groupMember.setUniqueType(groupMember.getVerificationDocument());
                        groupMember.setPostedBy(groupMember1.get().getPostedBy());
                        groupMember.setPostedFlag(groupMember1.get().getPostedFlag());
                        groupMember.setPostedTime(groupMember1.get().getPostedTime());

                        groupMember.setModifiedBy(UserRequestContext.getCurrentUser());
                        groupMember.setEntityId(EntityRequestContext.getCurrentEntityId());
                        groupMember.setModifiedTime(new Date());
                        groupMember.setModifiedFlag('Y');

                        System.out.println(groupMember);

                        GroupMember groupMember2 = groupMemberRepository.save(groupMember);
                        response.setMessage("Group Member with Name: " + groupMember2.getGroupName() + " MODIFIED SUCCESSFULLY " + " At " + groupMember2.getModifiedTime());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(groupMember2);
                    } else {
                        response.setMessage("Group Member Not Found: !! Please Contact Admin/ Manager for New Registration/Approval.");
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
                }
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteGroupMember(Long id) {
        try {
            groupMemberRepository.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }


    public EntityResponse getTotalGroupMembers() {
        try {
            EntityResponse response = new EntityResponse<>();
            Integer totalGroupMembers = groupMemberRepository.countAllGroupMembers();
            if (totalGroupMembers > 0) {
                response.setMessage("Total Unverified Group Members is: " + totalGroupMembers);
                response.setStatusCode(HttpStatus.OK.value());
                response.setEntity(totalGroupMembers);
            } else {
                response.setMessage("Total Unverified Group Members is " + totalGroupMembers);
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
                response.setEntity(totalGroupMembers);
            }
            return response;
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
}


