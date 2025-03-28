package cn.memoryzy.json.ui.dialog;

import cn.memoryzy.json.bundle.JsonAssistantBundle;
import cn.memoryzy.json.constant.HtmlConstant;
import cn.memoryzy.json.constant.LanguageHolder;
import cn.memoryzy.json.enums.UrlType;
import cn.memoryzy.json.model.YamlDocEntry;
import cn.memoryzy.json.ui.editor.ViewerModeLanguageTextEditor;
import cn.memoryzy.json.util.JsonAssistantUtil;
import cn.memoryzy.json.util.UIManager;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.speedSearch.ListWithFilter;
import com.intellij.ui.speedSearch.SpeedSearchUtil;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Memory
 * @since 2024/9/25
 */
@SuppressWarnings("DuplicatedCode")
public class MultiYamlDocumentChooser extends DialogWrapper {

    private JList<YamlDocEntry> showList;
    private EditorTextField showTextField;

    private final List<Object> values;

    public MultiYamlDocumentChooser(List<Object> values) {
        super((Project) null, true);
        this.values = values;

        setTitle(JsonAssistantBundle.messageOnSystem("dialog.yaml.chooser.title"));
        setOKButtonText(JsonAssistantBundle.messageOnSystem("dialog.yaml.chooser.ok"));
        setCancelButtonText(JsonAssistantBundle.messageOnSystem("dialog.yaml.chooser.cancel"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        showTextField = new ViewerModeLanguageTextEditor(LanguageHolder.YAML, null, "", true);
        showTextField.setFont(UIManager.consolasFont(14));

        showList = new JBList<>(fillListModel());
        showList.setFont(UIManager.jetBrainsMonoFont(13));
        showList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showList.addListSelectionListener(new UpdateEditorListSelectionListener());
        showList.setCellRenderer(new IconListCellRenderer());

        // 初始化鼠标左键双击事件
        initLeftMouseDoubleClickListener();
        // 初始化回车事件
        initEnterListener();
        // 默认选中第一条
        selectFirstItemInList();

        UIManager.updateComponentColorsScheme(showList);
        UIManager.updateComponentColorsScheme(showTextField);

        JBLabel label = new JBLabel();
        label.setFont(JBFont.label().deriveFont(13F));
        label.setText(HtmlConstant.wrapHtml(JsonAssistantBundle.messageOnSystem("dialog.yaml.chooser.hint")));
        label.setBorder(JBUI.Borders.emptyBottom(10));

        JComponent wrapComponent = UIManager.wrapListWithFilter(showList, YamlDocEntry::getShortText, true);
        rebuildListWithFilter();

        JPanel firstPanel = new JPanel(new BorderLayout());
        firstPanel.add(label, BorderLayout.NORTH);
        firstPanel.add(wrapComponent, BorderLayout.CENTER);
        firstPanel.setBorder(JBUI.Borders.empty(3));

        JBSplitter splitter = new JBSplitter(true, 0.3f);
        splitter.setFirstComponent(firstPanel);
        splitter.setSecondComponent(showTextField);

        ScrollingUtil.installActions(showList);
        ScrollingUtil.ensureSelectionExists(showList);

        splitter.setPreferredSize(JBUI.size(450, 500));

        return splitter;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return showList;
    }

    @Override
    protected @NonNls @Nullable String getHelpId() {
        return UrlType.YAML_DIALOG.getId();
    }

    @Override
    protected void doOKAction() {
        if (getOKAction().isEnabled()) {
            // 执行逻辑
            if (executeOkAction()) {
                close(OK_EXIT_CODE);
            }
        }
    }

    private boolean executeOkAction() {
        YamlDocEntry selectedValue = showList.getSelectedValue();
        return selectedValue != null;
    }

    private void rebuildListWithFilter() {
        ListWithFilter<?> listWithFilter = ComponentUtil.getParentOfType(ListWithFilter.class, showList);
        if (listWithFilter != null) {
            listWithFilter.getSpeedSearch().update();
            if (showList.getModel().getSize() == 0) listWithFilter.resetFilter();
        }
    }

    private void initEnterListener() {
        DumbAwareAction.create(event -> close(OK_EXIT_CODE))
                .registerCustomShortcutSet(CustomShortcutSet.fromString("ENTER"), showList, getDisposable());
    }

    private void initLeftMouseDoubleClickListener() {
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                close(OK_EXIT_CODE);
                return true;
            }
        }.installOn(showList);
    }

    private DefaultListModel<YamlDocEntry> fillListModel() {
        List<YamlDocEntry> models = YamlDocEntry.of(values);
        return JBList.createDefaultListModel(models);
    }

    private void selectFirstItemInList() {
        if (showList.getModel().getSize() > 0) {
            showList.setSelectedIndex(0);
        }
    }

    public YamlDocEntry getSelectValue() {
        return showList.getSelectedValue();
    }


    public static class IconListCellRenderer extends ColoredListCellRenderer<YamlDocEntry> {
        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends YamlDocEntry> list, YamlDocEntry value,
                                             int index, boolean selected, boolean hasFocus) {
            append((index + 1) + "  ", SimpleTextAttributes.GRAY_ATTRIBUTES, false);
            append(" " + value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES, true);
            setIcon(AllIcons.FileTypes.Yaml);
            SpeedSearchUtil.applySpeedSearchHighlighting(list, this, true, selected);
        }
    }

    public class UpdateEditorListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            YamlDocEntry selectedValue = showList.getSelectedValue();
            if (selectedValue != null) {
                showTextField.setText(JsonAssistantUtil.normalizeLineEndings(selectedValue.getLongText()));
            }
        }
    }

}
