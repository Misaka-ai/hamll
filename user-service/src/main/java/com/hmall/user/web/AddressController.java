package com.hmall.user.web;

import com.hmall.pojo.Address;
import com.hmall.user.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private IAddressService addressService;

    /**
     * 按id查找地址
     *
     * @param id id
     * @return {@link Address}
     */
    @GetMapping("{id}")
    public Address findAddressById(@PathVariable("id") Long id) {
        return addressService.getById(id);
    }

    /**
     * 按用户id查找地址
     *
     * @param userId 用户id
     * @return {@link List}<{@link Address}>
     */
    @GetMapping("/uid/{userId}")
    public List<Address> findAddressByUserId(@PathVariable("userId") Long userId) {
        return addressService.query().eq("user_id", userId).list();
    }
}
