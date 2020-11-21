package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.wallet.apis.enums.CompartmentType;
import com.flipkart.wallet.manager.model.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.flipkart.wallet.manager.model.Money.HUNDRED;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WMEntity {
    private String transactionId;
    private BigDecimal amount;
    private String splitDetails;
    private String caishenSplit;
    private String authId;
    private BigDecimal grossAmount;
    private String merchantId;
    private String refHash;
    private String walletId;
    //    private Timestamp actual_date;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String tenantTransactionId;
    private String walletTransactionId;
    private String operation;
    private String actualDate;
    private String status;

    public WMEntity(String transactionId,
                    BigDecimal amount,
                    String caishenSplit,
                    String authId,
                    String merchantId,
                    String refHash,
                    String walletId,
                    Timestamp createdAt,
                    Timestamp updatedAt,
                    String tenantTransactionId,
                    String walletTransactionId,
                    String operation,
                    String status) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.caishenSplit = caishenSplit;
        this.authId = authId;
        this.merchantId = merchantId;
        this.refHash = refHash;
        this.walletId = walletId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.tenantTransactionId = tenantTransactionId;
        this.walletTransactionId = walletTransactionId;
        this.operation = operation;
        this.status = status;
    }

    public static class WMEntityMapper implements ResultSetMapper<WMEntity> {
        @Override
        public WMEntity map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new WMEntity(r.getString("transaction_id"),
                    r.getBigDecimal("amount"),
                    r.getString("caishen_split"),
                    "authId",
                    r.getString("merchant_id"),
                    r.getString("ref_hash"),
                    r.getString("wallet_id"),
                    r.getTimestamp("created_at"),
                    Timestamp.from(Instant.now()),
                    r.getString("tenant_transaction_id"),
                    r.getString("wallet_transaction_id"),
                    r.getString("operation"),
                    r.getString("status"));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class SplitDetail {
        private String topId;
        private CompartmentType compartment;
        private BigDecimal amount;
        private BigDecimal taxAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CaishenSplit {
        private String splitId;
        private CompartmentType provider;
        private Integer splitAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RedeemRefHash {
        private String date;
    }

    public void createSplitDetails(String caishenSplit, OperationType operation) throws IOException {
        List<CaishenSplit> caishenSplits = new ObjectMapper().readValue(caishenSplit, new TypeReference<List<CaishenSplit>>() {
        });
        List<SplitDetail> splitDetails = caishenSplits.stream().map(x ->
                new SplitDetail(x.getSplitId(),
                        x.getProvider(),
                        BigDecimal.valueOf(x.getSplitAmount()).movePointLeft(2),
                        createTaxAmount(operation, BigDecimal.valueOf(x.getSplitAmount()).movePointLeft(2), x.getProvider())
                )).collect(Collectors.toList());
        this.splitDetails = new ObjectMapper().writeValueAsString(splitDetails);
    }

    private BigDecimal createTaxAmount(OperationType operationType, BigDecimal splitAmount, CompartmentType compartmentType) {
        if (operationType == OperationType.TOPUP || operationType == OperationType.REFUND || operationType == OperationType.RELEASE) {
            return BigDecimal.ZERO;
        } else if (operationType == OperationType.BLOCK || operationType == OperationType.BLOCK_UPDATE) {
            return splitAmount.subtract(calculatePrincipleAmount(splitAmount, getTaxPercentage(compartmentType)));
        } else if (operationType == OperationType.REDEEM) {
            return calculateTotalAmount(splitAmount, getTaxPercentage(compartmentType)).subtract(splitAmount);
        } else {
            System.out.println("operationType = " + operationType);
            throw new IllegalArgumentException();
        }
    }

    private BigDecimal getTaxPercentage(CompartmentType compartmentType) {
        if (compartmentType == CompartmentType.FREE_CREDIT) {
            return new BigDecimal(0.00);
        } else {
            return new BigDecimal(18.00);
        }
    }

    public void createActualDate(OperationType operationType, String refHash, Timestamp createdAt) throws IOException {
        if (operationType == OperationType.REDEEM || operationType == OperationType.RELEASE) {
            this.actualDate = new ObjectMapper().readValue(refHash, RedeemRefHash.class).getDate();
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            this.actualDate = formatter.format(createdAt);
        }
    }

    private static BigDecimal calculatePrincipleAmount(BigDecimal amount, BigDecimal taxPercent) {
        return amount.multiply(HUNDRED).divide(taxPercent.add(HUNDRED), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateTotalAmount(BigDecimal amount, BigDecimal taxPercent) {
        return amount.multiply(taxPercent.add(HUNDRED)).divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }

}
