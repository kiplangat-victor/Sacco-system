package co.ke.emtechhouse.Utils;

import co.ke.emtechhouse.Utils.HttpInterceptor.EntityRequestContext;
import co.ke.emtechhouse.Utils.HttpInterceptor.UserRequestContext;
import co.ke.emtechhouse.Utils.Responses.EntityResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OkhttpService {
    public EntityResponse getInterestCode(@NotNull String code, Double amount) throws IOException {
        EntityResponse resp = new EntityResponse<>();
        String url = URLS.system_config_service+"api/v1/interestcode/params/find/charge/rate/by/code/"+code+"/and/amount/"+amount;
        log.info(url);
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS).readTimeout(300, TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .get().url(url).addHeader("userName", UserRequestContext.getCurrentUser()).addHeader("entityId", EntityRequestContext.getCurrentEntityId()).build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        JSONObject jo = new JSONObject(res);
        String message = jo.getString("message");
        Integer statusCode = jo.getInt("statusCode");
        if (statusCode == 200) {
            JSONObject interest = jo.getJSONObject("entity");
            Interestdetails interestdetails = new Interestdetails();
            interestdetails.setInterestPeriod(interest.getString("interestPeriod"));
            interestdetails.setCalculationMethod(interest.getString("calculationMethod"));
            interestdetails.setRate(interest.getDouble("rate"));
            interestdetails.setPenalInterest(interest.getDouble("penalInterest"));
            resp.setMessage(message);
            resp.setStatusCode(statusCode);
            resp.setEntity(interestdetails);
        }else{
            resp.setMessage(message);
            resp.setStatusCode(statusCode);
            resp.setEntity("");
        }
        return resp;
    }
}
