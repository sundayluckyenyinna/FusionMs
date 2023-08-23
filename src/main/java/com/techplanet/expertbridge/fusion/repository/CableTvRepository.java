package com.techplanet.expertbridge.fusion.repository;


import com.techplanet.expertbridge.fusion.model.BillerLookupData;

import java.util.List;

public interface CableTvRepository
{
    List<BillerLookupData> findBillerLookupByBillerIdAndProvider(String id, String provider);
    public List<BillerLookupData> findBillerLookupByBillerIdAndProviderFromConfig(String billerId, String provider);
}
