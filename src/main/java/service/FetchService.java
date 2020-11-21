package service;

import com.flipkart.wallet.manager.model.enums.OperationType;
import com.google.common.collect.Lists;
import dao.WMEntityDAO;
import entity.WMEntity;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class FetchService {
    private WMEntityDAO wmEntityDAO;
    @Inject
    public FetchService(WMEntityDAO wmEntityDAO) {
        this.wmEntityDAO = wmEntityDAO;
    }

    public List<WMEntity> fetchWmEntity() {
        List<WMEntity> wmEntities = wmEntityDAO.getEntities(Lists.newArrayList(OperationType.TOPUP.name(),
                OperationType.BLOCK.name(),
                OperationType.BLOCK_UPDATE.name(),
                OperationType.REDEEM.name(),
                OperationType.RELEASE.name(),
                OperationType.REFUND.name()), "success");
        System.out.println("wmEntityList size is " + wmEntities.size());
        wmEntities.forEach( wmEntity -> {
            try {
                wmEntity.createActualDate(OperationType.valueOf(wmEntity.getOperation()), wmEntity.getRefHash(), wmEntity.getCreatedAt());
                wmEntity.createSplitDetails(wmEntity.getCaishenSplit(), OperationType.valueOf(wmEntity.getOperation()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return wmEntities;
    }

    public void setWmEntity(List<WMEntity> wmEntities) {
         List<List<WMEntity>> partitions = Lists.partition(wmEntities, 200);
         partitions.forEach(partition -> {
             wmEntityDAO.insertBulk(partition);
         });
    }
}
