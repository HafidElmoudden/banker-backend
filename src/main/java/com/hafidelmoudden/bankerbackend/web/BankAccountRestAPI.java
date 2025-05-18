package com.hafidelmoudden.bankerbackend.web;

import com.hafidelmoudden.bankerbackend.dtos.*;
import com.hafidelmoudden.bankerbackend.exceptions.BalanceNotSufficientException;
import com.hafidelmoudden.bankerbackend.exceptions.BankAccountNotFoundException;
import com.hafidelmoudden.bankerbackend.exceptions.CustomerNotFoundException;
import com.hafidelmoudden.bankerbackend.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }
    
    @GetMapping("/accounts")
    public Page<BankAccountDTO> listAccounts(
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size) {
        return bankAccountService.listAccountsWithPagination(PageRequest.of(page, size));
    }
    
    @PostMapping("/accounts")
    public BankAccountDTO createAccount(@RequestBody AccountRequest accountRequest) throws CustomerNotFoundException {
        if (accountRequest.getAccountType().equalsIgnoreCase("CURRENT")) {
            return bankAccountService.saveCurrentBankAccount(
                    accountRequest.getInitialBalance(),
                    accountRequest.getOverDraft() != null ? accountRequest.getOverDraft() : 9000, // Default overdraft
                    accountRequest.getCustomerId());
        } else {
            return bankAccountService.saveSavingBankAccount(
                    accountRequest.getInitialBalance(),
                    accountRequest.getInterestRate() != null ? accountRequest.getInterestRate() : 5.5, // Default interest rate
                    accountRequest.getCustomerId());
        }
    }
    
    @GetMapping("/accounts/search")
    public Page<BankAccountDTO> searchAccounts(
            @RequestParam(name="keyword", defaultValue = "") String keyword,
            @RequestParam(name="page", defaultValue = "0") int page,
            @RequestParam(name="size", defaultValue = "10") int size) {
        return bankAccountService.searchAccounts(keyword, PageRequest.of(page, size));
    }
    
    @GetMapping("/accounts/customer/{customerId}")
    public List<BankAccountDTO> getCustomerAccounts(@PathVariable Long customerId) {
        return bankAccountService.getCustomerAccounts(customerId);
    }
    
    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name="size",defaultValue = "5")int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }
    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }
    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount());
    }
}
