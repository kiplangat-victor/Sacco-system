package com.emtechhouse.accounts.KraStampedTransactions;

import com.emtechhouse.accounts.KraStampedTransactions.Deviceinfo.DeviceInfo;
import com.emtechhouse.accounts.KraStampedTransactions.Deviceinfo.DeviceInfoRepo;
import com.emtechhouse.accounts.KraStampedTransactions.Tariffs.Tariff;
import com.emtechhouse.accounts.KraStampedTransactions.Tariffs.TariffRepo;
import com.emtechhouse.accounts.KraStampedTransactions.dto.DeviceInfoDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.KraStampedTransactionRequestDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.StampedTransactionDTO;
import com.emtechhouse.accounts.KraStampedTransactions.dto.StampedTransactionResponse;
import com.emtechhouse.accounts.Utils.Responses.EntityResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Lob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@Api(value = "/Excise API", tags = "Excise API")
@RequestMapping("/api/v1/exciseduty")
public class StampedTransactionController {
    private final StampedTransactionService stampedTransactionService;
    private final Kraservice kraservice;
    private final DeviceInfoRepo deviceInfoRepo;
    private final TariffRepo tariffRepo;
    private final StampedTransactionRepo stampedTransactionRepo;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.exciseDuty.kraMiddlewareInitializeDeviceURL}")
    private String KRA_MIDDLEWARE_DEVICE_INIT_URL;
    @Value("${spring.application.exciseDuty.kraMiddlewareGetTariffURL}")
    private String  KRA_MIDDLEWARE_GET_TARIFF_URL;
    @Value("${spring.application.exciseDuty.kraMiddlewareSendTransactionURL}")
    private String  KRA_MIDDLEWARE_SEND_TRANSACTION_URL;
    @Value("${spring.application.exciseDuty.transTyCd}")
    private String  KRA_MIDDLEWARE_TRANS_TYPE_CD;
    @Value("${spring.application.exciseDuty.rcptTyCd}")
    private String  KRA_MIDDLEWARE_RECPT_TYPE_CD;

    public StampedTransactionController(StampedTransactionService stampedTransactionService, Kraservice kraservice, DeviceInfoRepo deviceInfoRepo, TariffRepo tariffRepo, StampedTransactionRepo stampedTransactionRepo, ObjectMapper objectMapper) {
        this.stampedTransactionService = stampedTransactionService;
        this.kraservice = kraservice;
        this.deviceInfoRepo = deviceInfoRepo;
        this.tariffRepo = tariffRepo;
        this.stampedTransactionRepo = stampedTransactionRepo;
        this.objectMapper = objectMapper;
    }
    @PostMapping("/create")
    public EntityResponse createDevice(@RequestBody DeviceInfo deviceInfo) {
        EntityResponse response = new EntityResponse();
        List<DeviceInfo> deviceInfos = deviceInfoRepo.findAll();
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setStatusCode(HttpStatus.OK.value());
        response.setEntity(deviceInfoRepo.save(deviceInfo));
        return  response;
    }
    @PostMapping("Stamp/Transaction")
    public EntityResponse createTransaction(@RequestBody StampedTransaction stampedTransaction) throws JsonProcessingException {
        EntityResponse response = new EntityResponse();
        Optional<Tariff> checkTarrif = tariffRepo.findByTarrifNm("Counter Cash Withdrawal");
        if (checkTarrif.isPresent()) {
            if (checkTarrif.get().getExDtCharge().equalsIgnoreCase("Y")) {
                Double exDutyAmt = stampedTransaction.getTrnAmt() * (checkTarrif.get().getExdutyRt() / 100);
                LocalDateTime localDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
                String formattedDateTime = localDateTime.format(formatter);
                DeviceInfo deviceInfos = deviceInfoRepo.findAll().get(0);
                stampedTransaction.setPin(deviceInfos.getPin());
                stampedTransaction.setBhfId(deviceInfos.getBhfId());
                stampedTransaction.setDvcSrlNo(deviceInfos.getDvcSrlNo());
                stampedTransaction.setDt(Long.valueOf(formattedDateTime.toString()));
                stampedTransaction.setTransTyCd(KRA_MIDDLEWARE_TRANS_TYPE_CD);
                stampedTransaction.setRcptTyCd(KRA_MIDDLEWARE_RECPT_TYPE_CD);
                stampedTransaction.setRefId("0");
                stampedTransaction.setTarrifTyCd(checkTarrif.get().getTarrifCd());
                stampedTransaction.setExRt(checkTarrif.get().getExdutyRt());
                stampedTransaction.setTrnAmt(stampedTransaction.getTrnAmt());
                stampedTransaction.setExDutyAmt(exDutyAmt);
                stampedTransaction.setStatus("Pending");
                stampedTransaction.setPostedBy("System");
                stampedTransaction.setPostedFlag('Y');
                stampedTransaction.setPostedTime(new Date());
                stampedTransactionRepo.save(stampedTransaction);
//                         post to stamp transaction
                KraStampedTransactionRequestDTO transactionRequestDTO = new KraStampedTransactionRequestDTO();
                StampedTransactionDTO stampedTransaction1 = new StampedTransactionDTO();
                stampedTransaction1.setPin(stampedTransaction.getPin());
                stampedTransaction1.setBhfId(stampedTransaction.getBhfId());
                stampedTransaction1.setDvcSrlNo(stampedTransaction.getDvcSrlNo());
                stampedTransaction1.setDt(stampedTransaction.getDt());
                stampedTransaction1.setAccNo(stampedTransaction.getAccNo());
                stampedTransaction1.setTransTyCd(stampedTransaction.getTransTyCd());
                stampedTransaction1.setRcptTyCd(stampedTransaction.getRcptTyCd());
                stampedTransaction1.setInvId(stampedTransaction.getInvId());
                stampedTransaction1.setRefId(stampedTransaction.getRefId());
                stampedTransaction1.setTarrifTyCd(stampedTransaction.getTarrifTyCd());
                stampedTransaction1.setExRt(stampedTransaction.getExRt());
                stampedTransaction1.setTrnAmt(stampedTransaction.getTrnAmt());
                stampedTransaction1.setExDutyAmt(stampedTransaction.getExDutyAmt());
                transactionRequestDTO.setData(stampedTransaction1);
                response = kraservice.sendTransactionToKraMiddleware(transactionRequestDTO);
                JSONObject kraResponse = new JSONObject(response);
                if (kraResponse.getInt("statusCode") == HttpStatus.OK.value()){
                    StampedTransactionResponse dataResp = objectMapper.readValue(kraResponse.get("entity").toString(), StampedTransactionResponse.class);
                    Optional<StampedTransaction> transactionCheck = stampedTransactionRepo.findByInvId(dataResp.getRcptNo());
                    if (transactionCheck.isPresent()){
                        StampedTransaction transaction = transactionCheck.get();
                        transaction.setSignatureGenerated(dataResp.getRcptStamp());
                        transaction.setStatus("Completed");
                        transaction.setCleared(true);
                        stampedTransactionRepo.save(transaction);
                    }
                }
            }
        }
        return response;
    }
    @GetMapping("/all/stamped")
    public EntityResponse findAll() {
        return stampedTransactionService.findAll();
    }
    @GetMapping("/find/{id}")
    public EntityResponse findById(@PathVariable("id") Long id) {
        return stampedTransactionService.findById(id);
    }
    @GetMapping("/all/kra/tariffs")
    public EntityResponse findAllTariffs() {
        EntityResponse response = new EntityResponse();
        List<DeviceInfo> deviceInfos = deviceInfoRepo.findAll();
        DeviceInfoDTO deviceInfoDTO = new DeviceInfoDTO();
        deviceInfoDTO.setPin(deviceInfos.get(0).getPin());
        deviceInfoDTO.setDvcSrlNo(deviceInfos.get(0).getDvcSrlNo());
        deviceInfoDTO.setBhfId(deviceInfos.get(0).getBhfId());
        response = kraservice.getKraTarrifs(deviceInfoDTO);
        return response;
    }
    @PostMapping("/add/selected/tariffs")
    public EntityResponse addSelectedTariffs(@RequestBody  List<Tariff> tariffList){
        EntityResponse response = new EntityResponse();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setEntity(tariffRepo.saveAll(tariffList));
        return response;
    }
    @GetMapping("/all/selected/tariffs")
    public EntityResponse findAllSelectedTariffs(){
        EntityResponse response = new EntityResponse();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage(HttpStatus.OK.getReasonPhrase());
        response.setEntity(tariffRepo.findAll());
        return response;
    }
}
