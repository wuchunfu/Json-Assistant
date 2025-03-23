package cn.memoryzy.json.action.query;

import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.enums.JsonQuerySchema;
import cn.memoryzy.json.service.persistent.state.QueryState;
import cn.memoryzy.json.ui.JsonQueryComponentProvider;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import com.intellij.openapi.project.DumbAwareToggleAction;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/12/27
 */
public class JsonPathToggleAction extends DumbAwareToggleAction implements UpdateInBackground {

    private final QueryState queryState;
    private final JsonQueryComponentProvider queryComponentProvider;

    public JsonPathToggleAction(QueryState queryState, JsonQueryComponentProvider queryComponentProvider) {
        super();
        this.queryState = queryState;
        this.queryComponentProvider = queryComponentProvider;
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.messageOnSystem("action.toggle.jsonpath.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.toggle.jsonpath.description"));
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return queryState.querySchema == JsonQuerySchema.JSONPath;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        // 限制单选
        queryState.querySchema = state ? JsonQuerySchema.JSONPath : JsonQuerySchema.JMESPath;
        queryComponentProvider.clearSearchAndResultText();
    }
}
