package com.emtechhouse.accounts.TransactionService.EODProcess.DataBackUp;

import com.emtechhouse.accounts.TransactionService.EODProcess.performEOD.EODResponses.EODRes;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeader.TransactionHeader;
import com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory.TranHeaderHistory;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class DataBackUpService {

    Gson gson = new Gson();

    @Value("${data.backup.folder}")
    private String backUpsFolder;

    //TimeStamp
    String timeStamp = new SimpleDateFormat("YYYYMMddHHmmss").format(Calendar.getInstance().getTime());

    //    TODO: PRE END OF DAY BACKUP DATABASE
    public EODRes preEODBackup(String dbUsername, String dbPassword, String dbName, String dbOutputfile)
    {
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("PERFORM PRE-EOD DATABASE BACKUP");

        dbOutputfile = "PRE_EOD_"+timeStamp+".sql";
        log.info("*** Begin Pre EOD Backup Process - "+new Date()+ " ***");
        try {
            String command = String.format("mysqldump -u%s -p%s --add-drop-table --databases %s -r %s",
                    dbUsername, dbPassword, dbName, backUpsFolder+dbOutputfile);
            Process process = Runtime.getRuntime().exec(command);
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                log.info("*** Pre EOD Backup Process Completed Successfully! " + new Date() + " File Name - "+dbOutputfile+" ***");
                eodRes.setIssues("NA");
                eodRes.setMessage("PRE - EOD DATABASE BACKUP COMPLETED SUCCESSFULLY! FILE NAME - "+dbOutputfile);
                eodRes.setStatus(true);
            } else {
                log.info("*** Pre EOD Backup Process Failed! " + new Date() + " ***");
                eodRes.setIssues("NA");
                eodRes.setMessage("PRE - EOD DATABASE BACKUP PROCESS FAILED!");
                eodRes.setStatus(false);
            }
            return eodRes;
        }
        catch (Exception e) {
            log.error("*** Pre EOD Backup Process Failed! " + new Date() + " Error - " + e.getMessage() + " ***", e);
            eodRes.setIssues(e.getMessage().toUpperCase());
            eodRes.setMessage("PRE - EOD DATABASE BACKUP PROCESS FAILED!");
            eodRes.setStatus(false);
            return eodRes;
        }

    }

    //    TODO: POST END OF DAY BACKUP DATABASE
    public EODRes postEODBackup(String dbUsername,String dbPassword,String dbName,String dbOutputfile)
    {
        log.info("*** Begin Post EOD Backup Process - "+new Date()+ " ***");
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("PERFORM POST-EOD DATABASE BACKUP");
        dbOutputfile = "POST_EOD_"+timeStamp+".sql";
        try {
            String command = String.format("mysqldump -u%s -p%s --add-drop-table --databases %s -r %s",
                    dbUsername, dbPassword, dbName, backUpsFolder+dbOutputfile);
            Process process = Runtime.getRuntime().exec(command);
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                log.info("*** Post EOD Backup Process Completed Successfully! " + new Date() + " File Name - "+dbOutputfile+" ***");
                eodRes.setIssues("NA");
                eodRes.setMessage("POST - EOD DATABASE BACKUP COMPLETED SUCCESSFULLY! FILE NAME - "+dbOutputfile);
                eodRes.setStatus(true);
            } else {
                log.info("*** Post EOD Backup Process Failed! " + new Date() + " ***");
                eodRes.setIssues("NA");
                eodRes.setMessage("POST - EOD DATABASE BACKUP PROCESS FAILED!");
                eodRes.setStatus(false);
            }
            return  eodRes;
        }
        catch (Exception e)
        {
            log.info("*** Post EOD Backup Process Failed! " + new Date() + " Error - "+e.getLocalizedMessage()+" ***");
            eodRes.setIssues(e.getMessage().toUpperCase());
            eodRes.setMessage("POST - EOD DATABASE BACKUP PROCESS FAILED!");
            eodRes.setStatus(false);
            return eodRes;
        }
    }

    //BACK UP HTD Data to a log file
    public EODRes writeHTDDataToFile(List<TranHeaderHistory> data)
    {
        log.info("*** Begin HTD Data back up to a log file - "+new Date()+ " ***");
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("HTD DATA BACKUP TO A FILE");
        String fileName = "HTD_"+timeStamp+".json";
            File file = new File(backUpsFolder+fileName);
            FileWriter fr = null;
            try {
                fr = new FileWriter(file);
                for (TranHeaderHistory th :data) {
                    fr.write(gson.toJson(th));
                }
                fr.close();
                log.info("*** HTD Data back up to a file Completed Successfully! " + new Date() + " File Location - "+backUpsFolder+fileName+" ***");
                eodRes.setIssues("NA");
                eodRes.setMessage("HTD DATA BACK UP TO FILE COMPLETED SUCCESSFULLY! FILE LOCATION - "+ backUpsFolder+fileName);
                eodRes.setStatus(true);

                return eodRes;
            } catch (IOException e) {
                log.info("*** HTD Data back up to a log file Failed! " + new Date() + " Error - "+e.getLocalizedMessage()+" ***");
                eodRes.setIssues(e.getMessage().toUpperCase());
                eodRes.setMessage("HTD DATA BACK UP TO FILE FAILED!");
                eodRes.setStatus(false);
                return eodRes;
            }
    }


    //BACK UP DTD DATA TO LOG FILE
    public EODRes writeDTDDataToFile(List<TransactionHeader> data)
    {
        log.info("*** Begin DTD Data back up to a log file - "+new Date()+ " ***");
        EODRes eodRes = new EODRes();
        eodRes.setEodStep("DTD DATA BACKUP TO A FILE");
        String fileName = "DTD_"+timeStamp+".json";
        File file = new File(backUpsFolder+fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            for (TransactionHeader th :data) {
                fr.write(gson.toJson(th));
            }
            fr.close();
            log.info("*** DTD Data back up to a file Completed Successfully! " + new Date() + " File Location - "+backUpsFolder+fileName+" ***");
            eodRes.setIssues("NA");
            eodRes.setMessage("DTD DATA BACK UP TO FILE COMPLETED SUCCESSFULLY! FILE LOCATION - "+ backUpsFolder+fileName);
            eodRes.setStatus(true);

            return eodRes;
        } catch (IOException e) {
            log.info("*** DTD Data back up to a log file Failed! " + new Date() + " Error - "+e.getLocalizedMessage()+" ***");
            eodRes.setIssues(e.getMessage().toUpperCase());
            eodRes.setMessage("DTD DATA BACK UP TO FILE FAILED!");
            eodRes.setStatus(false);
            return eodRes;
        }
    }

    //    TODO: RESTORE DATABASE
    public static boolean restore(String dbUsername, String dbPassword, String dbName, String sourceFile)
            throws IOException, InterruptedException {
        String[] command = new String[]{
                "mysql",
                "-u" + dbUsername,
                "-p" + dbPassword,
                "-e",
                " source " + sourceFile,
                dbName
        };
        Process runtimeProcess = Runtime.getRuntime().exec(command);
        int processComplete = runtimeProcess.waitFor();
        return processComplete == 0;
    }
}
