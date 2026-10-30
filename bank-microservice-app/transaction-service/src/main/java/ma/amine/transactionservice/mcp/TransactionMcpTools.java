package ma.amine.transactionservice.mcp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.amine.transactionservice.domain.Transaction;
import ma.amine.transactionservice.service.TransactionService;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionMcpTools {

    private final TransactionService transactionService;

    @McpTool(description = "Get all transactions across all accounts")
    public List<Transaction> getAllTransactions() {
        log.info("MCP: getAllTransactions called");
        return transactionService.getAllTransactions();
    }

    @McpTool(description = "Find a transaction by its UUID")
    public Transaction getTransactionById(
            @McpToolParam(description = "The transaction UUID") UUID id) {
        log.info("MCP: getTransactionById called for ID: {}", id);
        return transactionService.getTransactionById(id);
    }

    @McpTool(description = "Get all transactions for a specific bank account ID")
    public List<Transaction> getTransactionsByAccountId(
            @McpToolParam(description = "The account UUID") UUID accountId) {
        log.info("MCP: getTransactionsByAccountId called for account: {}", accountId);
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @McpTool(description = "Record a new transaction with business rule validations (account active, balance sufficient, max limit)")
    public Transaction recordTransaction(
            @McpToolParam(description = "The transaction to record (accountId, amount, currency, merchantCategory, country)") Transaction transaction) {
        log.info("MCP: recordTransaction called for account: {}", transaction.getAccountId());
        return transactionService.recordTransaction(transaction);
    }

    @McpTool(description = "Execute a transfer between two bank accounts and record transactions")
    public String executeTransfer(
            @McpToolParam(description = "Source account UUID") UUID sourceAccountId,
            @McpToolParam(description = "Destination account UUID") UUID destinationAccountId,
            @McpToolParam(description = "Amount to transfer") double amount,
            @McpToolParam(description = "Currency (e.g. EUR)") String currency,
            @McpToolParam(description = "Country code (e.g. MA)") String country) {
        log.info("MCP: executeTransfer called from {} to {}, amount: {}", sourceAccountId, destinationAccountId, amount);
        transactionService.executeTransfer(sourceAccountId, destinationAccountId, BigDecimal.valueOf(amount), currency, country);
        return "Transfer of " + amount + " " + currency + " from " + sourceAccountId + " to " + destinationAccountId + " executed successfully";
    }
}
