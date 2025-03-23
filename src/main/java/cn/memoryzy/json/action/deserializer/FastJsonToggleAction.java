package cn.memoryzy.json.action.deserializer;

import cn.memoryzy.json.service.persistent.state.DeserializerState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.actionSystem.UpdateInBackground;
import org.jetbrains.annotations.NotNull;

/**
 * @author Memory
 * @since 2024/12/26
 */
public class FastJsonToggleAction extends ToggleAction implements UpdateInBackground {

    private final DeserializerState deserializerState;

    public FastJsonToggleAction(DeserializerState deserializerState) {
        super("@JSONField (FastJSON)", null, null);
        this.deserializerState = deserializerState;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return deserializerState.fastJsonAnnotation;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        deserializerState.fastJsonAnnotation = state;
    }

}