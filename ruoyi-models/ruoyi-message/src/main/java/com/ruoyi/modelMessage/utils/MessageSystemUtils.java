package com.ruoyi.modelMessage.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.modelMessage.domain.MessageSystem;
import com.ruoyi.modelMessage.domain.MessageTemplate;
import com.ruoyi.modelMessage.domain.MessageVariable;
import com.ruoyi.modelMessage.mapper.MessageTemplateMapper;
import com.ruoyi.modelMessage.mapper.MessageVariableMapper;

@Component
public class MessageSystemUtils {

    private static final String NUMERIC_CHARACTERS = "0123456789";
    private static final int CODE_LENGTH = 6;

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;

    @Autowired
    private MessageVariableMapper messageVariableMapper;

    /**
     * 解析输入字符串，提取模板代码和参数
     * 
     * @param input 输入字符串
     * @return 解析结果
     */
    public Map<String, Object> parseInput(String input) {
        if (input == null) {
            throw new ServiceException("输入内容不能为空！");
        }
        String templateCode = null;
        String queryParams = "";
        if (input.startsWith("SMS_")) {
            int templateCodeEndIndex = input.indexOf('?');
            if (templateCodeEndIndex != -1) {
                templateCode = input.substring(0, templateCodeEndIndex);
                queryParams = input.substring(templateCodeEndIndex + 1);
            } else {
                templateCode = input;
            }
        }
        MessageTemplate templateContent = null;
        List<String> variableNames = new ArrayList<>();
        if (templateCode != null) {
            templateContent = messageTemplateMapper.selectMessageTemplateByTemplateCode(templateCode);
            if (templateContent == null) {
                throw new ServiceException("未找到该模版签名！ " + templateCode);
            }
            variableNames = extractVariableNamesFromTemplate(templateContent);
        }

        Map<String, String> params = new HashMap<>();
        if (!queryParams.isEmpty()) {
            for (String param : queryParams.split("&")) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length != 2) {
                    throw new ServiceException("无效的参数格式：" + param);
                }
                params.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        if (templateContent != null) {
            for (String varName : variableNames) {
                if (!params.containsKey(varName)) {
                    params.put(varName, generateRandomCode(CODE_LENGTH));
                }
            }
        }
        return Map.of("templateCode", templateCode, "params", params,
                    "templateContent", templateContent != null ? templateContent.getTemplateContent() : input,
                    "variableNames", variableNames);
    }

    /**
     * 生成指定长度的随机数字字符串
     * 
     * @param length 长度
     * @return 随机数字字符串
     */
    public String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(NUMERIC_CHARACTERS.length());
            char randomChar = NUMERIC_CHARACTERS.charAt(index);
            codeBuilder.append(randomChar);
        }
        return codeBuilder.toString();
    }

    /**
     * 提取模板中的变量名
     * 
     * @param templateContent 模板内容
     * @return 变量名列表
     */
    public List<String> extractVariableNamesFromTemplate(MessageTemplate templateContent) {
        List<String> variableNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(templateContent.getTemplateContent());
        while (matcher.find()) {
            variableNames.add(matcher.group(1));
        }
        return variableNames;
    }

    /**
     * 填充模板
     * 
     * @param template 模板
     * @param params 参数
     * @return 填充后的模板
     */
    public String fillTemplate(String template, Map<String, String> params) {
        String filledTemplate = template;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            filledTemplate = filledTemplate.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return filledTemplate;
    }

    /**
     * 清除内置变量的随机数字
     * 
     * @param params 参数
     */
    public void clearBuiltInVariables(Map<String, String> params) {
        List<MessageVariable> builtInVariables = messageVariableMapper.selectMessageVariable();
        for (MessageVariable variable : builtInVariables) {
            String variableContent = variable.getVariableContent();
            params.remove(variableContent);
        }
    }

    /**
     * 处理模板消息并填充内置变量
     */
    public String processTemplateMessage(MessageSystem messageSystem, String notificationContent) throws Exception {
        Map<String, Object> parsedParams = parseInput(notificationContent);
        String templateCode = (String) parsedParams.get("templateCode");
        MessageTemplate templateContent = null;

        if (templateCode != null) {
            templateContent = messageTemplateMapper.selectMessageTemplateByTemplateCode(templateCode); //查询获取模版内容
        }

        @SuppressWarnings("unchecked")
        Map<String, String> params = (Map<String, String>) parsedParams.get("params");

        // 检查参数
        List<String> variableNames = new ArrayList<>();
        if (templateContent != null) {
            variableNames = extractVariableNamesFromTemplate(templateContent);
            for (String varName : variableNames) {
                if (!params.containsKey(varName)) {
                    throw new ServiceException("缺少参数: " + varName);
                }
            }
        }
        clearBuiltInVariables(params); // 清除内置变量
        fillBuiltInVariables(params, messageSystem, variableNames);
        // 如果未找到模板，使用原始内容作为模板
        String templateContentStr = templateContent != null ? templateContent.getTemplateContent() : notificationContent;
        return fillTemplate(templateContentStr, params);
    }

    /**
     * 填充内置变量
     * 
     * @param params 参数
     * @param message 消息
     * @param variableNames 变量名列表
     */
    public void fillBuiltInVariables(Map<String, String> params, MessageSystem message, List<String> variableNames) {
        List<MessageVariable> builtInVariables = messageVariableMapper.selectMessageVariables(variableNames);
        for (MessageVariable variable : builtInVariables) {
            String variableContent = variable.getVariableContent();
            String variableValue = "recipients".equals(variableContent) ? message.getMessageRecipient() : generateVariableContent(variableContent);
            params.putIfAbsent(variableContent, variableValue);
        }
    }

    /**
     * 生成变量内容
     * 
     * @param variableContent 变量内容
     * @return 生成的变量内容
     */
    public String generateVariableContent(String variableContent) {
        switch (variableContent) {
            case "time":
                return DateUtils.dateTimeNow("HH:mm:ss");
            case "date":
                return DateUtils.dateTimeNow("yyyy-MM-dd");
            case "datetime":
                return DateUtils.dateTimeNow("yyyy-MM-dd HH:mm:ss");
            case "addresser":
            case "recipients":
                return SecurityUtils.getUsername();
            case "code":
                return generateRandomCode(CODE_LENGTH); // 确保这里生成随机验证码
            case "RandomnDigits":
                return RandomStringUtils.randomNumeric(CODE_LENGTH);
            case "RandomnCharacters":
                return RandomStringUtils.randomAlphabetic(CODE_LENGTH);
            case "RandomN-digitLetters":
                return RandomStringUtils.randomAlphanumeric(CODE_LENGTH);
            default:
                throw new ServiceException("不明确的类型 " + variableContent);
        }
    }
}
