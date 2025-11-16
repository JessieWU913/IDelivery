package cs.qqq.Controller;

import cs.qqq.Entity.Address;
import cs.qqq.Entity.SysUser;
import cs.qqq.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配送地址Controller
 * 处理地址管理相关请求
 */
@Controller
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 地址列表页面
     */
    @GetMapping("/list")
    public String addressList(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }

        List<Address> addresses = addressService.getUserAddresses(user.getUserId());
        model.addAttribute("addresses", addresses);

        return "address/list";
    }

    /**
     * 添加地址页面
     */
    @GetMapping("/add")
    public String addAddressPage() {
        return "address/add";
    }

    /**
     * 添加地址（AJAX）
     */
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addAddress(@RequestBody Address address, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        address.setUserId(user.getUserId());

        try {
            boolean success = addressService.addAddress(address);
            result.put("success", success);
            result.put("message", success ? "添加成功" : "添加失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 编辑地址页面
     */
    @GetMapping("/edit/{addressId}")
    public String editAddressPage(@PathVariable Long addressId, HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login.html";
        }

        Address address = addressService.getAddressById(addressId);
        if (address == null || !address.getUserId().equals(user.getUserId())) {
            model.addAttribute("error", "地址不存在或无权操作");
            return "comm/error_403";
        }

        model.addAttribute("address", address);
        return "address/edit";
    }

    /**
     * 更新地址（AJAX）
     */
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateAddress(@RequestBody Address address, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        // 验证地址是否属于该用户
        Address existAddress = addressService.getAddressById(address.getAddressId());
        if (existAddress == null || !existAddress.getUserId().equals(user.getUserId())) {
            result.put("success", false);
            result.put("message", "地址不存在或无权操作");
            return result;
        }

        address.setUserId(user.getUserId());

        try {
            boolean success = addressService.updateAddress(address);
            result.put("success", success);
            result.put("message", success ? "更新成功" : "更新失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 删除地址（AJAX）
     */
    @PostMapping("/delete/{addressId}")
    @ResponseBody
    public Map<String, Object> deleteAddress(@PathVariable Long addressId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            boolean success = addressService.deleteAddress(addressId, user.getUserId());
            result.put("success", success);
            result.put("message", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 设置默认地址（AJAX）
     */
    @PostMapping("/setDefault/{addressId}")
    @ResponseBody
    public Map<String, Object> setDefaultAddress(@PathVariable Long addressId, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        try {
            boolean success = addressService.setDefaultAddress(addressId, user.getUserId());
            result.put("success", success);
            result.put("message", success ? "设置成功" : "设置失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 获取用户地址列表（AJAX，用于订单确认页面）
     */
    @GetMapping("/listByUser")
    @ResponseBody
    public Map<String, Object> getAddressListByUser(HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        SysUser user = (SysUser) session.getAttribute("currentUser");
        if (user == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        List<Address> addresses = addressService.getUserAddresses(user.getUserId());
        result.put("success", true);
        result.put("addresses", addresses);

        return result;
    }
}
