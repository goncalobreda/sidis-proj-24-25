package com.example.acquisitionserviceCommand.service;

import com.example.acquisitionserviceCommand.dto.CreateAcquisitionDTO;
import com.example.acquisitionserviceCommand.dto.UserSyncDTO;
import com.example.acquisitionserviceCommand.model.Acquisition;
import com.example.acquisitionserviceCommand.model.Reader;

public interface AcquisitionService {

    Acquisition createAcquisition(CreateAcquisitionDTO dto);

    Acquisition approveAcquisition(String acquisitionID);

    Acquisition rejectAcquisition(String acquisitionID, String reason);

    Reader createFromUserSyncDTO(UserSyncDTO userSyncDTO);
}
