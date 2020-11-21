import dao.WMEntityDAO;
import entity.WMEntity;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import service.FetchService;

import java.util.List;

/**
 * AuthId -> need Add AuthId
 */
public class Driver {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/fdp-migration";
        String user = "root";
        String password = "password";
        DBI dbi = new DBI(url, user, password);
        Handle relayerHandle = dbi.open();
        WMEntityDAO wmEntityDAO = relayerHandle.attach(WMEntityDAO.class);
        FetchService fetchService = new FetchService(wmEntityDAO);
        List<WMEntity> wmEntityList = fetchService.fetchWmEntity();
        System.out.println("size is " + wmEntityList.size());
        fetchService.setWmEntity(wmEntityList);
    }

}
