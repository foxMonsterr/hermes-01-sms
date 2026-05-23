package com.snack.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

/**
 * 分页数据封装（不包含 code/msg，由外层 Result 提供）
 *
 * @param <T> 记录类型
 */
public class PageData<T> {

    private List<T> records;
    private long total;
    private long page;
    private long size;

    public PageData() {}

    public static <T> PageData<T> of(IPage<T> page) {
        PageData<T> pd = new PageData<>();
        pd.records = page.getRecords();
        pd.total = page.getTotal();
        pd.page = page.getCurrent();
        pd.size = page.getSize();
        return pd;
    }

    // ---------------- getters / setters ----------------

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getPage() { return page; }
    public void setPage(long page) { this.page = page; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
}
