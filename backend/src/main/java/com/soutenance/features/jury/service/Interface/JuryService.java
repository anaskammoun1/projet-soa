package com.soutenance.features.jury.service.Interface;

import com.soutenance.features.jury.dto.JuryDTO;
import com.soutenance.features.jury.entity.Jury;

public interface JuryService {

    JuryDTO findBySoutenance(Long soutenanceId);

    JuryDTO affecter(JuryDTO dto);

    void delete(Long soutenanceId);

    Jury save(Jury jury);
}
