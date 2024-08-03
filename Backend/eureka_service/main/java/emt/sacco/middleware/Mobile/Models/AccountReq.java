package emt.sacco.middleware.Mobile.Models;

import lombok.Data;

@Data
public class AccountReq {
        private String username;
        private String email;
        private String password;
        private String AccountType;
}