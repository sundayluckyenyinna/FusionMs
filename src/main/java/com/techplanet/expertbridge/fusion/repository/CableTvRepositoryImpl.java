package com.techplanet.expertbridge.fusion.repository;

import com.techplanet.expertbridge.fusion.model.BillerLookupData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Repository
public class CableTvRepositoryImpl implements CableTvRepository {

    @Autowired
    Environment env;

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<BillerLookupData> findBillerLookupByBillerIdAndProvider(String billerId, String provider) {
        TypedQuery<BillerLookupData> query = em.createQuery("Select t from BillerLookupData t where t.billerId = :billerId and t.vendorName = :name", BillerLookupData.class)
                .setParameter("billerId", billerId)
                .setParameter("name", provider);
        return query.getResultList();
    }

    @Override
    public List<BillerLookupData> findBillerLookupByBillerIdAndProviderFromConfig(String billerId, String provider) {
        List<BillerLookupData> products = new ArrayList<>();
        String activeVendor = env.getProperty("thirdParty.bills.vendor");
        assert activeVendor != null;
        String activeVendorConfigKey = activeVendor.toLowerCase();
        String baseKey = String.join(".", "thirdParty", "bills", activeVendorConfigKey, "electricity", "products");
        int electricityPrepaidCount = Integer.parseInt(Objects.requireNonNull(env.getProperty(baseKey.concat(".").concat("prepaid.").concat("count"))));
        int electricPostpaidCount = Integer.parseInt(Objects.requireNonNull(env.getProperty(baseKey.concat(".").concat("postpaid.").concat("count"))));

        List<BillerLookupData> prepaidProducts = new ArrayList<>();
        for (int i = 1; i <= electricityPrepaidCount; i++) {
            String product = env.getProperty(baseKey.concat(".prepaid.").concat("prepaid".concat(String.valueOf(i))));
            assert product != null;
            String[] splits = product.split("\\|");

            BillerLookupData billerLookupData = new BillerLookupData();
            billerLookupData.setBillerId("ELECTRICITY");
            billerLookupData.setBillerName(splits[0]);
            billerLookupData.setBillerDescription("PREPAID");
            billerLookupData.setBillerShorthand("ELE");
            billerLookupData.setVendorName(activeVendor.toUpperCase());
            billerLookupData.setVendorShortCode(activeVendor);
            billerLookupData.setBillerCode(splits[1]);
            billerLookupData.setCreatedAt(LocalDateTime.now());
            billerLookupData.setCreatedBy("SYSTEM");
            billerLookupData.setUpdatedAt(LocalDateTime.now());
            billerLookupData.setUpdatedBy("SYSTEM");

            prepaidProducts.add(billerLookupData);
        }

        List<BillerLookupData> postpaidProducts = new ArrayList<>();
        for (int i = 1; i <= electricPostpaidCount; i++) {
            String product = env.getProperty(baseKey.concat(".postpaid.").concat("postpaid".concat(String.valueOf(i))));
            assert product != null;
            String[] splits = product.split("\\|");

            BillerLookupData billerLookupData = new BillerLookupData();
            billerLookupData.setBillerId("ELECTRICITY");
            billerLookupData.setBillerName(splits[0]);
            billerLookupData.setBillerDescription("POSTPAID");
            billerLookupData.setBillerShorthand("ELE");
            billerLookupData.setVendorName(activeVendor.toUpperCase());
            billerLookupData.setVendorShortCode(activeVendor);
            billerLookupData.setBillerCode(splits[1]);
            billerLookupData.setCreatedAt(LocalDateTime.now());
            billerLookupData.setCreatedBy("SYSTEM");
            billerLookupData.setUpdatedAt(LocalDateTime.now());
            billerLookupData.setUpdatedBy("SYSTEM");

            postpaidProducts.add(billerLookupData);
        }

        products.addAll(prepaidProducts);
        products.addAll(postpaidProducts);

        return products;
    }
}
