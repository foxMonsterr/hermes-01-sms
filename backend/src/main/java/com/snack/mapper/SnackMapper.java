package com.snack.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.snack.entity.Snack;
import org.apache.ibatis.annotations.Mapper;

/**
 * 零食 Mapper
 */
@Mapper
public interface SnackMapper extends BaseMapper<Snack> {
}
