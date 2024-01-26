package com.ruoyi.online.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.online.domain.OnlineMb;
import com.ruoyi.online.service.IOnlineMbService;
import com.ruoyi.online.utils.SqlMapper;

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

    @RequestMapping("/api/**")
    public Object api(@RequestBody(required = false) Map<String, Object> data, HttpServletResponse response,
            HttpServletRequest request) {
        OnlineMb selectOnlineMb = new OnlineMb();
        selectOnlineMb.setPath(request.getRequestURI().replace("/online/api", ""));
        selectOnlineMb.setMethod(request.getMethod());
        Map<String, Object> object = new HashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        if (params != null) {
            object.putAll(params);
        }
        if (data != null) {
            object.putAll(data);
        }
        List<OnlineMb> selectOnlineMbList = onlineMbService.selectOnlineMbList(selectOnlineMb);
        if (selectOnlineMbList.size() == 0) {
            return error("没有相关接口");
        } else if (selectOnlineMbList.size() > 1) {
            return error("存在多个接口");
        } else {
            OnlineMb onlineMb = selectOnlineMbList.get(0);
            String sql = "<script>\n" + onlineMb.getSql() + "\n</script>";
            SqlSession sqlSession = sqlSessionFactory.openSession();
            SqlMapper sqlMapper = new SqlMapper(sqlSession);
            return switch (onlineMb.getActuator()) {
                case "selectList" -> getDataTable(sqlMapper.selectList(sql, object));
                case "insert" -> toAjax(sqlMapper.insert(sql, object));
                case "selectOne" -> success(sqlMapper.selectOne(sql, object));
                case "update" -> toAjax(sqlMapper.update(sql, object));
                case "delete" -> toAjax(sqlMapper.delete(sql, object));
                default -> error("错误的执行器");
            };
        }
    }

}
