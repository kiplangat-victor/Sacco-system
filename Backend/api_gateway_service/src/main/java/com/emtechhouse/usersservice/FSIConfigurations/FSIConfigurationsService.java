package com.emtechhouse.usersservice.FSIConfigurations;

import com.emtechhouse.usersservice.utils.EntityResponse;
import com.emtechhouse.usersservice.utils.HttpInterceptor.UserRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FSIConfigurationsService {
    private final FSIConfigurationsRepository fsiConfigurationsRepository;

    public FSIConfigurationsService(FSIConfigurationsRepository fsiConfigurationsRepository) {
        this.fsiConfigurationsRepository = fsiConfigurationsRepository;
    }

    public EntityResponse addHeader(@RequestBody FSIConfigurations fsiConfigurations) {
        try {
            String user = UserRequestContext.getCurrentUser();
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> checkEntityId = fsiConfigurationsRepository.findByEntityId(fsiConfigurations.getEntityId());
            if (checkEntityId.isPresent()) {
                response.setMessage("FSI ENTITY ID HEADER ALREADY CONFIGURED");
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                fsiConfigurations.setStatus("ACTIVATED");
                fsiConfigurations.setPostedBy(user);
                fsiConfigurations.setPostedFlag('Y');
                fsiConfigurations.setPostedTime(new Date());
                FSIConfigurations addHeaders = fsiConfigurationsRepository.save(fsiConfigurations);
                response.setMessage("FSI CONFIGURATIONS HEADERS ADDED SUCCESSFULLY AT " + new Date());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(addHeaders);
            }

            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse getHeaders() {
        try {
            EntityResponse response = new EntityResponse();
            List<FSIConfigurations> fsiConfigurationsList = fsiConfigurationsRepository.findAll();
            System.out.println(fsiConfigurationsList);
            if (fsiConfigurationsList.size() > 0) {
                response.setMessage("TOTAL FSI HEADER CONFIGURATIONS SO FAR IS " + fsiConfigurationsList.size());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(fsiConfigurationsList);
            } else {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse getHeaderById(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> checkId = fsiConfigurationsRepository.findById(id);
            if (checkId.isPresent()) {
                response.setMessage("FSI HEADER CONFIGURATIONS FOUND");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(checkId);
            } else {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse getHeaderByEntityId(String entityId) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> optionalFSIConfigurations = fsiConfigurationsRepository.findByEntityId(entityId);
            if (optionalFSIConfigurations.isPresent()) {
                response.setMessage("FSI HEADER CONFIGURATIONS FOUND");
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(optionalFSIConfigurations);
            } else {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse updateHeader(FSIConfigurations fsiConfigurations) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> optionalFSIConfigurations = fsiConfigurationsRepository.findById(fsiConfigurations.getId());
            if (!optionalFSIConfigurations.isPresent()) {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            } else {
                FSIConfigurations updateHeaders = fsiConfigurationsRepository.save(fsiConfigurations);
                response.setMessage("FSI CONFIGURATIONS HEADERS UPDATED SUCCESSFULLY AT " + new Date());
                response.setStatusCode(HttpStatus.CREATED.value());
                response.setEntity(updateHeaders);
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse deleteHeaderById(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> optionalFSIConfigurations = fsiConfigurationsRepository.findById(id);
            if (optionalFSIConfigurations.isPresent()) {
                fsiConfigurationsRepository.deleteById(optionalFSIConfigurations.get().getId());
                response.setMessage("FSI HEADER CONFIGURATIONS DELETED SUCCESSFULLY AT " + new Date());
                response.setStatusCode(HttpStatus.OK.value());
            } else {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse attachHeaders() {
        try {
            EntityResponse response = new EntityResponse();
            Optional<FSIConfigurations> headers = fsiConfigurationsRepository.getConfiguredHeaders();
            if (headers.isPresent()) {
                response.setMessage(HttpStatus.FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.FOUND.value());
                response.setEntity(headers);
            } else {
                response.setMessage(HttpStatus.NOT_FOUND.getReasonPhrase());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }

    public EntityResponse verify(Long id) {
        try {
            EntityResponse response = new EntityResponse();
            String user = UserRequestContext.getCurrentUser();
            Optional<FSIConfigurations> checkId = fsiConfigurationsRepository.findById(id);
            if (checkId.isPresent()) {
                FSIConfigurations fsiConfigurations = checkId.get();
//                if (fsiConfigurations.getVerifiedFlag().equals('Y')){
//                    response.setMessage("FSI ENTITY ID HEADER ALREADY CONFIGURED");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                } else if (fsiConfigurations.getVerifiedBy().equals(user)) {
//                    response.setMessage("YOU CAN NOT VERIFY THIS PROCESS SINCE YOU INITIATED");
//                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
//                } else {
                    fsiConfigurations.setVerifiedBy(user);
                    fsiConfigurations.setVerifiedTime(new Date());
                    fsiConfigurations.setVerifiedFlag('Y');
                    FSIConfigurations verify = fsiConfigurationsRepository.save(fsiConfigurations);
                    response.setMessage("FSI HEADER CONFIGURATIONS VERIFY SUCCESSFULLY AT " + verify.getVerifiedTime());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setEntity(verify);
//                }
            } else {
                response.setMessage("NO FSI HEADER CONFIGURATIONS FOUND AS AT " + new Date());
                response.setStatusCode(HttpStatus.NOT_FOUND.value());
            }
            return response;
        } catch (Exception e) {
            log.info("Caught Error " + "" + e);
            return null;
        }
    }
}
