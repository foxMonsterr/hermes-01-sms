package com.snack.service;

import com.snack.common.PageData;
import com.snack.dto.DisposalDTO;
import com.snack.vo.DisposalRecordVO;
import com.snack.vo.DisposalStatsVO;
import java.util.Map;

public interface DisposalService {
    PageData<DisposalRecordVO> page(Long userId, int page, int size, String startDate, String endDate);
    Map<String, Object> dispose(Long userId, Long snackId, DisposalDTO dto);
    DisposalStatsVO stats(Long userId, String startDate, String endDate);
}
