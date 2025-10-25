package com.api.service;

import com.api.model.Account;
import com.api.model.Transfer;
import com.api.repository.AccountRepository;
import com.api.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class TransferService {
    
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    
    public TransferService(AccountRepository accountRepository, 
                          TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }
    
    @Transactional
    public Transfer execute(String from, String to, BigDecimal amount, String description) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        Account fromAccount = accountRepository.findByAccountNumber(from)
            .orElseThrow(() -> new IllegalArgumentException("Source account not found: " + from));
        
        Account toAccount = accountRepository.findByAccountNumber(to)
            .orElseThrow(() -> new IllegalArgumentException("Destination account not found: " + to));
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in account: " + from);
        }
        
        fromAccount.debit(amount);
        toAccount.credit(amount);
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        Transfer transfer = new Transfer();
        transfer.setFromAccountNumber(from);
        transfer.setToAccountNumber(to);
        transfer.setAmount(amount);
        transfer.setDescription(description);
        transfer.setStatus("COMPLETED");
        
        return transferRepository.save(transfer);
    }
}