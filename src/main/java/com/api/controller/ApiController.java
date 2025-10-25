package com.api.controller;

import com.api.model.Account;
import com.api.model.Transfer;
import com.api.repository.AccountRepository;
import com.api.repository.TransferRepository;
import com.api.service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final TransferService transferService;
    
    public ApiController(AccountRepository accountRepository,
                        TransferRepository transferRepository,
                        TransferService transferService) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.transferService = transferService;
    }
    
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account saved = accountRepository.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @GetMapping("/accounts/{accountNumber}")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }
    
    @PostMapping("/transfers")
    public ResponseEntity<Transfer> createTransfer(@RequestBody Map<String, Object> request) {
        String fromAcc = (String) request.get("fromAccountNumber");
        String toAcc = (String) request.get("toAccountNumber");
        BigDecimal amt = new BigDecimal(request.get("amount").toString());
        String desc = (String) request.get("description");
        
        Transfer result = transferService.execute(fromAcc, toAcc, amt, desc);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/transfers/history/{accountNumber}")
    public List<Transfer> getHistory(@PathVariable String accountNumber) {
        return transferRepository.findByFromAccountNumberOrToAccountNumber(accountNumber, accountNumber);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}