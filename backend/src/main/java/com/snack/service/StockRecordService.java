package com.snack.service;

import com.snack.common.PageData;
import com.snack.dto.StockRecordQueryDTO;
import com.snack.vo.StockRecordStatsVO;
import com.snack.vo.StockRecordVO;

public interface StockRecordService {

    /** 分页查询库存流水 */
    PageData<StockRecordVO> page(Long currentUserId, StockRecordQueryDTO query);

    /** 按零食查询流水（不强依赖snack是否删除） */
    PageData<StockRecordVO> pageBySnack(Long currentUserId, Long snackId, StockRecordQueryDTO query);

    /** 出入库统计汇总 */
    StockRecordStatsVO stats(Long currentUserId);
}
