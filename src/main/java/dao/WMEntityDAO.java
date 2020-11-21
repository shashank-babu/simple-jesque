package dao;

import com.codahale.metrics.annotation.Timed;
import entity.WMEntity;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;


@UseStringTemplate3StatementLocator("~")
public interface WMEntityDAO {
    @RegisterMapper(WMEntity.WMEntityMapper.class)
    @SqlQuery("select " +
            " wa.transaction_id as transaction_id, " +
            " wa.tenant_transaction_id as tenant_transaction_id, " +
            " wa.wallet_transaction_id as wallet_transaction_id, " +
            " wa.operation as operation, " +
            " wa.amount as amount, " +
            " cl.compartment_txn_split as caishen_split, " +
            " wa.merchant_id as merchant_id, " +
            " tm.udf2 as ref_hash, " +
            " wa.wallet_id as wallet_id, " +
            " wa.created_at as created_at, " +
            " wa.status as status " +
            " from wallet_audit as wa join CompartmentLedger as cl join TransactionMaster as tm on wa.wallet_transaction_id = cl.txn_id and wa.wallet_transaction_id = tm.txn_id " +
            " where wa.operation in (<operations>) AND wa.status = :statse")
    List<WMEntity> getEntities(@BindIn("operations") List<String> operations, @Bind("statse") String status);

    @Timed
    @SqlBatch("INSERT INTO `wallet_manager_entity` (`transaction_id`, `wallet_transaction_id`, `tenant_transaction_id`, `merchant_id`, `wallet_id`, `auth_id`, `amount`, `gross_amount`, " +
            " `actual_date`, `created_at`, `updated_at`, `operation`, `split_details`, `ref_hash`, `status`) VALUES " +
            " (:wm_entity.transactionId, :wm_entity.walletTransactionId, :wm_entity.tenantTransactionId, " +
            "  :wm_entity.merchantId, :wm_entity.walletId, :wm_entity.authId, :wm_entity.amount, :wm_entity.grossAmount, " +
            "  :wm_entity.actualDate, :wm_entity.createdAt, :wm_entity.updatedAt, :wm_entity.operation, :wm_entity.splitDetails, " +
            "  :wm_entity.refHash, :wm_entity.status) ")
    void insertBulk(@BindBean("wm_entity") List<WMEntity> wmEntities);
}
