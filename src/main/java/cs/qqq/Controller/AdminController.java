package cs.qqq.Controller;

import cs.qqq.Entity.Merchant;
import cs.qqq.Entity.SysRole;
import cs.qqq.Entity.SysUser;
import cs.qqq.Mapper.MerchantMapper;
import cs.qqq.Mapper.OrderMapper;
import cs.qqq.Mapper.ProductMapper;
import cs.qqq.Mapper.UserMapper;
import cs.qqq.Service.RoleService;
import cs.qqq.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 管理员首页 - 显示统计数据
     */
    @GetMapping("/index")
    public String index(Model model) {
        // 获取统计数据
        long totalUsers = userMapper.countAllUsers();
        long totalMerchants = merchantMapper.countAllMerchants();
        long totalProducts = productMapper.countAllProducts();
        long totalOrders = orderMapper.countAllOrders();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalMerchants", totalMerchants);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalOrders", totalOrders);

        // 获取最近5条订单
        List<java.util.Map<String, Object>> recentOrders = orderMapper.getRecentOrders(5);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/index";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String users(Model model) {
        List<SysUser> userList = userService.getAllUsers();
        List<SysRole> roleList = roleService.getAllRoles();
        
        model.addAttribute("users", userList);
        model.addAttribute("roles", roleList);
        
        return "admin/user_list";
    }

    /**
     * 搜索用户
     */
    @PostMapping("/users/search")
    public String searchUsers(Model model, 
                            @RequestParam(required = false) String userName,
                            @RequestParam(required = false) String phone,
                            @RequestParam(required = false) Long roleId) {
        List<SysUser> userList = userService.findAllUsers(userName, phone, roleId, null, null);
        List<SysRole> roleList = roleService.getAllRoles();
        
        model.addAttribute("users", userList);
        model.addAttribute("roles", roleList);
        
        return "admin/user_list";
    }

    /**
     * 更新用户状态（启用/禁用）
     */
    @PostMapping("/users/updateStatus")
    @ResponseBody
    public Map<String, Object> updateUserStatus(@RequestParam Long userId, 
                                               @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysUser user = userService.findUserById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            user.setStatus(status);
            userService.updateUser(user);
            
            result.put("success", true);
            result.put("message", status.equals("0") ? "用户已启用" : "用户已禁用");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 删除用户
     */
    @PostMapping("/users/delete/{userId}")
    @ResponseBody
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysUser user = userService.findUserById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 不允许删除管理员
            if (user.getRoleId() != null && (user.getRoleId() == 1 || user.getRoleId() == 2)) {
                result.put("success", false);
                result.put("message", "不能删除管理员用户");
                return result;
            }
            
            userMapper.deleteUserByID(userId);
            
            result.put("success", true);
            result.put("message", "用户已删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 商户管理页面
     */
    @GetMapping("/merchants")
    public String merchants(Model model) {
        // 获取所有商户信息（包含关联的用户名）
        List<Map<String, Object>> merchantList = merchantMapper.findAllWithUserInfo();
        model.addAttribute("merchants", merchantList);
        return "admin/merchant_list";
    }
    
    /**
     * 添加商户页面
     */
    @GetMapping("/merchants/add")
    public String addMerchantPage() {
        return "admin/merchant_add";
    }
    
    /**
     * 创建商户（同时创建用户账号和商户信息）
     */
    @PostMapping("/merchants/add")
    @ResponseBody
    public Map<String, Object> addMerchant(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("开始创建商户，参数: " + params);
            
            // 1. 创建用户账号（角色ID=3表示商户）
            SysUser user = new SysUser();
            user.setUserName((String) params.get("username"));
            user.setPassword((String) params.get("password"));
            user.setRoleId(3L); // 商户角色
            user.setPhone((String) params.get("phone"));
            user.setStatus("1"); // 设置状态为启用
            
            // 检查用户名是否已存在
            SysUser existUser = userMapper.findByUsername(user.getUserName());
            if (existUser != null) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }
            
            // 插入用户
            userMapper.saveUser(user);
            System.out.println("用户创建成功，用户ID: " + user.getUserId());
            
            if (user.getUserId() == null) {
                result.put("success", false);
                result.put("message", "用户创建失败");
                return result;
            }
            
            // 2. 创建商户信息
            Merchant merchant = new Merchant();
            merchant.setUserId(user.getUserId()); // 关联用户ID
            merchant.setMerchantName((String) params.get("merchantName"));
            merchant.setPhone((String) params.get("phone"));
            merchant.setAddress((String) params.get("address"));
            merchant.setDescription((String) params.get("description"));
            merchant.setBusinessHours((String) params.get("businessHours"));
            merchant.setStatus(Integer.parseInt(params.get("status").toString()));
            merchant.setRating(new java.math.BigDecimal("5.0")); // 默认评分
            merchant.setSales(0); // 默认销量
            
            System.out.println("准备插入商户信息: " + merchant);
            int merchantResult = merchantMapper.insert(merchant);
            System.out.println("商户插入结果: " + merchantResult + ", 商户ID: " + merchant.getMerchantId());
            
            if (merchantResult > 0) {
                result.put("success", true);
                result.put("message", "商户创建成功");
                result.put("userId", user.getUserId());
                result.put("merchantId", merchant.getMerchantId());
            } else {
                result.put("success", false);
                result.put("message", "商户信息创建失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 菜品管理页面
     */
    @GetMapping("/products")
    public String products(Model model) {
        // 获取所有菜品
        List<Map<String, Object>> productList = productMapper.getAllProductsWithMerchant();
        model.addAttribute("products", productList);
        return "admin/product_list";
    }

    /**
     * 下架菜品
     */
    @PostMapping("/products/offline/{productId}")
    @ResponseBody
    public Map<String, Object> offlineProduct(@PathVariable Long productId) {
        Map<String, Object> result = new HashMap<>();
        try {
            productMapper.updateProductStatus(productId, 0); // 0表示下架
            result.put("success", true);
            result.put("message", "菜品已下架");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 订单管理页面
     */
    @GetMapping("/orders")
    public String orders(Model model) {
        // 获取所有订单
        List<Map<String, Object>> orderList = orderMapper.getAllOrdersWithDetails();
        model.addAttribute("orders", orderList);
        return "admin/order_list";
    }
    
    /**
     * 跳转到添加用户页面
     */
    @GetMapping("/users/add")
    public String addUserPage(Model model) {
        List<SysRole> roleList = roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "admin/user_add";
    }
    
    /**
     * 添加用户
     */
    @PostMapping("/users/add")
    @ResponseBody
    public Map<String, Object> addUser(@RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam Long roleId,
                                      @RequestParam(required = false) String phone,
                                      @RequestParam(required = false) String email,
                                      HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查用户名是否已存在
            SysUser existUser = userMapper.findByUsername(username);
            if (existUser != null) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return result;
            }
            
            SysUser newUser = new SysUser();
            newUser.setUserName(username);
            newUser.setPassword(password);
            newUser.setRoleId(roleId);
            newUser.setPhone(phone);
            newUser.setEmail(email);
            newUser.setStatus("1"); // 1表示启用
            
            userService.saveUser(newUser);
            
            result.put("success", true);
            result.put("message", "用户添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 跳转到编辑用户页面
     */
    @GetMapping("/users/edit/{userId}")
    public String editUserPage(@PathVariable Long userId, Model model) {
        SysUser user = userService.findUserById(userId);
        List<SysRole> roleList = roleService.getAllRoles();
        
        model.addAttribute("user", user);
        model.addAttribute("roles", roleList);
        
        return "admin/user_edit";
    }
    
    /**
     * 编辑用户
     */
    @PostMapping("/users/edit")
    @ResponseBody
    public Map<String, Object> editUser(@RequestParam Long userId,
                                       @RequestParam String username,
                                       @RequestParam(required = false) String password,
                                       @RequestParam Long roleId,
                                       @RequestParam(required = false) String phone,
                                       @RequestParam(required = false) String email,
                                       HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysUser user = userService.findUserById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            user.setUserName(username);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            user.setRoleId(roleId);
            user.setPhone(phone);
            user.setEmail(email);
            
            userService.updateUser1(user);
            
            result.put("success", true);
            result.put("message", "用户更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 角色管理页面
     */
    @GetMapping("/roles")
    public String roles(Model model) {
        List<SysRole> roleList = roleService.getAllRoles();
        model.addAttribute("roles", roleList);
        return "admin/role_list";
    }
    
    /**
     * 跳转到添加角色页面
     */
    @GetMapping("/roles/add")
    public String addRolePage() {
        return "admin/role_add";
    }
    
    /**
     * 添加角色
     */
    @PostMapping("/roles/add")
    @ResponseBody
    public Map<String, Object> addRole(@RequestParam String roleName,
                                      @RequestParam(required = false) String roleDesc,
                                      HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysRole role = new SysRole();
            role.setRoleName(roleName);
            role.setRoleDesc(roleDesc);
            role.setStatus("1"); // 1表示启用
            
            roleService.addRole(role);
            
            result.put("success", true);
            result.put("message", "角色添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 跳转到编辑角色页面
     */
    @GetMapping("/roles/edit/{roleId}")
    public String editRolePage(@PathVariable Long roleId, Model model) {
        SysRole role = roleService.findRoleById(roleId);
        model.addAttribute("role", role);
        return "admin/role_edit";
    }
    
    /**
     * 编辑角色
     */
    @PostMapping("/roles/edit")
    @ResponseBody
    public Map<String, Object> editRole(@RequestParam Long roleId,
                                       @RequestParam String roleName,
                                       @RequestParam(required = false) String roleDesc,
                                       HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysRole role = roleService.findRoleById(roleId);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return result;
            }
            
            role.setRoleName(roleName);
            role.setRoleDesc(roleDesc);
            
            roleService.updateRole1(role);
            
            result.put("success", true);
            result.put("message", "角色更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除角色
     */
    @PostMapping("/roles/delete/{roleId}")
    @ResponseBody
    public Map<String, Object> deleteRole(@PathVariable Long roleId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 检查是否为系统预设角色
            if (roleId <= 5) {
                result.put("success", false);
                result.put("message", "不能删除系统预设角色");
                return result;
            }
            
            SysRole role = roleService.findRoleById(roleId);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return result;
            }
            
            roleService.deleteRole(roleId);
            
            result.put("success", true);
            result.put("message", "角色已删除");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新角色状态
     */
    @PostMapping("/roles/updateStatus")
    @ResponseBody
    public Map<String, Object> updateRoleStatus(@RequestParam Long roleId,
                                               @RequestParam String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            SysRole role = roleService.findRoleById(roleId);
            if (role == null) {
                result.put("success", false);
                result.put("message", "角色不存在");
                return result;
            }
            
            role.setStatus(status);
            roleService.updateRole(role);
            
            result.put("success", true);
            result.put("message", status.equals("0") ? "角色已启用" : "角色已禁用");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }
}
