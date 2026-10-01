package com.rephone.express;

import com.rephone.express.model.ExpressPickupRequest;
import com.rephone.express.model.ExpressPickupResult;
import com.rephone.express.model.ExpressTrace;

/**
 * 快递100 服务接口。key/customer 走环境变量，
 * 出站请求必须经 HttpGuard 校验，本模块不落任何密钥。
 *
 * <p>注意：实时查询（queryTrace）与上门取件（createPickup）是快递100 两个独立签约的产品，
 * 只开通查询的账号调用取件接口会被拒。
 */
public interface ExpressService {

    /** 预约上门取件，返回任务号与运单号（运单号可能为空，由后续回调补齐）。需单独开通寄件服务。 */
    ExpressPickupResult createPickup(ExpressPickupRequest request);

    /** 取消取件任务。需单独开通寄件服务。 */
    void cancelPickup(String taskNo);

    /**
     * 查询物流轨迹。
     *
     * @param com       快递100 公司编码（小写），如 shunfeng
     * @param expressNo 运单号
     * @param phone     收/寄件人手机号；顺丰速运、顺丰快运、中通快递必填，其他可为 null
     */
    ExpressTrace queryTrace(String com, String expressNo, String phone);
}
