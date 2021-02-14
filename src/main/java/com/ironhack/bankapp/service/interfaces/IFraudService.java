package com.ironhack.bankapp.service.interfaces;

import java.math.BigDecimal;

public interface IFraudService {
    Boolean checkFrauds(Long id, BigDecimal nextTransactionAmount);
}
