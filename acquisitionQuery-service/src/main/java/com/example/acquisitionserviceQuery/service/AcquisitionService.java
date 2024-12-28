package com.example.acquisitionserviceQuery.service;

import com.example.acquisitionserviceQuery.dto.AcquisitionSyncDTO;
import com.example.acquisitionserviceQuery.model.Acquisition;

import java.util.List;

public interface AcquisitionService {

    List<Acquisition> findAllAcquisitions();

    List<Acquisition> findPendingAcquisitions();

    void syncAcquisitionFromConsumer(AcquisitionSyncDTO syncDTO);

    void updateAcquisitionStatusFromConsumer(AcquisitionSyncDTO syncDTO);

}
