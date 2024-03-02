package com.ruoyi.online.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.online.domain.OnlineMb;
import com.ruoyi.online.service.IOnlineMbService;
import com.ruoyi.online.utils.SqlMapper;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@Anonymous
@RequestMapping("/online")
public class OnLineController extends BaseController {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Autowired
    private IOnlineMbService onlineMbService;
    @Resource(name = "ss")
    private PermissionService permissionService;

    @RequestMapping("/api/**")
    public Object api(@RequestParam(required = false) HashMap<String, Object> params,
            @RequestBody(required = false) HashMap<String, Object> data, HttpServletRequest request,
            HttpServletResponse response) {
        OnlineMb selectOnlineMb = new OnlineMb();
        selectOnlineMb.setPath(request.getRequestURI().replace("/online/api", ""));
        selectOnlineMb.setMethod(request.getMethod());
        Map<String, Object> object = new HashMap<>();
        HashMap<String, Object> object_params = new HashMap<String, Object>();
        String keyregex = "params\\[(.*?)\\]";
        Pattern pattern = Pattern.compile(keyregex);
        if (params != null) {
            params.keySet().forEach(key -> {
                Matcher matcher = pattern.matcher(key);
                if (matcher.find()) {
                    object_params.put(matcher.group(1), params.get(key));
                } else {
                    object.put(key, params.get(key));
                }
            });
        }
        if (data != null) {
            if (data.containsKey("params")) {
                object_params.putAll((HashMap<String, Object>) data.get("params"));
                data.remove("params");
            }
            object.putAll(data);
        }
        object.put("params", object_params);

        List<OnlineMb> selectOnlineMbList = onlineMbService.selectOnlineMbList(selectOnlineMb);
        if (selectOnlineMbList.size() == 0) {
            return AjaxResult.error("没有资源"+selectOnlineMb.getPath());
        } else if (selectOnlineMbList.size() > 1) {
            return AjaxResult.error(500,"系统错误，在线接口重复");
        } else {
            OnlineMb onlineMb = selectOnlineMbList.get(0);
            boolean permissionFlag = true;
            if (onlineMb.getPermissionType() != null) {
                switch (onlineMb.getPermissionType()) {
                    case "hasPermi" -> permissionFlag = permissionService.hasPermi(onlineMb.getPermissionValue());
                    case "lacksPermi" -> permissionFlag = !permissionService.lacksPermi(onlineMb.getPermissionValue());
                    case "hasAnyPermi" -> permissionFlag = permissionService.hasAnyPermi(onlineMb.getPermissionValue());
                    case "hasRole" -> permissionFlag = permissionService.hasRole(onlineMb.getPermissionValue());
                    case "lacksRole" -> permissionFlag = !permissionService.lacksRole(onlineMb.getPermissionValue());
                    case "hasAnyRoles" -> permissionFlag = permissionService.hasAnyRoles(onlineMb.getPermissionValue());
                }
            }
            if (!permissionFlag) {
                return AjaxResult.error(403,"没有权限，请联系管理员授权");
            }
            if (onlineMb.getDeptId() != null && onlineMb.getDeptId().equals("1")) {
                object.put("deptId", SecurityUtils.getDeptId());
            }
            if (onlineMb.getUserId() != null && onlineMb.getUserId().equals("1")) {
                object.put("userId", SecurityUtils.getUserId());
            }

            String sql = "<script>\n" + onlineMb.getSql() + "\n</script>";
            SqlSession sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = new SqlMapper(sqlSession);
            return switch (onlineMb.getActuator()) {
                case "selectList" -> getDataTable(sqlMapper.selectList(sql, object));
                case "insert" -> toAjax(sqlMapper.insert(sql, object));
                case "selectOne" -> success(sqlMapper.selectOne(sql, object));
                case "update" -> toAjax(sqlMapper.update(sql, object));
                case "delete" -> toAjax(sqlMapper.delete(sql, object));
                default -> AjaxResult.error(500,"系统错误，执行器错误");
            };
        }
    }

}
