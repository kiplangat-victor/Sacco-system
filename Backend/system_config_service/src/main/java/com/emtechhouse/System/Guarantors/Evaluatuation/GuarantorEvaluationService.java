package com.emtechhouse.System.Guarantors.Evaluatuation;

import com.emtechhouse.System.Guarantors.GuarantorParametersRepo;
import com.emtechhouse.System.Utils.URLS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GuarantorEvaluationService {
    @Autowired
    private GuarantorParametersRepo guarantorParametersRepo;


    //Check Guarantor Activeness Status --- Connect to accounts service (current account)
//    public void checkActiveness() throws IOException {
//        String requestJson = "";
//        String url = URLS.;
//        OkHttpClient client = null;
//        client = new OkHttpClient.Builder()
//                .connectTimeout(90000, TimeUnit.MILLISECONDS)
//                .readTimeout(90000, TimeUnit.MILLISECONDS)
//                .build();
//        MediaType mediaType = MediaType.parse("application/json");
//        RequestBody body = RequestBody.create(requestJson, mediaType);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .addHeader("content-type", "application/json")
//                .build();
//        Response response = client.newCall(request).execute();
//    }

    //Check Guarantor Loan Status --- Connect to accounts service (loan accounts)

    //Check Subsequent Guarantees status --- query from guarantors table

    //Check Number of loans guaranteed --- query from guarantors table
}
