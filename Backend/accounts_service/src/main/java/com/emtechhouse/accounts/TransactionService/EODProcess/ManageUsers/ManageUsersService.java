package com.emtechhouse.accounts.TransactionService.EODProcess.ManageUsers;

import com.emtechhouse.accounts.TransactionService.EODProcess.SystemCalendar.SystemCalendarRepository;
import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses.EODRes;
import com.emtechhouse.accounts.Utils.HttpInterceptor.EntityRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ManageUsersService {
    @Autowired
    private SystemCalendarRepository repository;

    //Disable User Account (One User)
    public EODRes disableUserAccountDuringEOD(String username) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("DISABLE USER ACCOUNT DURING EOD PROCESS!");
        try {
            repository.disableAccountForOneUser(username);
            eodRes.setIssues("NA");
            eodRes.setMessage("USER - "+username+" ACCOUNT DISABLED SUCCESSFULLY!");
            eodRes.setStatus(true);
        } catch (Exception e) {
            log.info("*** Error When Disabling User Account - " + e.getLocalizedMessage() + " ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("DISABLE USER ACCOUNT DURING EOD FAILED - { USER ACCOUNT } - "+username);
            eodRes.setStatus(false);
        }
        return eodRes;
    }

    //Enable User Account (One User)
    public EODRes enableUserAccountInEOD(String username) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("ENABLE USER ACCOUNT DURING EOD PROCESS!");
        try
        {
            repository.enableAccountForOneUser("eod_"+username);
            eodRes.setIssues("NA");
            eodRes.setMessage("USER - "+username+" ACCOUNT ENABLED SUCCESSFULLY!");
            eodRes.setStatus(true);
        } catch (Exception e) {
            log.info("*** Error When Enabling User Account - " + e.getLocalizedMessage() + " ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("ENABLE USER ACCOUNT DURING EOD FAILED - { USER ACCOUNT } - "+username);
            eodRes.setStatus(false);
        }
        return eodRes;
    }


    //Enable All Users During EOD
    public EODRes enableAllUserAccountsDuringEOD(String entityId) {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("ENABLE USER ACCOUNTS DURING EOD PROCESS");
        try
        {
            repository.enableUserAccounts(entityId);
            eodRes.setIssues("NA");
            eodRes.setMessage("ALL USER ACCOUNTS ENABLED SUCCESSFULLY!");
            eodRes.setStatus(true);
        } catch (Exception e) {
            log.info("*** Error When Enabling User Accounts - " + e.getLocalizedMessage() + " ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("ENABLE USER ACCOUNTS DURING EOD FAILED!");
            eodRes.setStatus(false);
        }
        return eodRes;
    }
}
