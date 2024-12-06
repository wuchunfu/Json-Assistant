package cn.memoryzy.json.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.service.persistent.JsonAssistantPersistentState;
import cn.memoryzy.json.util.JavaUtil;
import cn.memoryzy.json.util.JsonUtil;
import cn.memoryzy.json.util.Notifications;
import cn.memoryzy.json.util.PlatformUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * @author Memory
 * @since 2023/11/27
 */
public class JavaBeanToJsonAction extends AnAction implements UpdateInBackground {

    private static final Logger LOG = Logger.getInstance(JavaBeanToJsonAction.class);

    public JavaBeanToJsonAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.message("action.serialize.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.serialize.description"));
        presentation.setIcon(JsonAssistantIcons.JSON);
    }


    @Override
    @SuppressWarnings("DuplicatedCode")
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        // 获取当前类
        PsiClass psiClass = JavaUtil.getPsiClass(event.getDataContext());
        // 执行操作
        convertAttributesToJsonAndNotify(project, psiClass, JsonUtil::formatJson, LOG);
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        // 设置可见性
        event.getPresentation().setEnabledAndVisible(isEnable(getEventProject(event), event.getDataContext()));
    }


    /**
     * 将 Java 属性转换为 JSON/JSON5 并复制到剪贴板，同时显示忽略的字段（如果有的话）
     *
     * @param project       项目对象
     * @param psiClass      当前类对象
     * @param jsonConverter JSON 转换器
     */
    public static void convertAttributesToJsonAndNotify(Project project, PsiClass psiClass, Function<Map<String, Object>, String> jsonConverter, Logger log) {
        // JsonMap
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        // 忽略的属性
        Map<String, List<String>> ignoreMap = new LinkedHashMap<>();
        // 相关配置
        JsonAssistantPersistentState persistentState = JsonAssistantPersistentState.getInstance();

        try {
            // 递归添加所有属性，包括嵌套属性
            JavaUtil.recursionAddProperty(project, psiClass, jsonMap, ignoreMap, persistentState.attributeSerializationState);
        } catch (Error e) {
            log.error(e);
            Notifications.showNotification(JsonAssistantBundle.messageOnSystem("error.serialize.recursion"), NotificationType.ERROR, project);
            return;
        }

        // 执行转换
        String jsonStr = jsonConverter.apply(jsonMap);

        // 添加至剪贴板
        PlatformUtil.setClipboard(jsonStr);

        Set<Map.Entry<String, List<String>>> entries = ignoreMap.entrySet();
        // 移除 value 为空列表的键值对
        entries.removeIf(entry -> entry.getValue().isEmpty());

        if (CollUtil.isNotEmpty(entries)) {
            Notifications.showFullNotification(
                    JsonAssistantBundle.messageOnSystem("notification.serialize.ignore.title"),
                    generateNotificationContent(entries),
                    NotificationType.INFORMATION,
                    project);
        } else {
            Notifications.showNotification(JsonAssistantBundle.messageOnSystem("notification.serialize.copy"), NotificationType.INFORMATION, project);
        }
    }


    public static boolean isEnable(Project project, DataContext dataContext) {
        return Objects.nonNull(project) && JavaUtil.isJavaFile(dataContext) && JavaUtil.hasJavaProperty(dataContext);
    }


    public static String generateNotificationContent(Set<Map.Entry<String, List<String>>> entries) {
        String notificationContent = "<ul>{}</ul>";
        String concreteContent = "<li>{}</li>";

        List<String> nameList = new ArrayList<>(entries.size());
        // 类名 key，value 字段集
        for (Map.Entry<String, List<String>> entry : entries) {
            // 类全限定名
            String key = entry.getKey();
            // 被忽略字段集合
            List<String> value = entry.getValue();

            key = StringUtil.getShortName(key);
            for (String fieldName : value) {
                nameList.add(fieldName + " (" + key + ")");
            }
        }

        // 强调倒序
        StringBuilder builder = new StringBuilder();
        for (int i = nameList.size() - 1; i >= 0; i--) {
            builder.append(StrUtil.format(concreteContent, nameList.get(i)));
        }

        return StrUtil.format(notificationContent, builder.toString());
    }

}