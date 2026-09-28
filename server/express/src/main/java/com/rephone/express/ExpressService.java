package com.rephone.express;

import com.rephone.express.model.ExpressPickupRequest;
import com.rephone.express.model.ExpressTrace;

/**
 * 快递100 服务接口。P4 实现：key/secret 走环境变量，
 * 出站请求必须经 HttpGuard 校验，本模块不落任何密钥。
 */
public interface ExpressService {

    /** 预约上门取件，返回快递100 任务号。 */
    String createPickup(ExpressPickupRequest request);

    /** 取消取件任务。 */
    void cancelPickup(String taskNo);

    /** 查询物流轨迹。 */
    ExpressTrace queryTrace(String expressNo);
}
