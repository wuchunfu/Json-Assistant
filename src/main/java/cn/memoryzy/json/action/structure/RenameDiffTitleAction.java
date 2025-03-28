package cn.memoryzy.json.action.structure;

import cn.hutool.core.util.ReflectUtil;
import cn.memoryzy.json.action.JsonTextDiffAction;
import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.ui.dialog.DiffNameDialog;
import com.intellij.diff.editor.SimpleDiffVirtualFile;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.tools.util.DiffSplitter;
import com.intellij.diff.tools.util.SimpleDiffPanel;
import com.intellij.diff.tools.util.side.TwosideContentPanel;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.ui.components.panels.Wrapper;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author Memory
 * @since 2025/2/27
 */
public class RenameDiffTitleAction extends DumbAwareAction implements UpdateInBackground {

    public RenameDiffTitleAction() {
        super();
        setEnabledInModalContext(true);
        Presentation presentation = getTemplatePresentation();
        presentation.setText(JsonAssistantBundle.messageOnSystem("action.rename.diffTitle.text"));
        presentation.setDescription(JsonAssistantBundle.messageOnSystem("action.rename.diffTitle.description"));
        presentation.setIcon(AllIcons.Actions.Edit);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ImmutablePair<JBLabel, JBLabel> pair = getTwoLabel(e.getDataContext());
        if (pair == null) {
            return;
        }

        new DiffNameDialog(pair.getLeft(), pair.getRight()).show();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(isEnabled(e.getDataContext()));
    }

    private boolean isEnabled(DataContext dataContext) {
        boolean enable = false;
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if (virtualFile instanceof SimpleDiffVirtualFile) {
            SimpleDiffRequest diffRequest = virtualFile.getUserData(JsonTextDiffAction.DIFF_REQUEST_KEY);
            if (Objects.nonNull(diffRequest)) {
                ImmutablePair<JBLabel, JBLabel> pair = getTwoLabel(dataContext);
                if (pair != null && pair.getLeft() != null && pair.getRight() != null) {
                    enable = true;
                }
            }
        }

        return enable;
    }

    public static ImmutablePair<JBLabel, JBLabel> getTwoLabel(DataContext dataContext) {
        JBPanelWithEmptyText coreComponent = (JBPanelWithEmptyText) PlatformDataKeys.CONTEXT_COMPONENT.getData(dataContext);
        if (coreComponent == null || coreComponent.getComponentCount() == 0) return null;

        JBSplitter splitter = findComponent(coreComponent, JBSplitter.class);
        if (splitter == null) return null;

        SimpleDiffPanel diffPanel = findComponent(splitter.getFirstComponent(), SimpleDiffPanel.class);
        if (diffPanel == null) return null;

        JPanel jPanel = findComponent(diffPanel, JPanel.class, Wrapper.class); // 自定义方法排除Wrapper
        if (jPanel == null) return null;

        BorderLayoutPanel borderLayoutPanel = findComponent(jPanel, BorderLayoutPanel.class);
        if (borderLayoutPanel == null) return null;

        TwosideContentPanel twosideContentPanel = findComponent(borderLayoutPanel, TwosideContentPanel.class);
        if (twosideContentPanel == null) return null;

        DiffSplitter diffSplitter = twosideContentPanel.getSplitter();
        JComponent firstPanel = diffSplitter.getFirstComponent();
        JComponent secondPanel = diffSplitter.getSecondComponent();

        Component myTitleFirst = (Component) ReflectUtil.getFieldValue(firstPanel, "myTitle");
        Component myTitleSecond = (Component) ReflectUtil.getFieldValue(secondPanel, "myTitle");

        JBLabel leftSourceLabel = findDeepNestedComponent(myTitleFirst, JBLabel.class);
        JBLabel rightSourceLabel = findDeepNestedComponent(myTitleSecond, JBLabel.class);

        return ImmutablePair.of(leftSourceLabel, rightSourceLabel);
    }

    public static <T extends Component> T findComponent(Container container, Class<T> clazz) {
        if (container == null) return null;

        for (Component component : container.getComponents()) {
            if (clazz.isInstance(component)) {
                return clazz.cast(component);
            }
            if (component instanceof Container) {
                T result = findComponent((Container) component, clazz);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static <T extends Component> T findDeepNestedComponent(Component root, Class<T> clazz) {
        if (root == null) return null;
        if (clazz.isInstance(root)) return clazz.cast(root);

        for (Component comp : ((Container) root).getComponents()) {
            T found = findDeepNestedComponent(comp, clazz);
            if (found != null) return found;
        }
        return null;
    }

    private static <T extends Component> T findComponent(Container container, Class<T> clazz, Class<?>... excludeClasses) {
        if (container == null) return null;

        for (Component component : container.getComponents()) {
            boolean shouldExclude = false;
            for (Class<?> excludeClass : excludeClasses) {
                if (excludeClass.isInstance(component)) {
                    shouldExclude = true;
                    break;
                }
            }
            if (shouldExclude) continue;

            if (clazz.isInstance(component)) {
                return clazz.cast(component);
            }
            if (component instanceof Container) {
                T result = findComponent((Container) component, clazz, excludeClasses);
                if (result != null) return result;
            }
        }
        return null;
    }


}
