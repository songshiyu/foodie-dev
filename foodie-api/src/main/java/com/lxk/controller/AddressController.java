package com.lxk.controller;

import com.lxk.pojo.UserAddress;
import com.lxk.pojo.bo.AddressBO;
import com.lxk.pojo.vo.NewItemsVO;
import com.lxk.service.AddressService;
import com.lxk.utils.MobileEmailUtils;
import com.lxk.utils.ResultJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author songshiyu
 * @date 2020/6/28 22:25
 **/

@Api(value = "地址相关接口", tags = {"地址相关接口"})
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 5.设置默认地址
     */
    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public ResultJSONResult sixNewItems(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return ResultJSONResult.errorMsg("");
        }

        List<UserAddress> addressList = addressService.queryAll(userId);
        return ResultJSONResult.ok(addressList);
    }

    @ApiOperation(value = "用户新增收货地址", notes = "用户新增收货地址", httpMethod = "POST")
    @PostMapping("/add")
    public ResultJSONResult add(@RequestBody AddressBO addressBO) {

        ResultJSONResult result = checkAddress(addressBO);
        if (result.getStatus() != 200) {
            return result;
        }
        addressService.addNewUserAddress(addressBO);
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "用户修改收货地址", notes = "用户修改收货地址", httpMethod = "POST")
    @PostMapping("/update")
    public ResultJSONResult update(@RequestBody AddressBO addressBO) {

        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return ResultJSONResult.errorMsg("修改地址错误:addressId不能为空");
        }

        ResultJSONResult result = checkAddress(addressBO);
        if (result.getStatus() != 200) {
            return result;
        }
        addressService.updateUserAddress(addressBO);
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "用户删除收货地址", notes = "用户删除收货地址", httpMethod = "POST")
    @PostMapping("/delete")
    public ResultJSONResult delete(@RequestParam String userId, @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ResultJSONResult.errorMsg("请确认是否选择地址信息！");
        }
        addressService.deleteUseraddress(userId, addressId);
        return ResultJSONResult.ok();
    }

    @ApiOperation(value = "用户设置默认收货地址", notes = "用户设置默认收货地址", httpMethod = "POST")
    @PostMapping("/setDefalut")
    public ResultJSONResult setDefalut(@RequestParam String userId, @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return ResultJSONResult.errorMsg("请确认是否选择地址信息！");
        }
        addressService.setDefalutUserAddress(userId, addressId);
        return ResultJSONResult.ok();
    }

    private ResultJSONResult checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return ResultJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return ResultJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return ResultJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return ResultJSONResult.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return ResultJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return ResultJSONResult.errorMsg("收货地址信息不能为空");
        }

        return ResultJSONResult.ok();
    }
}
