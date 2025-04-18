package cn.memoryzy.json.action.transform;

import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.model.strategy.GlobalJsonConverter;
import cn.memoryzy.json.util.Json5Util;
import cn.memoryzy.json.util.PlatformUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/11/4
 */
public class ToJsonAction extends DumbAwareAction implements UpdateInBackground {

    public ToJsonAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.message("action.json5.to.json.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.json5.to.json.description"));
        presentation.setIcon(AllIcons.FileTypes.Json);
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public void actionPerformed(@NotNull AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        GlobalJsonConverter.convertBetweenJsonAndJson5(
                dataContext,
                PlatformUtil.getEditor(dataContext),
                Json5Util::convertJson5ToJson,
                JsonAssistantBundle.messageOnSystem("hint.selection.json5.to.json"),
                JsonAssistantBundle.messageOnSystem("hint.global.json5.to.json"));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        // 确保当前是JSON5
        DataContext dataContext = event.getDataContext();
        event.getPresentation().setEnabledAndVisible(
                GlobalJsonConverter.validateEditorJson5(
                        getEventProject(event), PlatformUtil.getEditor(dataContext)));
    }
}
