package com.emtechhouse.System.Guarantors;

import com.emtechhouse.System.Utils.Responses.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuarantorParametersService {
    @Autowired
    private GuarantorParametersRepo guarantorParametersRepo;

    //Save
    public ResponseEntity<?> addNewParameters(GuarantorsParameters parameters)
    {
        try {
            if (guarantorParametersRepo.findByEntityId(parameters.getEntityId()).isEmpty()) {

                guarantorParametersRepo.save(parameters);
                return new ResponseEntity<>(new MessageResponse("Guarantor Parameters Added Successfully!"), HttpStatus.OK);
            } else {
                System.out.println("Already exists");
                return new ResponseEntity<>(new MessageResponse("Guarantor Parameters For this Entity Already Exist!"), HttpStatus.EXPECTATION_FAILED);
            }
        }
        catch (Exception e)
        {
            log.info("ADD GUARANTOR PARAMETERS :: Error Occured ! - "+e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("Error Occured ! - "+e.getLocalizedMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    //Update
    public ResponseEntity<?> updateParameters(GuarantorsParameters parameters)
    {
        try
        {
            guarantorParametersRepo.save(parameters);
            return new ResponseEntity<>(new MessageResponse("Guarantor Parameters Update Successfully!"), HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.info("UPDATE GUARANTOR PARAMETERS :: Error Occured ! - "+e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("Error Occured ! - "+e.getLocalizedMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    //Find By ID
    public ResponseEntity<?> findParametersById(Long id)
    {
        try
        {
            return new ResponseEntity<>(guarantorParametersRepo.findById(id),HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.info("FETCH BY ID GUARANTOR PARAMETERS :: Error Occured ! - "+e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("Error Occured ! - "+e.getLocalizedMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    //Find By Entity ID
    public ResponseEntity<?> findParametersByEntityId(String entityId)
    {
        try
        {
            return new ResponseEntity<>(guarantorParametersRepo.findByEntityId(entityId),HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.info("FETCH BY ENTITY ID GUARANTOR PARAMETERS :: Error Occured ! - "+e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("Error Occured ! - "+e.getLocalizedMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }


    //Delete By ID
    public ResponseEntity<?> deleteParametersById(Long id)
    {
        try
        {
            guarantorParametersRepo.deleteById(id);
            return new ResponseEntity<>(new MessageResponse("Guarantor Parameters Deleted Successfully!"),HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.info("FETCH BY ID GUARANTOR PARAMETERS :: Error Occured ! - "+e.getLocalizedMessage());
            return new ResponseEntity<>(new MessageResponse("Error Occured ! - "+e.getLocalizedMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
