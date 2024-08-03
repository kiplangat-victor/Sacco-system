package com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar;

import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import com.emtechhouse.accounts.Utils.HttpInterceptor.UserRequestContext;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SystemCalendarService {
    @Autowired
    private SystemCalendarRepository systemCalendarRepository;

    EntityResponse response = new EntityResponse<>();

    //Add New System Date
    public EntityResponse addNewSystemDate(SystemCalendar sc)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    List<SystemCalendar> checkCalendar = systemCalendarRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),'N');
                    if(!checkCalendar.isEmpty()){
                        response.setMessage("SYSTEM DATE ALREADY ADDED FOR ENTITY - "+EntityRequestContext.getCurrentEntityId()+"!");
                        response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                        response.setEntity("");
                        return response;
                    }else{
                        sc.setPostedBy(UserRequestContext.getCurrentUser());
                        sc.setEntityId(EntityRequestContext.getCurrentEntityId());
                        sc.setPostedFlag('Y');
                        sc.setPostedTime(new Date());
                        systemCalendarRepository.save(sc);
                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(sc);
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING ADDITION OF SYSTEM DATE :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Modify System Date
    public EntityResponse modifySystemDate(SystemCalendar sc)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    List<SystemCalendar> checkCalendar = systemCalendarRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),'N');
                    if(!checkCalendar.isEmpty()){
                        sc.setPostedBy(UserRequestContext.getCurrentUser());
                        sc.setEntityId(EntityRequestContext.getCurrentEntityId());
                        sc.setPostedFlag('Y');
                        sc.setPostedTime(new Date());
                        sc.setModifiedFlag('Y');
                        sc.setVerifiedFlag('N');
                        sc.setModifiedTime(new Date());
                        sc.setModifiedBy(UserRequestContext.getCurrentUser());
                        sc.setEntityId(EntityRequestContext.getCurrentEntityId());
                        systemCalendarRepository.save(sc);
                        response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                        response.setStatusCode(HttpStatus.CREATED.value());
                        response.setEntity(sc);
                        return response;
                    }
                    else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING MODIFICATION OF SYSTEM DATE :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Verify
    public EntityResponse verifySystemDate(Long id)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    Optional<SystemCalendar> checkCalendar = systemCalendarRepository.findById(id);
                    if(checkCalendar.isPresent()){
                        SystemCalendar sc = checkCalendar.get();
                        if (sc.getPostedBy().equalsIgnoreCase(UserRequestContext.getCurrentUser())){
                            response.setMessage("You Can Not Verify What you initiated");
                            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                            response.setEntity("");
                            return response;
                        }else {
                            sc.setVerifiedFlag('Y');
                            sc.setVerifiedTime(new Date());
                            sc.setVerifiedBy(UserRequestContext.getCurrentUser());
                            systemCalendarRepository.save(sc);
                            response.setMessage(HttpStatus.CREATED.getReasonPhrase());
                            response.setStatusCode(HttpStatus.CREATED.value());
                            response.setEntity(sc);
                            return response;
                        }
                    }
                    else {
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING VERIFICATION OF SYSTEM DATE :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Delete System Date
    public EntityResponse deleteSystemDate(Long id)
    {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    Optional<SystemCalendar> systemCalendar = systemCalendarRepository.findById(id);
                    if (systemCalendar.isPresent()) {
                        SystemCalendar sc = systemCalendar.get();
                        sc.setDeletedFlag('Y');
                        sc.setDeletedTime(new Date());
                        sc.setDeletedBy(UserRequestContext.getCurrentUser());
                        systemCalendarRepository.save(sc);
                        response.setMessage(HttpStatus.OK.getReasonPhrase());
                        response.setStatusCode(HttpStatus.OK.value());
                        response.setEntity(sc);
                        return response;
                    } else {
                        EntityResponse response = new EntityResponse();
                        response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                        response.setStatusCode(HttpStatus.NOT_FOUND.value());
                        response.setEntity("");
                        return response;
                    }
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING DELETION OF SYSTEM DATE :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Select for one entity id
    public EntityResponse getSystemDateByEntityId() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    List<SystemCalendar> sysCalendar = systemCalendarRepository.findByEntityIdAndDeletedFlag(EntityRequestContext.getCurrentEntityId(),'N');
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(sysCalendar);
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING FETCHING OF SYSTEM DATES :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Select by Id
    public EntityResponse getSystemDateById(Long id) {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    List<SystemCalendar> sysCalendar = systemCalendarRepository.findByIdAndDeletedFlag(id,'N');
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(sysCalendar);
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING FETCHING OF SYSTEM DATES :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }

    //Select All System Dates
    public EntityResponse getAllSystemDates() {
        try {
            if (UserRequestContext.getCurrentUser().isEmpty()) {
                response.setMessage("User Name not present in the Request Header");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                response.setEntity("");
                return response;
            } else {
                if (EntityRequestContext.getCurrentEntityId().isEmpty()) {
                    response.setMessage("Entity not present in the Request Header");
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
                    response.setEntity("");
                    return response;
                } else {
                    List<SystemCalendar> sysCalendar = systemCalendarRepository.findByDeletedFlag('N');
                    response.setMessage(HttpStatus.OK.getReasonPhrase());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(sysCalendar);
                    return response;
                }
            }
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            response.setMessage("ERROR DURING FETCHING OF SYSTEM DATES :: "+e.getLocalizedMessage());
            response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            response.setEntity("");
            return response;
        }
    }
}
