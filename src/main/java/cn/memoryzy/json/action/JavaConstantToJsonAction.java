package cn.memoryzy.json.action;

import cn.hutool.core.util.StrUtil;
import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.constant.FileTypeHolder;
import cn.memoryzy.json.util.Json5Util;
import cn.memoryzy.json.util.JsonUtil;
import cn.memoryzy.json.util.PsiUtil;
import cn.memoryzy.json.util.TextTransformUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/9/6
 */
public class JavaConstantToJsonAction extends AnAction implements UpdateInBackground {

    public JavaConstantToJsonAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.message("action.extractor.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.extractor.description"));
        presentation.setIcon(JsonAssistantIcons.ESC);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        String json = PsiUtil.computeStringExpression(event.getDataContext());
        // 格式化
        String formatted = JsonUtil.isJson(json) ? JsonUtil.formatJson(json) : Json5Util.formatJson5(json);
        // 输出
        TextTransformUtil.applyTextWhenNotWritable(getEventProject(event), formatted, FileTypeHolder.JSON5);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void update(@NotNull AnActionEvent event) {
        String json = PsiUtil.computeStringExpression(event.getDataContext());
        event.getPresentation().setEnabledAndVisible(
                getEventProject(event) != null
                        && StrUtil.isNotBlank(json)
                        && (JsonUtil.isJson(json) || Json5Util.isJson5(json)));
    }

}
