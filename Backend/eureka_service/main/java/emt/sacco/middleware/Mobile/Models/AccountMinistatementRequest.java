package emt.sacco.middleware.Mobile.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data

public class AccountMinistatementRequest {

//    @JsonProperty("GetAccountMinistatement")
//    public emt.sacco.middleware.AccountResource.AccountTransactions.model.AccountMinistatement.AccountMinistatements AccountMinistatements;
    @Data
    public static class AccountMinistatements{
//        public emt.sacco.middleware.AccountResource.AccountTransactions.model.AccountMinistatement.AccountMinistatements.Request request;

        @Data
        public static class Request {
//            @JsonProperty("FSIIdentityId")
//            public emt.sacco.middleware.AccountResource.AccountTransactions.model.AccountMinistatement.AccountMinistatements.Request.FSIIdentityId fSIIdentityId;
//            @JsonProperty("TransactionTypeName")
//            public String transactionTypeName;
//            @JsonProperty("FSILinkType")
//            public String fSILinkType;
//            @JsonProperty("Acid")
//            public String acid;
//            @JsonProperty("ToDate")
//            public String toDate;
//            @JsonProperty("FromDate")
//            public String fromDate;
//            @JsonProperty("maxCount")
//            public String maxCount;
            @JsonProperty("EntityId")
            public String entityId;

//            @JsonProperty("TransactionReceiptNumber")
//            public String transactionReceiptNumber;
//            @JsonProperty("FIAccountNumber")
//            public String fIAccountNumber;
//            @JsonProperty("BankShortCode")
//            public String bankShortCode;
//            @JsonProperty("MessageId")
//            public emt.sacco.middleware.AccountResource.AccountTransactions.model.AccountMinistatement.AccountMinistatements.Request.MessageId messageId;
            @JsonProperty("TransactionId")
            public String transactionId;

            @Data
            public static class MessageId {
                @JsonProperty("Id")
                private String id;
                @JsonProperty("TimeStamp")
                private String timeStamp;
            }
//            @Data
//            public static class FSIIdentityId{
//                @JsonProperty("ShortCode")
//                public String shortCode;
//                @JsonProperty("MSISDN")
//                public String mSISDN;
//                @JsonProperty("VmtReferenceNumber")
//                public String vmtReferenceNumber;
//                @JsonProperty("GroupID")
//                public String groupID;
//            }
        }
    }
}




//
//
//
//package emt.sacco.middleware.AccountResource.AccountTransactions.model;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//
//@Data
//public class AccountMinistatement {
//    @JsonProperty("GetAccountMinistatement")
//    public AccountMinistatement getAccountMinistatement;
//    public Object request;
//
//    @Data
//    public static class AccountMinistatement{
//        public Request request;
//
//        @Data
//        public static class MessageId{
//            @JsonProperty("Id")
//            public String id;
//            @JsonProperty("TimeStamp")
//            public String timeStamp;
//        }
//
//        @Data
//        public static class Request{
//            @JsonProperty("FSIIdentityId")
//            public FSIIdentityId fSIIdentityId;
//            @JsonProperty("TransactionTypeName")
//            public String transactionTypeName;
//            @JsonProperty("FSILinkType")
//            public String fSILinkType;
//
//
//
//            @JsonProperty("TransactionReceiptNumber")
//            public String transactionReceiptNumber;
//            @JsonProperty("FIAccountNumber")
//            public String fIAccountNumber;
//            @JsonProperty("BankShortCode")
//            public String bankShortCode;
//            @JsonProperty("MaximumNumberOfTransactions")
//            public String maximumNumberOfTransactions;
//            @JsonProperty("MessageId")
//            public MessageId messageId;
//            @JsonProperty("TransactionId")
//            public String transactionId;
//        }
//
//        @Data
//        public static class FSIIdentityId{
//            @JsonProperty("ShortCode")
//            public String shortCode;
//            @JsonProperty("MSISDN")
//            public String mSISDN;
//            @JsonProperty("VmtReferenceNumber")
//            public String vmtReferenceNumber;
//            @JsonProperty("GroupID")
//            public String groupID;
//        }
//    }
//}

