package com.hmall.client;

import com.hmall.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Address客户端
 *
 * @author liudo
 * @version 1.0
 * @project hmall
 * @date 2023/8/25 20:04:57
 */
@FeignClient("user-service")
public interface UserClient {

    /**
     * 按id查找地址
     *
     * @param id id
     * @return {@link Address}
     */
    @GetMapping("/address/{id}")
    Address findAddressById(@PathVariable Long id);
}
