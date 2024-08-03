package emt.sacco.middleware.SecurityImpl.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Forgotpassword {
    private String emailAddress;
}
