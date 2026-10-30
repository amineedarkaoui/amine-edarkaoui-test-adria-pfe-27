package ma.amine.accountservice.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.accountservice.domain.Account;
import ma.amine.accountservice.domain.AccountStatus;
import ma.amine.accountservice.service.AccountService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountMcpTools {

    private final AccountService accountService;

    @McpTool(description = "Get all bank accounts")
    public List<Account> getAllAccounts() {
        log.info("MCP: getAllAccounts called");
        return accountService.getAllAccounts();
    }

    @McpTool(description = "Find a bank account by ID")
    public Account getAccountById(
            @McpToolParam(description = "The bank account UUID") UUID id) {
        log.info("MCP: getAccountById called for ID: {}", id);
        return accountService.getAccountById(id);
    }

    @McpTool(description = "Save or create a new bank account")
    public Account createAccount(
            @McpToolParam(description = "The account to save (customerReference, currency, balance, status)") Account account) {
        log.info("MCP: createAccount called for customer: {}", account.getCustomerReference());
        return accountService.createAccount(account);
    }

    @McpTool(description = "Credit money into a bank account")
    public Account creditAccount(
            @McpToolParam(description = "The account UUID") UUID id,
            @McpToolParam(description = "Amount to credit") double amount) {
        log.info("MCP: creditAccount called for account: {}, amount: {}", id, amount);
        return accountService.credit(id, amount);
    }

    @McpTool(description = "Debit money from a bank account")
    public Account debitAccount(
            @McpToolParam(description = "The account UUID") UUID id,
            @McpToolParam(description = "Amount to debit") double amount) {
        log.info("MCP: debitAccount called for account: {}, amount: {}", id, amount);
        return accountService.debit(id, amount);
    }

    @McpTool(description = "Transfer money from one account to another")
    public String transfer(
            @McpToolParam(description = "Source account UUID") UUID sourceAccountId,
            @McpToolParam(description = "Destination account UUID") UUID destinationAccountId,
            @McpToolParam(description = "Amount to transfer") double amount) {
        log.info("MCP: transfer called from {} to {}, amount: {}", sourceAccountId, destinationAccountId, amount);
        accountService.transfer(sourceAccountId, destinationAccountId, amount);
        return "Transfer of " + amount + " from " + sourceAccountId + " to " + destinationAccountId + " executed successfully";
    }

    @McpTool(description = "Update the status of a bank account (ACTIVE or BLOCKED)")
    public Account updateAccountStatus(
            @McpToolParam(description = "The account UUID") UUID id,
            @McpToolParam(description = "New status: ACTIVE or BLOCKED") AccountStatus status) {
        log.info("MCP: updateAccountStatus called for account: {}, status: {}", id, status);
        return accountService.updateStatus(id, status);
    }
}
