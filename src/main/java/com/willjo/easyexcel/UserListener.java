package com.willjo.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.willjo.dal.entity.UserEntity;
import com.willjo.service.AsyncUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Easy Excel User解析
 *
 * @author Chen Jiaying
 */
public class UserListener extends AnalysisEventListener<UserEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserListener.class);

    /**
     * 每隔3000条存储数据库，然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 3000;
    
    /**
     * 缓存的数据
     */
    private List<UserEntity> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    
    private AsyncUserService asyncUserService;

    public UserListener() {

    }

    public UserListener(AsyncUserService asyncUserService) {
        this.asyncUserService = asyncUserService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param context {@link AnalysisContext}
     */
    @Override
    public void invoke(UserEntity data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            // 存储数据库
            asyncUserService.saveUser(cachedDataList);
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }
    
    /**
     * 所有数据解析完成了，都会来调用
     *
     * @param context {@link AnalysisContext}
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 存储数据库
        asyncUserService.saveUser(cachedDataList);
        LOGGER.info("所有数据解析完成！");
    }
}