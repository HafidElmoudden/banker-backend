package com.hafidelmoudden.bankerbackend.mappers;

import com.hafidelmoudden.bankerbackend.dtos.AccountOperationDTO;
import com.hafidelmoudden.bankerbackend.dtos.BankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.CurrentBankAccountDTO;
import com.hafidelmoudden.bankerbackend.dtos.CustomerDTO;
import com.hafidelmoudden.bankerbackend.dtos.SavingBankAccountDTO;
import com.hafidelmoudden.bankerbackend.entities.AccountOperation;
import com.hafidelmoudden.bankerbackend.entities.BankAccount;
import com.hafidelmoudden.bankerbackend.entities.CurrentAccount;
import com.hafidelmoudden.bankerbackend.entities.Customer;
import com.hafidelmoudden.bankerbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO=new CustomerDTO();
        BeanUtils.copyProperties(customer,customerDTO);
        return customerDTO;
    }
    
    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        Customer customer=new Customer();
        BeanUtils.copyProperties(customerDTO,customer);
        return customer;
    }

    public SavingBankAccountDTO fromSavingBankAccount(SavingAccount savingAccount){
        SavingBankAccountDTO savingBankAccountDTO=new SavingBankAccountDTO();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDTO);
        
        // Set customer information
        if (savingAccount.getCustomer() != null) {
            savingBankAccountDTO.setCustomerDTO(fromCustomer(savingAccount.getCustomer()));
            // Add additional customer fields for frontend convenience
            savingBankAccountDTO.setCustomerId(savingAccount.getCustomer().getId());
            savingBankAccountDTO.setCustomerName(savingAccount.getCustomer().getName());
        }
        
        // Set accountId for frontend compatibility
        savingBankAccountDTO.setAccountId(savingAccount.getId());
        
        savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
        savingBankAccountDTO.setAccountType("Saving Account");
        return savingBankAccountDTO;
    }

    public SavingAccount fromSavingBankAccountDTO(SavingBankAccountDTO savingBankAccountDTO){
        SavingAccount savingAccount=new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDTO,savingAccount);
        savingAccount.setCustomer(fromCustomerDTO(savingBankAccountDTO.getCustomerDTO()));
        return savingAccount;
    }

    public CurrentBankAccountDTO fromCurrentBankAccount(CurrentAccount currentAccount){
        CurrentBankAccountDTO currentBankAccountDTO=new CurrentBankAccountDTO();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDTO);
        
        // Set customer information
        if (currentAccount.getCustomer() != null) {
            currentBankAccountDTO.setCustomerDTO(fromCustomer(currentAccount.getCustomer()));
            // Add additional customer fields for frontend convenience
            currentBankAccountDTO.setCustomerId(currentAccount.getCustomer().getId());
            currentBankAccountDTO.setCustomerName(currentAccount.getCustomer().getName());
        }
        
        // Set accountId for frontend compatibility
        currentBankAccountDTO.setAccountId(currentAccount.getId());
        
        currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
        currentBankAccountDTO.setAccountType("Current Account");
        return currentBankAccountDTO;
    }

    public CurrentAccount fromCurrentBankAccountDTO(CurrentBankAccountDTO currentBankAccountDTO){
        CurrentAccount currentAccount=new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDTO,currentAccount);
        currentAccount.setCustomer(fromCustomerDTO(currentBankAccountDTO.getCustomerDTO()));
        return currentAccount;
    }

    public AccountOperationDTO fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDTO accountOperationDTO=new AccountOperationDTO();
        BeanUtils.copyProperties(accountOperation,accountOperationDTO);
        return accountOperationDTO;
    }
    
    public BankAccountDTO toBankAccountDTO(BankAccount bankAccount) {
        if (bankAccount instanceof SavingAccount) {
            return fromSavingBankAccount((SavingAccount) bankAccount);
        } else if (bankAccount instanceof CurrentAccount) {
            return fromCurrentBankAccount((CurrentAccount) bankAccount);
        }
        return null;
    }
}
