package emt.sacco.middleware.Utils;

import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.BasicActions.SBasicactions;
import emt.sacco.middleware.SecurityImpl.enumeration.Roles.Workclass.Privileges.SPrivilege;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class InitAuth {
    @Value("${spring.application.basicActions}")
    private String basicActions;

    public List<SPrivilege> getAllPriviledges(){
        String json = "{ \"privileges\":  [\n" +
                "      { name: 'DASHBOARD', selected: true, code: 1 },\n" +
                "      { name: 'CONFIGURATIONS', selected: true, code: 2 },\n" +
                "      { name: 'ATM', selected: true, code: 3 },\n" +
                "      { name: 'POS', selected: true, code: 4 },\n" +
                "      " +
                "      { name: 'ADMIN', selected: true, code: 5 },\n" +
                "      { name: 'AGENCY', selected: true, code: 6 },\n" +
                "      { name: 'ACCOUNTS MANAGEMENT', selected: true, code: 7 },\n" +
                "      { name: 'COLLATERALS MANAGEMENT', selected: true, code: 8 },\n" +
                "      { name: 'TRANSACTION MAINTENANCE', selected: true, code: 9 },\n" +
                "      { name: 'EOD MANAGEMENT', selected: true, code: 10 },\n" +
                "      { name: 'REPORTS', selected: true, code: 11 },\n" +
                "      { name: 'ACCESS MANAGEMENT', selected: true, code: 12 },\n" +
                "      { name: 'ACCOUNTS STATEMENT', selected: false, code: 13 },\n" +
                "      { name: 'APPROVAL MANAGEMENT', selected: false, code: 14 },\n" +
                "    ]}";
        List<SPrivilege> SPrivilegeList = new ArrayList<>();
        JSONObject jo = new JSONObject(json);
        JSONArray ja = jo.getJSONArray("privileges");
        for (Object obj : ja) {
            JSONObject jo2 = new JSONObject(obj.toString());
            SPrivilege SPrivilege = new Gson().fromJson(jo2.toString(), SPrivilege.class);
            SPrivilegeList.add(SPrivilege);
        }
        return SPrivilegeList;
    }
    public List<SBasicactions> getAllBasicActions() {
        List<SBasicactions> SBasicactionsList = new ArrayList<>();
        try {
            File file = ResourceUtils.getFile(basicActions);
            System.out.println("*****************************************************************");
            if (file.exists()){
                System.out.println("PATH FOR BASIC ACTIONS " + file.getName() );
            }
            InputStream in = new FileInputStream(file);
            String strbasicactions = readFileToString(file.getAbsolutePath());
            JSONObject jo = new JSONObject(strbasicactions);
            JSONArray ja = jo.getJSONArray("basicActions");
            for (Object obj : ja) {
                JSONObject jo2 = new JSONObject(obj.toString());
                SBasicactions basicActions = new Gson().fromJson(jo2.toString(), SBasicactions.class);
                SBasicactionsList.add(basicActions);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return SBasicactionsList;
    }

    public String readFileToString(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }


    public List<SBasicactions> getViewBasicActions() {
        List<SBasicactions> allActions = getAllBasicActions();
        List<SBasicactions> viewActions = new ArrayList<>();
        for (SBasicactions SBasicactions : allActions){
            if (isViewAction(SBasicactions.getName())){
                viewActions.add(SBasicactions);
            }
        }
        return viewActions;
    }

    private boolean isViewAction(String name) {
        String[] exceptions = new String[]{"ADD","MODIFY","DELETE","VERIFY","SATISFY","CLOSE","POST","ENTER","DETACH",
                "CASH WITHDRAWAL","CASH DEPOSIT","TRANSFER","PROCESS CHEQUE"};
        for (String e: exceptions) {
            if (name.equalsIgnoreCase(e))
                return false;
        }
        return true;
    }
}
