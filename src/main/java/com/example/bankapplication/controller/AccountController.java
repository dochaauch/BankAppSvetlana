package com.example.bankapplication.controller;

import com.example.bankapplication.dto.AccountDTO;
import com.example.bankapplication.dto.AccountListDTO;
import com.example.bankapplication.dto.CreateAccountDTO;
import com.example.bankapplication.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/createAccount")
    public AccountDTO createAccount(@RequestBody CreateAccountDTO account){
        return accountService.createAccount(account);
    }

    @GetMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDTO getAccountById(@PathVariable UUID id){
        return accountService.getAccountById(id);
    }

    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public AccountListDTO getAllAccounts(){
        return accountService.getAllAccountsStatus();
    }

    @DeleteMapping("deleteAccount/{id}")
    public void delete(@PathVariable UUID id){
        accountService.deleteAccountById(id);
    }
}