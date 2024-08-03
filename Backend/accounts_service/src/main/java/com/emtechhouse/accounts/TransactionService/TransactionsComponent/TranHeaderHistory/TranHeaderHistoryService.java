package com.emtechhouse.accounts.TransactionService.TransactionsComponent.TranHeaderHistory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TranHeaderHistoryService {
    @Autowired
    private TranHeaderHistoryRepository tranHeaderHistoryRepository;

    public TranHeaderHistory saveTranHeader(TranHeaderHistory tranHeader) {
        return tranHeaderHistoryRepository.save(tranHeader);
    }

    public TranHeaderHistory updateTranHeader(TranHeaderHistory tranHeader) {
        return tranHeaderHistoryRepository.save(tranHeader);
    }

    public TranHeaderHistory retrieveTranHeader(String transactionCode) {
        return tranHeaderHistoryRepository.findByTransactionCode(transactionCode).orElse(null);
    }
}
