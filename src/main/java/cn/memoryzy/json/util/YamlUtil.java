package cn.memoryzy.json.util;

import com.google.common.collect.Lists;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Memory
 * @since 2024/9/19
 */
public class YamlUtil {

    public static boolean isYaml(String text) {
        // yaml.load(text) 也可以解析 Json 格式数据，所以在此先判断是否为 Json
        if (JsonUtil.isJsonStr(text)) {
            return false;
        }

        // 若单文档解析失败，则解析多文档
        if (isSingleYamlDocument(text)) {
            return true;
        }

        return containsMultipleYamlDocuments(text);
    }

    public static boolean isSingleYamlDocument(String yamlStr) {
        try {
            Yaml yaml = new Yaml();
            Object obj = yaml.load(yamlStr);
            return obj instanceof List || obj instanceof Map;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean containsMultipleYamlDocuments(String yamlStr) {
        List<Boolean> resultList = new ArrayList<>();

        try {
            Yaml yaml = new Yaml();
            Iterable<Object> iterable = yaml.loadAll(yamlStr);
            for (Object obj : iterable) {
                resultList.add(obj instanceof List || obj instanceof Map);
            }
        } catch (Exception ex) {
            return false;
        }

        return resultList.stream().allMatch(el -> el);
    }

    public static List<Object> loadAll(String yamlStr) {
        return Lists.newArrayList(yaml().loadAll(yamlStr));
    }

    public static String toJson(String yamlStr) {
        Yaml yaml = yaml();
        Object obj = yaml.load(yamlStr);
        return JsonUtil.toJsonStr(obj);
    }

    public static String toYaml(String jsonStr) {
        Yaml yaml = yaml();
        Object obj = JsonUtil.toBean(jsonStr);
        return yaml.dump(obj);
    }

    public static String toYaml(Object obj) {
        return yaml().dump(obj);
    }

    private static Yaml yaml() {
        // 设置 YAML 输出选项
        DumperOptions dumperOptions = new DumperOptions();
        // 设置 YAML 的输出样式为块样式（更易读）
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // Yaml 非线程安全类，每次使用必须创建新的
        return new Yaml(dumperOptions);
    }

}
