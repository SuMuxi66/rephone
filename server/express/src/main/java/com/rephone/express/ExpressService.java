package com.rephone.express;

import com.rephone.express.model.ExpressPickupRequest;
import com.rephone.express.model.ExpressPickupResult;
import com.rephone.express.model.ExpressTrace;

/**
 * 快递100 服务接口。P4 实现：key/secret 走环境变量，
 * 出站请求必须经 HttpGuard 校验，本模块不落任何密钥。
 */
public interface ExpressService {

    /** 预约上门取件，返回任务号与运单号（运单号可能为空，由后续回调补齐）。 */
    ExpressPickupResult createPickup(ExpressPickupRequest request);

    /** 取消取件任务。 */
    void cancelPickup(String taskNo);

    /** 查询物流轨迹。 */
    ExpressTrace queryTrace(String expressNo);
}
