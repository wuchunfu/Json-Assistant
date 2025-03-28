package cn.memoryzy.json.action.group;

import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.constant.ActionHolder;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAware;
import icons.JsonAssistantIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Memory
 * @since 2024/8/3
 */
public class ConvertOtherFormatsGroup extends DefaultActionGroup implements DumbAware, UpdateInBackground {

    public ConvertOtherFormatsGroup() {
        super();
        setPopup(true);
        setEnabledInModalContext(true);
        Presentation templatePresentation = getTemplatePresentation();
        templatePresentation.setText(JsonAssistantBundle.message("group.convert.other.formats.text"));
        templatePresentation.setDescription(JsonAssistantBundle.messageOnSystem("group.convert.other.formats.description"));
        templatePresentation.setIcon(JsonAssistantIcons.FUNCTION);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent event) {
        return new AnAction[]{
                ActionHolder.TO_XML_ACTION,
                ActionHolder.TO_YAML_ACTION,
                ActionHolder.TO_TOML_ACTION,
                ActionHolder.TO_JSON5_ACTION,
                ActionHolder.TO_JSON_ACTION,
                ActionHolder.TO_PROPERTIES_ACTION,
                ActionHolder.TO_URL_PARAM_ACTION
        };
    }

}
