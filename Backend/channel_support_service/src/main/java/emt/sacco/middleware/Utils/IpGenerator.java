package emt.sacco.middleware.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpGenerator {
    private String getHostIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
